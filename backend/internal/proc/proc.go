package proc

import (
	"context"
	"errors"
	"fmt"
	"net/http"
	"strconv"
	"time"

	"github.com/usamaroman/music_room/backend/internal/config"
	"github.com/usamaroman/music_room/backend/internal/proc/request"
	"github.com/usamaroman/music_room/backend/internal/storage"
	"github.com/usamaroman/music_room/backend/internal/storage/dbo"
	"github.com/usamaroman/music_room/backend/internal/storage/repo"
	"github.com/usamaroman/music_room/backend/pkg/jwt"
	rds "github.com/usamaroman/music_room/backend/pkg/redis"
	"github.com/usamaroman/music_room/backend/pkg/utils"

	"github.com/gin-gonic/gin"
	"github.com/go-playground/validator/v10"
	"github.com/jackc/pgx/v5"
	"github.com/redis/go-redis/v9"
	"go.uber.org/zap"
	"golang.org/x/crypto/bcrypt"
	gomail "gopkg.in/mail.v2"
)

type Collections interface {
	Users() *repo.Users
	Tracks() *repo.Tracks
}

type Cache interface {
	Set(ctx context.Context, key string, value any, expiration time.Duration)
	Get(ctx context.Context, key string) (string, error)
}

type proc struct {
	cfg *config.Config
	log *zap.Logger

	router  *gin.Engine
	httpsrv *http.Server
	storage Collections
	cache   Cache
}

func NewProc(logger *zap.Logger, cfg *config.Config, storage *storage.Collection, redisClient *rds.Client) *proc {
	router := gin.Default()

	return &proc{
		log:    logger,
		cfg:    cfg,
		router: router,
		httpsrv: &http.Server{
			Addr:    fmt.Sprintf("%s:%s", cfg.HTTP.Host, cfg.HTTP.Port),
			Handler: router,
		},
		storage: storage,
		cache:   redisClient,
	}
}

func (p *proc) RegisterRoutes() {
	p.log.Info("routes registration")

	p.router.GET("/status", func(c *gin.Context) {
		c.String(http.StatusOK, "health\n")
	})

	apiGroup := p.router.Group("/auth")

	apiGroup.POST("/registration", p.registration)
	apiGroup.POST("/login", p.login)
	apiGroup.POST("/code/:userID", p.verificationCode)
	apiGroup.POST("/submit", p.submitCode)
	apiGroup.POST("/refresh", p.refreshToken)
}

func (p *proc) registration(c *gin.Context) {
	var req request.Registration

	err := c.ShouldBindJSON(&req)
	if err != nil {
		p.log.Error("failed to bind request", zap.Error(err))
		c.JSON(http.StatusBadRequest, gin.H{
			"err": err.Error(),
		})

		return
	}

	err = validator.New().Struct(req)
	if err != nil {
		p.log.Error("failed to validate struct", zap.Error(err))
		c.JSON(http.StatusBadRequest, gin.H{
			"err": err.Error(),
		})

		return
	}

	exists, err := p.storage.Users().ExistsBYEmail(c, req.Email)
	if err != nil {
		p.log.Error("failed to check user for existing", zap.Error(err))
		c.JSON(http.StatusInternalServerError, gin.H{
			"err": err.Error(),
		})

		return
	}

	if exists {
		c.JSON(http.StatusBadRequest, gin.H{
			"err": "email is already used",
		})

		return
	}

	exists, err = p.storage.Users().ExistsBYNickname(c, req.Nickname)
	if err != nil {
		p.log.Error("failed to check user for existing", zap.Error(err))
		c.JSON(http.StatusInternalServerError, gin.H{
			"err": err.Error(),
		})

		return
	}

	if exists {
		c.JSON(http.StatusBadRequest, gin.H{
			"err": "nickname is already used",
		})

		return
	}

	generateFromPassword, err := bcrypt.GenerateFromPassword([]byte(req.Password), 3)
	if err != nil {
		p.log.Error("failed to hash password", zap.Error(err))
		c.JSON(http.StatusInternalServerError, gin.H{
			"err": err.Error(),
		})

		return
	}

	userID, err := p.storage.Users().Create(c, &dbo.User{
		Email:    req.Email,
		Nickname: req.Nickname,
		Password: string(generateFromPassword),
	})
	if err != nil {
		p.log.Error("failed to create user", zap.Error(err))
		c.JSON(http.StatusInternalServerError, gin.H{
			"err": err.Error(),
		})

		return
	}

	c.JSON(http.StatusCreated, gin.H{
		"id": userID,
	})
}

func (p *proc) login(c *gin.Context) {
	var req request.Login

	err := c.ShouldBindJSON(&req)
	if err != nil {
		p.log.Error("failed to bind request", zap.Error(err))
		c.JSON(http.StatusBadRequest, gin.H{
			"err": err.Error(),
		})

		return
	}

	err = validator.New().Struct(req)
	if err != nil {
		p.log.Error("failed to validate struct", zap.Error(err))
		c.JSON(http.StatusBadRequest, gin.H{
			"err": err.Error(),
		})

		return
	}

	user, err := p.storage.Users().ByEmail(c, req.Email)
	if err != nil {
		if errors.Is(err, pgx.ErrNoRows) {
			c.JSON(http.StatusBadRequest, gin.H{
				"err": "no such user",
			})

			return
		}

		p.log.Error("failed to get user", zap.Error(err))
		c.JSON(http.StatusInternalServerError, gin.H{
			"err": err.Error(),
		})

		return
	}

	err = bcrypt.CompareHashAndPassword([]byte(user.Password), []byte(req.Password))
	if err != nil {
		if errors.Is(err, bcrypt.ErrMismatchedHashAndPassword) {
			c.JSON(http.StatusInternalServerError, gin.H{
				"err": "wrong password",
			})

			return
		}

		p.log.Error("failed to compare passwords", zap.Error(err))
		c.JSON(http.StatusInternalServerError, gin.H{
			"err": err.Error(),
		})

		return
	}

	accessToken, err := jwt.GenerateAccessToken(user)
	if err != nil {
		p.log.Error("failed to generate access token", zap.Error(err))
		c.JSON(http.StatusInternalServerError, gin.H{
			"err": err.Error(),
		})

		return
	}

	refreshToken, err := jwt.GenerateRefreshToken(user.ID)
	if err != nil {
		p.log.Error("failed to generate refresh token", zap.Error(err))
		c.JSON(http.StatusInternalServerError, gin.H{
			"err": err.Error(),
		})

		return
	}

	c.JSON(http.StatusOK, gin.H{
		"access_token":  accessToken,
		"refresh_token": refreshToken,
	})
}

func (p *proc) verificationCode(c *gin.Context) {
	id := c.Param("userID")

	userID, err := strconv.Atoi(id)
	if err != nil {
		p.log.Error("failed to convert string to int", zap.Error(err))
		c.JSON(http.StatusBadRequest, gin.H{
			"err": err.Error(),
		})

		return
	}

	user, err := p.storage.Users().ByID(c, userID)
	if err != nil {
		if errors.Is(err, pgx.ErrNoRows) {
			c.JSON(http.StatusInternalServerError, gin.H{
				"err": "user does not exist",
			})

			return
		}

		p.log.Error("failed to get user", zap.Int("id", userID), zap.Error(err))
		c.JSON(http.StatusInternalServerError, gin.H{
			"err": err.Error(),
		})

		return
	}

	code := utils.GenerateNumber()

	m := gomail.NewMessage()
	m.SetHeader("From", p.cfg.SMTP.Email)
	m.SetHeader("To", user.Email)
	m.SetHeader("Subject", "Verification Code")
	m.SetBody("text/plain", fmt.Sprintf("code is %d", code))
	d := gomail.NewDialer("smtp.gmail.com", 587, p.cfg.SMTP.Email, p.cfg.SMTP.Password)

	if err = d.DialAndSend(m); err != nil {
		fmt.Println(err)
	}

	p.cache.Set(c, strconv.Itoa(userID), code, 60*time.Second)

	c.JSON(http.StatusOK, gin.H{
		"code":   code,
		"userID": userID,
	})
}

func (p *proc) submitCode(c *gin.Context) {
	var req request.Submit

	err := c.ShouldBindJSON(&req)
	if err != nil {
		p.log.Error("failed to bind request", zap.Error(err))
		c.JSON(http.StatusBadRequest, gin.H{
			"err": err.Error(),
		})

		return
	}

	value, err := p.cache.Get(c, req.UserID)
	if err == redis.Nil {
		p.log.Error("key does not exist", zap.String("key", req.UserID))
		c.JSON(http.StatusBadRequest, gin.H{
			"err": "key does not exist",
		})

		return
	} else if err != nil {
		p.log.Error("failed to get value", zap.String("key", req.UserID))
		c.JSON(http.StatusInternalServerError, gin.H{
			"err": err.Error(),
		})

		return
	}

	if value != req.Code {
		c.JSON(http.StatusInternalServerError, gin.H{
			"err": "wrong code",
		})

		return
	}

	id, err := strconv.Atoi(req.UserID)
	if err != nil {
		p.log.Error("failed to convert int to string", zap.Error(err))
		c.JSON(http.StatusInternalServerError, gin.H{
			"err": err.Error(),
		})

		return
	}

	if err = p.storage.Users().SetActive(c, id); err != nil {
		p.log.Error("failed to update user", zap.Int("id", id), zap.Error(err))
		c.JSON(http.StatusInternalServerError, gin.H{
			"err": err.Error(),
		})

		return
	}

	c.JSON(http.StatusOK, gin.H{
		"msg": "user registered",
	})
}

func (p *proc) refreshToken(c *gin.Context) {
	var req request.Refresh

	err := c.ShouldBindJSON(&req)
	if err != nil {
		p.log.Error("failed to bind request", zap.Error(err))
		c.JSON(http.StatusBadRequest, gin.H{
			"err": err.Error(),
		})

		return
	}

	err = validator.New().Struct(req)
	if err != nil {
		p.log.Error("failed to validate struct", zap.Error(err))
		c.JSON(http.StatusBadRequest, gin.H{
			"err": err.Error(),
		})

		return
	}

	jwtMapClaims, err := jwt.ParseRefreshToken(req.Token)
	if err != nil {
		p.log.Error("failed to parse refresh token", zap.Error(err))
		c.JSON(http.StatusBadRequest, gin.H{
			"err": err.Error(),
		})

		return
	}

	id := jwtMapClaims["id"].(float64)

	user, err := p.storage.Users().ByID(c, int(id))
	if err != nil {
		p.log.Error("failed to get user", zap.Int("id", int(id)), zap.Error(err))
		c.JSON(http.StatusInternalServerError, gin.H{
			"err": err.Error(),
		})

		return
	}

	accessToken, err := jwt.GenerateAccessToken(user)
	if err != nil {
		p.log.Error("failed to generate access token", zap.Error(err))
		c.JSON(http.StatusInternalServerError, gin.H{
			"err": err.Error(),
		})

		return
	}

	refreshToken, err := jwt.GenerateRefreshToken(user.ID)
	if err != nil {
		p.log.Error("failed to generate refresh token", zap.Error(err))
		c.JSON(http.StatusInternalServerError, gin.H{
			"err": err.Error(),
		})

		return
	}

	c.JSON(http.StatusOK, gin.H{
		"access_token":  accessToken,
		"refresh_token": refreshToken,
	})
}

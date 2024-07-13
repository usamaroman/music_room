package proc

import (
	"errors"
	"fmt"
	"github.com/jackc/pgx/v5"
	"net/http"

	"github.com/usamaroman/music_room/backend/internal/config"
	"github.com/usamaroman/music_room/backend/internal/proc/request"
	"github.com/usamaroman/music_room/backend/internal/storage"
	"github.com/usamaroman/music_room/backend/internal/storage/dbo"
	"github.com/usamaroman/music_room/backend/internal/storage/repo"
	"github.com/usamaroman/music_room/backend/pkg/redis"

	"github.com/gin-gonic/gin"
	"github.com/go-playground/validator/v10"
	"go.uber.org/zap"
	"golang.org/x/crypto/bcrypt"
)

type Collections interface {
	Users() *repo.Users
}

type Cache interface {
}

type proc struct {
	log *zap.Logger

	router  *gin.Engine
	httpsrv *http.Server
	storage Collections
	cache   Cache
}

func NewProc(logger *zap.Logger, cfg *config.Config, storage *storage.Collection, redisClient *redis.Client) *proc {
	router := gin.Default()

	return &proc{
		log:    logger,
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
	apiGroup.POST("/code", p.verificationCode)
	apiGroup.POST("/submit", p.submitCode)
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

	c.JSON(http.StatusOK, gin.H{
		"user": user,
	})
}

func (p *proc) verificationCode(c *gin.Context) {

}

func (p *proc) submitCode(c *gin.Context) {

}

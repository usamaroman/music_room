package proc

import (
	"bytes"
	"context"
	"errors"
	"fmt"
	"io"
	"mime/multipart"
	"net/http"
	"os"
	"strconv"
	"time"

	"github.com/usamaroman/music_room/backend/internal/config"
	"github.com/usamaroman/music_room/backend/internal/proc/request"
	"github.com/usamaroman/music_room/backend/internal/storage"
	"github.com/usamaroman/music_room/backend/internal/storage/dbo"
	"github.com/usamaroman/music_room/backend/internal/storage/repo"
	"github.com/usamaroman/music_room/backend/pkg/jwt"
	"github.com/usamaroman/music_room/backend/pkg/minio"
	rds "github.com/usamaroman/music_room/backend/pkg/redis"
	"github.com/usamaroman/music_room/backend/pkg/utils"

	"github.com/gin-gonic/gin"
	"github.com/go-playground/validator/v10"
	"github.com/google/uuid"
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

type S3 interface {
	SaveMp3(ctx context.Context, filename, filepath string) error
	SaveImage(ctx context.Context, filename, filepath string) error
	Cleanup(ctx context.Context, bucketName string)
}

type proc struct {
	cfg *config.Config
	log *zap.Logger

	router  *gin.Engine
	httpsrv *http.Server
	storage Collections
	cache   Cache
	s3      S3
}

func NewProc(logger *zap.Logger, cfg *config.Config, storage *storage.Collection, redisClient *rds.Client, minio *minio.Client) *proc {
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
		s3:      minio,
	}
}

func (p *proc) FillData() error {
	requests := []struct {
		track       request.Track
		mp3FilePath string
		imagePath   string
	}{
		{
			track: request.Track{
				Title:    "A lot",
				Artist:   "21 savage",
				Duration: "1:20",
			},
			mp3FilePath: "./data/tracks/a lot.mp3",
			imagePath:   "./data/covers/a lot.jpeg",
		},
		{
			track: request.Track{
				Title:    "Goosebumps",
				Artist:   "Travis Scott",
				Duration: "1:20",
			},
			mp3FilePath: "./data/tracks/goose.mp3",
			imagePath:   "./data/covers/goosebumps.jpg",
		},
		{
			track: request.Track{
				Title:    "Lucid Dreams",
				Artist:   "Juice WRLD",
				Duration: "1:20",
			},
			mp3FilePath: "./data/tracks/lucid.mp3",
			imagePath:   "./data/covers/lucid dreams.jpg",
		},
		{
			track: request.Track{
				Title:    "Runaway",
				Artist:   "Kanye West",
				Duration: "1:20",
			},
			mp3FilePath: "./data/tracks/runaway.mp3",
			imagePath:   "./data/covers/runaway.jfif",
		},
	}

	for _, req := range requests {
		body := &bytes.Buffer{}
		writer := multipart.NewWriter(body)

		mp3File, err := os.Open(req.mp3FilePath)
		if err != nil {
			fmt.Println(err)
			continue
		}
		defer mp3File.Close()

		imageFile, err := os.Open(req.imagePath)
		if err != nil {
			fmt.Println(err)
			continue
		}
		defer imageFile.Close()

		mp3Part, err := writer.CreateFormFile("track", req.mp3FilePath)
		if err != nil {
			fmt.Println(err)
			continue
		}

		_, err = io.Copy(mp3Part, mp3File)
		if err != nil {
			fmt.Println(err)
			continue
		}

		imagePart, err := writer.CreateFormFile("image", req.imagePath)
		if err != nil {
			fmt.Println(err)
			continue
		}

		_, err = io.Copy(imagePart, imageFile)
		if err != nil {
			fmt.Println(err)
			continue
		}

		writer.WriteField("title", req.track.Title)
		writer.WriteField("artist", req.track.Artist)
		writer.WriteField("duration", req.track.Duration)

		err = writer.Close()
		if err != nil {
			fmt.Println(err)
			continue
		}

		r, err := http.NewRequest("POST", fmt.Sprintf("http://%s:%s/tracks", p.cfg.HTTP.Host, p.cfg.HTTP.Port), body)
		if err != nil {
			fmt.Println(err)
			continue
		}

		r.Header.Set("Content-Type", writer.FormDataContentType())

		client := &http.Client{}
		response, err := client.Do(r)
		if err != nil {
			fmt.Println(err)
			continue
		}
		defer response.Body.Close()

		responseBody, err := io.ReadAll(response.Body)
		if err != nil {
			fmt.Println(err)
			continue
		}

		fmt.Println(string(responseBody))
	}

	return nil
}

func (p *proc) Cleanup() error {
	if err := p.storage.Tracks().Cleanup(context.Background()); err != nil {
		p.log.Error("failed to cleanup database", zap.Error(err))
		return err
	}

	p.s3.Cleanup(context.Background(), minio.CoversBucket)
	p.s3.Cleanup(context.Background(), minio.TracksBucket)

	return nil
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

	tracksGroup := p.router.Group("/tracks")
	tracksGroup.POST("", p.createTrack)
	tracksGroup.GET("", p.getTracks)
	tracksGroup.GET("/:trackID", p.getTrack)
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
			c.JSON(http.StatusBadRequest, gin.H{
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
			c.JSON(http.StatusBadRequest, gin.H{
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
		p.log.Error("failed to send email", zap.String("email", user.Email), zap.Error(err))
		c.JSON(http.StatusInternalServerError, gin.H{
			"err": err.Error(),
		})

		return
	}


	p.log.Info("sent email", zap.String("email", user.Email))
	p.cache.Set(c, strconv.Itoa(userID), code, 60*time.Second)

	c.JSON(http.StatusNoContent, nil)
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

	value, err := p.cache.Get(c, fmt.Sprintf("%d", req.UserID))
	if err == redis.Nil {
		p.log.Error("key does not exist", zap.Int("key", req.UserID))
		c.JSON(http.StatusBadRequest, gin.H{
			"err": "key does not exist",
		})

		return
	} else if err != nil {
		p.log.Error("failed to get value", zap.Int("key", req.UserID))
		c.JSON(http.StatusInternalServerError, gin.H{
			"err": err.Error(),
		})

		return
	}

	if value != req.Code {
		c.JSON(http.StatusBadRequest, gin.H{
			"err": "wrong code",
		})

		return
	}

	if err = p.storage.Users().SetActive(c, req.UserID); err != nil {
		p.log.Error("failed to update user", zap.Int("id", req.UserID), zap.Error(err))
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
		if errors.Is(err, pgx.ErrNoRows) {
			c.JSON(http.StatusInternalServerError, gin.H{
				"err": "user does not exist",
			})

			return
		}

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

func (p *proc) createTrack(c *gin.Context) {
	var req request.Track

	if err := c.ShouldBind(&req); err != nil {
		p.log.Error("failed to bind request", zap.Error(err))
		c.JSON(http.StatusBadRequest, gin.H{
			"err": err.Error(),
		})

		return
	}

	mp3File, err := c.FormFile("track")
	if err != nil {
		p.log.Error("failed to get mp3 file", zap.Error(err))
		c.JSON(http.StatusBadRequest, gin.H{
			"err": err.Error(),
		})

		return
	}
	mp3File.Filename = uuid.New().String() + ".mp3"

	mp3Path := fmt.Sprintf("./uploads/%s", mp3File.Filename)
	if err = c.SaveUploadedFile(mp3File, mp3Path); err != nil {
		p.log.Error("failed to save mp3 file", zap.Error(err))
		c.JSON(http.StatusInternalServerError, gin.H{
			"err": err.Error(),
		})

		return
	}

	err = p.s3.SaveMp3(c, mp3File.Filename, mp3Path)
	if err != nil {
		p.log.Error("failed to save mp3 file into s3", zap.Error(err))
		c.JSON(http.StatusInternalServerError, gin.H{
			"err": err.Error(),
		})

		return
	}

	imageFile, err := c.FormFile("image")
	if err != nil {
		p.log.Error("failed to get image file", zap.Error(err))
		c.JSON(http.StatusBadRequest, gin.H{
			"err": err.Error(),
		})

		return
	}
	imageFile.Filename = uuid.New().String() + ".png"

	imagePath := fmt.Sprintf("./uploads/%s", imageFile.Filename)
	if err = c.SaveUploadedFile(imageFile, imagePath); err != nil {
		p.log.Error("failed to save image file", zap.Error(err))
		c.JSON(http.StatusInternalServerError, gin.H{
			"err": err.Error(),
		})

		return
	}

	err = p.s3.SaveImage(c, imageFile.Filename, imagePath)
	if err != nil {
		p.log.Error("failed to save image into s3", zap.Error(err))
		c.JSON(http.StatusInternalServerError, gin.H{
			"err": err.Error(),
		})

		return
	}

	defer func() {
		if err = os.RemoveAll("./uploads"); err != nil {
			p.log.Error("failed to remove files", zap.Error(err))
			return
		}
	}()

	trackID, err := p.storage.Tracks().Create(c, &dbo.Track{
		Title:    req.Title,
		Artist:   req.Artist,
		Cover:    imageFile.Filename,
		Mp3:      mp3File.Filename,
		Duration: 0,
	})
	if err != nil {
		p.log.Error("failed to save track into database", zap.Error(err))
		c.JSON(http.StatusInternalServerError, gin.H{
			"err": err.Error(),
		})

		return
	}

	c.JSON(http.StatusCreated, gin.H{
		"track_id": trackID,
	})
}

func (p *proc) getTracks(c *gin.Context) {
	tracks, err := p.storage.Tracks().GetAll(c)
	if err != nil {
		p.log.Error("failed to get all tracks", zap.Error(err))
		c.JSON(http.StatusInternalServerError, gin.H{
			"err": err.Error(),
		})

		return
	}

	c.JSON(http.StatusOK, gin.H{
		"tracks": tracks,
	})
}

func (p *proc) getTrack(c *gin.Context) {
	id := c.Param("trackID")

	trackID, err := strconv.Atoi(id)
	if err != nil {
		p.log.Error("failed to convert string to int", zap.Error(err))
		c.JSON(http.StatusBadRequest, gin.H{
			"err": err.Error(),
		})

		return
	}

	track, err := p.storage.Tracks().ByID(c, trackID)
	if err != nil {
		if errors.Is(err, pgx.ErrNoRows) {
			c.JSON(http.StatusBadRequest, gin.H{
				"err": "track does not exist",
			})

			return
		}

		p.log.Error("failed to get tracks", zap.Int("track id", trackID), zap.Error(err))
		c.JSON(http.StatusInternalServerError, gin.H{
			"err": err.Error(),
		})

		return
	}

	c.JSON(http.StatusOK, track)
}

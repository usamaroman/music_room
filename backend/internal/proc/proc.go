package proc

import (
	"fmt"
	"net/http"

	"github.com/usamaroman/music_room/backend/internal/config"
	"github.com/usamaroman/music_room/backend/internal/storage"
	"github.com/usamaroman/music_room/backend/internal/storage/repo"
	"github.com/usamaroman/music_room/backend/pkg/redis"

	"github.com/gin-gonic/gin"
	"go.uber.org/zap"
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
	p.router.POST("/registration", p.registration)
	p.router.POST("/login", p.login)
}

func (p *proc) registration(c *gin.Context) {
	c.String(http.StatusOK, "registration\n")
}

func (p *proc) login(c *gin.Context) {
	c.String(http.StatusOK, "login\n")
}

package proc

import (
	"fmt"
	"net/http"

	"github.com/usamaroman/music_room/backend/internal/config"
	"github.com/usamaroman/music_room/backend/internal/storage"
	"github.com/usamaroman/music_room/backend/internal/storage/repo"

	"github.com/gin-gonic/gin"
	"go.uber.org/zap"
)

type Collections interface {
	Users() *repo.Users
}


type proc struct {
	log *zap.Logger

	router  *gin.Engine
	httpsrv *http.Server
	storage Collections
}

func NewProc(logger *zap.Logger, cfg *config.Config, storage *storage.Collection) *proc {
	router := gin.Default()

	return &proc{
		log:    logger,
		router: router,
		httpsrv: &http.Server{
			Addr:    fmt.Sprintf("%s:%s", cfg.HTTP.Host, cfg.HTTP.Port),
			Handler: router,
		},
	}
}

func (p *proc) RegisterRoutes() {
	p.log.Info("routes registration")

	p.router.GET("/status", func(c *gin.Context) {
		c.String(http.StatusOK, "health\n")
	})
	p.router.POST("/registration", func(c *gin.Context) {
		c.String(http.StatusOK, "registration\n")
	})
	p.router.POST("/login", func(c *gin.Context) {
		c.String(http.StatusOK, "registration\n")
	})
}

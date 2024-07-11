package proc

import (
	"fmt"
	"net/http"

	"github.com/usamaroman/music_room/backend/internal/config"

	"github.com/gin-gonic/gin"
	"go.uber.org/zap"
)

type proc struct {
	log *zap.Logger

	router  *gin.Engine
	httpsrv *http.Server
}

func NewProc(logger *zap.Logger, cfg *config.Config) *proc {
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
}

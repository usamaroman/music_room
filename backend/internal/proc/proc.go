package proc

import (
	"fmt"
	"net/http"

	"github.com/usamaroman/music_room/backend/internal/config"

	"go.uber.org/zap"
)

type proc struct {
	log *zap.Logger

	httpsrv *http.Server
}

func NewProc(logger *zap.Logger, cfg *config.Config) *proc {
	return &proc{
		log: logger,
		httpsrv: &http.Server{
			Addr: fmt.Sprintf("%s:%s", cfg.HTTP.Host, cfg.HTTP.Port),
		},
	}
}

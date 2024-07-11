package app

import (
	"github.com/usamaroman/music_room/backend/internal/config"
	"github.com/usamaroman/music_room/backend/internal/proc"

	"go.uber.org/fx"
	"go.uber.org/fx/fxevent"
	"go.uber.org/zap"
)

func NewService() *fx.App {
	return fx.New(
		fx.Provide(
			zap.NewProduction,
			config.New,
		),

		fx.Options(
			proc.New(),
		),

		fx.WithLogger(func(log *zap.Logger) fxevent.Logger {
			return &fxevent.ZapLogger{
				Logger: log,
			}
		}),
	)
}

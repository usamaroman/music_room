package proc

import (
	"context"

	"github.com/usamaroman/music_room/backend/internal/storage"
	"github.com/usamaroman/music_room/backend/pkg/minio"
	"github.com/usamaroman/music_room/backend/pkg/redis"

	"go.uber.org/fx"
	"go.uber.org/zap"
)

const moduleName = "proc"

type HTTPServer interface {
	RegisterRoutes()
}

type DataMocker interface {
	FillData() error
	Cleanup() error
}

func New() fx.Option {
	return fx.Module(
		moduleName,

		fx.Provide(
			NewProc,
			storage.NewCollection,
		),

		fx.Options(
			storage.NewModule(),
			redis.NewModule(),
			minio.NewModule(),
		),

		fx.Invoke(
			func(lc fx.Lifecycle, p *proc) {
				lc.Append(procOnStart(p))
				lc.Append(registerRouter(p))
				lc.Append(fillData(p))
			},
		),

		fx.Decorate(func(log *zap.Logger) *zap.Logger {
			return log.Named(moduleName)
		}),
	)
}

func procOnStart(p *proc) fx.Hook {
	return fx.Hook{
		OnStart: func(_ context.Context) error {
			p.log.Info("proc started")

			go func() {
				if err := p.httpsrv.ListenAndServe(); err != nil {
					p.log.Error("failed to listen and serve http server", zap.Error(err))
					return
				}
			}()

			return nil
		},
		OnStop: func(_ context.Context) error {
			p.log.Info("proc stopped")

			return nil
		},
	}
}

func registerRouter(p HTTPServer) fx.Hook {
	return fx.Hook{
		OnStart: func(_ context.Context) error {
			p.RegisterRoutes()
			return nil
		},
	}
}

func fillData(p DataMocker) fx.Hook {
	return fx.Hook{
		OnStart: func(_ context.Context) error {
			return p.FillData()
		},
		OnStop: func(_ context.Context) error {
			return p.Cleanup()
		},
	}
}

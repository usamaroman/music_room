package proc

import (
	"context"

	"go.uber.org/fx"
	"go.uber.org/zap"
)

const moduleName = "proc"

func New() fx.Option {
	return fx.Module(
		moduleName,

		fx.Provide(
			NewProc,
		),

		fx.Invoke(
			func(lc fx.Lifecycle, p *proc) {
				lc.Append(procOnStart(p))
			},
		),

		fx.Decorate(func(log *zap.Logger) *zap.Logger {
			return log.Named(moduleName)
		}),
	)
}

func procOnStart(p *proc) fx.Hook {
	return fx.Hook{
		OnStart: func(ctx context.Context) error {
			p.log.Info("proc started")

			return nil
		},
		OnStop: func(ctx context.Context) error {
			p.log.Info("proc stopped")

			return nil
		},
	}
}

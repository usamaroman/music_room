package redis

import (
	"context"

	"go.uber.org/fx"
	"go.uber.org/zap"
)

const moduleName = "redis"

func NewModule() fx.Option {
	return fx.Module(
		moduleName,

		fx.Provide(NewClient),

		fx.Invoke(func(cl *Client, lc fx.Lifecycle) {
			lc.Append(
				fx.Hook{
					OnStart: func(ctx context.Context) error {
						cl.log.Info("redis started")

						ping := cl.redisClient.Ping(ctx)
						cl.log.Info("redis ping", zap.Any("cmd", ping))

						return nil
					},
					OnStop: func(_ context.Context) error {
						cl.log.Info("redis stopped")
						return cl.redisClient.Close()
					},
				},
			)
		}),

		fx.Decorate(func(log *zap.Logger) *zap.Logger {
			return log.Named(moduleName)
		}),
	)
}

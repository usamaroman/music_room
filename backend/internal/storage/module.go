package storage

import (
	"context"
	"fmt"

	"github.com/usamaroman/music_room/backend/internal/config"
	"github.com/usamaroman/music_room/backend/internal/storage/repo"
	"github.com/usamaroman/music_room/backend/pkg/postgresql"
	"github.com/usamaroman/music_room/backend/schema"

	"go.uber.org/fx"
	"go.uber.org/zap"
)

const moduleName = "storage"

func NewModule() fx.Option {
	return fx.Module(
		moduleName,

		fx.Provide(
			repo.NewUsers,
			repo.NewTracks,
		),

		fx.Options(postgresql.NewModule()),

		fx.Invoke(func(
			lc fx.Lifecycle,
			log *zap.Logger,
			cfg *config.Config,
		) {
			lc.Append(fx.Hook{
				OnStart: func(ctx context.Context) error {
					url := fmt.Sprintf("postgres://%s:%s@%s:%s/%s?sslmode=disable",
						cfg.Database.User,
						cfg.Database.Password,
						cfg.Database.Host,
						cfg.Database.Port,
						cfg.Database.Database,
					)

					postgresql.Migrate(log, &schema.DB, url)

					return nil
				},
			})
		}),

		fx.Decorate(func(log *zap.Logger) *zap.Logger {
			return log.Named(moduleName)
		}),
	)
}

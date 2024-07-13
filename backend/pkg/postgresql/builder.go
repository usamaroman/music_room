package postgresql

import (
	"context"
	"fmt"
	"time"

	"github.com/usamaroman/music_room/backend/internal/config"

	"github.com/jackc/pgx/v5/pgxpool"
	"go.uber.org/zap"
)

type QBuilder struct {
	pool *pgxpool.Pool
}

func NewQBuilder(log *zap.Logger, cfg *config.Config) (*QBuilder, error) {
	ctx, cancel := context.WithTimeout(context.Background(), time.Second*30)
	defer cancel()

	url := fmt.Sprintf("postgres://%s:%s@%s:%s/%s?sslmode=disable",
		cfg.Database.User,
		cfg.Database.Password,
		cfg.Database.Host,
		cfg.Database.Port,
		cfg.Database.Database,
	)

	log.Info("connection url", zap.String("url", url))

	if cfg.Database.AutoCreate {
		exists, err := CreateDatabase(ctx, url)
		if err != nil {
			log.Error("failed to create database", zap.Error(err))

			return nil, err
		}
		if exists {
			log.Info("the database already exists")
		} else {
			log.Info("the database was created successfully")
		}
	}

	// psqlCfg, err := pgx.ParseConfigWithOptions(url, pgx.ParseConfigOptions{})
	// if err != nil {
	// 	log.Error("failed to parse postgres config", zap.Error(err))

	// 	return nil, err
	// }

	pgCfg, err := pgxpool.ParseConfig(url)
	if err != nil {
		log.Error("failed to parse postgres config", zap.Error(err))

		return nil, err
	}

	pgCfg.MaxConns = 20                      // Maximum number of connections in the pool
	pgCfg.MinConns = 5                       // Minimum number of connections in the pool
	pgCfg.MaxConnLifetime = time.Hour        // Maximum connection lifetime
	pgCfg.MaxConnIdleTime = 30 * time.Minute // Maximum idle time before connection is closed
	pgCfg.HealthCheckPeriod = time.Minute    // Health check period
	// pgCfg.ConnConfig.Tracer = otelpgx.NewTracer()

	conn, err := pgxpool.NewWithConfig(ctx, pgCfg)
	if err != nil {
		log.Error("pool constructor error", zap.Error(err))

		return nil, err
	}

	return &QBuilder{conn}, nil
}

func (qb QBuilder) Pool() *pgxpool.Pool {
	return qb.pool
}

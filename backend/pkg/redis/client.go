package redis

import (
	"fmt"

	"github.com/usamaroman/music_room/backend/internal/config"

	"github.com/redis/go-redis/v9"
	"go.uber.org/zap"
)

type Client struct {
	log *zap.Logger

	redisClient *redis.Client
}

func NewClient(log *zap.Logger, cfg *config.Config) *Client {
	redisClient := redis.NewClient(&redis.Options{
		Addr:     fmt.Sprintf("%s:%s", cfg.Redis.Host, cfg.Redis.Port),
		Password: cfg.Redis.Password,
		DB:       cfg.Redis.Database,
	})

	return &Client{
		log:         log,
		redisClient: redisClient,
	}
}

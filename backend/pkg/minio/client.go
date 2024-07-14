package minio

import (
	"context"
	"fmt"

	"github.com/usamaroman/music_room/backend/internal/config"

	"github.com/minio/minio-go/v7"
	"github.com/minio/minio-go/v7/pkg/credentials"
	"go.uber.org/zap"
)

const TracksBucket = "tracks"
const CoversBucket = "covers"

type Client struct {
	log         *zap.Logger
	minioClient *minio.Client
}

func NewClient(cfg *config.Config, log *zap.Logger) *Client {
	endpoint := fmt.Sprintf("%s:%s", cfg.Minio.Host, cfg.Minio.Port)
	accessKeyID := cfg.Minio.User
	secretAccessKey := cfg.Minio.Password

	minioClient, err := minio.New(endpoint, &minio.Options{
		Creds: credentials.NewStaticV4(accessKeyID, secretAccessKey, ""),
	})
	if err != nil {
		log.Error("failed to init minio", zap.Error(err))
		return nil
	}

	return &Client{
		log:         log,
		minioClient: minioClient,
	}
}

func (c *Client) Client() *minio.Client {
	return c.minioClient
}

func (c *Client) SaveMp3(ctx context.Context, filename, filepath string) error {
	object, err := c.minioClient.FPutObject(ctx, TracksBucket, filename, filepath, minio.PutObjectOptions{
		ContentType: "audio/mpeg",
	})
	if err != nil {
		return err
	}

	c.log.Info("saved mp3 file", zap.Int64("size", object.Size))

	return nil
}

func (c *Client) SaveImage(ctx context.Context, filename, filepath string) error {
	object, err := c.minioClient.FPutObject(ctx, CoversBucket, filename, filepath, minio.PutObjectOptions{
		ContentType: "image/png",
	})
	if err != nil {
		return err
	}

	c.log.Info("saved image file", zap.Int64("size", object.Size))

	return nil
}

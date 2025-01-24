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

	if err = createBuckets(context.Background(), minioClient, log); err != nil {
		log.Error("failed to create buckets in minio", zap.Error(err))
		return nil
	}

	return &Client{
		log:         log,
		minioClient: minioClient,
	}
}

func createBuckets(ctx context.Context, client *minio.Client, log *zap.Logger) error {
	buckets := []string{CoversBucket, TracksBucket}

	for _, bucket := range buckets {
		if err := client.MakeBucket(ctx, bucket, minio.MakeBucketOptions{
			Region: "by",
		}); err != nil {
			exists, errBucketExists := client.BucketExists(ctx, bucket)
			if errBucketExists == nil && exists {
				log.Debug("bucket already exists", zap.String("bucket", bucket))
			} else {
				log.Error("failed to create bucket", zap.String("bucket", bucket))
				return err
			}
		}
	}

	return nil
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

func (c *Client) Cleanup(ctx context.Context, bucketName string) {
	objectsChan := make(chan minio.ObjectInfo)

	go func() {
		defer close(objectsChan)

		for obj := range c.minioClient.ListObjects(ctx, bucketName, minio.ListObjectsOptions{}) {
			if obj.Err != nil {
				return
			}
			objectsChan <- obj
		}
	}()

	errChan := c.minioClient.RemoveObjects(ctx, bucketName, objectsChan, minio.RemoveObjectsOptions{})

	for err := range errChan {
		c.log.Error("failed to remove object from minio", zap.String("object name", err.ObjectName), zap.Error(err.Err))

		return
	}

	c.log.Info("bucket is cleared", zap.String("bucket name", bucketName))
}

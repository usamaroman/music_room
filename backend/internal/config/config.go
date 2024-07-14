package config

import (
	"github.com/ilyakaznacheev/cleanenv"
	"go.uber.org/zap"
)

type Config struct {
	HTTP struct {
		Port string `yaml:"port" env:"PORT" env-default:"8080"`
		Host string `yaml:"host" env:"HOST" env-default:"localhost"`
	} `yaml:"http"`
	Database struct {
		User       string `yaml:"user" env:"PG_USER" env-default:"postgres"`
		Password   string `yaml:"password" env:"PG_PASSWORD" env-default:"5432"`
		Host       string `yaml:"host" env:"PG_HOST" env-default:"127.0.0.1"`
		Port       string `yaml:"port" env:"PG_PORT" env-default:"5432"`
		Database   string `yaml:"database" env:"PG_DATABASE" env-default:"music_room"`
		AutoCreate bool   `yaml:"auto_create" env:"PG_AUTO_CREATE" env-default:"true"`
	} `yaml:"postgresql"`
	Redis struct {
		Host     string `yaml:"host" env:"REDIS_HOST" env-default:"127.0.0.1"`
		Port     string `yaml:"port" env:"REDIS_PORT" env-default:"6379"`
		Password string `yaml:"password" env:"REDIS_PASSWORD" env-default:"6379"`
		Database int    `yaml:"database" env:"REDIS_DATABASE" env-default:"0"`
	} `yaml:"redis"`
	SMTP struct {
		Email    string `yaml:"email" env:"SMTP_EMAIL" env-default:"testcarbookingservice@gmail.com"`
		Password string `yaml:"password" env:"SMTP_PASSWORD" env-default:"dykp brpi kneo lrsh"`
	} `yaml:"smtp"`
	Minio struct {
		Host     string `yaml:"host" env:"MINIO_HOST" env-default:"localhost"`
		Port     string `yaml:"port" env:"MINIO_PORT" env-default:"9000"`
		User     string `yaml:"user" env:"MINIO_USER" env-default:"minio"`
		Password string `yaml:"password" env:"MINIO_PASSWORD" env-default:"minio123"`
	} `yaml:"minio"`
}

func New(log *zap.Logger) (*Config, error) {
	var cfg Config

	err := cleanenv.ReadConfig("config.yaml", &cfg)
	if err != nil {
		return nil, err
	}

	log.Info("configuration", zap.Any("cfg", cfg))

	return &cfg, nil
}

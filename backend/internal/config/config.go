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

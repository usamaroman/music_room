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

package repo

import (
	"github.com/usamaroman/music_room/backend/pkg/postgresql"

	"go.uber.org/zap"
)

type Users struct {
	log *zap.Logger
	qb  QueryBuilder
}

func NewUsers(qb *postgresql.QBuilder, log *zap.Logger) *Users {
	return &Users{qb: qb, log: log}
}

package repo

import (
	"github.com/usamaroman/music_room/backend/pkg/db"	
	
	"go.uber.org/zap"
)

type Users struct {
	log *zap.Logger
	qb  QueryBuilder
}

func NewUsers(qb *db.QBuilder, log *zap.Logger) *Users {
	return &Users{qb: qb, log: log}
}

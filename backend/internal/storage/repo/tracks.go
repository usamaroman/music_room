package repo

import (
	"github.com/usamaroman/music_room/backend/pkg/postgresql"

	"go.uber.org/zap"
)

type Tracks struct {
	log *zap.Logger
	qb  QueryBuilder
}

func NewTracks(qb *postgresql.QBuilder, log *zap.Logger) *Tracks {
	return &Tracks{qb: qb, log: log}
}

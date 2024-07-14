package repo

import (
	"context"

	"github.com/usamaroman/music_room/backend/internal/storage/dbo"
	"github.com/usamaroman/music_room/backend/pkg/postgresql"

	"github.com/georgysavva/scany/v2/pgxscan"
	"github.com/jackc/pgx/v5"
	"go.uber.org/zap"
)

type Tracks struct {
	log *zap.Logger
	qb  QueryBuilder
}

func NewTracks(qb *postgresql.QBuilder, log *zap.Logger) *Tracks {
	return &Tracks{qb: qb, log: log}
}

func (repo *Tracks) Create(ctx context.Context, track *dbo.Track) (int, error) {
	q := `insert into tracks (title, artist, cover, mp3, duration) values ($1, $2, $3, $4, $5) returning id`

	var id int
	if err := repo.qb.Pool().QueryRow(ctx, q, track.Title, track.Artist, track.Cover, track.Mp3, track.Duration).Scan(&id); err != nil {
		repo.log.Error("failed to insert track into database", zap.Error(err))

		return 0, err
	}

	return id, nil
}

func (repo *Tracks) GetAll(ctx context.Context) ([]dbo.Track, error) {
	q := `select * from tracks`

	var tracks []dbo.Track
	if err := pgxscan.Select(ctx, repo.qb.Pool(), &tracks, q); err != nil {
		repo.log.Error("failed to get tracks from database", zap.Error(err))
		return nil, err
	}

	return tracks, nil
}

func (repo *Tracks) ByID(ctx context.Context, id int) (*dbo.Track, error) {
	q := `select * from tracks where id = $1`

	var tracks []dbo.Track
	if err := pgxscan.Select(ctx, repo.qb.Pool(), &tracks, q, id); err != nil {
		repo.log.Error("failed to get track from database", zap.Int("id", id), zap.Error(err))
		return nil, err
	}

	if len(tracks) == 0 {
		return nil, pgx.ErrNoRows
	}

	return &tracks[0], nil
}

func (repo *Tracks) ByTitle(ctx context.Context, title string) ([]dbo.Track, error) {
	q := `select * from tracks where title = $1`

	var tracks []dbo.Track
	if err := pgxscan.Select(ctx, repo.qb.Pool(), &tracks, q, title); err != nil {
		repo.log.Error("failed to get track from database", zap.String("title", title), zap.Error(err))
		return nil, err
	}

	return tracks, nil
}

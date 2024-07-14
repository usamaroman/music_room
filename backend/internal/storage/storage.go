package storage

import "github.com/usamaroman/music_room/backend/internal/storage/repo"

type Collection struct {
	usersRepo  *repo.Users
	tracksRepo *repo.Tracks
}

func NewCollection(u *repo.Users, tr *repo.Tracks) *Collection {
	return &Collection{
		usersRepo:  u,
		tracksRepo: tr,
	}
}

func (c Collection) Users() *repo.Users {
	return c.usersRepo
}

func (c Collection) Tracks() *repo.Tracks {
	return c.tracksRepo
}

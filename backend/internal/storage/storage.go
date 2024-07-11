package storage

import "github.com/usamaroman/music_room/backend/internal/storage/repo"

type Collection struct {
	usersRepo *repo.Users
}

func NewCollection(u *repo.Users) *Collection {
	return &Collection{
		usersRepo: u,
	}
}

func (c Collection) Users() *repo.Users {
	return c.usersRepo
}
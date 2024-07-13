package dbo

import "time"

type User struct {
	ID        int
	Email     string
	Nickname  string
	Password  string
	Avatar    string
	CreatedAt time.Time
}

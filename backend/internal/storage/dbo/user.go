package dbo

import (
	"database/sql"
	"time"
)

type User struct {
	ID        int            `json:"id"`
	Email     string         `json:"email"`
	Nickname  string         `json:"nickname"`
	Password  string         `json:"-"`
	Avatar    sql.NullString `json:"avatar"`
	IsActive  bool           `json:"is_active"`
	CreatedAt time.Time      `json:"created_at"`
}

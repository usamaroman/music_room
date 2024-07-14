package dbo

import (
	"database/sql"
	"time"
)

type User struct {
	ID        int            `json:"id" db:"id"`
	Email     string         `json:"email" db:"email"`
	Nickname  string         `json:"nickname" db:"nickname"`
	Password  string         `json:"-" db:"password"`
	Avatar    sql.NullString `json:"avatar" db:"avatar"`
	IsActive  bool           `json:"is_active" db:"is_active"`
	CreatedAt time.Time      `json:"created_at" db:"created_at"`
}

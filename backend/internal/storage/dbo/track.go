package dbo

import (
	"time"
)

type Track struct {
	ID        int       `json:"id" db:"id"`
	Title     string    `json:"title" db:"title"`
	Artist    string    `json:"artist" db:"artist"`
	Cover     string    `json:"cover" db:"cover"`
	Duration  int       `json:"duration" db:"duration"`
	CreatedAt time.Time `json:"created_at" db:"created_at"`
}

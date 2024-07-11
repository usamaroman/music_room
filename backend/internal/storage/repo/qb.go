package repo

import "github.com/jackc/pgx/v5/pgxpool"

type QueryBuilder interface {
	Pool() *pgxpool.Pool
}
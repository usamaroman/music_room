package postgresql

import (
	"context"
	"fmt"

	"github.com/jackc/pgx/v5/pgxpool"
)

func CreateDatabase(ctx context.Context, url string) (exists bool, err error) {
	conn, err := pgxpool.New(ctx, replaceDbName(url, "postgres"))
	if err != nil {
		return false, err
	}
	defer conn.Close()

	checkingSql := `select exists(select datname from pg_catalog."pg_database" where datname = $1) as exist`
	dbName, err := parseDatabaseName(url)
	if err != nil {
		return false, err
	}

	row := conn.QueryRow(ctx, checkingSql, dbName)
	if err := row.Scan(&exists); err != nil {
		return false, err
	}

	if exists {
		return true, nil
	}

	if _, err := conn.Exec(ctx, fmt.Sprintf(`CREATE DATABASE %s`, dbName)); err != nil {
		return false, err
	}

	return false, nil
}

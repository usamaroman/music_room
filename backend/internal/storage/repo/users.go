package repo

import (
	"context"

	"github.com/usamaroman/music_room/backend/internal/storage/dbo"
	"github.com/usamaroman/music_room/backend/pkg/postgresql"

	"github.com/georgysavva/scany/v2/pgxscan"
	"github.com/jackc/pgx/v5"
	_ "github.com/jackc/pgx/v5/pgxpool"
	"go.uber.org/zap"
)

type Users struct {
	log *zap.Logger
	qb  QueryBuilder
}

func NewUsers(qb *postgresql.QBuilder, log *zap.Logger) *Users {
	return &Users{qb: qb, log: log}
}

func (repo *Users) Create(ctx context.Context, user *dbo.User) (int, error) {
	q := `insert into users (email, nickname, password) values ($1, $2, $3) returning id`

	var id int
	err := repo.qb.Pool().QueryRow(ctx, q, user.Email, user.Nickname, user.Password).Scan(&id)
	if err != nil {
		repo.log.Error("failed to insert user into database", zap.Error(err))

		return 0, err
	}

	return id, nil
}

func (repo *Users) ExistsByID(ctx context.Context, id int) (bool, error) {
	q := `SELECT COUNT(*) FROM users WHERE id = $1`

	var count int
	err := repo.qb.Pool().QueryRow(ctx, q, id).Scan(&count)
	if err != nil {
		repo.log.Error("failed to get users count", zap.Error(err))
		return false, err
	}

	if count == 0 {
		return false, nil
	}

	return true, nil
}

func (repo *Users) ExistsBYEmail(ctx context.Context, email string) (bool, error) {
	q := `SELECT COUNT(*) FROM users WHERE email = $1`

	var count int
	err := repo.qb.Pool().QueryRow(ctx, q, email).Scan(&count)
	if err != nil {
		repo.log.Error("failed to get users count", zap.Error(err))
		return false, err
	}

	if count == 0 {
		return false, nil
	}

	return true, nil
}

func (repo *Users) ExistsBYNickname(ctx context.Context, nickname string) (bool, error) {
	q := `SELECT COUNT(*) FROM users WHERE nickname = $1`

	var count int
	err := repo.qb.Pool().QueryRow(ctx, q, nickname).Scan(&count)
	if err != nil {
		repo.log.Error("failed to get users count", zap.Error(err))
		return false, err
	}

	if count == 0 {
		return false, nil
	}

	return true, nil
}

func (repo *Users) ByID(ctx context.Context, id int) (*dbo.User, error) {
	q := `select * from users where id = $1`

	var users []dbo.User
	if err := pgxscan.Select(ctx, repo.qb.Pool(), &users, q, id); err != nil {
		repo.log.Error("failed to get user from database", zap.Int("id", id), zap.Error(err))

		return nil, err
	}

	if len(users) == 0 {
		return nil, pgx.ErrNoRows
	}

	return &users[0], nil
}

func (repo *Users) ByEmail(ctx context.Context, email string) (*dbo.User, error) {
	q := `select * from users where email = $1 and is_active = true`

	var users []dbo.User
	if err := pgxscan.Select(ctx, repo.qb.Pool(), &users, q, email); err != nil {
		repo.log.Error("failed to get user from database", zap.String("email", email), zap.Error(err))

		return nil, err
	}

	if len(users) == 0 {
		return nil, pgx.ErrNoRows
	}

	return &users[0], nil
}

func (repo *Users) SetActive(ctx context.Context, id int) error {
	q := `update users set is_active = true where id = $1`

	exec, err := repo.qb.Pool().Exec(ctx, q, id)
	if err != nil {
		repo.log.Error("failed to update user", zap.Int("id", id), zap.Error(err))
		return err
	}

	repo.log.Info("executed", zap.Int64("rows affected", exec.RowsAffected()))

	return nil
}

package jwt

import (
	"fmt"
	"time"

	"github.com/usamaroman/music_room/backend/internal/storage/dbo"

	"github.com/golang-jwt/jwt/v5"
)

func GenerateAccessToken(u *dbo.User) (token string, err error) {
	t := jwt.New(jwt.SigningMethodHS256)
	mapClaims := t.Claims.(jwt.MapClaims)
	mapClaims["id"] = u.ID
	mapClaims["email"] = u.Email
	mapClaims["is_active"] = u.IsActive
	mapClaims["exp"] = time.Now().Add(30 * time.Minute).Unix()

	token, err = t.SignedString([]byte("secret"))
	if err != nil {
		return "", err
	}

	return token, err
}

func GenerateRefreshToken(id int) (token string, err error) {
	t := jwt.New(jwt.SigningMethodHS256)
	mapClaims := t.Claims.(jwt.MapClaims)
	mapClaims["id"] = id
	mapClaims["exp"] = time.Now().Add(time.Hour * 24).Unix()

	token, err = t.SignedString([]byte("refresh_token"))
	if err != nil {
		return "", err
	}

	return token, err
}

func ParseAccessToken(token string) (jwt.MapClaims, error) {
	claims, err := jwt.Parse(token, func(token *jwt.Token) (interface{}, error) {
		if _, ok := token.Method.(*jwt.SigningMethodHMAC); !ok {
			return nil, fmt.Errorf("There was an error in parsing")
		}
		return []byte("secret"), nil
	})

	if err != nil {
		return nil, err
	}

	return claims.Claims.(jwt.MapClaims), err
}

func ParseRefreshToken(token string) (jwt.MapClaims, error) {
	claims, err := jwt.Parse(token, func(token *jwt.Token) (interface{}, error) {
		if _, ok := token.Method.(*jwt.SigningMethodHMAC); !ok {
			return nil, fmt.Errorf("There was an error in parsing")
		}
		return []byte("refresh_token"), nil
	})

	if err != nil {
		return nil, err
	}

	return claims.Claims.(jwt.MapClaims), err
}

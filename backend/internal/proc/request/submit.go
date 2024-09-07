package request

type Submit struct {
	UserID int    `json:"user_id"`
	Code   string `json:"code"`
}

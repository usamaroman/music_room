package request

type Submit struct {
	UserID string `json:"user_id"`
	Code   string `json:"code"`
}

package request

type Refresh struct {
	Token string `json:"token" validate:"required"`
}

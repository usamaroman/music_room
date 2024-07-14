package request

type Registration struct {
	Email    string `json:"email" validate:"required,email"`
	Password string `json:"password" validate:"required,min=6,max=30"`
	Nickname string `json:"nickname" validate:"required,max=30"`
}

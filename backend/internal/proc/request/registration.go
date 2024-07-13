package request

type Registration struct {
	Email    string `json:"email" validate:"required,email"`
	Password string `json:"password" validate:"required,min=6"`
	Phone    string `json:"phone" validate:"required"`
	Nickname string `json:"nickname" validate:"required"`
}

package response

type Registration struct {
	ID int `json:"id"`
}

type Login struct {
	AccessToken string `json:"access_token"`
	RefreshToken string `json:"refresh_token"`
}

type Submit struct {
	Msg string `json:"msg"`
}
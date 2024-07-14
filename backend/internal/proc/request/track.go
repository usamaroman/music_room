package request

type Track struct {
	Title    string `form:"title"`
	Artist   string `form:"artist"`
	Duration string `form:"duration"`
}

package utils

import (
	"testing"
)

func TestGenerateNumber(t *testing.T) {
	for i := 0; i < 1000; i++ {
		num := GenerateNumber()

		if num < 1000 || num > 9999 {
			t.Errorf("Generated number is not a four-digit number: %d", num)
		}

		for num > 0 {
			digit := num % 10
			if digit == 0 {
				t.Errorf("Generated number contains a zero digit: %d", num)
			}
			num /= 10
		}
	}
}

func TestPow(t *testing.T) {
	tests := []struct {
		title string
		x     int
		y     int
		res   int
		err   bool
	}{
		{
			title: "ok",
			x:     2,
			y:     2,
			res:   4,
			err:   false,
		},
		{
			title: "ok",
			x:     1,
			y:     1,
			res:   1,
			err:   false,
		},
		{
			title: "ok",
			x:     2,
			y:     0,
			res:   1,
			err:   false,
		},
		{
			title: "wrong",
			x:     2,
			y:     2,
			res:   5,
			err:   true,
		},
		{
			title: "wrong",
			x:     2,
			y:     2,
			res:   5,
			err:   true,
		},
		{
			title: "ok",
			x:     7,
			y:     3,
			res:   343,
			err:   false,
		},
		{
			title: "wrong",
			x:     7,
			y:     3,
			res:   33,
			err:   true,
		},
		{
			title: "wrong",
			x:     7,
			y:     3,
			res:   33,
			err:   true,
		},
	}

	for _, tt := range tests {
		t.Run(tt.title, func(t *testing.T) {
			r := pow(tt.x, tt.y)

			if r == tt.res && tt.err {
				t.Fatal("wrong result", "wanted", tt.res, "got", r)
			}

			if r != tt.res && !tt.err {
				t.Fatal("wrong result", "wanted", tt.res, "got", r)
			}

		})
	}
}

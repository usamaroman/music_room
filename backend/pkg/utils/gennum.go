package utils

import (
	"math/rand"
	"time"
)

func GenerateNumber() int {
	rand.NewSource(time.Now().Unix())

	var n int
	var res int

	for i := range 4 {
		n = rand.Intn(10)
		if n == 0 {
			n = 1
		}

		res += pow(10, i) * n
	}

	return res
}

func pow(x, y int) int {
	if y == 0 {
		return 1
	}

	if y == 1 {
		return x
	}

	res := x
	for i := 2; i <= y; i++ {
		res *= x
	}

	return res
}

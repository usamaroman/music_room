package utils

import (
	"log"
	"testing"
)

func TestGenerateNumber(t *testing.T) {
	for _ = range 4 {
		log.Println(GenerateNumber())
	}
}

func TestPow(t *testing.T) {
	log.Println(pow(2, 2))
	log.Println(pow(7, 3))
}

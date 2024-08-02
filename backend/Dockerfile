FROM golang:1.22-alpine AS builder

WORKDIR /usr/local/src/service

COPY go.mod go.sum ./

RUN go mod download

COPY . .

RUN go build -v -o /usr/local/bin/service cmd/app/main.go

FROM alpine AS runner

COPY --from=builder /usr/local/bin/service /usr/local/bin/service

WORKDIR /usr/local/bin
CMD [ "service" ]
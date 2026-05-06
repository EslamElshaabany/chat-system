FROM golang:1.26-alpine AS build
WORKDIR /app
COPY go.mod ./
RUN go mod download
COPY . .
RUN go build -o ingestion ./cmd/api

FROM alpine:3.19
WORKDIR /app
COPY --from=build /app/ingestion .
ENTRYPOINT ["./ingestion"]
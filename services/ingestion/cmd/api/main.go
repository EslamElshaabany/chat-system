package main

import (
	"context"
	"encoding/json"
	"log"
	"net/http"
	"os"
	"os/signal"
	"syscall"
	"time"

	"go.opentelemetry.io/contrib/instrumentation/net/http/otelhttp"

	"github.com/eslamelshaabany/chat-system/services/ingestion/internal/telemetry"
)

type healthResponse struct {
	Status    string    `json:"status"`
	Timestamp time.Time `json:"timestamp"`
}

func main() {
	ctx, cancelOTel := context.WithCancel(context.Background())
	defer cancelOTel()

	shutdownOTel, err := telemetry.Setup(ctx, "ingestion", "0.0.1")
	if err != nil {
		log.Fatalf("OTel setup failed: %v", err)
	}
	defer func() {
		shutdownCtx, cancel := context.WithTimeout(context.Background(), 5*time.Second)
		defer cancel()
		_ = shutdownOTel(shutdownCtx)
	}()

	mux := http.NewServeMux()
	mux.HandleFunc("GET /healthz", livenessHandler)
	mux.HandleFunc("GET /readyz", readinessHandler)

	port := os.Getenv("PORT")
	if port == "" {
		port = "8090"
	}
	addr := ":" + port

	srv := &http.Server{
		Addr:    addr,
		Handler: otelhttp.NewHandler(mux, "ingestion-server"),
	}

	// start server in background
	go func() {
		log.Printf("starting server on %s", addr)
		if err := srv.ListenAndServe(); err != nil && err != http.ErrServerClosed {
			log.Fatalf("server error: %v", err)
		}
	}()

	// wait for SIGINT or SIGTERM
	quit := make(chan os.Signal, 1)
	signal.Notify(quit, syscall.SIGINT, syscall.SIGTERM)
	<-quit

	log.Println("shutting down...")

	// give in-flight requests 10 seconds to finish
	ctx, cancel := context.WithTimeout(context.Background(), 10*time.Second)
	defer cancel()

	if err := srv.Shutdown(ctx); err != nil {
		log.Fatalf("forced shutdown: %v", err)
	}

	log.Println("done")
}

func livenessHandler(w http.ResponseWriter, r *http.Request) {
	w.Header().Set("Content-Type", "application/json")
	json.NewEncoder(w).Encode(healthResponse{
		Status:    "ok",
		Timestamp: time.Now(),
	})
}

func readinessHandler(w http.ResponseWriter, r *http.Request) {
	w.Header().Set("Content-Type", "application/json")
	json.NewEncoder(w).Encode(healthResponse{
		Status:    "ok",
		Timestamp: time.Now(),
	})
}

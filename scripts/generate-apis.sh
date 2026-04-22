#!/bin/bash

# Configuration
CONTRACT="./api/chat-system-contract.yaml"
GEN_CLI="npx @openapitools/openapi-generator-cli"

# Cleanup previous generations (optional but recommended for clean slate)
# rm -rf services/java-api/generated
# rm -rf services/go-creation/generated

echo "🚀 Generating Java Spring Boot API..."
$GEN_CLI generate \
    -i "$CONTRACT" \
    -g spring \
    -o ./services/java-api \
    --api-package org.openapitools.api \
    --model-package org.openapitools.model \
    --tags Applications,Chats,Messages \
    --additional-properties=interfaceOnly=true,useTags=true,skipDefaultInterface=true,useSpringBoot3=true

echo "🚀 Generating Go Creation Service API..."
$GEN_CLI generate \
    -i "$CONTRACT" \
    -g go-server \
    -o ./services/go-creation \
    --tags ChatsCreation,MessagesCreation \
    --additional-properties=packageName=creationapi,sourceFolder=gen

echo "✅ Generation complete!"
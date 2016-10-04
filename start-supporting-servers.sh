#!/bin/bash
./gradlew clean
./gradlew assemble
./gradlew config-service:image
docker-compose up -d
#!/usr/bin/env bash

# Clean the Gradle project
./gradlew clean

docker-compose build

docker-compose up -d

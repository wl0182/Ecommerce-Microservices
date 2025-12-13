#!/bin/zsh

echo "Stopping all running microservices..."

# Kill only Spring Boot processes started by Maven inside this project
ps -ef | grep "spring-boot:run" | grep -v grep | awk '{print $2}' | xargs -I {} kill {}

echo "Done."



#!/bin/zsh

PID_FILE=.microservices-pids

if [ ! -f "$PID_FILE" ]; then
  echo "No PID file found. Are services running?"
  exit 1
fi

echo "Stopping microservices..."

while read pid; do
  if ps -p $pid > /dev/null; then
    echo "Stopping PID $pid"
    kill -15 $pid
  else
    echo "Process $pid already stopped"
  fi
done < $PID_FILE

rm -f $PID_FILE

echo "Shutdown signals sent."
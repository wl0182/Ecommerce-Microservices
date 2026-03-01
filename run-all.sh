#!/bin/zsh

PID_FILE=.microservices-pids
rm -f $PID_FILE

echo "Starting microservices..."

mvn -pl Eureka-Server spring-boot:run &
echo $! >> $PID_FILE

sleep 5

mvn -pl user-service spring-boot:run &
echo $! >> $PID_FILE

mvn -pl order-service spring-boot:run &
echo $! >> $PID_FILE

mvn -pl product-service spring-boot:run &
echo $! >> $PID_FILE

mvn -pl Cart-Service spring-boot:run &
echo $! >> $PID_FILE

mvn -pl Payment-Service spring-boot:run &
echo $! >> $PID_FILE

mvn -pl Notification-Service spring-boot:run &
echo $! >> $PID_FILE

mvn -pl Shipping-Service spring-boot:run &
echo $! >> $PID_FILE

echo "All services started."
echo "PIDs stored in $PID_FILE"

wait
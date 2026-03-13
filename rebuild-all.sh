#!/bin/zsh

PID_FILE=.microservices-pids
rm -f $PID_FILE

echo "Rebuilding common-events module..."
mvn -pl common-events clean install || { echo "Failed to build common-events"; exit 1; }

echo "Rebuilding all microservices..."
MICROSERVICES=(Eureka-Server user-service order-service product-service Cart-Service Payment-Service Notification-Service Shipping-Service)

for service in $MICROSERVICES; do
    echo "Building $service..."
    mvn -pl $service clean package || { echo "Failed to build $service"; exit 1; }
done

echo "Starting microservices..."
for service in $MICROSERVICES; do
    echo "Starting $service..."
    mvn -pl $service spring-boot:run &
    echo $! >> $PID_FILE
    sleep 5
done

echo "All services started."
echo "PIDs stored in $PID_FILE"

wait
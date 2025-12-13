#!/bin/zsh
mvn -pl Eureka-Server spring-boot:run &
sleep 5
mvn -pl user-service spring-boot:run &
mvn -pl order-service spring-boot:run &
mvn -pl product-service spring-boot:run &
mvn -pl Cart-Service spring-boot:run &
mvn -pl Payment-Service spring-boot:run &
mvn -pl Notification-Service spring-boot:run &
mvn -pl Shipping-Service spring-boot:run &
wait


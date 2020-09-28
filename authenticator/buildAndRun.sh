#!/bin/sh
mvn clean package && docker build -t org.dennysm.microservice/authenticator .
docker rm -f authenticator || true && docker run -d -p 8080:8080 -p 4848:4848 --name authenticator org.dennysm.microservice/authenticator 

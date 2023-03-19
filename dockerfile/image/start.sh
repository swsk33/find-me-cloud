#!/bin/sh
java -jar \
	-Dspring.cloud.consul.host=$CONSUL_HOST \
	-Dspring.cloud.consul.port=$CONSUL_PORT \
	-Dspring.data.mongodb.host=$MONGO_HOST \
	-Dspring.data.mongodb.port=$MONGO_PORT \
	-Dspring.data.mongodb.username=$MONGO_USER \
	-Dsping.data.mongodb.password=$MONGO_PASSWORD \
find-me-image.jar
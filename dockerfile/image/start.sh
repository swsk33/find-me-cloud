#!/bin/sh
COMMAND='java -jar '\
'-Dserver.port=$APP_PORT '\
'-Dspring.cloud.consul.host=$CONSUL_HOST '\
'-Dspring.cloud.consul.port=$CONSUL_PORT '\
'-Dspring.data.mongodb.host=$MONGO_HOST '\
'-Dspring.data.mongodb.port=$MONGO_PORT '

if [ -n "$MONGO_USER" ]; then
	COMMAND=$COMMAND'-Dspring.data.mongodb.username=$MONGO_USER '
fi

if [ -n "$MONGO_PASSWORD" ]; then
	COMMAND=$COMMAND'-Dspring.data.mongodb.password=$MONGO_PASSWORD '
fi

COMMAND=$COMMAND'find-me-image.jar'
eval "$COMMAND"
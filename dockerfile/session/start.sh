#!/bin/sh
COMMAND='exec java -jar '\
'-Dserver.port=$APP_PORT '\
'-Dspring.cloud.consul.host=$CONSUL_HOST '\
'-Dspring.cloud.consul.port=$CONSUL_PORT '\
'-Dspring.data.redis.host=$REDIS_HOST '\
'-Dspring.data.redis.port=$REDIS_PORT '\
'-Dspring.data.mongodb.host=$MONGO_HOST '\
'-Dspring.data.mongodb.port=$MONGO_PORT '\
'-Dspring.kafka.bootstrap-servers=$KAFKA_URL '

if [ -n "$APP_HOST" ]; then
	COMMAND=$COMMAND'-Dspring.cloud.consul.discovery.ip-address=$APP_HOST '
fi

if [ -n "$REDIS_PASSWORD" ]; then
	COMMAND=$COMMAND'-Dspring.data.redis.password=$REDIS_PASSWORD '
fi

if [ -n "$MONGO_USER" ]; then
	COMMAND=$COMMAND'-Dspring.data.mongodb.username=$MONGO_USER '
fi

if [ -n "$MONGO_PASSWORD" ]; then
	COMMAND=$COMMAND'-Dspring.data.mongodb.password=$MONGO_PASSWORD '
fi

COMMAND=$COMMAND'find-me-session.jar'
eval "$COMMAND"

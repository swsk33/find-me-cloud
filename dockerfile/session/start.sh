#!/bin/sh
COMMAND='java -jar '\
'-Dspring.cloud.consul.host=$CONSUL_HOST '\
'-Dspring.cloud.consul.port=$CONSUL_PORT '\
'-Dspring.data.redis.host=$REDIS_HOST '\
'-Dspring.data.redis.port=$REDIS_PORT '\
'-Dspring.kafka.bootstrap-servers=$KAFKA_URL '\

if [ -n "$REDIS_PASSWORD" ]; then
	COMMAND=$COMMAND'-Dspring.data.redis.password=$REDIS_PASSWORD '
fi

COMMAND=$COMMAND'find-me-session.jar'
eval "$COMMAND"
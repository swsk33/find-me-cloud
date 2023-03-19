#!/bin/sh
java -jar \
	-Dspring.cloud.consul.host=$CONSUL_HOST \
	-Dspring.cloud.consul.port=$CONSUL_PORT \
	-Dspring.data.redis.host=$REDIS_HOST \
	-Dspring.data.redis.port=$REDIS_PORT \
	-Dsping.data.redis.password=$REDIS_PASSWORD \
find-me-gateway.jar
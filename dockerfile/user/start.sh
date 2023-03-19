#!/bin/sh
java -jar \
	-Dspring.cloud.consul.host=$CONSUL_HOST \
	-Dspring.cloud.consul.port=$CONSUL_PORT \
	-Dspring.data.mongodb.host=$MONGO_HOST \
	-Dspring.data.mongodb.port=$MONGO_PORT \
	-Dspring.data.mongodb.username=$MONGO_USER \
	-Dsping.data.mongodb.password=$MONGO_PASSWORD \
	-Dspring.data.redis.host=$REDIS_HOST \
	-Dspring.data.redis.port=$REDIS_PORT \
	-Dspring.data.redis.password=$REDIS_PASSWORD \
	-Dspring.mail.host=$EMAIL_SMTP_HOST \
	-Dspring.mail.username=$EMAIL_USER \
	-Dspring.mail.password=$EMAIL_PASSWORD \
find-me-user.jar
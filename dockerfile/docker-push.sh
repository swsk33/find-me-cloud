#!/bin/bash
# 一键发布镜像脚本
VERSION=$1
if [ ! $VERSION ]; then
	echo 请指定版本号！
else
	# 登录DockerHub
	docker login
	# 推送镜像
	docker push swsk33/find-me-web:$VERSION
	docker push swsk33/find-me-gateway:$VERSION
	docker push swsk33/find-me-user:$VERSION
	docker push swsk33/find-me-session:$VERSION
	# 覆盖latest
	docker push swsk33/find-me-web
	docker push swsk33/find-me-gateway
	docker push swsk33/find-me-user
	docker push swsk33/find-me-session
	# 退出登录
	docker logout
fi

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
	docker push swsk33/find-me-image:$VERSION
	# 修改为latest
	docker tag swsk33/find-me-web:$VERSION swsk33/find-me-web
	docker tag swsk33/find-me-gateway:$VERSION swsk33/find-me-gateway
	docker tag swsk33/find-me-user:$VERSION swsk33/find-me-user
	docker tag swsk33/find-me-session:$VERSION swsk33/find-me-session
	docker tag swsk33/find-me-image:$VERSION swsk33/find-me-image
	# 覆盖latest
	docker push swsk33/find-me-web
	docker push swsk33/find-me-gateway
	docker push swsk33/find-me-user
	docker push swsk33/find-me-session
	docker push swsk33/find-me-image
	# 退出登录
	docker logout
fi
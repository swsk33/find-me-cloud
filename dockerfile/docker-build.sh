#!/bin/bash
# 一键构建镜像脚本
VERSION=$1
if [ ! $VERSION ]; then
	echo 请指定版本号！
else
	echo 正在构建网关...
	docker build -f ./gateway/Dockerfile -t swsk33/find-me-gateway:$VERSION ./gateway
	echo 网关构建完成！
	echo 正在构建用户模块...
	docker build -f ./user/Dockerfile -t swsk33/find-me-user:$VERSION ./user
	echo 用户模块构建完成！
	echo 正在构建会话模块...
	docker build -f ./session/Dockerfile -t swsk33/find-me-session:$VERSION ./session
	echo 会话模块构建完成！
	echo 正在构建Nginx模块...
	docker build -f ./nginx/Dockerfile -t swsk33/find-me-web:$VERSION ./nginx
	echo Nginx模块构建完成！
	echo 全部镜像构建完成！
	echo 创建latest Tag...
	# 修改为latest
	docker tag swsk33/find-me-web:$VERSION swsk33/find-me-web
	docker tag swsk33/find-me-gateway:$VERSION swsk33/find-me-gateway
	docker tag swsk33/find-me-user:$VERSION swsk33/find-me-user
	docker tag swsk33/find-me-session:$VERSION swsk33/find-me-session
	echo 操作全部完成！
fi

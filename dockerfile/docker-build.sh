#!/bin/bash
# 一键构建镜像脚本
VERSION=$1
if [ ! $VERSION ]; then
	echo 请指定版本号！
else
	echo 正在构建Nginx模块...
	docker build -f ./nginx/Dockerfile -t swsk33/find-me-web-supermap:$VERSION ./nginx
	echo Nginx模块构建完成！
	echo 全部镜像构建完成！
	echo 创建latest Tag...
	# 修改为latest
	docker tag swsk33/find-me-web-supermap:$VERSION swsk33/find-me-web-supermap
	echo 操作完成！
fi
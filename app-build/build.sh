#!/bin/bash
REQUEST_HOST=$1

if [ ! "$REQUEST_HOST" ]; then
	echo 请指定后端请求地址！必须是 http://xxx.com 或者是 https://xxx.com 的形式！
	exit
fi

mkdir -p ./build-project/static
echo 清理上次构建...
rm -rf ./build-project/static/* ./build-project/unpackage/release/*
cd ../web-vue-vite
VITE_HOST="$REQUEST_HOST" npm run build
cp -rf ./dist/* ../app-build/build-project/static/
echo 构建完成！请使用HBuilderX进行云打包！
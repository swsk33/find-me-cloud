#!/bin/bash
echo 清理上次构建...
rm -rf ./*/*.tar ./*/*.jar
echo 开始构建后端...
cd ../server-springcloud
mvn clean package
cp -f ./find-me-gateway/target/*.jar ../dockerfile/gateway/find-me-gateway.jar
cp -f ./find-me-service/find-me-user/target/*.jar ../dockerfile/user/find-me-user.jar
cp -f ./find-me-service/find-me-session/target/*.jar ../dockerfile/session/find-me-session.jar
echo 后端构建完成！
echo 开始构建前端...
cd ../web-vue-vite
npm run build
7z a -ttar ../dockerfile/nginx/vue.tar ./dist/*
echo 前端构建完成！

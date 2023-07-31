#!/bin/bash
echo 清理上次构建...
rm -rf ./*/*.tar ./*/*.jar
echo 构建前端...
cd ../web-vue-vite
npm run build
7z a -ttar ../dockerfile/nginx/vue.tar ./dist/*
echo 前端构建完成！
FROM azul/zulu-openjdk-alpine:17-jre-headless-latest
# 添加文件
ADD find-me-image.jar /app/
ADD start.sh /
# 设定权限
RUN chmod +x /start.sh
# 工作目录
WORKDIR /app
# 环境变量
# 语言设置
ENV LANG=C.UTF-8
# 端口设定
ENV APP_PORT=8800
# Consul注册中心
ENV CONSUL_HOST=127.0.0.1
ENV CONSUL_PORT=8500
# MongoDB数据库
ENV MONGO_HOST=127.0.0.1
ENV MONGO_PORT=27017
ENV MONGO_USER=""
ENV MONGO_PASSWORD=""
# 端口暴露
EXPOSE 8800
CMD ["/start.sh"]
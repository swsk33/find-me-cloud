FROM azul/zulu-openjdk-alpine:17-jre-headless-latest
# 添加文件
ADD find-me-session.jar /app/
ADD start.sh /
# 设定权限
RUN chmod +x /start.sh
# 工作目录
WORKDIR /app
# 环境变量
# 语言设置
ENV LANG=C.UTF-8
# 应用程序地址端口设定
ENV APP_PORT=8800
ENV APP_HOST=""
# Consul注册中心
ENV CONSUL_HOST=127.0.0.1
ENV CONSUL_PORT=8500
# Redis数据库
ENV REDIS_HOST=127.0.0.1
ENV REDIS_PORT=6379
ENV REDIS_PASSWORD=""
# Kafka消息中间件
ENV KAFKA_URL=127.0.0.1:9092
# 端口暴露
EXPOSE 8800
CMD ["/start.sh"]
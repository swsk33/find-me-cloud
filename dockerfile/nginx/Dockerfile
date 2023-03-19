FROM nginx
# 清理容器
RUN rm -rf /usr/share/nginx/html/*
# 加入静态网页和配置文件
ADD vue.tar /usr/share/nginx/html/
ADD cert.pem /etc/nginx/
ADD key.pem /etc/nginx/
ADD nginx.conf /etc/nginx/conf.d/default.conf
# 环境变量
# 语言设置
ENV LANG=C.UTF-8
# 数据卷
VOLUME ["/etc/nginx"]
CMD ["nginx", "-g", "daemon off;"]
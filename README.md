# 找到我

## 1，介绍

这是一个仿照**位置共享**功能的WebGIS应用平台，但是**更加专注于位置共享**，借助这个平台，可以更加灵敏、实时地和朋友共享位置。

### (1) 主要功能

- 创建房间，并和同一房间内用户共享实时位置，一个房间支持多个用户共享位置
- 通过地图找到自己以及同伴们的位置，并附带方向和高程显示（部分手机不支持方向和高程数据获取）
- 提供基本用户功能和邮件验证码重置密码功能

### (2) 软件架构

总体来说，该平台是一个前后端分离的分布式架构，用户端使用浏览器即可访问。

该平台前端使用Vue 3框架进行开发，使用Vite作为构建工具，主要由下列部分组成：

- 高德地图API
- element-plus 组件库
- Vue Router 单页路由
- Pinia 状态管理
- sass CSS预处理器
- Vue-use 响应式API库
- gcoord 坐标转换

后端为Spring Cloud分布式微服务架构，使用的中间件和主要外部库如下：

- Consul 服务发现和注册中心
- MongoDB 主要存储和文件存储数据库
- Redis 缓存数据库
- Kafka 消息中间件
- Sa-Token 认证和鉴权
- HuTool 实用工具
- Spring WebSocket 用于实时会话支持

![image-20230319210924789](https://swsk33-note.oss-cn-shanghai.aliyuncs.com/image-20230319210924789.png)

其中，所有的模块都可以部署为集群形式，在本项目中，对于分布式情况下的会话的解决方案，主要思路是使用消息中间件对实时消息的发送和接收进行解耦，大致如下图：

![](https://swsk33-note.oss-cn-shanghai.aliyuncs.com/findMe-UML.dio-分布式会话解决方案.png)

### (3) 隐私说明

地理位置一直是用户隐私中相当重要的一部分，但是该平台网页端会读取移动设备的地理位置来完成该平台的主要功能。不过该平台不会以任何形式收集、储存或者是散播使用者的地理位置信息，地理位置信息只会被广播到同一房间下的所有用户那里，不会在服务端储存，请放心使用。

第一次使用该平台网页端时，可能会弹出授予浏览器地理位置权限的对话框，点击确定授予才能正常使用该平台。

## 2，部署说明

该平台可以通过Docker容器化部署的方式完成在自己的服务器上的部署并投入使用，这里提供两种方式：**单机一键部署**和**单独分模块分布式部署**。

在部署之前，需要现在服务端上面安装好Docker Engine，这里不再赘述安装过程，若没有安装，请先参考[官方文档](https://docs.docker.com/engine/install/debian/)完成Docker Engine的安装。

下列两种方式大家二选一即可。

### (1) 单机一键部署

#### 1. 下载`docker-compose.yml`文件并配置邮箱

首先下载这个[docker-compose.yml](https://gitee.com/swsk33/find-me-cloud/raw/master/dockerfile/docker-compose.yml)文件到服务器，并使用文本编辑器打开，找到其中的用户模块部分的邮箱配置：

![image-20230319212556183](https://swsk33-note.oss-cn-shanghai.aliyuncs.com/image-20230319212556183.png)

将这里的环境变量值修改成你的邮箱的各个对应配置，配置该邮箱主要是用于给用户发送邮件验证码用的，可以先自行去注册一个邮箱例如QQ、163等等，并开启`SMTP`服务。

#### 2. 启动所有容器

然后在这个`docker-compose.yml`文件所在位置执行下列命令：

```bash
docker compose up -d
```

等待数十秒，全部容器启动成功。

#### 3. 配置`Https`

<span id="nginx-https">由于</span>该平台用到了定位服务，而在现在的安卓或者IOS操作系统中，只有支持`Https`的站点才能获取位置，因此该平台的Nginx服务器已经默认开启了`Https`，但是其中自带的证书是无效的，需要自行准备一个有效的证书并替换一下。

获取或者生成证书可以参考下列文章：

- 阿里云申请免费证书：[传送门](https://juejin.cn/post/6989106629111185438)
- 使用Let's encrypt申请免费证书：[传送门](https://juejin.cn/post/6989104824721604639)

准备好证书之后，将**证书**和**密钥文件**分别命名为`cert.pem`和`key.pem`，然后覆盖到Nginx容器的**配置文件数据卷**中即可，按照上述方式启动容器之后，Nginx的配置文件数据卷应当位于`/var/lib/docker/volumes/dockerfile_find-me-nginx-config/_data`目录下，将你的证书和密钥文件重命名后覆盖进去即可。

然后执行下列命令重启容器使其配置生效：

```bash
docker compose restart
```

到此，单机一键配置和部署完成！访问你的服务器地址（域名），即可正常地使用该平台的所有功能了！

如果要销毁所有容器，可以通过下列命令：

```bash
# 保留数据卷
docker compose down

# 不保留数据卷
docker compose down -v
```

### (2) 单独分模块分布式部署

虽然单机一键部署非常方便，但是这对服务器的要求是非常高的，如果你的服务器没有4GB甚至以上的内存，可能无法很好地全部运行这些容器。

因此，可以将这些容器**部署到不同的服务器上**，完成分模块的分布式部署。其中，网关以及各个服务模块，都可以以**集群**的形式部署。

#### 1. 部署所需要的数据库和中间件

该平台依赖下列数据库和中间件：

- MongoDB 数据库
- Redis 数据库
- Kafka 消息中间件
- Consul 服务发现与注册中心

这里不再赘述如何部署这些数据库和中间件，这里给出一些参考，可以先按照这些参考完成上述中间件和数据库的部署：

- Docker部署MongoDB：[传送门](https://juejin.cn/post/7190013540302848061)
- Docker部署Redis：[传送门](https://juejin.cn/post/7186975485212622908)
- Kafka简易单机镜像主页：[传送门](https://hub.docker.com/r/swsk33/kafka-standalone)
- Consul镜像主页：[传送门](https://hub.docker.com/_/consul)

对于Consul注册中心，可以使用下列命令部署一个单机实例：

```bash
docker run -id --name=consul -p 8300:8300 -p 8301:8301 -p 8302:8302 -p 8500:8500 -p 8600:8600 -v consul-data:/consul/data consul agent -server -ui -node=server-1 -bootstrap-expect=1 -client=0.0.0.0 -advertise=服务器外网地址
```

最后的`-advertise`参数值替换为部署的服务器的外网地址！

#### 2. 服务功能模块部署

##### ① 用户模块

拉取镜像：

```bash
docker pull swsk33/find-me-user
```

通过下列命令创建并运行容器：

```bash
docker run -id --name=find-me-user -p 8800:8800 \
-e CONSUL_HOST=Consul注册中心地址 \
-e CONSUL_PORT=Consul注册中心端口 \
-e MONGO_HOST=MongoDB数据库地址 \
-e MONGO_PORT=MongoDB数据库端口 \
-e MONGO_USER=MongoDB数据库用户名 \
-e MONGO_PASSWORD=MongoDB数据库密码 \
-e REDIS_HOST=Redis数据库地址 \
-e REDIS_PORT=Redis数据库端口 \
-e REDIS_PASSWORD=Redis数据库密码 \
-e EMAIL_SMTP_HOST=SMTP邮件服务器 \
-e EMAIL_USER=你的邮箱 \
-e EMAIL_PASSWORD=邮箱授权码 \
swsk33/find-me-user
```

将上述命令中环境变量传参部分改成自己实际的值，上述所有环境变量的意义和默认值如下：

|    环境变量名     |           意义           |      默认值      |
| :---------------: | :----------------------: | :--------------: |
|   `CONSUL_HOST`   |    Consul注册中心地址    |   `127.0.0.1`    |
|   `CONSUL_PORT`   |    Consul注册中心端口    |      `8500`      |
|   `MONGO_HOST`    |    MongoDB数据库地址     |   `127.0.0.1`    |
|   `MONGO_PORT`    |    MongoDB数据库端口     |     `27017`      |
|   `MONGO_USER`    |   MongoDB数据库用户名    | `""`（空字符串） |
| `MONGO_PASSWORD`  |    MongoDB数据库密码     | `""`（空字符串） |
|   `REDIS_HOST`    |     Redis数据库地址      |   `127.0.0.1`    |
|   `REDIS_PORT`    |     Redis数据库端口      |      `6379`      |
| `REDIS_PASSWORD`  |     Redis数据库密码      | `""`（空字符串） |
| `EMAIL_SMTP_HOST` |       邮箱SMTP地址       |  `smtp.163.com`  |
|   `EMAIL_USER`    | 配置用于发通知邮件的邮箱 | `""`（空字符串） |
| `EMAIL_PASSWORD`  |        邮箱授权码        | `""`（空字符串） |

建议在命令行中，使用英文双引号`"`包围配置值，如果你要配置的某一项的值和上述的默认值相同，则可以在命令中省去这一条变量，**下面其它容器同样遵循这样的规则**。

##### ② 会话模块

拉取镜像：

```bash
docker pull swsk33/find-me-session
```

通过下列命令创建并运行容器：

```bash
docker run -id --name=find-me-session -p 8800:8800 \
-e CONSUL_HOST=Consul注册中心地址 \
-e CONSUL_PORT=Consul注册中心端口 \
-e REDIS_HOST=Redis数据库地址 \
-e REDIS_PORT=Redis数据库端口 \
-e REDIS_PASSWORD=Redis数据库密码 \
-e KAFKA_URL=Kafka地址:端口 \
swsk33/find-me-session
```

上述所有环境变量的意义和默认值如下：

|    环境变量名    |        意义        |      默认值      |
| :--------------: | :----------------: | :--------------: |
|  `CONSUL_HOST`   | Consul注册中心地址 |   `127.0.0.1`    |
|  `CONSUL_PORT`   | Consul注册中心端口 |      `8500`      |
|   `REDIS_HOST`   |  Redis数据库地址   |   `127.0.0.1`    |
|   `REDIS_PORT`   |  Redis数据库端口   |      `6379`      |
| `REDIS_PASSWORD` |  Redis数据库密码   | `""`（空字符串） |
|   `KAFKA_URL`    |  Kafka地址和端口   | `127.0.0.1:9092` |

##### ③ 图片模块

拉取镜像：

```bash
docker pull swsk33/find-me-image
```

通过下列命令创建并运行容器：

```bash
docker run -id --name=find-me-image -p 8800:8800 \
-e CONSUL_HOST=Consul注册中心地址 \
-e CONSUL_PORT=Consul注册中心端口 \
-e MONGO_HOST=MongoDB数据库地址 \
-e MONGO_PORT=MongoDB数据库端口 \
-e MONGO_USER=MongoDB数据库用户名 \
-e MONGO_PASSWORD=MongoDB数据库密码 \
swsk33/find-me-image
```

上述所有环境变量的意义和默认值如下：

|    环境变量名    |        意义         |      默认值      |
| :--------------: | :-----------------: | :--------------: |
|  `CONSUL_HOST`   | Consul注册中心地址  |   `127.0.0.1`    |
|  `CONSUL_PORT`   | Consul注册中心端口  |      `8500`      |
|   `MONGO_HOST`   |  MongoDB数据库地址  |   `127.0.0.1`    |
|   `MONGO_PORT`   |  MongoDB数据库端口  |     `27017`      |
|   `MONGO_USER`   | MongoDB数据库用户名 | `""`（空字符串） |
| `MONGO_PASSWORD` |  MongoDB数据库密码  | `""`（空字符串） |

#### 3. 网关部署

拉取镜像：

```bash
docker pull swsk33/find-me-gateway
```

通过下列命令创建并运行容器：

```bash
docker run -id --name=find-me-gateway -p 8800:8800 \
-e CONSUL_HOST=Consul注册中心地址 \
-e CONSUL_PORT=Consul注册中心端口 \
-e REDIS_HOST=Redis数据库地址 \
-e REDIS_PORT=Redis数据库端口 \
-e REDIS_PASSWORD=Redis数据库密码 \
swsk33/find-me-gateway
```

上述所有环境变量的意义和默认值如下：

|    环境变量名    |        意义        |      默认值      |
| :--------------: | :----------------: | :--------------: |
|  `CONSUL_HOST`   | Consul注册中心地址 |   `127.0.0.1`    |
|  `CONSUL_PORT`   | Consul注册中心端口 |      `8500`      |
|   `REDIS_HOST`   |  Redis数据库地址   |   `127.0.0.1`    |
|   `REDIS_PORT`   |  Redis数据库端口   |      `6379`      |
| `REDIS_PASSWORD` |  Redis数据库密码   | `""`（空字符串） |

#### 4. Nginx服务器部署

先拉取镜像：

```bash
docker pull swsk33/find-me-web
```

创建并启动容器：

```bash
docker run -id --name=find-me-nginx -p 80:80 -p 443:443 -v find-me-nginx-config:/etc/nginx swsk33/find-me-web
```

这里需要修改一下Nginx配置文件，按照上述命令启动后，Nginx配置文件位于`/var/lib/docker/volumes/find-me-nginx-config/_data/conf.d/default.conf`，通过文本编辑器打开，找到下图：

![image-20230319221344847](https://swsk33-note.oss-cn-shanghai.aliyuncs.com/image-20230319221344847.png)

注意，上述`server`后面接服务器地址和端口，**不能以`http`开头！**

如果你的网关配置了多个集群，则需要都配置在这里例如：

```nginx
# 网关集群列表
upstream gateway {
    server 网关服务器1地址:8800;
    server 网关服务器2地址:8800;
    server 网关服务器3地址:8800;
    ...
}
```

地址和端口根据你配置的实际情况为准。

然后，还需要准备好一个有效的`Https`证书和密钥，覆盖到这个Nginx容器的数据卷目录下，这一步参考上面第一种方式的[配置Https部分](#nginx-https)。

只不过这里，按照上述命令启动后，证书和密钥的需要覆盖到的位置在`/var/lib/docker/volumes/find-me-nginx-config/_data`目录下。

然后重启Nginx容器：

```bash
docker restart find-me-nginx
```

到此，分布式配置完成！访问你的Nginx容器所在服务器地址（域名），即可访问到网站。

除了Nginx服务器之外，其余每个服务功能模块和网关模块都可以部署为集群的形式，按照同样地方式多部署在几台服务器即可！无需做其余配置，除了部署网关集群时，需要在Nginx配置文件中配置集群列表。

> 更新日期：2023.3.19
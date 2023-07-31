# 找到我

## 1，介绍

这是一个仿照**位置共享**功能的WebGIS应用平台，但是**更加专注于位置共享**，借助这个平台，可以更加灵敏、实时地和朋友共享位置。

### (1) 主要功能

- 房间功能：

	- 用户可以创建并分享房间，并和同一房间内用户共享实时位置，一个房间支持多个用户共享位置
	- 可以通过多种方式分享房间邀请其他人加入，例如分享房间ID和密码或者是分享一键加入房间的链接等等
- 房间内还带有群聊功能，用户可以在房间内发送文字信息聊天
- 房间模板功能：
	- 除了临时创建房间之外，还可以创建一个永久的、专属于几个人的房间，这个房间称之为**房间模板**

	- 拥有同一个房间模板的用户可以直接通过模板加入同一个房间，更加方便地共享位置，而不需要临时去创建并分享房间

- 用户位置可视化：
	- 多个用户加入同一个房间后，可以通过地图找到自己以及同伴们的位置，并附带方向和高程显示（部分手机可能不支持方向和高程数据获取）
	- 针对不同设备陀螺仪兼容性或者环境问题导致的方向显示不准确的问题，该程序客户端具备方向自动校正的能力
	- 在地图上放置集结点
	- 除此之外，针对IOS设备特殊的权限问题，也进行了一些适配

- 用户基本功能：
	- 用户登录注册和自定义
	- 邮件验证码重置密码功能


对于功能的具体介绍，可以查看后面的使用手册。

### (2) 软件架构

总体来说，该平台是一个前后端分离的分布式架构，用户端使用浏览器即可访问。

该平台前端使用Vue 3框架进行开发，使用Vite作为构建工具，主要由下列部分组成：

- Supermap iClient JavaScript作为WebGIS库
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
- Hutool 实用工具
- SpringBoot WebSocket 用于实时会话支持

![image-20230730112020116](https://swsk33-note.oss-cn-shanghai.aliyuncs.com/undefinedimage-20230730112020116.png)

其中，所有的模块都可以部署为集群形式，在本项目中，对于分布式情况下的会话的解决方案，主要思路是使用消息中间件对实时消息的发送和接收进行解耦，大致如下图：

![](https://swsk33-note.oss-cn-shanghai.aliyuncs.com/2023323115624.png)

### (3) 隐私说明

地理位置一直是用户隐私中相当重要的一部分，但是该平台网页端会读取移动设备的地理位置来完成该平台的主要功能。不过该平台不会以任何形式收集、储存或者是散播使用者的地理位置信息，地理位置信息只会被广播到同一房间下的所有用户那里，不会在服务端储存，请放心使用。

第一次使用该平台网页端时，可能会弹出授予浏览器地理位置权限的对话框，点击确定授予才能正常使用该平台。

## 2，文档和说明

可以通过下列链接查看该平台的部署和使用说明：

- 部署手册：[传送门](./doc/部署手册.md)
- 用户说明书：[传送门](./doc/用户手册.md)
- 如何授予手机浏览器定位权限：[传送门](./doc/如何授予浏览器定位权限.md)

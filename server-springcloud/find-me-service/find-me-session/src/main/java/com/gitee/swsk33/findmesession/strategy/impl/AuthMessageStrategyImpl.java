package com.gitee.swsk33.findmesession.strategy.impl;

import com.gitee.swsk33.findmeentity.dataobject.User;
import com.gitee.swsk33.findmeentity.factory.MessageFactory;
import com.gitee.swsk33.findmeentity.model.Message;
import com.gitee.swsk33.findmeentity.model.Result;
import com.gitee.swsk33.findmeentity.model.Room;
import com.gitee.swsk33.findmeentity.param.MessageType;
import com.gitee.swsk33.findmefeign.feignclient.UserClient;
import com.gitee.swsk33.findmesession.cache.RoomCache;
import com.gitee.swsk33.findmesession.context.KafkaConsumerContext;
import com.gitee.swsk33.findmesession.strategy.RealTimeMessageStrategy;
import com.gitee.swsk33.findmesession.websocket.SessionWebSocketAPI;
import com.gitee.swsk33.findmeutility.singleton.JacksonMapper;
import com.gitee.swsk33.findmeutility.util.BCryptUtils;
import jakarta.annotation.Resource;
import jakarta.websocket.Session;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import static com.gitee.swsk33.findmeutility.util.KafkaNameGenerator.generateName;

/**
 * 认证类型消息处理器（最终加入房间逻辑也包含在此）
 */
@Slf4j
@Component
public class AuthMessageStrategyImpl implements RealTimeMessageStrategy {

	@Autowired
	private RoomCache roomCache;

	@Autowired
	private UserClient userClient;

	@Resource
	private KafkaTemplate<String, Message<?>> kafkaTemplate;

	@Autowired
	private KafkaConsumerContext kafkaConsumerContext;

	@Override
	public void handleMessage(Message<?> message, Session session, String roomId, long userId) throws Exception {
		Room getRoom = roomCache.getRoom(roomId, false);
		if (getRoom == null) {
			session.getAsyncRemote().sendObject(MessageFactory.createMessage(MessageType.FAILED, "房间不存在！"));
			session.close();
			return;
		}
		// 检测用户是否已经在房间
		if (getRoom.getUsers().containsKey(userId)) {
			session.getAsyncRemote().sendObject(MessageFactory.createMessage(MessageType.FAILED, "已加入房间！"));
			return;
		}
		// 比对密码
		// 序列化内容体
		String password = JacksonMapper.getMapper().convertValue(message.getData(), String.class);
		if (!BCryptUtils.match(password, getRoom.getPassword())) {
			session.getAsyncRemote().sendObject(MessageFactory.createMessage(MessageType.FAILED, "房间密码错误！"));
			session.close();
			return;
		}
		// 判断用户登录
		Result<User> getUser = userClient.isLoginById(userId);
		if (!getUser.isSuccess()) {
			session.getAsyncRemote().sendObject(MessageFactory.createMessage(MessageType.FAILED, "用户未登录！认证失败！"));
			session.close();
			return;
		}
		// 成功加入房间
		session.getAsyncRemote().sendObject(MessageFactory.createMessage(MessageType.SUCCESS, "加入房间成功！"));
		SessionWebSocketAPI.removeAutoExpireSession(userId);
		log.info("用户id：" + userId + "的会话认证通过！已被确定为持久会话！");
		// 把用户加入房间
		roomCache.addUserToRoom(roomId, getUser.getData());
		// 广播消息-房间改变，用户加入
		kafkaTemplate.send(generateName(roomId), MessageFactory.createMessage(MessageType.ROOM_CHANGED, userId, roomCache.getRoom(roomId, true)));
		kafkaTemplate.send(generateName(roomId), MessageFactory.createMessage(MessageType.USER_JOIN, userId, getUser.getData()));
		// 与此同时，为该用户创建一个专用的kafka消费者用于接受该房间内其他用户信息
		kafkaConsumerContext.addConsumerTask(roomId, userId, session);
		log.info("用户：" + getUser.getData().getNickname() + "成功加入房间！");
	}

}
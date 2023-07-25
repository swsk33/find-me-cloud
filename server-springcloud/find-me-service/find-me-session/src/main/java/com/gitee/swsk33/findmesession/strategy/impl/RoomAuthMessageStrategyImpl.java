package com.gitee.swsk33.findmesession.strategy.impl;

import com.gitee.swsk33.findmeentity.factory.MessageFactory;
import com.gitee.swsk33.findmeentity.model.Message;
import com.gitee.swsk33.findmeentity.model.Result;
import com.gitee.swsk33.findmeentity.param.MessageType;
import com.gitee.swsk33.findmesession.context.KafkaConsumerContext;
import com.gitee.swsk33.findmesession.service.RoomService;
import com.gitee.swsk33.findmesession.strategy.RealTimeMessageStrategy;
import com.gitee.swsk33.findmeutility.singleton.JacksonMapper;
import jakarta.websocket.Session;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 普通房间的认证类型消息处理器
 */
@Slf4j
@Component
public class RoomAuthMessageStrategyImpl implements RealTimeMessageStrategy {

	@Autowired
	private RoomService roomService;

	@Autowired
	private KafkaConsumerContext kafkaConsumerContext;

	@Override
	public void handleMessage(Message<?> message, Session session, String roomId, long userId) throws Exception {
		// 序列化内容体
		String password = JacksonMapper.getMapper().convertValue(message.getData(), String.class);
		// 加入房间
		Result<Void> addRoomResult = roomService.userJoinRoom(roomId, password, userId);
		if (!addRoomResult.isSuccess()) {
			session.getAsyncRemote().sendObject(MessageFactory.createMessage(MessageType.AUTH_FAILED, addRoomResult.getMessage()));
			session.close();
			return;
		}
		session.getAsyncRemote().sendObject(MessageFactory.createMessage(MessageType.AUTH_SUCCESS, "加入房间成功！"));
		// 与此同时，为该用户创建一个专用的kafka消费者用于接受该房间内其他用户信息
		kafkaConsumerContext.addConsumerTask(roomId, userId, session);
	}

}
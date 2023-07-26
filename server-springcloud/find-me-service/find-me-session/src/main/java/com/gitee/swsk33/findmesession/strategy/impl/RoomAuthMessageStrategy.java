package com.gitee.swsk33.findmesession.strategy.impl;

import com.gitee.swsk33.findmeentity.factory.MessageFactory;
import com.gitee.swsk33.findmeentity.model.Message;
import com.gitee.swsk33.findmeentity.model.Result;
import com.gitee.swsk33.findmeentity.param.MessageType;
import com.gitee.swsk33.findmesession.service.RoomService;
import com.gitee.swsk33.findmesession.strategy.RealTimeMessageStrategy;
import com.gitee.swsk33.findmeutility.singleton.JacksonMapper;
import jakarta.websocket.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 普通房间的认证类型消息处理器
 */
@Component
public class RoomAuthMessageStrategy implements RealTimeMessageStrategy {

	@Autowired
	private RoomService roomService;

	@Override
	public void handleMessage(Message<?> message, Session session, String roomId, long userId) throws Exception {
		// 序列化内容体
		String password = JacksonMapper.getMapper().convertValue(message.getData(), String.class);
		// 首先进行认证
		Result<Void> authResult = roomService.auth(roomId, password, userId);
		if (!authResult.isSuccess()) {
			session.getAsyncRemote().sendObject(MessageFactory.createMessage(MessageType.AUTH_FAILED, authResult.getMessage()));
			session.close();
		}
		// 认证成功后就加入房间
		Result<Void> addRoomResult = roomService.userJoinRoom(roomId, userId, session);
		if (!addRoomResult.isSuccess()) {
			session.getAsyncRemote().sendObject(MessageFactory.createMessage(MessageType.AUTH_FAILED, addRoomResult.getMessage()));
			session.close();
		}
	}

}
package com.gitee.swsk33.findmesession.strategy.impl;

import com.gitee.swsk33.findmeentity.factory.MessageFactory;
import com.gitee.swsk33.findmeentity.model.Message;
import com.gitee.swsk33.findmeentity.model.Result;
import com.gitee.swsk33.findmeentity.param.MessageType;
import com.gitee.swsk33.findmesession.service.RoomTemplateService;
import com.gitee.swsk33.findmesession.strategy.RealTimeMessageStrategy;
import jakarta.websocket.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 用于通过房间模板进入房间时的认证策略
 */
@Component
public class RoomTemplateAuthStrategy implements RealTimeMessageStrategy {

	@Autowired
	private RoomTemplateService roomTemplateService;

	@Override
	public void handleMessage(Message<?> message, Session session, String roomId, long userId) throws Exception {
		// 执行认证逻辑
		Result<Void> result = roomTemplateService.authAndJoinThroughTemplate(roomId, userId, session);
		// 认证失败则发送消息并断开连接
		if (!result.isSuccess()) {
			session.getAsyncRemote().sendObject(MessageFactory.createMessage(MessageType.AUTH_FAILED, result.getMessage()));
			session.close();
		}
	}

}
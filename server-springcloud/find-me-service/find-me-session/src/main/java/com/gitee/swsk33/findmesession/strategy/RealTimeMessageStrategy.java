package com.gitee.swsk33.findmesession.strategy;

import com.gitee.swsk33.findmeentity.model.Message;
import jakarta.websocket.Session;
import org.springframework.stereotype.Component;

/**
 * WebSocket中实时消息策略
 */
@Component
public interface RealTimeMessageStrategy {

	/**
	 * 处理收到的实时消息
	 *
	 * @param message 消息
	 * @param session 对应的会话对象
	 * @param roomId  对应房间id
	 * @param userId  对应用户id
	 */
	void handleMessage(Message<?> message, Session session, String roomId, long userId) throws Exception;

}
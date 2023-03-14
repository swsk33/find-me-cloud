package com.gitee.swsk33.findmesession.websocket;

import com.gitee.swsk33.findmeentity.model.Message;
import com.gitee.swsk33.findmesession.cache.RoomCache;
import com.gitee.swsk33.findmesession.encoder.MessageWebSocketEncoder;
import jakarta.annotation.Resource;
import jakarta.websocket.*;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 会话WebSocket接口
 */
@RestController
@ServerEndpoint(value = "/ws/session/room/{roomId}", encoders = MessageWebSocketEncoder.class)
@Slf4j
public class SessionWebSocketAPI {

	@Resource
	private KafkaTemplate<String, Message<?>> kafkaTemplate;

	@Autowired
	private RoomCache roomCache;

	/**
	 * 连接建立成功调用的方法
	 */
	@OnOpen
	public void onOpen(Session session, @PathParam("roomId") String roomId) {
		log.info("有用户加入会话！");
	}

	/**
	 * 连接关闭调用的方法
	 */
	@OnClose
	public void onClose(Session session) throws IOException {
		System.out.println("有一连接断开！ID：" + session.getId());
		session.close();
	}

	/**
	 * 收到客户端消息后调用的方法
	 */
	@OnMessage
	public void onMessage(String message, Session session) throws Exception {
		System.out.println("来自客户端的消息：" + message + "，ID是：" + session.getId());
		// 返回消息
		session.getBasicRemote().sendText("接收到消息！内容：" + message);
	}

	/**
	 * 发生错误时调用
	 */
	@OnError
	public void onError(Session session, Throwable error) {
		System.out.println("发生错误！ID：" + session.getId());
		error.printStackTrace();
	}

}
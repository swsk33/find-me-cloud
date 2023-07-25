package com.gitee.swsk33.findmesession.websocket;

import com.gitee.swsk33.findmeentity.factory.MessageFactory;
import com.gitee.swsk33.findmeentity.model.Message;
import com.gitee.swsk33.findmeentity.param.MessageType;
import com.gitee.swsk33.findmesession.encoder.MessageEncoder;
import com.gitee.swsk33.findmesession.service.RoomService;
import com.gitee.swsk33.findmesession.strategy.context.RealTimeMessageContext;
import com.gitee.swsk33.findmeutility.singleton.JacksonMapper;
import jakarta.websocket.*;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.*;

/**
 * 用于普通房间的WebSocket会话端口
 */
@Slf4j
@Component
@ServerEndpoint(value = "/ws/session/room/{roomId}/{userId}", encoders = MessageEncoder.class)
public class RoomSessionAPI {

	/**
	 * 用于延时断开为认证的会话的定时器
	 */
	private static final ScheduledExecutorService notLoginSessionExecutor = Executors.newScheduledThreadPool(8);

	/**
	 * 存放所有未认证的会话的定时器
	 * 键为用户id，值为定时器
	 */
	private static final Map<Long, ScheduledFuture<?>> notLoginSessions = new ConcurrentHashMap<>();

	/**
	 * 容器上下文，用于在WebSocket类中获取Bean
	 * 由于WebSocket类的声明周期和Spring Bean并不相同，因此无法在此自动装配，只能创建时静态装配这个上下文对象，通过上下文对象获取Bean
	 */
	private static ApplicationContext applicationContext;

	@Autowired
	public void setApplicationContext(ApplicationContext applicationContext) {
		RoomSessionAPI.applicationContext = applicationContext;
	}

	/**
	 * 加入一个会话，使其在两分钟后自动过期
	 *
	 * @param session 会话对象
	 * @param userId  会话对应用户id
	 */
	public static void addAutoExpireSession(Session session, long userId) {
		ScheduledFuture<?> future = notLoginSessionExecutor.schedule(() -> {
			try {
				if (!session.isOpen()) {
					log.warn("用户id：" + userId + "的会话已被关闭！退出定时任务！");
					// 从列表中移除自己
					notLoginSessions.remove(userId);
					return;
				}
				session.getAsyncRemote().sendObject(MessageFactory.createMessage(MessageType.FAILED, "认证超时！连接断开！"));
				session.close();
				// 从列表中移除自己
				notLoginSessions.remove(userId);
				log.warn("用户id：" + userId + "的会话过期！");
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}, 1, TimeUnit.MINUTES);
		notLoginSessions.put(userId, future);
		log.warn("用户id：" + userId + "的会话目前为未认证会话！1分钟后过期！");
	}

	/**
	 * 从自动过期的会话列表中移除一个用户的会话并取消自动过期定时任务
	 *
	 * @param userId 会话对应的用户id
	 */
	public static void removeAutoExpireSession(long userId) {
		ScheduledFuture<?> future = notLoginSessions.remove(userId);
		future.cancel(true);
	}

	/**
	 * 连接建立成功调用的方法
	 */
	@OnOpen
	public void onOpen(Session session, @PathParam("userId") long userId) {
		log.info("用户(id:{})加入会话！等待用户认证...", userId);
		// 刚建立的会话是未认证会话，2分钟后自动过期
		addAutoExpireSession(session, userId);
	}

	/**
	 * 连接关闭调用的方法
	 */
	@OnClose
	public void onClose(@PathParam("roomId") String roomId, @PathParam("userId") long userId) {
		log.info("用户(id:{})断开连接！", userId);
		// 使用Spring上下文容器手动获取Bean，下面也一样
		RoomService roomService = applicationContext.getBean(RoomService.class);
		roomService.removeUserFromRoom(roomId, userId);
	}

	/**
	 * 收到客户端消息后调用的方法
	 */
	@OnMessage
	public void onMessage(String message, Session session, @PathParam("roomId") String roomId, @PathParam("userId") long userId) throws Exception {
		// 反序列化
		Message<?> messageObject = JacksonMapper.getMapper().readValue(message, Message.class);
		// 拦截未认证会话的非认证消息
		if (notLoginSessions.containsKey(userId) && messageObject.getType() != MessageType.AUTH) {
			session.getAsyncRemote().sendObject(MessageFactory.createMessage(MessageType.AUTH_FAILED, "未认证！"));
			return;
		}
		// 传入消息策略处理器
		RealTimeMessageContext realTimeMessageContext = applicationContext.getBean(RealTimeMessageContext.class);
		realTimeMessageContext.handleMessage(messageObject, session, roomId, userId);
	}

	/**
	 * 发生错误时调用
	 */
	@OnError
	public void onError(Throwable error, @PathParam("userId") long userId) {
		log.error("会话发生错误！用户id：{}", userId);
		error.printStackTrace();
	}

}
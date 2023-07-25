package com.gitee.swsk33.findmesession.strategy.context;

import com.gitee.swsk33.findmeentity.model.Message;
import com.gitee.swsk33.findmeentity.param.MessageType;
import com.gitee.swsk33.findmesession.strategy.RealTimeMessageStrategy;
import com.gitee.swsk33.findmesession.strategy.impl.RoomAuthMessageStrategyImpl;
import com.gitee.swsk33.findmesession.strategy.impl.ChatMessageStrategyImpl;
import com.gitee.swsk33.findmesession.strategy.impl.PositionMessageStrategy;
import com.gitee.swsk33.findmesession.strategy.impl.RallyChangeMessageStrategyImpl;
import jakarta.annotation.PostConstruct;
import jakarta.websocket.Session;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * 实时消息的策略上下文容器
 */
@Component
@Slf4j
public class RealTimeMessageContext {

	/**
	 * 策略容器
	 */
	private static final Map<MessageType, RealTimeMessageStrategy> STRATEGY_MAP = new HashMap<>();

	@Autowired
	private BeanFactory beanFactory;

	@PostConstruct
	private void initStrategy() {
		STRATEGY_MAP.put(MessageType.AUTH, beanFactory.getBean(RoomAuthMessageStrategyImpl.class));
		STRATEGY_MAP.put(MessageType.POSITION_CHANGED, beanFactory.getBean(PositionMessageStrategy.class));
		STRATEGY_MAP.put(MessageType.RALLY_CHANGED, beanFactory.getBean(RallyChangeMessageStrategyImpl.class));
		STRATEGY_MAP.put(MessageType.CHAT, beanFactory.getBean(ChatMessageStrategyImpl.class));
		log.info("所有消息处理策略初始化完成！");
	}

	/**
	 * 根据消息类型执行不同的消息处理策略
	 *
	 * @param message 消息对象
	 * @param session 对应的用户会话
	 * @param roomId  对应的房间id
	 * @param userId  对应的用户id
	 */
	public void handleMessage(Message<?> message, Session session, String roomId, long userId) throws Exception {
		// 检查策略是否存在
		if (!STRATEGY_MAP.containsKey(message.getType())) {
			log.error("策略不存在！");
			return;
		}
		// 执行策略
		STRATEGY_MAP.get(message.getType()).handleMessage(message, session, roomId, userId);
	}

}
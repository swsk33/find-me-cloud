package com.gitee.swsk33.findmesession.strategy.impl;

import com.gitee.swsk33.findmeentity.model.Message;
import com.gitee.swsk33.findmesession.strategy.RealTimeMessageStrategy;
import jakarta.websocket.Session;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import static com.gitee.swsk33.findmeutility.KafkaNameGenerator.generateName;

/**
 * 实时位置改变的消息策略
 */
@Slf4j
@Component
public class PositionMessageStrategy implements RealTimeMessageStrategy {

	@Autowired
	private KafkaTemplate<String, Message<?>> kafkaTemplate;

	@Override
	public void handleMessage(Message<?> message, Session session, String roomId, long userId) {
		// 广播到消息队列
		kafkaTemplate.send(generateName(roomId), message);
	}

}
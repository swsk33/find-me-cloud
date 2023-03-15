package com.gitee.swsk33.findmesession.strategy.impl;

import com.gitee.swsk33.findmeentity.model.Message;
import com.gitee.swsk33.findmeentity.model.RallyPoint;
import com.gitee.swsk33.findmeentity.model.Room;
import com.gitee.swsk33.findmesession.cache.RoomCache;
import com.gitee.swsk33.findmesession.strategy.RealTimeMessageStrategy;
import jakarta.annotation.Resource;
import jakarta.websocket.Session;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import static com.gitee.swsk33.findmeutility.KafkaNameGenerator.generateName;

/**
 * 集结点变化消息处理策略
 */
@Slf4j
@Component
public class RallyChangeMessageStrategyImpl implements RealTimeMessageStrategy {

	@Resource
	private KafkaTemplate<String, Message<?>> kafkaTemplate;

	@Autowired
	private RoomCache roomCache;

	@Override
	public void handleMessage(Message<?> message, Session session, String roomId, long userId) {
		// 获取房间
		Room getRoom = roomCache.getRoom(roomId, true);
		if (getRoom == null) {
			log.error("收到集结点变化，但房间为空！");
			return;
		}
		// 改变集结点
		roomCache.setRallyPoint(roomId, (RallyPoint) message.getData());
		// 广播变化给用户
		kafkaTemplate.send(generateName(roomId), message);
		log.info("重设集结点！");
	}

}
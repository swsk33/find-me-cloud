package com.gitee.swsk33.findmesession.strategy.impl;

import com.gitee.swsk33.findmeentity.model.Message;
import com.gitee.swsk33.findmeentity.model.RallyPoint;
import com.gitee.swsk33.findmeentity.model.Room;
import com.gitee.swsk33.findmesession.cache.RoomCache;
import com.gitee.swsk33.findmesession.strategy.RealTimeMessageStrategy;
import com.gitee.swsk33.findmeutility.singleton.JacksonMapper;
import jakarta.annotation.Resource;
import jakarta.websocket.Session;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import static com.gitee.swsk33.findmeutility.util.KafkaNameGenerator.generateName;

/**
 * 集结点变化消息处理策略
 */
@Slf4j
@Component
public class RallyChangeMessageStrategy implements RealTimeMessageStrategy {

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
		// 反序列化消息体
		RallyPoint point = JacksonMapper.getMapper().convertValue(message.getData(), RallyPoint.class);
		roomCache.setRallyPoint(roomId, point);
		// 广播变化给用户
		kafkaTemplate.send(generateName(roomId), message);
		log.info("重设集结点！");
	}

}
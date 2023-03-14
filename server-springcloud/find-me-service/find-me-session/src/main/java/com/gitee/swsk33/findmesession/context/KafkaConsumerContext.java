package com.gitee.swsk33.findmesession.context;

import com.gitee.swsk33.findmeentity.model.Message;
import com.gitee.swsk33.findmesession.factory.KafkaDynamicConsumerFactory;
import jakarta.websocket.Session;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.*;

/**
 * Kafka消费者上下文
 */
@Slf4j
@Component
public class KafkaConsumerContext {

	/**
	 * 存放所有自己创建的Kafka消费者任务
	 * key: 用户id
	 * value: kafka消费者任务
	 */
	private final Map<Long, KafkaConsumer<String, Message<?>>> consumerMap = new ConcurrentHashMap<>();

	/**
	 * 存放所有定时任务的哈希表
	 * key: 用户id
	 * value: 定时任务对象，用于定时执行kafka消费者的消息消费任务
	 */
	private final Map<Long, ScheduledFuture<?>> scheduleMap = new ConcurrentHashMap<>();

	/**
	 * 任务调度器，用于定时任务
	 */
	private final ScheduledExecutorService executor = Executors.newScheduledThreadPool(24);

	@Autowired
	private KafkaDynamicConsumerFactory kafkaConsumerFactory;

	/**
	 * 创建一个Kafka消费者用于获取其他人共享的位置信息，并向房间内某个用户推送
	 *
	 * @param roomId  房间id
	 * @param userId  推送用户id
	 * @param session 用户websocket会话
	 */
	public void addConsumerTask(String roomId, long userId, Session session) throws Exception {
		// 创建消费者
		KafkaConsumer<String, Message<?>> consumer = kafkaConsumerFactory.createConsumer(roomId, userId);
		// 创建定时任务，每隔100ms拉取消息并推送给用户
		ScheduledFuture<?> future = executor.scheduleAtFixedRate(() -> {
			// 消费者拉取这个房间内共享的地理位置信息
			ConsumerRecords<String, Message<?>> records = consumer.poll(Duration.ofMillis(100));
			// 推送给用户
			for (ConsumerRecord<String, Message<?>> record : records) {
				session.getAsyncRemote().sendObject(record.value());
			}
		}, 0, 100, TimeUnit.MILLISECONDS);
		// 存入任务和消费者以便于后续管理
		consumerMap.put(userId, consumer);
		scheduleMap.put(userId, future);
		log.info("已创建用于向用户(id=" + userId + ")拉取并推送消息的消费者！");
	}

	/**
	 * 移除Kafka消费者定时任务并关闭消费者订阅
	 *
	 * @param userId 用户id
	 */
	public void removeConsumerTask(long userId) {
		if (!consumerMap.containsKey(userId)) {
			return;
		}
		// 取出对应的消费者与任务，并停止
		KafkaConsumer<String, Message<?>> consumer = consumerMap.get(userId);
		ScheduledFuture<?> future = scheduleMap.get(userId);
		consumer.close();
		future.cancel(true);
		// 移除列表中的消费者和任务
		consumerMap.remove(userId);
		scheduleMap.remove(userId);
		log.info("已移除用于向用户(id=" + userId + ")拉取并推送消息的消费者！");
	}

}
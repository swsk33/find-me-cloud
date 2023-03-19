package com.gitee.swsk33.findmesession.context;

import com.gitee.swsk33.findmeentity.model.Message;
import com.gitee.swsk33.findmesession.factory.KafkaDynamicConsumerFactory;
import com.gitee.swsk33.findmeutility.singleton.JacksonMapper;
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
	public synchronized void addConsumerTask(String roomId, long userId, Session session) throws Exception {
		// 创建消费者
		KafkaConsumer<String, Message<?>> consumer = kafkaConsumerFactory.createConsumer(roomId, userId);
		// 存入消费者以便于后续管理
		consumerMap.put(userId, consumer);
		// 创建定时任务，每隔100ms拉取消息并推送给用户
		ScheduledFuture<?> future = executor.scheduleAtFixedRate(() -> {
			// 每次执行之前检查订阅者是否已经被取消（不存在于订阅者列表中则说明并取消）
			// 由于Kafka消费者非线程安全，所以将取消订阅逻辑和拉取消息逻辑一起放定时器中，判断列表中是否存在消费者而确定是否取消订阅并关闭任务
			if (!consumerMap.containsKey(userId)) {
				// 取消订阅
				consumer.unsubscribe();
				consumer.close();
				// 关闭定时任务
				scheduleMap.remove(userId).cancel(true);
				log.info("已移除用于向用户(id=" + userId + ")拉取并推送消息的消费者！");
				return;
			}
			try {
				// 消费者拉取这个房间内共享的实时消息
				ConsumerRecords<String, Message<?>> records = consumer.poll(Duration.ofMillis(100));
				// 推送给用户
				for (ConsumerRecord<String, Message<?>> record : records) {
					Message<?> data = record.value();
					// 如果这个消息是自己产生的，则不在发送回给自己以节省流量
					if (data.getSenderId() == userId) {
						continue;
					}
					// 序列化消息并返回给用户
					session.getBasicRemote().sendText(JacksonMapper.getMapper().writeValueAsString(data));
				}
			} catch (Exception e) {
				log.error("Kafka消费者(用户id：" + userId + ")拉取消息或者序列化时发生错误！");
				e.printStackTrace();
			}
		}, 0, 100, TimeUnit.MILLISECONDS);
		// 存入任务以便于后续管理
		scheduleMap.put(userId, future);
		log.info("已创建用于向用户(id=" + userId + ")拉取并推送消息的消费者！");
	}

	/**
	 * 移除Kafka消费者定时任务并关闭消费者订阅
	 *
	 * @param userId 用户id
	 */
	public synchronized void removeConsumerTask(long userId) {
		if (!consumerMap.containsKey(userId)) {
			return;
		}
		// 移除对应的消费者
		consumerMap.remove(userId);
	}

}
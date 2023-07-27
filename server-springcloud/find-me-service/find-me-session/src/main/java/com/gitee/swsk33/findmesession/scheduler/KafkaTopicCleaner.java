package com.gitee.swsk33.findmesession.scheduler;

import cn.hutool.core.util.StrUtil;
import com.gitee.swsk33.findmeentity.model.Room;
import com.gitee.swsk33.findmesession.cache.RoomCache;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.common.KafkaFuture;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static com.gitee.swsk33.findmeentity.param.RedissonLockName.KAFKA_TOPIC_CLEAN;
import static com.gitee.swsk33.findmeutility.util.KafkaNameGenerator.recoverName;

/**
 * 定时自动清理Kafka无用的Topic的调度器
 */
@Slf4j
@Component
public class KafkaTopicCleaner {

	@Autowired
	private AdminClient adminClient;

	@Autowired
	private RoomCache roomCache;

	@Autowired
	private RedissonClient redissonClient;

	/**
	 * 定期清理不存在的房间的Kafka Topic
	 */
	@Scheduled(fixedDelay = 30, timeUnit = TimeUnit.MINUTES)
	private void cleanKafkaTopic() {
		// 分布式锁加锁
		RLock lock = redissonClient.getLock(KAFKA_TOPIC_CLEAN);
		lock.lock();
		try {
			// 存放无用的Kafka的Topic的列表
			List<String> notUsedTopicList = new ArrayList<>();
			// 列出所有的Topic
			KafkaFuture<Set<String>> kafkaTopicResult = adminClient.listTopics().names();
			Set<String> allTopics = kafkaTopicResult.get();
			// 遍历检查哪些Topic对应的房间已经不存在了
			for (String eachTopic : allTopics) {
				String roomId = recoverName(eachTopic);
				if (StrUtil.isEmpty(roomId)) {
					continue;
				}
				// 去Redis查找这个房间，若房间不存在则将当前Topic加入删除列表
				Room getRoom = roomCache.getRoom(roomId, true);
				if (getRoom == null) {
					notUsedTopicList.add(eachTopic);
				}
			}
			// 删除无用的Topic
			if (!notUsedTopicList.isEmpty()) {
				adminClient.deleteTopics(notUsedTopicList);
				log.info("已清理{}个Kafka Topic！", notUsedTopicList.size());
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// 释放分布式锁
			lock.unlock();
		}
	}

}
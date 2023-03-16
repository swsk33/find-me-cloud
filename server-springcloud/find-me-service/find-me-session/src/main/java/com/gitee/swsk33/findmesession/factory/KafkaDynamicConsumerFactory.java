package com.gitee.swsk33.findmesession.factory;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Properties;

import static com.gitee.swsk33.findmeutility.util.KafkaNameGenerator.generateName;

@Component
public class KafkaDynamicConsumerFactory {

	@Autowired
	private KafkaProperties kafkaProperties;

	@Value("${spring.kafka.consumer.key-deserializer}")
	private String keyDeSerializerClassName;

	@Value("${spring.kafka.consumer.value-deserializer}")
	private String valueDeSerializerClassName;

	/**
	 * 创建一个Kafka消费者用于向房间内用户广播消息
	 *
	 * @param roomId 房间id消费者订阅的话题
	 * @param userId 用户id作为消费者组名
	 * @return 消费者对象
	 */
	public <K, V> KafkaConsumer<K, V> createConsumer(String roomId, long userId) throws ClassNotFoundException {
		Properties consumerProperties = new Properties();
		// 设定一些关于新的消费者的配置信息
		consumerProperties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProperties.getBootstrapServers());
		// 设定新的消费者的组名
		consumerProperties.put(ConsumerConfig.GROUP_ID_CONFIG, generateName(String.valueOf(userId)));
		// 设定反序列化方式
		consumerProperties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, Class.forName(keyDeSerializerClassName));
		consumerProperties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, Class.forName(valueDeSerializerClassName));
		// 信任所有类以反序列化
		consumerProperties.put("spring.json.trusted.packages", "*");
		// 新建一个消费者
		KafkaConsumer<K, V> consumer = new KafkaConsumer<>(consumerProperties);
		// 使这个消费者订阅对应话题
		consumer.subscribe(Collections.singleton(generateName(roomId)));
		return consumer;
	}

}
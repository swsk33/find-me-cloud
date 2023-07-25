package com.gitee.swsk33.findmebeanconfig.autoconfigure;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

/**
 * Kafka管理器配置
 */
@Slf4j
@Configuration
@ConditionalOnClass(AdminClient.class)
public class KafkaAdminAutoConfig {

	/**
	 * 读取kafka地址配置
	 */
	@Value("${spring.kafka.bootstrap-servers}")
	private String kafkaServerURL;

	/**
	 * 注入一个kafka管理实例
	 *
	 * @return kafka管理对象
	 */
	@Bean
	public AdminClient adminClient() {
		Properties properties = new Properties();
		properties.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaServerURL);
		log.info("Kafka管理器对象已完成自动配置！");
		return AdminClient.create(properties);
	}

}
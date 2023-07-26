package com.gitee.swsk33.findmeentity.param;

/**
 * 存放分布式锁的名称常量
 */
public class RedissonLockName {

	/**
	 * 房间变化
	 */
	public static final String ROOM_CHANGE = "room_change";

	/**
	 * 房间模板发生变化
	 */
	public static final String ROOM_TEMPLATE_CHANGE = "room_template_change";

	/**
	 * 使用房间模板创建房间
	 */
	public static final String TEMPLATE_ROOM_CREATE = "template_room_create";

	/**
	 * Kafka主题清理
	 */
	public static final String KAFKA_TOPIC_CLEAN = "kafka_topic_clean";

}
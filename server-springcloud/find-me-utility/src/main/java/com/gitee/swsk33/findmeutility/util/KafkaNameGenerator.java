package com.gitee.swsk33.findmeutility.util;

/**
 * Kafka名称（groupId或者topic名字）生成器
 * 实质上就是在名字前面加上业务名前缀方式和其它混淆
 */
public class KafkaNameGenerator {

	/**
	 * 统一前缀
	 */
	private static final String PREFIX = "find-me-";

	/**
	 * 生成topic名或者groupId
	 *
	 * @param name 传入名
	 * @return 加上前缀的名字
	 */
	public static String generateName(String name) {
		return PREFIX + name;
	}

	/**
	 * 传入一个已经加上了前缀的topic名或者groupId，恢复为去除前缀后的名字
	 *
	 * @param kafkaName 已加上前缀的名字
	 * @return 去掉前缀后的名字，如果不是以该前缀开头，返回null
	 */
	public static String recoverName(String kafkaName) {
		if (!kafkaName.startsWith(PREFIX)) {
			return null;
		}
		return kafkaName.substring(PREFIX.length());
	}

}
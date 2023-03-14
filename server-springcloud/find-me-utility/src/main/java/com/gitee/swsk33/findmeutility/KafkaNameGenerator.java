package com.gitee.swsk33.findmeutility;

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

}
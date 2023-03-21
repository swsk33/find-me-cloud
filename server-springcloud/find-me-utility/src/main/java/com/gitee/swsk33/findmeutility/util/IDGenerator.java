package com.gitee.swsk33.findmeutility.util;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.RandomUtil;

/**
 * ID生成器
 */
public class IDGenerator {

	/**
	 * 生成一个雪花id
	 *
	 * @return 生成的雪花id
	 */
	public static long generateSnowflakeId() {
		return IdUtil.getSnowflakeNextId();
	}

	/**
	 * 生成一个UUID
	 *
	 * @return 生成的UUID
	 */
	public static String generateUUID() {
		return IdUtil.simpleUUID();
	}

	/**
	 * 生成一个房间ID
	 *
	 * @return 房间ID，长度为10的字符串，小写
	 */
	public static String generateRoomID() {
		return RandomUtil.randomString(10).toLowerCase();
	}

}
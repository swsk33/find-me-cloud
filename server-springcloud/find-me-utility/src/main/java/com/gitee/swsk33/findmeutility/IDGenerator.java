package com.gitee.swsk33.findmeutility;

import cn.hutool.core.util.IdUtil;

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

}
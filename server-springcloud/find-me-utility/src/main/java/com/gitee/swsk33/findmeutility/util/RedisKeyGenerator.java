package com.gitee.swsk33.findmeutility.util;

/**
 * Redis键生成器
 */
public class RedisKeyGenerator {

	/**
	 * 前缀
	 */
	private static final String PREFIX = "find-me:";

	/**
	 * 生成用于存放会话房间的键
	 * <ul>
	 *     <li>键：room_房间id</li>
	 *     <li>值：房间对象</li>
	 * </ul>
	 *
	 * @param roomId 房间id
	 * @return 键
	 */
	public static String generateRoomKey(String roomId) {
		return PREFIX + "session-room:" + roomId;
	}

}
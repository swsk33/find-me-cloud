package com.gitee.swsk33.findmeutility.util;

import com.gitee.swsk33.findmeentity.param.EmailType;

/**
 * Redis键生成器
 */
public class RedisKeyGenerator {

	/**
	 * 生成用于在Redis存放验证码的键
	 * 在生成邮件验证码时，可能不同的验证码用于不同的服务<br>
	 * 因此这里约定，在Redis中存放的邮件验证码形式如下：
	 * <ul>
	 *     <li>键：email_服务类型_对应用户邮箱</li>
	 *     <li>值：验证码数字</li>
	 * </ul>
	 *
	 * @param email 这个验证码服务对应的用户邮箱
	 * @param type  邮件服务类型
	 * @return 键
	 */
	public static String generateMailCodeKey(String email, EmailType type) {
		return "email_" + type.toString() + "_" + email;
	}

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
		return "sessionRoom_" + roomId;
	}

}
package com.gitee.swsk33.findmeutility.util;

import cn.hutool.crypto.digest.BCrypt;
import cn.hutool.crypto.digest.DigestUtil;

/**
 * BCrypt实用工具
 */
public class BCryptUtils {

	/**
	 * BCrypt加密
	 *
	 * @param origin 原字符串
	 * @return 加密后字符串
	 */
	public static String encode(String origin) {
		return DigestUtil.bcrypt(origin);
	}

	/**
	 * 和加密后密码对比
	 *
	 * @param plaintext 原密码
	 * @param encoded   加密后密码
	 * @return 是否匹配
	 */
	public static boolean match(String plaintext, String encoded) {
		return BCrypt.checkpw(plaintext, encoded);
	}

}
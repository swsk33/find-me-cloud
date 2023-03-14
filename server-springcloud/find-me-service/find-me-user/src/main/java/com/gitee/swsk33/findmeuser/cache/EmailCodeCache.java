package com.gitee.swsk33.findmeuser.cache;

import com.gitee.swsk33.findmeentity.annotation.RedisCache;
import com.gitee.swsk33.findmeentity.param.EmailType;

/**
 * 邮件验证码缓存操作
 */
@RedisCache
public interface EmailCodeCache {

	/**
	 * 生成验证码并存到Redis中，有效期5分钟
	 *
	 * @param email 验证码服务对应的用户邮箱
	 * @param type  验证码服务类型
	 * @return 验证码
	 */
	int generateCodeToCache(String email, EmailType type);

	/**
	 * 检查验证码，若检验正确则删除缓存中的验证码
	 *
	 * @param email 验证码服务对应的用户邮箱
	 * @param type  验证码服务类型
	 * @param code  传入验证码
	 * @return 是否正确
	 */
	boolean checkCodeInCache(String email, EmailType type, int code);

}
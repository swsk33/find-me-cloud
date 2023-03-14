package com.gitee.swsk33.findmeuser.cache.impl;

import com.gitee.swsk33.findmeentity.param.EmailType;
import com.gitee.swsk33.findmeuser.cache.EmailCodeCache;
import com.gitee.swsk33.findmeutility.EmailCodeKeyUtils;
import jakarta.annotation.Resource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class EmailCodeCacheImpl implements EmailCodeCache {

	@Resource
	private RedisTemplate<String, Integer> redisTemplate;

	/**
	 * 生成随机6位数验证码
	 *
	 * @return 验证码
	 */
	private int generateCode() {
		return (int) ((Math.random() * 9 + 1) * 100000);
	}

	@Override
	public int generateCodeToCache(String email, EmailType type) {
		int code = generateCode();
		// 存入Redis并设定五分钟过期
		redisTemplate.opsForValue().set(EmailCodeKeyUtils.generateMailCodeKey(email, type), code, 5, TimeUnit.MINUTES);
		return code;
	}

	@Override
	public boolean checkCodeInCache(String email, EmailType type, int code) {
		String key = EmailCodeKeyUtils.generateMailCodeKey(email, type);
		Integer getCode = redisTemplate.opsForValue().get(key);
		if (getCode != null && getCode == code) {
			redisTemplate.delete(key);
			return true;
		}
		return false;
	}

}
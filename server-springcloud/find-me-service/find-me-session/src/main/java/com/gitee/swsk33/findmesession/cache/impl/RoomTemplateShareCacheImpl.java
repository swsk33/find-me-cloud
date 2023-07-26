package com.gitee.swsk33.findmesession.cache.impl;

import com.gitee.swsk33.findmeentity.model.RoomTemplateShare;
import com.gitee.swsk33.findmesession.cache.RoomTemplateShareCache;
import jakarta.annotation.Resource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

import static com.gitee.swsk33.findmeutility.util.RedisKeyGenerator.generateRoomTemplateShareKey;

@Component
public class RoomTemplateShareCacheImpl implements RoomTemplateShareCache {

	@Resource
	private RedisTemplate<String, RoomTemplateShare> redisTemplate;

	@Override
	public void addShare(RoomTemplateShare roomTemplateShare) {
		redisTemplate.opsForValue().set(generateRoomTemplateShareKey(roomTemplateShare.getId()), roomTemplateShare, 1, TimeUnit.DAYS);
	}

	@Override
	public RoomTemplateShare getShare(String id) {
		return redisTemplate.opsForValue().get(generateRoomTemplateShareKey(id));
	}

}
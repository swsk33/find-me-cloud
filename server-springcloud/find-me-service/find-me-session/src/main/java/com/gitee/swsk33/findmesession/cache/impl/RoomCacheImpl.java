package com.gitee.swsk33.findmesession.cache.impl;

import com.gitee.swsk33.findmeentity.model.Room;
import com.gitee.swsk33.findmesession.cache.RoomCache;
import jakarta.annotation.Resource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

import static com.gitee.swsk33.findmeutility.RedisKeyGenerator.generateRoomKey;

@Component
public class RoomCacheImpl implements RoomCache {

	@Resource
	private RedisTemplate<String, Room> redisTemplate;

	@Override
	public void addOrSetRoom(Room room) {
		redisTemplate.opsForValue().set(generateRoomKey(room.getId()), room);
	}

	@Override
	public void setRoomExpire(String roomId) {
		redisTemplate.expire(generateRoomKey(roomId), 5, TimeUnit.MINUTES);
	}

	@Override
	public void cancelRoomExpire(String roomId) {
		redisTemplate.persist(generateRoomKey(roomId));
	}

}
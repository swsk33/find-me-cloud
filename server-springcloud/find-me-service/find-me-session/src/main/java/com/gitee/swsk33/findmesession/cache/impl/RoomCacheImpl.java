package com.gitee.swsk33.findmesession.cache.impl;

import com.gitee.swsk33.findmeentity.dataobject.User;
import com.gitee.swsk33.findmeentity.model.RallyPoint;
import com.gitee.swsk33.findmeentity.model.Room;
import com.gitee.swsk33.findmeentity.param.CommonValue;
import com.gitee.swsk33.findmesession.cache.RoomCache;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

import static com.gitee.swsk33.findmeutility.util.RedisKeyGenerator.generateRoomKey;

@Slf4j
@Component
public class RoomCacheImpl implements RoomCache {

	@Resource
	private RedisTemplate<String, Room> redisTemplate;

	@Autowired
	private RedissonClient redissonClient;

	@Override
	public void addOrSetRoom(Room room) {
		redisTemplate.opsForValue().set(generateRoomKey(room.getId()), room);
	}

	@Override
	public void setRoomExpire(String roomId) {
		redisTemplate.expire(generateRoomKey(roomId), 5, TimeUnit.MINUTES);
		log.warn("由于房间(id:" + roomId + ")现在没有用户，将于5分钟后过期！");
	}

	@Override
	public void cancelRoomExpire(String roomId) {
		redisTemplate.persist(generateRoomKey(roomId));
		log.info("已取消房间(id:" + roomId + ")的过期时间！");
	}

	@Override
	public void addUserToRoom(String roomId, User user) {
		Room getRoom = getRoom(roomId, false);
		if (getRoom == null) {
			log.error("待操作房间不存在！");
		}
		// 分布式锁上锁
		RLock roomLock = redissonClient.getLock(CommonValue.LockName.ROOM_CHANGE);
		roomLock.lock();
		try {
			// 用户加入房间
			getRoom.getUsers().put(user.getId(), user);
			// 对于有人的房间取消其过期
			if (getRoom.getUsers().size() > 0) {
				cancelRoomExpire(roomId);
			}
			// 存入Redis
			addOrSetRoom(getRoom);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			roomLock.unlock();
		}
	}

	@Override
	public User removeUserFromRoom(String roomId, long userId) {
		Room getRoom = getRoom(roomId, false);
		if (getRoom == null) {
			log.error("待操作房间不存在！");
			return null;
		}
		User removedUser = null;
		// 分布式锁上锁
		RLock roomLock = redissonClient.getLock(CommonValue.LockName.ROOM_CHANGE);
		roomLock.lock();
		try {
			// 从房间移除用户
			removedUser = getRoom.getUsers().remove(userId);
			// 若房间为空，则使其5分钟后过期
			if (getRoom.getUsers().size() == 0) {
				setRoomExpire(roomId);
			}
			// 存入Redis
			addOrSetRoom(getRoom);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			roomLock.unlock();
		}
		return removedUser;
	}

	@Override
	public void setRallyPoint(String roomId, RallyPoint rallyPoint) {
		Room getRoom = getRoom(roomId, false);
		// 分布式锁上锁
		RLock roomLock = redissonClient.getLock(CommonValue.LockName.ROOM_CHANGE);
		roomLock.lock();
		try {
			getRoom.setRally(rallyPoint);
			addOrSetRoom(getRoom);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			roomLock.unlock();
		}
	}

	@Override
	public Room getRoom(String roomId, boolean blockPassword) {
		Room room = redisTemplate.opsForValue().get(generateRoomKey(roomId));
		if (room != null && blockPassword) {
			room.setPassword(null);
		}
		return room;
	}

}
package com.gitee.swsk33.findmesession.cache;

import com.gitee.swsk33.findmeentity.annotation.RedisCache;
import com.gitee.swsk33.findmeentity.model.Room;

/**
 * 操作房间的Redis缓存
 */
@RedisCache
public interface RoomCache {

	/**
	 * 添加一个房间对象，如果该房间已存在则以传入值覆盖修改
	 *
	 * @param room 房间对象
	 */
	void addOrSetRoom(Room room);

	/**
	 * 设定一个房间5分钟后过期
	 *
	 * @param roomId 房间id
	 */
	void setRoomExpire(String roomId);

	/**
	 * 取消房间过期时间
	 *
	 * @param roomId 房间id
	 */
	void cancelRoomExpire(String roomId);

}
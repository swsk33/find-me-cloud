package com.gitee.swsk33.findmesession.cache;

import com.gitee.swsk33.findmeentity.annotation.RedisCache;
import com.gitee.swsk33.findmeentity.dataobject.User;
import com.gitee.swsk33.findmeentity.model.RallyPoint;
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

	/**
	 * 向房间中添加用户
	 *
	 * @param roomId 房间id
	 * @param user   用户对象
	 */
	void addUserToRoom(String roomId, User user);

	/**
	 * 从房间移除用户
	 *
	 * @param roomId 房间id
	 * @param userId 用户id
	 * @return 被移除的用户对象
	 */
	User removeUserFromRoom(String roomId, long userId);

	/**
	 * 设定或者修改房间集结点
	 *
	 * @param roomId     房间id
	 * @param rallyPoint 集结点对象
	 */
	void setRallyPoint(String roomId, RallyPoint rallyPoint);

	/**
	 * 获取房间对象
	 *
	 * @param roomId        房间id
	 * @param blockPassword 屏蔽密码，为true时，取得房间对象后会删掉密码字段信息
	 * @return 房间对象
	 */
	Room getRoom(String roomId, boolean blockPassword);

}
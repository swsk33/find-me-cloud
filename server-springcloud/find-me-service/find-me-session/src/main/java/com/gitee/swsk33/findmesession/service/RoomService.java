package com.gitee.swsk33.findmesession.service;

import com.gitee.swsk33.findmeentity.model.Result;
import com.gitee.swsk33.findmeentity.model.Room;
import org.springframework.stereotype.Service;

/**
 * 房间服务
 */
@Service
public interface RoomService {

	/**
	 * 创建房间
	 *
	 * @param room 房间对象
	 * @return 结果
	 */
	Result<Room> createRoom(Room room);

	/**
	 * 根据id获取房间对象
	 *
	 * @param id 房间id
	 * @return 房间对象
	 */
	Result<Room> getRoom(String id);

	/**
	 * 用户加入至现有房间（普通房间认证以及用户加入房间等一系列操作的逻辑）
	 *
	 * @param roomId   房间id
	 * @param password 房间密码
	 * @param userId   用户id
	 * @return 结果
	 */
	Result<Void> userJoinRoom(String roomId, String password, long userId);

	/**
	 * 把一个用户从现有房间移除
	 *
	 * @param roomId 房间id
	 * @param userId 用户id
	 * @return 结果
	 */
	Result<Void> removeUserFromRoom(String roomId, long userId);

}
package com.gitee.swsk33.findmesession.service;

import com.gitee.swsk33.findmeentity.model.Result;
import com.gitee.swsk33.findmeentity.model.Room;
import jakarta.websocket.Session;
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
	 * 用户加入房间之前的认证
	 *
	 * @param roomId   房间id
	 * @param password 房间密码
	 * @param userId   用户id
	 * @return 结果
	 */
	Result<Void> auth(String roomId, String password, long userId);

	/**
	 * 用户认证后，加入至现有的房间
	 *
	 * @param roomId  房间id
	 * @param userId  用户id
	 * @param session 用户对应的session会话对象
	 * @return 结果
	 */
	Result<Void> userJoinRoom(String roomId, long userId, Session session);

	/**
	 * 用户从现有的房间退出
	 *
	 * @param roomId 房间id
	 * @param userId 用户id
	 * @return 结果
	 */
	Result<Void> userExitRoom(String roomId, long userId);

}
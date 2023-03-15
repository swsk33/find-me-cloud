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

}
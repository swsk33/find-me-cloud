package com.gitee.swsk33.findmesession.service.impl;

import com.gitee.swsk33.findmeentity.factory.ResultFactory;
import com.gitee.swsk33.findmeentity.model.Result;
import com.gitee.swsk33.findmeentity.model.Room;
import com.gitee.swsk33.findmesession.cache.RoomCache;
import com.gitee.swsk33.findmesession.service.RoomService;
import com.gitee.swsk33.findmeutility.BCryptUtils;
import com.gitee.swsk33.findmeutility.IDGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class RoomServiceImpl implements RoomService {

	@Autowired
	private RoomCache roomCache;

	@Override
	public Result<Room> createRoom(Room room) {
		// 设定id
		room.setId(IDGenerator.generateUUID());
		// 加密密码
		room.setPassword(BCryptUtils.encode(room.getPassword()));
		// 清空非必要数据
		room.setRally(null);
		room.setUsers(new ArrayList<>());
		// 储存房间
		roomCache.addOrSetRoom(room);
		// 新创建时无人加入，设定5分钟后过期
		roomCache.setRoomExpire(room.getId());
		return ResultFactory.createSuccessResult("创建房间完成！", room);
	}

}
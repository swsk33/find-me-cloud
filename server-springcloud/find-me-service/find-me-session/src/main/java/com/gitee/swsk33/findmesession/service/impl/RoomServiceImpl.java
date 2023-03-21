package com.gitee.swsk33.findmesession.service.impl;

import com.gitee.swsk33.findmeentity.factory.ResultFactory;
import com.gitee.swsk33.findmeentity.model.Result;
import com.gitee.swsk33.findmeentity.model.Room;
import com.gitee.swsk33.findmesession.cache.RoomCache;
import com.gitee.swsk33.findmesession.service.RoomService;
import com.gitee.swsk33.findmeutility.util.BCryptUtils;
import com.gitee.swsk33.findmeutility.util.IDGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

@Component
public class RoomServiceImpl implements RoomService {

	@Autowired
	private RoomCache roomCache;

	@Override
	public Result<Room> createRoom(Room room) {
		// 生成id
		String roomId = IDGenerator.generateRoomID();
		// 检查这个id是否重复（检查1000次）
		Room tryGetRoom = roomCache.getRoom(roomId, false);
		boolean idRepeat = true;
		for (int i = 0; i < 1000; i++) {
			if (tryGetRoom == null) {
				idRepeat = false;
				break;
			}
		}
		if (idRepeat) {
			return ResultFactory.createFailedResult("服务器资源不足！请稍后再试！");
		}
		// 设定id
		room.setId(roomId);
		// 加密密码
		room.setPassword(BCryptUtils.encode(room.getPassword()));
		// 清空非必要数据和初始化
		room.setRally(null);
		room.setUsers(new ConcurrentHashMap<>());
		// 储存房间
		roomCache.addOrSetRoom(room);
		// 新创建时无人加入，设定5分钟后过期
		roomCache.setRoomExpire(room.getId());
		// 返回之前，屏蔽掉密码
		room.setPassword(null);
		return ResultFactory.createSuccessResult("创建房间完成！", room);
	}

	@Override
	public Result<Room> getRoom(String id) {
		Room getRoom = roomCache.getRoom(id, true);
		if (getRoom == null) {
			return ResultFactory.createFailedResult("房间id不存在！");
		}
		return ResultFactory.createSuccessResult("获取房间成功！", getRoom);
	}

}
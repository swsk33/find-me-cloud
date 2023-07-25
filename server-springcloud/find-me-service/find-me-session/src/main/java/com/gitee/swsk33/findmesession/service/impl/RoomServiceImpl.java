package com.gitee.swsk33.findmesession.service.impl;

import com.gitee.swsk33.findmeentity.dataobject.User;
import com.gitee.swsk33.findmeentity.factory.MessageFactory;
import com.gitee.swsk33.findmeentity.factory.ResultFactory;
import com.gitee.swsk33.findmeentity.model.Message;
import com.gitee.swsk33.findmeentity.model.Result;
import com.gitee.swsk33.findmeentity.model.Room;
import com.gitee.swsk33.findmeentity.param.MessageType;
import com.gitee.swsk33.findmefeign.feignclient.UserClient;
import com.gitee.swsk33.findmesession.cache.RoomCache;
import com.gitee.swsk33.findmesession.context.KafkaConsumerContext;
import com.gitee.swsk33.findmesession.service.RoomService;
import com.gitee.swsk33.findmesession.websocket.RoomSessionAPI;
import com.gitee.swsk33.findmeutility.util.BCryptUtils;
import com.gitee.swsk33.findmeutility.util.IDGenerator;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

import static com.gitee.swsk33.findmeutility.util.KafkaNameGenerator.generateName;

@Slf4j
@Component
public class RoomServiceImpl implements RoomService {

	@Autowired
	private RoomCache roomCache;

	@Autowired
	private UserClient userClient;

	@Resource
	private KafkaTemplate<String, Message<?>> kafkaTemplate;

	@Autowired
	private KafkaConsumerContext kafkaConsumerContext;

	@Override
	public Result<Room> createRoom(Room room) {
		// 生成id，使用UUID
		String roomId = IDGenerator.generateUUID();
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

	@Override
	public Result<Void> userJoinRoom(String roomId, String password, long userId) {
		// 首先判断房间是否存在
		Room getRoom = roomCache.getRoom(roomId, false);
		if (getRoom == null) {
			return ResultFactory.createFailedResult("房间不存在！");
		}
		// 然后判断对应用户是否登录
		// 若用户不存在，也会返回失败
		Result<User> getUser = userClient.isLoginById(userId);
		if (!getUser.isSuccess()) {
			return ResultFactory.createFailedResult("用户未登录或者不存在！");
		}
		// 检测用户是否已经在房间
		if (getRoom.getUsers().containsKey(userId)) {
			return ResultFactory.createFailedResult("用户已加入房间！");
		}
		// 比对密码
		if (!BCryptUtils.match(password, getRoom.getPassword())) {
			return ResultFactory.createFailedResult("房间密码错误！");
		}
		// 加入成功，将用户加入房间，并发送广播
		RoomSessionAPI.removeAutoExpireSession(userId);
		log.info("用户id：" + userId + "的会话认证通过！已被确定为持久会话！");
		// 把用户加入房间
		roomCache.addUserToRoom(roomId, getUser.getData());
		// 广播消息-房间改变，用户加入
		kafkaTemplate.send(generateName(roomId), MessageFactory.createMessage(MessageType.ROOM_CHANGED, userId, roomCache.getRoom(roomId, true)));
		kafkaTemplate.send(generateName(roomId), MessageFactory.createMessage(MessageType.USER_JOIN, userId, getUser.getData()));
		log.info("用户：" + getUser.getData().getNickname() + "成功加入房间！");
		return ResultFactory.createVoidSuccessResult("加入房间成功！");
	}

	@Override
	public Result<Void> removeUserFromRoom(String roomId, long userId) {
		// 关闭用户对应的kafka消费者及其任务
		kafkaConsumerContext.removeConsumerTask(userId);
		// 从房间移除用户
		User removedUser = roomCache.removeUserFromRoom(roomId, userId);
		Room getRoom = roomCache.getRoom(roomId, true);
		// 如果被移除的用户为null，说明这个用户本来就没加入房间，不进行通知
		// 如果房间还有人，则发送房间变化和用户退出通知
		if (removedUser != null && getRoom.getUsers().size() > 0) {
			kafkaTemplate.send(generateName(roomId), MessageFactory.createMessage(MessageType.ROOM_CHANGED, getRoom));
			kafkaTemplate.send(generateName(roomId), MessageFactory.createMessage(MessageType.USER_EXIT, userId, removedUser));
		}
		return ResultFactory.createVoidSuccessResult("用户已退出房间！");
	}

}
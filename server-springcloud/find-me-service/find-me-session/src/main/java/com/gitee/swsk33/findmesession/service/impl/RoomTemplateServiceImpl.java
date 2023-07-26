package com.gitee.swsk33.findmesession.service.impl;

import com.gitee.swsk33.findmeentity.dataobject.RoomTemplate;
import com.gitee.swsk33.findmeentity.dataobject.User;
import com.gitee.swsk33.findmeentity.factory.ResultFactory;
import com.gitee.swsk33.findmeentity.model.Result;
import com.gitee.swsk33.findmeentity.model.Room;
import com.gitee.swsk33.findmefeign.feignclient.UserClient;
import com.gitee.swsk33.findmesession.cache.RoomCache;
import com.gitee.swsk33.findmesession.mongodao.RoomTemplateDAO;
import com.gitee.swsk33.findmesession.service.RoomService;
import com.gitee.swsk33.findmesession.service.RoomTemplateService;
import jakarta.websocket.Session;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import static com.gitee.swsk33.findmeentity.param.RedissonLockName.TEMPLATE_ROOM_CREATE;

@Slf4j
@Component
public class RoomTemplateServiceImpl implements RoomTemplateService {

	@Autowired
	private RoomCache roomCache;

	@Autowired
	private RoomTemplateDAO roomTemplateDAO;

	@Autowired
	private RedissonClient redissonClient;

	@Autowired
	private UserClient userClient;

	@Autowired
	private RoomService roomService;

	@Override
	public Result<Void> createTemplate(RoomTemplate roomTemplate) {
		// 判断当前请求用户是否登录
		Result<User> loginResult = userClient.isLogin();
		if (!loginResult.isSuccess()) {
			return ResultFactory.createFailedResult(loginResult.getMessage());
		}
		// 获取用户对象
		User sessionUser = loginResult.getData();
		// 填补其它字段
		roomTemplate.setMasterId(sessionUser.getId());
		roomTemplate.setUserIdList(new ArrayList<>());
		// 把自己加入到拥有者列表
		roomTemplate.getUserIdList().add(sessionUser.getId());
		roomTemplateDAO.add(roomTemplate);
		return ResultFactory.createVoidSuccessResult("创建房间模板成功！");
	}

	@Override
	public Result<Void> deleteTemplate(String id) {
		// 判断当前请求用户是否登录
		Result<User> loginResult = userClient.isLogin();
		if (!loginResult.isSuccess()) {
			return ResultFactory.createFailedResult(loginResult.getMessage());
		}
		// 获取房间模板
		RoomTemplate getTemplate = roomTemplateDAO.getById(id);
		if (getTemplate == null) {
			return ResultFactory.createFailedResult("该房间模板不存在！");
		}
		// 检查当前用户是否是房间模板的创建者
		if (getTemplate.getMasterId() != loginResult.getData().getId()) {
			return ResultFactory.createFailedResult("不是该模板的创建者，不能删除该房间模板！");
		}
		// 执行删除
		roomTemplateDAO.delete(id);
		return ResultFactory.createVoidSuccessResult("删除完成！");
	}

	@Override
	public Result<Void> authAndJoinThroughTemplate(String templateId, long userId, Session session) {
		// 首先获取房间模板
		RoomTemplate getTemplate = roomTemplateDAO.getById(templateId);
		if (getTemplate == null) {
			return ResultFactory.createFailedResult("该房间模板不存在！");
		}
		// 然后判断对应用户是否登录
		// 若用户不存在，也会返回失败
		Result<User> getUser = userClient.isLoginById(userId);
		if (!getUser.isSuccess()) {
			return ResultFactory.createFailedResult("用户未登录或者不存在！");
		}
		// 认证：查看要加入的用户id是否存在于模板列表
		if (!getTemplate.getUserIdList().contains(userId)) {
			return ResultFactory.createFailedResult("用户不是该模板的拥有者！拒绝加入！");
		}
		// 到此，认证成功
		// 查看是否存在id为该模板id的普通房间
		Room getRoom = roomCache.getRoom(templateId, true);
		// 如果房间不存在，则执行创建房间操作
		if (getRoom == null) {
			log.warn("模板对应房间不存在！将进行创建...");
			// 上锁
			RLock lock = redissonClient.getLock(TEMPLATE_ROOM_CREATE);
			lock.lock();
			try {
				getRoom = new Room();
				getRoom.setId(templateId);
				getRoom.setName(getTemplate.getName());
				getRoom.setUsers(new ConcurrentHashMap<>());
				roomCache.addOrSetRoom(getRoom);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				lock.unlock();
			}
		}
		// 房间存在，检测用户是否在房间
		if (getRoom.getUsers().containsKey(userId)) {
			return ResultFactory.createFailedResult("用户已加入房间！");
		}
		// 最后把用户加入房间
		roomService.userJoinRoom(getRoom.getId(), userId, session);
		return ResultFactory.createVoidSuccessResult("加入模板房间成功！");
	}

	@Override
	public Result<Void> removeLoginUserFromTemplate(String templateId) {
		// 获取房间模板
		RoomTemplate getTemplate = roomTemplateDAO.getById(templateId);
		if (getTemplate == null) {
			return ResultFactory.createFailedResult("该房间模板不存在！");
		}
		// 从登录session中获取用户信息
		Result<User> userResult = userClient.isLogin();
		if (!userResult.isSuccess()) {
			return ResultFactory.createFailedResult("用户未登录！");
		}
		roomTemplateDAO.removeUserFromTemplate(templateId, userResult.getData().getId());
		return ResultFactory.createVoidSuccessResult("从模板移除用户完成！");
	}

	@Override
	public Result<List<RoomTemplate>> getTemplateByUser(long userId) {
		return ResultFactory.createSuccessResult("查询完成！", roomTemplateDAO.getByUser(userId));
	}

}
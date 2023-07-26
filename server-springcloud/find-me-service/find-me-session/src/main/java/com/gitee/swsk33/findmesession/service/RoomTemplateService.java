package com.gitee.swsk33.findmesession.service;

import com.gitee.swsk33.findmeentity.dataobject.RoomTemplate;
import com.gitee.swsk33.findmeentity.model.Result;
import jakarta.websocket.Session;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 房间模板服务
 */
@Service
public interface RoomTemplateService {

	/**
	 * 创建一个房间模板
	 *
	 * @param roomTemplate 房间模板对象
	 * @return 结果
	 */
	Result<Void> createTemplate(RoomTemplate roomTemplate);

	/**
	 * 删除一个房间模板
	 *
	 * @param id 模板id
	 * @return 结果
	 */
	Result<Void> deleteTemplate(String id);

	/**
	 * 认证完成后，通过房间模板加入一个房间<br>
	 *
	 * @param templateId 模板id
	 * @param userId     用户id
	 * @param session    用户对应的session会话对象
	 * @return 结果
	 */
	Result<Void> authAndJoinThroughTemplate(String templateId, long userId, Session session);

	/**
	 * 把当前登录的用户从指定的模板的拥有者列表中移除（如果当前登录的用户存在于指定模板的拥有者列表的话）
	 *
	 * @param templateId 模板id
	 * @return 结果
	 */
	Result<Void> removeLoginUserFromTemplate(String templateId);

	/**
	 * 获取一个用户所拥有的模板
	 *
	 * @param userId 用户id
	 * @return 结果
	 */
	Result<List<RoomTemplate>> getTemplateByUser(long userId);

}
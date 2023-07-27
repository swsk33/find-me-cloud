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
	 * 删除一个房间模板<br>
	 * 分为两种情况：<br>
	 * <ul>
	 *     <li>如果是模板创建者调用该接口，则直接从数据库删除该模板</li>
	 *     <li>如果是拥有该模板但是不是创建者的人调用该接口，则只是把这个用户从这个模板的拥有者列表中移除</li>
	 * </ul>
	 * 用户数据从登录session中获取
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
	 * 获取当前登录的用户所拥有的模板
	 *
	 * @return 结果
	 */
	Result<List<RoomTemplate>> getTemplateByLoginUser();

}
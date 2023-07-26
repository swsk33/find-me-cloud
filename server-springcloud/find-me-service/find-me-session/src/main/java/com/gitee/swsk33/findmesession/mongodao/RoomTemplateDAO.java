package com.gitee.swsk33.findmesession.mongodao;

import com.gitee.swsk33.findmeentity.annotation.MongoDAO;
import com.gitee.swsk33.findmeentity.dataobject.RoomTemplate;

import java.util.List;

/**
 * 房间模板数据库操作
 */
@MongoDAO
public interface RoomTemplateDAO {

	/**
	 * 添加一个房间模板
	 */
	void add(RoomTemplate roomTemplate);

	/**
	 * 删除一个房间模板
	 */
	void delete(String id);

	/**
	 * 根据id获取房间模板
	 */
	RoomTemplate getById(String id);

	/**
	 * 在一个房间模板对象中加入一个拥有者用户id
	 */
	void addUserToTemplate(String templateId, long userId);

	/**
	 * 从一个房间模板对象中移除一个拥有者id
	 */
	void removeUserFromTemplate(String templateId, long userId);

	/**
	 * 查找一个用户所拥有的模板
	 */
	List<RoomTemplate> getByUser(long userId);

}
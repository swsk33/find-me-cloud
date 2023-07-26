package com.gitee.swsk33.findmesession.mongodao.impl;

import cn.hutool.core.collection.ListUtil;
import com.gitee.swsk33.findmeentity.dataobject.RoomTemplate;
import com.gitee.swsk33.findmesession.mongodao.RoomTemplateDAO;
import com.gitee.swsk33.findmeutility.util.MongoUpdateUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.gitee.swsk33.findmeentity.param.RedissonLockName.ROOM_TEMPLATE_CHANGE;

@Component
public class RoomTemplateDAOImpl implements RoomTemplateDAO {

	@Autowired
	private MongoTemplate mongoTemplate;

	@Autowired
	private RedissonClient redissonClient;

	@Override
	public void add(RoomTemplate roomTemplate) {
		mongoTemplate.insert(roomTemplate);
	}

	@Override
	public void delete(String id) {
		Criteria criteria = Criteria.where("id").is(id);
		mongoTemplate.remove(criteria);
	}

	@Override
	public RoomTemplate getById(String id) {
		Query query = new Query(Criteria.where("id").is(id));
		List<RoomTemplate> getRoomTemplates = mongoTemplate.find(query, RoomTemplate.class);
		if (getRoomTemplates.isEmpty()) {
			return null;
		}
		return getRoomTemplates.get(0);
	}

	@Override
	public void addUserToTemplate(String templateId, long userId) {
		RoomTemplate template = getById(templateId);
		// 如果模板不存在直接结束
		if (template == null) {
			return;
		}
		// 上锁
		RLock lock = redissonClient.getLock(ROOM_TEMPLATE_CHANGE);
		lock.lock();
		try {
			// 把用户id加入到列表
			template.getUserIdList().add(userId);
			// 执行更新
			Query query = new Query(Criteria.where("id").is(templateId));
			Update update = MongoUpdateUtils.generateUpdate(template);
			mongoTemplate.updateFirst(query, update, RoomTemplate.class);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			lock.unlock();
		}
	}

	@Override
	public void removeUserFromTemplate(String templateId, long userId) {
		RoomTemplate template = getById(templateId);
		// 如果模板不存在直接结束
		if (template == null) {
			return;
		}
		// 上锁
		RLock lock = redissonClient.getLock(ROOM_TEMPLATE_CHANGE);
		lock.lock();
		try {
			// 从列表移除用户id
			List<Long> userIds = template.getUserIdList();
			int index = ListUtil.lastIndexOf(userIds, id -> id == userId);
			userIds.remove(index);
			// 执行更新
			Query query = new Query(Criteria.where("id").is(templateId));
			Update update = MongoUpdateUtils.generateUpdate(template);
			mongoTemplate.updateFirst(query, update, RoomTemplate.class);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			lock.unlock();
		}
	}

	@Override
	public List<RoomTemplate> getByUser(long userId) {
		Query query = new Query(Criteria.where("userIdList").is(userId));
		return mongoTemplate.find(query, RoomTemplate.class);
	}

}
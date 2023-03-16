package com.gitee.swsk33.findmeuser.mongodao.impl;

import com.gitee.swsk33.findmeentity.dataobject.User;
import com.gitee.swsk33.findmeuser.mongodao.UserDAO;
import com.gitee.swsk33.findmeutility.util.MongoUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UserDAOImpl implements UserDAO {

	@Autowired
	private MongoTemplate mongoTemplate;

	@Override
	public void add(User user) {
		mongoTemplate.insert(user);
	}

	@Override
	public void update(User user) {
		Criteria criteria = Criteria.where("id").is(user.getId());
		Query query = new Query(criteria);
		Update update = MongoUtils.generateUpdate(user);
		mongoTemplate.updateFirst(query, update, User.class);
	}

	@Override
	public User getByUsernameOrEmail(String credential) {
		Criteria usernameCriteria = Criteria.where("username").is(credential);
		Criteria emailCriteria = Criteria.where("email").is(credential);
		Criteria totalCriteria = new Criteria();
		totalCriteria.orOperator(usernameCriteria, emailCriteria);
		Query query = new Query(totalCriteria);
		List<User> userList = mongoTemplate.find(query, User.class);
		if (userList.isEmpty()) {
			return null;
		}
		return userList.get(0);
	}

	@Override
	public User getById(long id) {
		Criteria criteria = Criteria.where("id").is(id);
		Query query = new Query(criteria);
		List<User> userList = mongoTemplate.find(query, User.class);
		if (userList.isEmpty()) {
			return null;
		}
		return userList.get(0);
	}

}
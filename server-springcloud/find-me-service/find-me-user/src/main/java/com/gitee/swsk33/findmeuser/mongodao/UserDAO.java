package com.gitee.swsk33.findmeuser.mongodao;

import com.gitee.swsk33.findmeentity.annotation.MongoDAO;
import com.gitee.swsk33.findmeentity.dataobject.User;

@MongoDAO
public interface UserDAO {

	/**
	 * 增加一个用户
	 *
	 * @param user 用户对象
	 */
	void add(User user);

	/**
	 * 修改一个用户信息（根据id修改）
	 *
	 * @param user 传入新的用户对象
	 */
	void update(User user);

	/**
	 * 根据用户名或者邮箱获取用户
	 *
	 * @param credential 用户名或者邮箱
	 * @return 用户对象
	 */
	User getByUsernameOrEmail(String credential);

	/**
	 * 根据id获取用户
	 *
	 * @param id 用户id
	 * @return 用户对象
	 */
	User getById(long id);

}
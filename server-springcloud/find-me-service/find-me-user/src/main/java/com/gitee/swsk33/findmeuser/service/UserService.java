package com.gitee.swsk33.findmeuser.service;

import com.gitee.swsk33.findmeentity.dataobject.User;
import com.gitee.swsk33.findmeentity.model.Result;
import org.springframework.stereotype.Service;

/**
 * 用户服务
 */
@Service
public interface UserService {

	/**
	 * 用户注册
	 *
	 * @param user 用户信息
	 * @return 结果
	 */
	Result<Void> register(User user);

	/**
	 * 用户登录
	 *
	 * @param user 登录信息，其中username字段可以是用户名或者邮箱，password是明文密码
	 * @return 结果
	 */
	Result<Void> login(User user);

	/**
	 * 更新用户
	 *
	 * @param user 传入新的用户信息
	 * @return 结果
	 */
	Result<Void> update(User user);

}
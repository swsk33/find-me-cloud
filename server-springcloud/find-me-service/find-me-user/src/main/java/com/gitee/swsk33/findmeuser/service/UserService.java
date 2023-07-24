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

	/**
	 * 重置密码
	 *
	 * @param email    要重置密码的用户邮箱（先调用发送密码重置验证码接口）
	 * @param code     验证码
	 * @param password 新密码
	 * @return 结果
	 */
	Result<Void> resetPassword(String email, String code, String password);

}
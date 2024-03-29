package com.gitee.swsk33.findmeuser.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.StrUtil;
import com.gitee.swsk33.findmeentity.dataobject.User;
import com.gitee.swsk33.findmeentity.factory.ResultFactory;
import com.gitee.swsk33.findmeentity.model.Result;
import com.gitee.swsk33.findmeentity.param.CommonValue;
import com.gitee.swsk33.findmeentity.param.EmailType;
import com.gitee.swsk33.findmeuser.mongodao.UserDAO;
import com.gitee.swsk33.findmeuser.service.AvatarService;
import com.gitee.swsk33.findmeuser.service.UserService;
import com.gitee.swsk33.findmeutility.util.BCryptUtils;
import com.gitee.swsk33.findmeutility.util.IDGenerator;
import com.gitee.swsk33.findmeutility.util.MongoUpdateUtils;
import io.github.swsk33.codepostcore.service.EmailVerifyCodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserServiceImpl implements UserService {

	@Autowired
	private UserDAO userDAO;

	@Autowired
	private EmailVerifyCodeService emailCodeService;

	@Autowired
	private AvatarService avatarService;

	@Override
	public Result<Void> register(User user) {
		// 检查用户名是否被占用
		User getUser = userDAO.getByUsernameOrEmail(user.getUsername());
		if (getUser != null) {
			return ResultFactory.createFailedResult("用户名被占用！");
		}
		// 检查邮箱是否被占用
		getUser = userDAO.getByUsernameOrEmail(user.getEmail());
		if (getUser != null) {
			return ResultFactory.createFailedResult("邮箱被占用！");
		}
		// 执行注册
		// 生成id
		user.setId(IDGenerator.generateSnowflakeId());
		// 加密密码
		user.setPassword(BCryptUtils.encode(user.getPassword()));
		userDAO.add(user);
		return ResultFactory.createVoidSuccessResult("注册成功！");
	}

	@Override
	public Result<Void> login(User user) {
		User getUser = userDAO.getByUsernameOrEmail(user.getUsername());
		if (getUser == null) {
			return ResultFactory.createFailedResult("用户名或者邮箱不存在！");
		}
		// 密码比对
		if (!BCryptUtils.match(user.getPassword(), getUser.getPassword())) {
			return ResultFactory.createFailedResult("用户名或者密码错误！");
		}
		// 执行登录
		StpUtil.login(getUser.getId());
		// 然后把用户信息存入session
		StpUtil.getSession().set(CommonValue.SA_USER_SESSION_INFO_KEY, getUser);
		return ResultFactory.createVoidSuccessResult("登录成功！");
	}

	@Override
	public Result<Void> update(User user) {
		User getUser = userDAO.getById(user.getId());
		if (getUser == null) {
			return ResultFactory.createFailedResult("用户不存在！");
		}
		// 判断是否是在修改自己而不是别人
		if (user.getId() != StpUtil.getLoginIdAsLong()) {
			return ResultFactory.createFailedResult("您不能修改其他用户信息！");
		}
		// 删除没变化的内容
		MongoUpdateUtils.removeSame(getUser, user);
		// 如果没有什么要修改的内容，则直接返回
		if (MongoUpdateUtils.isObjectContentEmpty(user)) {
			return ResultFactory.createVoidSuccessResult("未修改！");
		}
		// 判断修改的用户名是否被占用
		if (user.getUsername() != null && userDAO.getByUsernameOrEmail(user.getUsername()) != null) {
			return ResultFactory.createFailedResult("用户名被占用！");
		}
		// 判断修改的邮箱是否被占用
		if (user.getEmail() != null && userDAO.getByUsernameOrEmail(user.getEmail()) != null) {
			return ResultFactory.createFailedResult("邮箱被占用！");
		}
		// 如果用户的密码被修改则加密一下
		if (!StrUtil.isEmpty(user.getPassword())) {
			user.setPassword(BCryptUtils.encode(user.getPassword()));
		}
		// 如果用户头像被修改，则删除上一个头像
		if (!StrUtil.isEmpty(user.getAvatarId()) && !StrUtil.isEmpty(getUser.getAvatarId())) {
			// 删除旧的
			avatarService.delete(getUser.getAvatarId());
		}
		// 执行修改
		userDAO.update(user);
		// 刷新session
		StpUtil.getSession().set(CommonValue.SA_USER_SESSION_INFO_KEY, userDAO.getById(user.getId()));
		return ResultFactory.createVoidSuccessResult("修改用户信息成功！");
	}

	@Override
	public Result<Void> resetPassword(String email, String code, String password) {
		User getUser = userDAO.getByUsernameOrEmail(email);
		if (getUser == null) {
			return ResultFactory.createFailedResult("该邮箱未被注册！");
		}
		// 校验验证码
		if (!emailCodeService.verifyCode(EmailType.PASSWORD_RESET, getUser.getId(), code)) {
			return ResultFactory.createFailedResult("验证码错误！");
		}
		// 重置密码
		User user = new User();
		user.setId(getUser.getId());
		user.setPassword(BCryptUtils.encode(password));
		userDAO.update(user);
		return ResultFactory.createVoidSuccessResult("重置密码成功！");
	}

}
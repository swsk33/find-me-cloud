package com.gitee.swsk33.findmeuser.api;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.StrUtil;
import com.gitee.swsk33.findmeentity.dataobject.User;
import com.gitee.swsk33.findmeentity.factory.ResultFactory;
import com.gitee.swsk33.findmeentity.model.Result;
import com.gitee.swsk33.findmeentity.param.CommonValue;
import com.gitee.swsk33.findmeentity.param.ValidationRules;
import com.gitee.swsk33.findmeuser.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user/common")
public class UserAPI {

	@Autowired
	private UserService userService;

	@PostMapping("/register")
	public Result<Void> register(@RequestBody @Validated(ValidationRules.AddData.class) User user, BindingResult result) {
		if (result.hasErrors()) {
			return ResultFactory.createFailedResult(result.getFieldError().getDefaultMessage());
		}
		return userService.register(user);
	}

	@PostMapping("/login")
	public Result<Void> login(@RequestBody @Validated(ValidationRules.UserLogin.class) User user, BindingResult result) {
		if (result.hasErrors()) {
			return ResultFactory.createFailedResult(result.getFieldError().getDefaultMessage());
		}
		return userService.login(user);
	}

	@PatchMapping("/update")
	public Result<Void> update(@RequestBody @Validated(ValidationRules.UpdateData.class) User user, BindingResult result) {
		if (result.hasErrors()) {
			return ResultFactory.createFailedResult(result.getFieldError().getDefaultMessage());
		}
		return userService.update(user);
	}

	@PutMapping("/reset-password/{code}")
	public Result<Void> resetPassword(@PathVariable String code, @RequestBody User user) {
		// 传入的user中需要email字段和password字段不为空，password表示新的密码
		if (user == null || StrUtil.isEmpty(user.getEmail()) || StrUtil.isEmpty(user.getPassword())) {
			return ResultFactory.createFailedResult("邮箱或者新密码不能为空！");
		}
		return userService.resetPassword(user.getEmail(), code, user.getPassword());
	}

	/**
	 * 判断用户是否登录
	 *
	 * @return 若登录则会返回用户信息
	 */
	@GetMapping("/is-login")
	public Result<User> isLogin() {
		if (!StpUtil.isLogin()) {
			return ResultFactory.createFailedResult("用户未登录！");
		}
		return ResultFactory.createSuccessResult("用户已登录！", (User) StpUtil.getSession().get(CommonValue.SA_USER_SESSION_INFO_KEY));
	}

	/**
	 * 用户退出登录
	 */
	@GetMapping("/logout")
	public Result<Void> logout() {
		StpUtil.logout();
		return ResultFactory.createVoidSuccessResult("退出登录成功！");
	}

	/**
	 * 判断指定id的用户是否登录（用于远程调用）
	 *
	 * @param id 用户id
	 * @return 结果，如果登录会返回该用户信息
	 */
	@GetMapping("/is-login-id/{id}")
	public Result<User> isLoginById(@PathVariable long id) {
		User getUser = (User) StpUtil.getSessionByLoginId(id).get(CommonValue.SA_USER_SESSION_INFO_KEY);
		if (getUser == null) {
			return ResultFactory.createFailedResult("用户未登录！");
		}
		return ResultFactory.createSuccessResult("用户已登录！", getUser);
	}

}
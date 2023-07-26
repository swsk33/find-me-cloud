package com.gitee.swsk33.findmefeign.feignclient;

import com.gitee.swsk33.findmeentity.dataobject.User;
import com.gitee.swsk33.findmeentity.model.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "find-me-user", path = "/api/user/common", contextId = "user-client")
public interface UserClient {

	/**
	 * 远程调用：判断当前用户是否登录
	 *
	 * @return 若用户登录则会返回用户信息
	 */
	@GetMapping("/is-login")
	Result<User> isLogin();

	/**
	 * 远程调用：判断指定id的用户是否登录
	 *
	 * @param id 用户id
	 * @return 若用户登录则会返回用户信息
	 */
	@GetMapping("/is-login-id/{id}")
	Result<User> isLoginById(@PathVariable long id);

}
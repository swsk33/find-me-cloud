package com.gitee.swsk33.findmefeign.feignclient;

import com.gitee.swsk33.findmeentity.dataobject.User;
import com.gitee.swsk33.findmeentity.model.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "find-me-user", path = "/api/user/common", contextId = "user-client")
public interface UserClient {

	@GetMapping("/is-login-id/{id}")
	Result<User> isLoginById(@PathVariable long id);

}
package com.gitee.swsk33.findmefeign.feignclient;

import com.gitee.swsk33.findmeentity.model.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "find-me-image", path = "/api/image/avatar", contextId = "image-client")
public interface AvatarClient {

	@DeleteMapping("/delete/{id}")
	Result<Void> delete(@PathVariable String id);

}
package com.gitee.swsk33.findmeimage.api;

import com.gitee.swsk33.findmeentity.model.Result;
import com.gitee.swsk33.findmeimage.service.AvatarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/image/avatar")
public class AvatarAPI {

	@Autowired
	private AvatarService avatarService;

	@PostMapping("/upload")
	public Result<String> upload(@RequestParam("avatar") MultipartFile file) throws IOException {
		return avatarService.upload(file);
	}

	@DeleteMapping("/delete/{id}")
	public Result<Void> delete(@PathVariable String id) {
		return avatarService.delete(id);
	}

	@GetMapping("/get/{id}")
	public byte[] getAvatar(@PathVariable String id) {
		Result<byte[]> result = avatarService.get(id);
		if (!result.isSuccess()) {
			return null;
		}
		return result.getData();
	}

}
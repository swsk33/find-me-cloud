package com.gitee.swsk33.findmeimage.api;

import com.gitee.swsk33.findmeentity.model.Avatar;
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
	public Result<Avatar> upload(@RequestParam("avatar") MultipartFile file) throws IOException {
		return avatarService.upload(file);
	}

	@DeleteMapping("/delete/{filename}")
	public Result<Void> delete(@PathVariable String filename) {
		return avatarService.delete(filename);
	}

	@GetMapping("/get/{filename}")
	public byte[] getAvatar(@PathVariable String filename) {
		Result<byte[]> result = avatarService.get(filename);
		if (!result.isSuccess()) {
			return null;
		}
		return result.getData();
	}

}
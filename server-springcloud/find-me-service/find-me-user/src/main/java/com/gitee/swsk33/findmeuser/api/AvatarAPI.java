package com.gitee.swsk33.findmeuser.api;

import com.gitee.swsk33.findmeentity.model.Result;
import com.gitee.swsk33.findmeuser.service.AvatarService;
import io.github.swsk33.fileliftcore.model.BinaryContent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/user/avatar")
public class AvatarAPI {

	@Autowired
	private AvatarService avatarService;

	@PostMapping("/upload")
	public Result<String> upload(@RequestParam("avatar") MultipartFile file) {
		return avatarService.upload(file);
	}

	@GetMapping("/get/{id}")
	public ResponseEntity<byte[]> getAvatar(@PathVariable String id) {
		Result<BinaryContent> result = avatarService.get(id);
		if (!result.isSuccess()) {
			return ResponseEntity.notFound().build();
		}
		return ResponseEntity.ok().contentType(MediaType.parseMediaType(result.getData().getContentType())).body(result.getData().getByteAndClose());
	}

}
package com.gitee.swsk33.findmesession.api;

import com.gitee.swsk33.findmeentity.model.Result;
import com.gitee.swsk33.findmesession.service.RoomTemplateShareService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/session/room-template-share")
public class RoomTemplateShareAPI {

	@Autowired
	private RoomTemplateShareService roomTemplateShareService;

	@PostMapping("/create/{templateId}")
	public Result<String> createShare(@PathVariable String templateId) {
		return roomTemplateShareService.createShare(templateId);
	}

	@GetMapping("/validate/{shareId}")
	public Result<Void> validateShare(@PathVariable String shareId) {
		return roomTemplateShareService.validateShare(shareId);
	}

}
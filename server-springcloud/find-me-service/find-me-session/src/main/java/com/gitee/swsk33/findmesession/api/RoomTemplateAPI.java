package com.gitee.swsk33.findmesession.api;

import com.gitee.swsk33.findmeentity.dataobject.RoomTemplate;
import com.gitee.swsk33.findmeentity.factory.ResultFactory;
import com.gitee.swsk33.findmeentity.model.Result;
import com.gitee.swsk33.findmeentity.param.ValidationRules;
import com.gitee.swsk33.findmesession.service.RoomTemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/session/room-template")
public class RoomTemplateAPI {

	@Autowired
	private RoomTemplateService roomTemplateService;

	@PostMapping("/create")
	public Result<Void> createTemplate(@RequestBody @Validated(ValidationRules.AddData.class) RoomTemplate roomTemplate, BindingResult errors) {
		if (errors.hasErrors()) {
			return ResultFactory.createFailedResult(errors.getFieldError().getDefaultMessage());
		}
		return roomTemplateService.createTemplate(roomTemplate);
	}

	@DeleteMapping("/delete/{id}")
	public Result<Void> deleteTemplate(@PathVariable String id) {
		return roomTemplateService.deleteTemplate(id);
	}

	@GetMapping("/get-by-login-user")
	public Result<List<RoomTemplate>> getByUser() {
		return roomTemplateService.getTemplateByLoginUser();
	}

}
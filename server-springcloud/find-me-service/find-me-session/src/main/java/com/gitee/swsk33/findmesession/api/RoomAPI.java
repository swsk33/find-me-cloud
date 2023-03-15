package com.gitee.swsk33.findmesession.api;

import com.gitee.swsk33.findmeentity.factory.ResultFactory;
import com.gitee.swsk33.findmeentity.model.Result;
import com.gitee.swsk33.findmeentity.model.Room;
import com.gitee.swsk33.findmesession.service.RoomService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/session/room")
public class RoomAPI {

	@Autowired
	private RoomService roomService;

	@PostMapping("/create")
	public Result<Room> create(@RequestBody @Valid Room room, BindingResult result) {
		if (result.hasErrors()) {
			return ResultFactory.createFailedResult(result.getFieldError().getDefaultMessage());
		}
		return roomService.createRoom(room);
	}

	@GetMapping("/get/{id}")
	public Result<Room> getRoomById(@PathVariable String id) {
		return roomService.getRoom(id);
	}

}
package com.gitee.swsk33.findmeuser.api;

import com.gitee.swsk33.findmeentity.factory.ResultFactory;
import com.gitee.swsk33.findmeentity.model.Result;
import com.gitee.swsk33.findmeuser.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user/email")
public class EmailAPI {

	@Autowired
	private EmailService emailService;

	@GetMapping("/password-reset-code/{email}")
	public Result<Void> sendPasswordResetMail(@PathVariable String email) {
		emailService.requestPasswordResetEmail(email);
		return ResultFactory.createVoidSuccessResult("邮件验证码已发送！");
	}

}
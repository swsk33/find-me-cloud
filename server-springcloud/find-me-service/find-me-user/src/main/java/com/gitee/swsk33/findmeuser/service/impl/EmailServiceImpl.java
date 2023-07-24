package com.gitee.swsk33.findmeuser.service.impl;

import com.gitee.swsk33.findmeentity.dataobject.User;
import com.gitee.swsk33.findmeentity.param.EmailType;
import com.gitee.swsk33.findmeuser.mongodao.UserDAO;
import com.gitee.swsk33.findmeuser.service.EmailService;
import io.github.swsk33.codepostcore.context.ServiceNameContext;
import io.github.swsk33.codepostcore.service.EmailVerifyCodeService;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class EmailServiceImpl implements EmailService {

	@Autowired
	private UserDAO userDAO;

	@Autowired
	private EmailVerifyCodeService emailCodeService;

	/**
	 * 初始化邮件服务名注册
	 */
	@PostConstruct
	private void init() {
		ServiceNameContext.register(EmailType.PASSWORD_RESET, "密码重置");
		log.info("已完成邮件验证码服务名初始化！");
	}

	@Async
	@Override
	public void requestPasswordResetEmail(String email) {
		User getUser = userDAO.getByUsernameOrEmail(email);
		if (getUser == null) {
			log.error("用户不存在！");
			return;
		}
		// 发送验证码
		emailCodeService.sendCode(EmailType.PASSWORD_RESET, getUser.getId(), email, 5, TimeUnit.MINUTES);
	}

}
package com.gitee.swsk33.findmeuser.service.impl;

import com.gitee.swsk33.findmeentity.dataobject.User;
import com.gitee.swsk33.findmeentity.factory.ResultFactory;
import com.gitee.swsk33.findmeentity.model.Result;
import com.gitee.swsk33.findmeentity.param.EmailType;
import com.gitee.swsk33.findmeuser.cache.EmailCodeCache;
import com.gitee.swsk33.findmeuser.mongodao.UserDAO;
import com.gitee.swsk33.findmeuser.service.EmailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class EmailServiceImpl implements EmailService {

	@Autowired
	private JavaMailSender javaMailSender;

	@Autowired
	private EmailCodeCache emailCodeCache;

	@Autowired
	private UserDAO userDAO;

	@Value("${spring.mail.username}")
	private String sender;

	@Async
	@Override
	public void sendTextEmail(String email, String title, String content) {
		SimpleMailMessage mailMessage = new SimpleMailMessage();
		mailMessage.setFrom(sender);
		mailMessage.setTo(email);
		mailMessage.setSubject(title);
		mailMessage.setText(content);
		javaMailSender.send(mailMessage);
		log.info("已向" + email + "发送邮件！");
	}

	@Override
	public Result<Void> requestPasswordResetEmail(String email) {
		User getUser = userDAO.getByUsernameOrEmail(email);
		if (getUser == null) {
			return ResultFactory.createFailedResult("该邮箱未被注册！");
		}
		// 发送邮件
		String title = "FindMe - 密码重置";
		String content = "您的密码重置验证码是：" + emailCodeCache.generateCodeToCache(email, EmailType.PASSWORD_RESET) + "，将在五分钟后失效！";
		sendTextEmail(email, title, content);
		return ResultFactory.createVoidSuccessResult("已发送密码重置邮件！");
	}

}
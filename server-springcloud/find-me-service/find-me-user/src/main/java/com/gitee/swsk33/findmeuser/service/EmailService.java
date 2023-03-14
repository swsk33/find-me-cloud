package com.gitee.swsk33.findmeuser.service;

import com.gitee.swsk33.findmeentity.model.Result;
import org.springframework.stereotype.Service;

/**
 * 邮箱验证码服务
 */
@Service
public interface EmailService {

	/**
	 * 发送文字邮件
	 *
	 * @param email   收件人
	 * @param title   标题
	 * @param content 内容
	 */
	void sendTextEmail(String email, String title, String content);

	/**
	 * 请求密码重置验证码邮件
	 *
	 * @param email 需要重置密码的用户邮箱
	 * @return 结果
	 */
	Result<Void> requestPasswordResetEmail(String email);

}
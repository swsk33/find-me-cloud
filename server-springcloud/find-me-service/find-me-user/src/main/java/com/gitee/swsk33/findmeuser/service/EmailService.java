package com.gitee.swsk33.findmeuser.service;

import org.springframework.stereotype.Service;

/**
 * 邮箱验证码服务
 */
@Service
public interface EmailService {

	/**
	 * 请求密码重置验证码邮件
	 *
	 * @param email 需要重置密码的用户邮箱
	 */
	void requestPasswordResetEmail(String email);

}
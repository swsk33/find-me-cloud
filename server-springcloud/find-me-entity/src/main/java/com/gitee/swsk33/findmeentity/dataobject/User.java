package com.gitee.swsk33.findmeentity.dataobject;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户类
 */
@Data
public class User implements Serializable {

	/**
	 * 主键id（雪花id）
	 */
	private long id;

	/**
	 * 用户名
	 */
	private String username;

	/**
	 * 昵称
	 */
	private String nickname;

	/**
	 * 密码
	 */
	private String password;

	/**
	 * 头像url
	 */
	private String avatar;

}
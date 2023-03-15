package com.gitee.swsk33.findmeentity.model;

import com.gitee.swsk33.findmeentity.dataobject.User;
import com.gitee.swsk33.findmeentity.param.ValidationRules;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.io.Serializable;
import java.util.Map;

/**
 * 会话房间
 */
@Data
public class Room implements Serializable {

	/**
	 * 房间id（UUID）
	 */
	private String id;

	/**
	 * 房间名
	 */
	@NotEmpty(groups = ValidationRules.AddData.class, message = "房间名不能为空！")
	private String name;

	/**
	 * 房间密码
	 */
	@NotEmpty(message = "密码不能为空！")
	private String password;

	/**
	 * 集结点
	 */
	private RallyPoint rally;

	/**
	 * 当前在这个房间的用户（键为用户id，值为用户对象）
	 */
	private Map<Long, User> users;

}
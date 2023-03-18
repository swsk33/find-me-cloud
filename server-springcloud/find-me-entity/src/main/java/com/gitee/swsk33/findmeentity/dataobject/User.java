package com.gitee.swsk33.findmeentity.dataobject;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.gitee.swsk33.findmeentity.param.ValidationRules;
import com.gitee.swsk33.findmeentity.serializer.LongToStringSerializer;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.io.Serializable;

/**
 * 用户类
 */
@Data
@JsonIgnoreProperties(allowSetters = true, value = {"password"})
public class User implements Serializable {

	/**
	 * 主键id（雪花id）
	 */
	@JsonSerialize(using = LongToStringSerializer.class)
	@NotNull(groups = ValidationRules.UpdateData.class, message = "用户id不能为空！")
	private Long id;

	/**
	 * 用户名
	 */
	@Pattern(groups = {ValidationRules.AddData.class, ValidationRules.UpdateData.class}, regexp = "^[\\u4E00-\\u9FA5A-Za-z0-9_]+$", message = "用户名只能是由中文、英文、数字或者下划线组成！")
	@NotEmpty(groups = {ValidationRules.AddData.class, ValidationRules.UserLogin.class}, message = "用户名不能为空！")
	private String username;

	/**
	 * 昵称
	 */
	@NotEmpty(groups = ValidationRules.AddData.class, message = "昵称不能为空！")
	private String nickname;

	/**
	 * 密码
	 */
	@NotEmpty(groups = {ValidationRules.AddData.class, ValidationRules.UserLogin.class}, message = "密码不能为空！")
	@Size(groups = {ValidationRules.AddData.class, ValidationRules.UpdateData.class}, min = 8, message = "密码长度不能小于8！")
	private String password;

	/**
	 * 邮箱
	 */
	@NotEmpty(groups = ValidationRules.AddData.class, message = "邮箱不能为空！")
	@Email(groups = {ValidationRules.AddData.class, ValidationRules.UpdateData.class}, message = "邮箱格式不正确！")
	private String email;

	/**
	 * 头像id（可以用于获取头像）
	 */
	private String avatarId;

}
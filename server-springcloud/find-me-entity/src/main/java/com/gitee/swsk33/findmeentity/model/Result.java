package com.gitee.swsk33.findmeentity.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 返回结果类
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Result<T> implements Serializable {

	/**
	 * 操作是否成功
	 */
	private boolean success;

	/**
	 * 返回消息
	 */
	private String message;

	/**
	 * 返回内容
	 */
	private T data;

}
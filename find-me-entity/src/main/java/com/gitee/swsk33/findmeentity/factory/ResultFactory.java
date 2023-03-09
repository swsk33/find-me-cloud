package com.gitee.swsk33.findmeentity.factory;

import com.gitee.swsk33.findmeentity.model.Result;

/**
 * 简单工厂模式-生成结果类
 */
public class ResultFactory {

	/**
	 * 生成成功结果
	 *
	 * @param message 消息
	 * @param data    结果返回内容
	 * @param <T>     结果数据体类型
	 * @return 结果
	 */
	public static <T> Result<T> createSuccessResult(String message, T data) {
		return new Result<>(true, message, data);
	}

	/**
	 * 生成没有结果数据体的成功结果
	 *
	 * @param message 消息
	 * @return 结果
	 */
	public static Result<Void> createVoidSuccessResult(String message) {
		return new Result<>(true, message, null);
	}

	/**
	 * 生成失败的结果
	 *
	 * @param message 消息
	 * @param <T>     结果数据体类型
	 * @return 结果
	 */
	public static <T> Result<T> createFailedResult(String message) {
		return new Result<>(false, message, null);
	}

}
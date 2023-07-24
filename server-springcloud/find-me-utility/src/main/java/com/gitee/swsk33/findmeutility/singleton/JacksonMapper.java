package com.gitee.swsk33.findmeutility.singleton;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

/**
 * 通用的Jackson序列化对象获取
 */
public class JacksonMapper {

	/**
	 * Jackson对象映射器单例
	 */
	private static volatile ObjectMapper mapper;

	/**
	 * 私有构造器
	 */
	private JacksonMapper() {
	}

	public static ObjectMapper getMapper() {
		if (mapper == null) {
			synchronized (JacksonMapper.class) {
				if (mapper == null) {
					mapper = new ObjectMapper();
					// 注册时间模块
					mapper.registerModule(new JavaTimeModule());
					// 进行配置
					// 开启输出缩进
					mapper.enable(SerializationFeature.INDENT_OUTPUT);
					// 允许序列化空的Java对象
					mapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
					// 忽略未知字段
					mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
				}
			}
		}
		return mapper;
	}

}
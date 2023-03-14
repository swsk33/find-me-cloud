package com.gitee.swsk33.findmeuser.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * Redis序列化配置
 */
@Configuration
public class RedisSerdeConfig {

	@Bean
	public RedisTemplate<Object, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
		// 字符串序列化器
		StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
		// JSON序列化器
		// Jackson序列化的映射器配置
		ObjectMapper mapper = new ObjectMapper();
		// 添加Java 8时间支持模块
		mapper.registerModule(new JavaTimeModule());
		// 设定序列化策略：直接基于字段而不依赖于getter和setter
		mapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.NONE);
		mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
		// 设定对象类型策略
		mapper.activateDefaultTyping(LaissezFaireSubTypeValidator.instance, ObjectMapper.DefaultTyping.NON_FINAL, JsonTypeInfo.As.PROPERTY);
		// 配置忽略空字段
		mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
		// 最后创建JSON序列化器
		Jackson2JsonRedisSerializer<Object> jsonSerializer = new Jackson2JsonRedisSerializer<>(mapper, Object.class);
		// 设定序列化
		RedisTemplate<Object, Object> redisTemplate = new RedisTemplate<>();
		redisTemplate.setConnectionFactory(redisConnectionFactory);
		// key和hashKey通常使用字符串序列化方式
		redisTemplate.setKeySerializer(stringRedisSerializer);
		redisTemplate.setHashKeySerializer(stringRedisSerializer);
		// value和hashValue通常使用JSON序列化方式
		redisTemplate.setValueSerializer(jsonSerializer);
		redisTemplate.setHashValueSerializer(jsonSerializer);
		return redisTemplate;
	}

}
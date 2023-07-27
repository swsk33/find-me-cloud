package com.gitee.swsk33.findmebeanconfig.autoconfigure;

import feign.RequestInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * 远程调用时，带上Cookie请求头防止Session丢失
 */
@Slf4j
@Configuration
@ConditionalOnClass(RequestInterceptor.class)
public class OpenFeignSessionAutoConfig {

	@Bean("requestInterceptor")
	public RequestInterceptor sessionFeignInterceptor() {
		log.info("OpenFeign远程调用拦截器已完成自动配置！");
		return requestTemplate -> {
			// 获取当前请求的所有属性
			ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
			if (attributes != null) {
				// 获取Cookie
				String cookie = attributes.getRequest().getHeader("Cookie");
				// 将Cookie信息添加到远程调用时的请求的头部
				requestTemplate.header("Cookie", cookie);
			}
		};
	}
}
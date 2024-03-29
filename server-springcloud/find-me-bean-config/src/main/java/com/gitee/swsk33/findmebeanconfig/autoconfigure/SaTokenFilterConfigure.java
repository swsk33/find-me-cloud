package com.gitee.swsk33.findmebeanconfig.autoconfigure;

import cn.dev33.satoken.context.SaHolder;
import cn.dev33.satoken.reactor.filter.SaReactorFilter;
import cn.dev33.satoken.router.SaRouter;
import cn.dev33.satoken.stp.StpUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 网关统一鉴权拦截器配置
 */
@Slf4j
@Configuration
@ConditionalOnClass(SaReactorFilter.class)
public class SaTokenFilterConfigure {

	@Bean
	public SaReactorFilter getSaReactorFilter() {
		log.info("Sa-Token网关鉴权拦截器已完成自动配置！");
		return new SaReactorFilter()
				// 拦截全部地址
				.addInclude("/**")
				// 放行一些地址
				.addExclude("/api/user/common/register", "/api/user/common/login", "/api/user/common/logout", "/api/user/email/password-reset-code/**", "/api/user/common/reset-password/**", "/ws/**")
				// 鉴权方法：每次访问进入
				.setAuth(object -> {
					// 登录校验
					SaRouter.match("/**", result -> StpUtil.checkLogin());
				}).setError(e -> {
					SaHolder.getResponse().setHeader("Content-Type", "application/json;charset=UTF-8");
					return "{\"success\":false,\"message\":\"用户没有登录！\"}";
				});
	}

}
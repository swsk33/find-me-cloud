package com.gitee.swsk33.findmebeanconfig.autoconfigure;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

@Slf4j
@Configuration
@ConditionalOnClass(ServerEndpointExporter.class)
public class WebSocketAutoConfig {

	@Bean
	public ServerEndpointExporter serverEndpointExporter() {
		log.info("WebSocket服务已完成自动配置！");
		return new ServerEndpointExporter();
	}

}
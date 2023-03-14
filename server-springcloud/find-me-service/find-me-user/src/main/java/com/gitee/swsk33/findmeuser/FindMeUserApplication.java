package com.gitee.swsk33.findmeuser;

import com.gitee.swsk33.findmefeign.feignclient.AvatarClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableDiscoveryClient
@EnableFeignClients(clients = AvatarClient.class)
@SpringBootApplication
@EnableAsync
public class FindMeUserApplication {

	public static void main(String[] args) {
		SpringApplication.run(FindMeUserApplication.class, args);
	}

}
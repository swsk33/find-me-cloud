package com.gitee.swsk33.findmesession;

import com.gitee.swsk33.findmefeign.feignclient.UserClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@EnableDiscoveryClient
@EnableFeignClients(clients = UserClient.class)
@SpringBootApplication
public class FindMeSessionApplication {

	public static void main(String[] args) {
		SpringApplication.run(FindMeSessionApplication.class, args);
	}

}
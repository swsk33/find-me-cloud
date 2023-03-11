package com.gitee.swsk33.findmesession;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication
public class FindMeSessionApplication {

	public static void main(String[] args) {
		SpringApplication.run(FindMeSessionApplication.class, args);
	}

}
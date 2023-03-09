package com.gitee.swsk33.findmeuser;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication
public class FindMeUserApplication {

	public static void main(String[] args) {
		SpringApplication.run(FindMeUserApplication.class, args);
	}

}
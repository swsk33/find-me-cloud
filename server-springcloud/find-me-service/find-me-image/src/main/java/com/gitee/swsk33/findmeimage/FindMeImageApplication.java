package com.gitee.swsk33.findmeimage;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication
public class FindMeImageApplication {

	public static void main(String[] args) {
		SpringApplication.run(FindMeImageApplication.class, args);
	}

}
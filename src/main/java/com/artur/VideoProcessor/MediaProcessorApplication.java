package com.artur.VideoProcessor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@EnableTransactionManagement
@EnableAsync
@SpringBootApplication
public class MediaProcessorApplication {

	public static void main(String[] args) {
		SpringApplication.run(MediaProcessorApplication.class, args);
	}
}


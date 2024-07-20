package com.artur.VideoProcessor;

import com.artur.VideoProcessor.config.ObjectStorageConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@EnableTransactionManagement
@EnableAsync
@SpringBootApplication(scanBasePackages = "com.artur")
@EnableConfigurationProperties(ObjectStorageConfig.class)
public class MediaProcessorApplication {

	public static void main(String[] args) {
		SpringApplication.run(MediaProcessorApplication.class, args);
	}
}


package com.artur.VideoProcessor;

import com.google.common.util.concurrent.ListenableFuture;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;

@EnableTransactionManagement
@SpringBootApplication
public class VideoProcessorApplication {

	public static void main(String[] args) {
		SpringApplication.run(VideoProcessorApplication.class, args);
	}
}


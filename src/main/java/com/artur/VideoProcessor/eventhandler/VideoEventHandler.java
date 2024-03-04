package com.artur.VideoProcessor.eventhandler;

import com.artur.VideoProcessor.service.MinioService;
import com.artur.VideoProcessor.tool.Ffmpeg;
import com.artur.VideoProcessor.utils.AppConstants;
import jakarta.annotation.PostConstruct;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.TopicPartition;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class VideoEventHandler {
    private static final Logger logger = LoggerFactory.getLogger(VideoEventHandler.class);

    @Autowired
    MinioService minioService;
    @Autowired
    Ffmpeg ffmpeg;
    @Autowired
    KafkaTemplate<String, Boolean> kafkaTemplate;

    @KafkaListener(
            topics = AppConstants.VIDEO_INPUT_TOPIC,
            topicPartitions = {@TopicPartition(topic = AppConstants.VIDEO_INPUT_TOPIC, partitions = {"0", "1", "2", "3", "4"})},
            groupId = "video-processor.video:consumer",
            containerFactory = "filenameListenerFactory",
            concurrency = "5"
    )
    public void consumeVideoEvent(ConsumerRecord<String, String> record){
        kafkaTemplate.send(AppConstants.VIDEO_OUTPUT_TOPIC, record.key(), process(record.value()));
    }

    private boolean process(String filename){
        logger.trace("Started processing video: " + filename);
        File tempDir = null;
        try {
            InputStream inputStream;
            try {
                inputStream = new ByteArrayInputStream(minioService.getObject(filename).readAllBytes());
            } catch (Exception e) {
                logger.error("Cannot download object: " + e);
                return false;
            }

            try (inputStream){
                tempDir = Files.createTempDirectory("tmp-ffmpeg").toFile();
                Path index = Path.of(tempDir + "/" + "index.mp4");
                Files.write(index, inputStream.readAllBytes());
                ffmpeg.convertVideoToHls(index.toFile());
            } catch (Exception e) {
                logger.error("Cannot convert video: " + e);
                return false;
            }

            String prefix = filename.substring(0, filename.lastIndexOf("/"));
            upload(tempDir, prefix);
         } finally {
            FileSystemUtils.deleteRecursively(tempDir);
        }
        logger.info("Video [" + filename + "] successfully processed");
        return true;
    }

    private void upload(File tempDir, String prefix){
        if(!prefix.endsWith("/")){
            prefix += "/";
        }
        List<String> uploadedObjects = new ArrayList<>();
        for(File file : Objects.requireNonNull(tempDir.listFiles())){
            try {
                String objectFilename = prefix + file.getName();
                minioService.uploadObject(file, objectFilename);
                uploadedObjects.add(objectFilename);
            } catch (Exception e) {
                for(String object: uploadedObjects){
                    try {
                        minioService.removeObject(object);
                    } catch (Exception ex) {
                        throw new RuntimeException(ex);
                    }
                }
                throw new RuntimeException("Cannot upload video: " + e);
            }
        }
    }
}

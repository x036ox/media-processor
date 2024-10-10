package com.artur.VideoProcessor.eventhandler;

import com.artur.objectstorage.service.ObjectStorageService;
import com.artur.VideoProcessor.tool.Ffmpeg;
import com.artur.VideoProcessor.utils.AppConstants;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.TopicPartition;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class VideoEventHandler {
    private static final Logger logger = LoggerFactory.getLogger(VideoEventHandler.class);

    @Autowired
    ObjectStorageService objectStorageService;
    @Autowired
    Ffmpeg ffmpeg;

    @KafkaListener(
            topics = AppConstants.VIDEO_INPUT_TOPIC,
            topicPartitions = {@TopicPartition(topic = AppConstants.VIDEO_INPUT_TOPIC, partitions = {"0", "1", "2", "3", "4"})},
            groupId = "video-processor.video:consumer",
            concurrency = "5"
    )
    @SendTo(AppConstants.VIDEO_OUTPUT_TOPIC)
    public boolean consumeVideoEvent(ConsumerRecord<String, String> record){
        return process(record.value());
    }

    private boolean process(String filename){
        logger.trace("Started processing video: " + filename);
        long start = System.currentTimeMillis();
        File tempDir = null;
        try {
            InputStream inputStream;
            try {
                inputStream = new ByteArrayInputStream(objectStorageService.getObject(filename).readAllBytes());
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
        logger.info("Video [{}] successfully processed in {} {}", filename, ((System.currentTimeMillis() - start) / 1000), "seconds");
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
                objectStorageService.uploadObject(file, objectFilename);
                uploadedObjects.add(objectFilename);
            } catch (Exception e) {
                for(String object: uploadedObjects){
                    try {
                        objectStorageService.removeObject(object);
                    } catch (Exception ex) {
                        throw new RuntimeException(ex);
                    }
                }
                throw new RuntimeException("Cannot upload video: " + e);
            }
        }
    }
}

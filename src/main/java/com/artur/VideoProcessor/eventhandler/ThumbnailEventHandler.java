package com.artur.VideoProcessor.eventhandler;

import com.artur.VideoProcessor.service.KafkaManager;
import com.artur.VideoProcessor.service.MinioService;
import com.artur.VideoProcessor.utils.AppConstants;
import com.artur.VideoProcessor.utils.ImageUtils;
import jakarta.annotation.PostConstruct;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.TopicPartition;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.time.Instant;

@Component
public class ThumbnailEventHandler {
    private static final Logger logger = LoggerFactory.getLogger(ThumbnailEventHandler.class);

    @Autowired
    MinioService minioService;
    @Autowired
    KafkaTemplate<String, Boolean> kafkaTemplate;

    @KafkaListener(
            id = "video-processor.thumbnail_consumer",
            topics = AppConstants.THUMBNAIL_INPUT_TOPIC,
            topicPartitions = {@TopicPartition(topic = AppConstants.THUMBNAIL_INPUT_TOPIC, partitions = {"0", "1", "2", "3", "4"})},
            groupId = "video-processor.thumbnail:consumer",
            containerFactory = "filenameListenerFactory",
            concurrency = "5"
    )
    public void consumeEvent(ConsumerRecord<String, String> record){
       kafkaTemplate.send(AppConstants.THUMBNAIL_OUTPUT_TOPIC, record.key(), process(record.value()));
    }

    private boolean process(String filename){
        logger.trace("Started processing thumbnail: " + filename);
        byte[] pictureBytes;
        try (InputStream pictureInputStream = minioService.getObject(filename)){
            pictureBytes = ImageUtils.compressVideoThumbnail(pictureInputStream);
        } catch (Exception e) {
            logger.error("Cannot process thumbnail: " + e);
            return false;
        }

        try {
            minioService.putObject(pictureBytes, filename);
        } catch (Exception e){
            logger.error("Cannot upload object: " + e);
            return false;
        }
        logger.info("Thumbnail [" + filename + "] successfully processed");
        return true;
    }
}

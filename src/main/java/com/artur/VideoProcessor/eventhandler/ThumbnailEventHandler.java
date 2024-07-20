package com.artur.VideoProcessor.eventhandler;

import com.artur.objectstorage.service.ObjectStorageService;
import com.artur.VideoProcessor.utils.AppConstants;
import com.artur.VideoProcessor.utils.ImageUtils;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.TopicPartition;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

@Component
public class ThumbnailEventHandler {
    private static final Logger logger = LoggerFactory.getLogger(ThumbnailEventHandler.class);

    @Autowired
    ObjectStorageService objectStorageService;

    @KafkaListener(
            id = "video-processor.thumbnail_consumer",
            topics = AppConstants.THUMBNAIL_INPUT_TOPIC,
            topicPartitions = {@TopicPartition(topic = AppConstants.THUMBNAIL_INPUT_TOPIC, partitions = {"0", "1", "2", "3", "4"})},
            groupId = "video-processor.thumbnail:consumer",
            concurrency = "5"
    )
    @SendTo(AppConstants.THUMBNAIL_OUTPUT_TOPIC)
    public boolean consumeEvent(ConsumerRecord<String, String> record){
        return process(record.value());
    }

    private boolean process(String filename){
        logger.trace("Started processing thumbnail: " + filename);
        byte[] pictureBytes;
        try (InputStream pictureInputStream = objectStorageService.getObject(filename)){
            pictureBytes = ImageUtils.compressVideoThumbnail(pictureInputStream);
        } catch (Exception e) {
            logger.error("Cannot process thumbnail: " + e);
            return false;
        }

        try {
            objectStorageService.putObject(new ByteArrayInputStream(pictureBytes), filename);
        } catch (Exception e){
            logger.error("Cannot upload object: " + e);
            return false;
        }
        logger.info("Thumbnail [" + filename + "] successfully processed");
        return true;
    }
}

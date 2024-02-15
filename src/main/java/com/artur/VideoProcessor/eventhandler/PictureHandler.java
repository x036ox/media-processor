package com.artur.VideoProcessor.eventhandler;

import com.artur.VideoProcessor.service.MinioService;
import com.artur.VideoProcessor.utils.AppConstants;
import com.artur.VideoProcessor.utils.ImageUtils;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;

@Component
public class PictureHandler {
    private static final Logger logger = LoggerFactory.getLogger(PictureHandler.class);

    @Autowired
    MinioService minioService;
    @Autowired
    KafkaTemplate<String, Boolean> kafkaTemplate;

    @KafkaListener(
            topics = AppConstants.USER_PICTURE_INPUT_TOPIC,
            groupId = "video-processor.user-picture:consumer",
            containerFactory = "filenameListenerFactory"
    )
    public void consumeVideoEvent(ConsumerRecord<String, String> record){
        kafkaTemplate.send(AppConstants.USER_PICTURE_OUTPUT_TOPIC,record.key(),  process(record.value()));
    }

    private boolean process(String filename){
        logger.trace("Started processing user picture: " + filename);
        byte[] pictureBytes;
        try (InputStream pictureInputStream = minioService.getObject(filename)){
            pictureBytes = ImageUtils.compressUserPicture(pictureInputStream);
        } catch (Exception e) {
            logger.error("Cannot process picture: " + e);
            return false;
        }

        try {
            minioService.putObject(pictureBytes, filename);
        } catch (Exception e){
            logger.error("Cannot upload object: " + e);
            return false;
        }
        logger.info(STR."User picture \{filename} successfully processed");
        return true;
    }
}

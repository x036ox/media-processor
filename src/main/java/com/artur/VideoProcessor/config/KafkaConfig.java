package com.artur.VideoProcessor.config;

import com.artur.VideoProcessor.utils.AppConstants;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.BooleanSerializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.*;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConfig {

    @Value("${spring.kafka.bootstrap-servers:localhost:9092}")
    String bootstrapServers;

//    @Bean
//    public ConsumerFactory<String, String> consumerFactory(){
//        Map<String, Object> props = new HashMap<>();
//        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
//        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
//        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
//        return new DefaultKafkaConsumerFactory<>(props);
//    }
//
//
//
//    @Primary
//    public ConcurrentKafkaListenerContainerFactory<String, String> filenameListenerFactory(ConsumerFactory<String, String> consumerFactory){
//        ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
//        factory.setConsumerFactory(consumerFactory);
//        return factory;
//    }


    @Bean
    public ProducerFactory<String, Boolean> producerFactory(){
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, BooleanSerializer.class);
        return new DefaultKafkaProducerFactory<>(props);
    }

    @Bean
    @Primary
    public ReplyingKafkaTemplate<String, Boolean, String> kafkaTemplate(ProducerFactory<String, Boolean> producerFactory,
                                                                ConcurrentKafkaListenerContainerFactory<String, String> factory){
        ConcurrentMessageListenerContainer<String, String> container = factory.createContainer(AppConstants.VIDEO_OUTPUT_TOPIC);
        container.getContainerProperties().setGroupId("yout-back:replier");
        var repTemplate = new ReplyingKafkaTemplate<>(producerFactory, container);
        factory.setReplyTemplate(repTemplate);
        repTemplate.setSharedReplyTopic(true);
        return repTemplate;
    }
//
//
//    @Bean
//    public NewTopic userPictureTopicInput(){
//        return TopicBuilder.name(AppConstants.USER_PICTURE_INPUT_TOPIC).build();
//    }
//
//    @Bean
//    public NewTopic videoTopicInput(){
//        return TopicBuilder.name(AppConstants.VIDEO_INPUT_TOPIC).build();
//    }
//
//    @Bean
//    public NewTopic thumbnailTopicInput(){
//        return TopicBuilder.name(AppConstants.THUMBNAIL_INPUT_TOPIC).build();
//    }
//
//    @Bean
//    public NewTopic userPictureTopicOutput(){
//        return TopicBuilder.name(AppConstants.USER_PICTURE_OUTPUT_TOPIC).partitions(5).build();
//    }
//
//    @Bean
//    public NewTopic videoTopicOutput(){
//        return TopicBuilder.name(AppConstants.VIDEO_OUTPUT_TOPIC).partitions(5).build();
//    }
//
//    @Bean
//    public NewTopic thumbnailTopicOutput(){
//        return TopicBuilder.name(AppConstants.THUMBNAIL_OUTPUT_TOPIC).partitions(5).build();
//    }
}

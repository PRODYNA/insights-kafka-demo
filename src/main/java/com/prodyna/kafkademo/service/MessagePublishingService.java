package com.prodyna.kafkademo.service;

import com.prodyna.demo.v1.user.event.UserCreated;
import com.prodyna.kafkademo.commons.KafkaConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

@Service
public class MessagePublishingService {

    private final Logger log = LoggerFactory.getLogger(MessagePublishingService.class);

    private String topicName;
    private String topicSource;
    private String topicType;

    private KafkaTemplate<String, Object> kafkaTemplate;

    public MessagePublishingService(final @Value("${kafka.topic.user.name}") String topicName,
            final @Value("${kafka.topic.user.source}") String topicSource,
            final @Value("${kafka.topic.user.type}") String topicType,
            final KafkaTemplate<String, Object> kafkaTemplate) {
        this.topicName = topicName;
        this.topicSource = topicSource;
        this.topicType = topicType;
        this.kafkaTemplate = kafkaTemplate;
    }


    public void publishMessage(final UserCreated userCreated) {
        final Message<?> message = this.buildMessage(userCreated);

        log.info("Message prepared for publishing: {}", message);

        kafkaTemplate.send(message);
    }

    private Message<?> buildMessage(final UserCreated userCreated) {
        return MessageBuilder.withPayload(userCreated)
                .setHeader(KafkaHeaders.TOPIC, topicName)
                .setHeader(KafkaHeaders.KEY, userCreated.getCreatedAt().toString())
                .setHeader(KafkaConstants.SOURCE, topicSource)
                .setHeader(KafkaConstants.TYPE, topicType)
                .setHeader(KafkaConstants.TIME, ZonedDateTime.of(LocalDateTime.now(), ZoneOffset.UTC).toString())
                .build();
    }

}

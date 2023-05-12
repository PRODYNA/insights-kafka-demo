package com.prodyna.kafkademo.config.kafka;

import lombok.AllArgsConstructor;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile({ "local" })
@AllArgsConstructor
public class KafkaTopicsConfiguration {

    @Bean
    public NewTopic kafkaTopicInvoice(final @Value("${kafka.topic.user.name}") String topicName) {
        return new NewTopic(topicName, 12, (short) 1);
    }

}

package com.prodyna.kafkademo.config.kafka.consumer;

import org.springframework.boot.autoconfigure.kafka.KafkaProperties;

import java.util.Map;

public interface KafkaConsumerProperties {

    KafkaProperties getKafkaProperties();

    Map<String, Object> getProperties();

}

package com.prodyna.kafkademo.config.kafka.producer;

import org.springframework.boot.autoconfigure.kafka.KafkaProperties;

import java.util.Map;

public interface KafkaProducerProperties {

    KafkaProperties getKafkaProperties();

    Map<String, Object> getProperties();

}

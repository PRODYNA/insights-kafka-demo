package com.prodyna.kafkademo.config.kafka.producer;

import java.util.Map;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;

public interface KafkaProducerProperties {

  KafkaProperties getKafkaProperties();

  Map<String, Object> getProperties();

}

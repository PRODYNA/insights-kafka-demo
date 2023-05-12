package com.prodyna.kafkademo.config.kafka.producer;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

@Configuration
public class KafkaProducerConfiguration {

  private KafkaProducerProperties kafkaProducerProperties;

  public KafkaProducerConfiguration(@Qualifier("kafkaProducerProperties") KafkaProducerProperties kafkaProducerProperties) {
    this.kafkaProducerProperties = kafkaProducerProperties;
  }

  @Bean
  public ProducerFactory<String, Object> producerFactory() {
    return new DefaultKafkaProducerFactory<>(kafkaProducerProperties.getProperties());
  }

  @Bean
  public KafkaTemplate<String, Object> kafkaTemplate() {
    return new KafkaTemplate<>(producerFactory());
  }

}

package com.prodyna.kafkademo.config.kafka.producer;

import io.confluent.kafka.serializers.KafkaAvroSerializer;
import io.confluent.kafka.serializers.KafkaAvroSerializerConfig;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
@AllArgsConstructor
public class LocalKafkaProducerConfiguration implements KafkaProducerProperties {

    @Getter
    private final KafkaProperties kafkaProperties;

    @Override
    public Map<String, Object> getProperties() {
        final Map<String, Object> properties = kafkaProperties.buildProducerProperties();

        properties.putAll(kafkaProperties.getProperties());

        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, KafkaAvroSerializer.class);
        properties.put(KafkaAvroSerializerConfig.AVRO_REMOVE_JAVA_PROPS_CONFIG, true);

        return properties;
    }

}

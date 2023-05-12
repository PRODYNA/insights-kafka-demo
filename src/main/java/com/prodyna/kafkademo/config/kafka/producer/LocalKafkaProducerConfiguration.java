package com.prodyna.kafkademo.config.kafka.producer;

import io.confluent.kafka.serializers.KafkaAvroSerializer;
import io.confluent.kafka.serializers.KafkaAvroSerializerConfig;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.util.Map;

@Configuration
@AllArgsConstructor
@Profile({ "local" })
public class LocalKafkaProducerConfiguration implements KafkaProducerProperties {

    public static final String VALUE_SUBJECT_NAME_STRATEGY = "value.subject.name.strategy";

    @Getter
    private final KafkaProperties kafkaProperties;

    @Override
    public Map<String, Object> getProperties() {
        final Map<String, Object> properties = kafkaProperties.buildProducerProperties();

        properties.putAll(kafkaProperties.getProperties());

        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, KafkaAvroSerializer.class);
        properties.put(VALUE_SUBJECT_NAME_STRATEGY,
                io.confluent.kafka.serializers.subject.TopicRecordNameStrategy.class);
        properties.put(KafkaAvroSerializerConfig.AVRO_REMOVE_JAVA_PROPS_CONFIG, true);

        return properties;
    }

}

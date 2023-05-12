package com.prodyna.kafkademo.service;

import lombok.AllArgsConstructor;
import org.apache.avro.specific.SpecificRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class KafkaMessageListenerService {

    private static final Logger log = LoggerFactory.getLogger(KafkaMessageListenerService.class);

    public static final String ID = "id";
    public static final String SOURCE = "source";
    public static final String TYPE = "type";
    public static final String TIME = "time";
    public static final String NO_HEADER_PROVIDED = "no_header_provided";

    private static final String PAYLOAD_INFO_LOGGING_PATTERN =
            "Thread id {} | Partition {} | Offset {} | Key {} | Timestamp {} | Payload {}";
    private static final String HEADER_INFO_LOGGING_PATTERN = "Headers: id {} | source {} | type {} | time {}";

    @KafkaListener(topics = "${kafka.topic.user.name}", clientIdPrefix = "${kafka.listener.clientIdPrefix}${kafka.topic.user.name}",
            groupId = "${kafka.topic.user.name}${kafka.listener.groupIdPostfix}")
    public void listenOrderDeliveryStateChanged(@Payload SpecificRecord payload, @Headers MessageHeaders messageHeaders,
            Acknowledgment acknowledgment) {
        logPayloadInfo(payload, messageHeaders);
        logHeaderInfo(messageHeaders);

        try {
            Thread.sleep(1000L);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        log.info("We are done with processing!");

        acknowledgment.acknowledge();
    }

    private void logPayloadInfo(SpecificRecord payload, MessageHeaders messageHeaders) {
        log.info(PAYLOAD_INFO_LOGGING_PATTERN,
                Thread.currentThread().getId(),
                messageHeaderToString(messageHeaders.getOrDefault(KafkaHeaders.RECEIVED_PARTITION, NO_HEADER_PROVIDED)),
                messageHeaderToString(messageHeaders.getOrDefault(KafkaHeaders.OFFSET, NO_HEADER_PROVIDED)),
                messageHeaderToString(messageHeaders.getOrDefault(KafkaHeaders.RECEIVED_KEY, NO_HEADER_PROVIDED)),
                messageHeaderToString(messageHeaders.getOrDefault(KafkaHeaders.RECEIVED_TIMESTAMP, NO_HEADER_PROVIDED)),
                payload);
    }

    private void logHeaderInfo(MessageHeaders messageHeaders) {
        log.info(HEADER_INFO_LOGGING_PATTERN,
                messageHeaderToString(messageHeaders.getOrDefault(ID, NO_HEADER_PROVIDED)),
                messageHeaderToString(messageHeaders.getOrDefault(SOURCE, NO_HEADER_PROVIDED)),
                messageHeaderToString(messageHeaders.getOrDefault(TYPE, NO_HEADER_PROVIDED)),
                messageHeaderToString(messageHeaders.getOrDefault(TIME, NO_HEADER_PROVIDED)));
    }

    private String messageHeaderToString(Object messageHeader) {
        if (messageHeader == null) {
            return "not found";
        }

        if (messageHeader instanceof byte[]) {
            return new String((byte[]) messageHeader);
        }

        return messageHeader.toString();
    }

}

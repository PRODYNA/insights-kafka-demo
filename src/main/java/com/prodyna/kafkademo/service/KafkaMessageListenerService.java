package com.prodyna.kafkademo.service;

import com.prodyna.demo.v1.user.event.UserCreated;
import com.prodyna.kafkademo.commons.KafkaConstants;
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

    private static final String PAYLOAD_INFO_LOGGING_PATTERN =
            "Thread id {} | Partition {} | Offset {} | Key {} | Timestamp {} | Payload {}";
    private static final String HEADER_INFO_LOGGING_PATTERN = "Headers: source {} | type {} | time {}";

    @KafkaListener(topics = "${kafka.topic.user.name}",
            clientIdPrefix = "${kafka.listener.clientIdPrefix}${kafka.topic.user.name}",
            groupId = "${kafka.topic.user.name}${kafka.listener.groupIdPostfix}")
    public void listenOrderDeliveryStateChanged(final @Payload UserCreated payload,
            final @Headers MessageHeaders messageHeaders,
            final Acknowledgment acknowledgment) {
        logPayloadInfo(payload, messageHeaders);
        logHeaderInfo(messageHeaders);

        try {
            Thread.sleep(1000L);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }

        log.info("We are done with processing, exit listener!");

        acknowledgment.acknowledge();
    }

    private void logPayloadInfo(SpecificRecord payload, MessageHeaders messageHeaders) {
        log.info(PAYLOAD_INFO_LOGGING_PATTERN,
                Thread.currentThread().getId(),
                messageHeaderToString(messageHeaders, KafkaHeaders.RECEIVED_PARTITION),
                messageHeaderToString(messageHeaders, KafkaHeaders.OFFSET),
                messageHeaderToString(messageHeaders, KafkaHeaders.RECEIVED_KEY),
                messageHeaderToString(messageHeaders, KafkaHeaders.RECEIVED_TIMESTAMP),
                payload);
    }

    private void logHeaderInfo(MessageHeaders messageHeaders) {
        log.info(HEADER_INFO_LOGGING_PATTERN,
                messageHeaderToString(messageHeaders, KafkaConstants.SOURCE),
                messageHeaderToString(messageHeaders, KafkaConstants.TYPE),
                messageHeaderToString(messageHeaders, KafkaConstants.TIME));
    }

    private String messageHeaderToString(final MessageHeaders messageHeaders, final String headerName) {
        final Object messageHeader = messageHeaders.getOrDefault(headerName, KafkaConstants.NO_HEADER_PROVIDED);


        if (messageHeader == null) {
            return "not found";
        }

        if (messageHeader instanceof byte[]) {
            return new String((byte[]) messageHeader);
        }

        return messageHeader.toString();
    }

}

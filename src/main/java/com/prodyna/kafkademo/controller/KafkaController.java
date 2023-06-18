package com.prodyna.kafkademo.controller;

import com.prodyna.demo.v1.user.event.UserCreated;
import com.prodyna.kafkademo.service.MessagePublishingService;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.ZonedDateTime;
import java.util.Random;
import java.util.stream.IntStream;

@RestController
@RequestMapping(value = "/api/kafka")
@AllArgsConstructor
public class KafkaController {

    private static final Logger log = LoggerFactory.getLogger(KafkaController.class);

    private MessagePublishingService messagePublishingService;

    @GetMapping("/publish/messages")
    public String produceInvoiceDocuments() {
        IntStream.range(0, 5).forEach(i -> messagePublishingService.publishMessage(this.buildMessage(i)));

        log.info("All messages received");

        return "OK";
    }

    private UserCreated buildMessage(final int i) {
        final int randomNumber = getRandomNumber(1, 200);

        return UserCreated.newBuilder()
                .setEmail("test" + i + randomNumber + "@demo.com")
                .setFirstName("hello" + randomNumber)
                .setLastName("world")
                .setCreatedBy("admin")
                .setCreatedAt(ZonedDateTime.now().toInstant())
                .build();
    }

    public int getRandomNumber(final int min, final int max) {
        final Random random = new Random();
        return random.nextInt(max - min) + min;
    }

}

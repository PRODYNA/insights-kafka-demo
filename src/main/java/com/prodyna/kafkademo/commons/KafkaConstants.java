package com.prodyna.kafkademo.commons;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class KafkaConstants {

    public static final String SOURCE = "source";
    public static final String TYPE = "type";
    public static final String TIME = "time";

    public static final String NO_HEADER_PROVIDED = "no_header_provided";

}

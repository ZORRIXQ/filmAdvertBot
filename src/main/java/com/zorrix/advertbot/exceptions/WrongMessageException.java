package com.zorrix.advertbot.exceptions;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
public class WrongMessageException extends RuntimeException {
    @Value("${exceptions.wrong-message.log-text}")
    String logText;

    @Value("${exceptions.wrong-message.text}")
    String response;
}

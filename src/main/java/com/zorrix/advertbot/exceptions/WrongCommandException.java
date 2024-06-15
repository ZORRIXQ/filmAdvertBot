package com.zorrix.advertbot.exceptions;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
public class WrongCommandException extends RuntimeException{
    @Value("${exceptions.wrong-command.log-text}")
    String logText;

    @Value("${exceptions.wrong-command.text}")
    String response;
}

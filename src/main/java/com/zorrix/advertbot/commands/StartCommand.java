package com.zorrix.advertbot.commands;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StartCommand implements Command {
    @Value("${command.start-command}")
    String command;

    @Value("${command.start-command.response-message}")
    String response;
}

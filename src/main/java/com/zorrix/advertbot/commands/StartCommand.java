package com.zorrix.advertbot.commands;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StartCommand implements Command {
    @Value("${command.start-command}")
    String command;

    @Override
    public String getCommand() {
        return command;
    }
}

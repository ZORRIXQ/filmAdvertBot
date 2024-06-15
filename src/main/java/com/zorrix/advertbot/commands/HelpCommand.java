package com.zorrix.advertbot.commands;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
public class HelpCommand implements Command{
    @Value("${command.help-command}")
    String command;

    @Value("${command.help-command.response-message}")
    String response;

}

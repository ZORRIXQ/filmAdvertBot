package com.zorrix.advertbot.commands;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;

public interface Command {
    public String getCommand();

}

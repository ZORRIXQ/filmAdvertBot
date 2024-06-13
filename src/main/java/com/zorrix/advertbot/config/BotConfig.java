package com.zorrix.advertbot.config;

import lombok.*;
import org.springframework.beans.factory.annotation.Value;
import lombok.experimental.FieldDefaults;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Configuration
@ComponentScan("com.zorrix.advertbot")
@Getter
//@Data
@PropertySource("classpath:application.properties")
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
public class BotConfig {
    static BotConfig instance = new BotConfig();

    @Value("${bot.name}")
    String botName;

    @Value("${bot.token}")
    String botToken;

    @Value("${bot.api-url}")
    String botApiUrl;
}

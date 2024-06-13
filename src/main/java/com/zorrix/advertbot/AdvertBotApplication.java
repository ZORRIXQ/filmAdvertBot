package com.zorrix.advertbot;

import com.zorrix.advertbot.config.BotConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class })
public class AdvertBotApplication {


    public static void main(String[] args) throws TelegramApiException {
        ApplicationContext context = SpringApplication.run(AdvertBotApplication.class, args);

        System.out.println("Starting bot");

        BotInitializer.initBot(context.getBean(AdvertBot.class));
    }

}

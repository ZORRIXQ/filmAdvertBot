package com.zorrix.advertbot;

import org.springframework.aop.ThrowsAdvice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@Component
public class BotInitializer {
    AdvertBot bot;

    @Autowired
    public BotInitializer(AdvertBot bot) {
        //Autowiring singleton bot instance in the BitInitializer bean
        this.bot = bot;
    }

    //register bot with initializer obj
    public void initBot() throws TelegramApiException {
        TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
        try {

            botsApi.registerBot(bot);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    //register bot without initializer obj
    public static void initBot(@Autowired AdvertBot bot) throws TelegramApiException {
        System.out.println("___________________\nINIT BOT\n----------------------");
        TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
        try {
            botsApi.registerBot(bot);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}

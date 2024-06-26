package com.zorrix.advertbot;

import com.zorrix.advertbot.config.BotConfig;
import com.zorrix.advertbot.service.ExecutorService;
import com.zorrix.advertbot.service.MessageHandlerService;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AdvertBot extends TelegramLongPollingBot {
    BotConfig config;
    MessageHandlerService messageHandler;

    @Autowired
    AdvertBot(BotConfig config,
              MessageHandlerService messageHandlerService) {
        this.config = config;
        this.messageHandler = messageHandlerService;
    }

    @Override
    public void onUpdateReceived(Update update) {
        //delegating the task to another node MessageHandler
        if (update.hasMessage() && update.getMessage().hasText()) {
            try {
                messageHandler.handleMessage(update.getMessage(), this);
            } catch (TelegramApiException e) {
                throw new RuntimeException(e);
            }
        } else if (update.hasCallbackQuery()) {
            //if user pressed inline buttons that returns callBackQuery
            try {
                messageHandler.handleCallBackQuery(update.getCallbackQuery(), this);
            } catch (TelegramApiException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public String getBotUsername() {
        return this.config.getBotName();
    }

    @Override
    public String getBotToken() {
        return this.config.getBotToken();
    }

    @Override
    public void onRegister() {
        System.out.println("Bot registered");
        super.onRegister();
    }

    @Override
    public void onUpdatesReceived(List<Update> updates) {
        super.onUpdatesReceived(updates);
    }

}

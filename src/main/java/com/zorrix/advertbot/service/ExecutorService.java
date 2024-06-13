package com.zorrix.advertbot.service;

import com.zorrix.advertbot.AdvertBot;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.jvnet.hk2.annotations.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ExecutorService {

    public void executeText(String text, long chatId, @Autowired AdvertBot bot) throws TelegramApiException {
        System.out.println("Executing text: " + text);

        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(text);
        bot.execute(sendMessage);
    }

    public void executeSendMessage(SendMessage sendMessage, @Autowired AdvertBot bot) throws TelegramApiException {
        bot.execute(sendMessage);
    }
}

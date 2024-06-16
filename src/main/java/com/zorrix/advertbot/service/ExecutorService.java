package com.zorrix.advertbot.service;

import com.zorrix.advertbot.AdvertBot;
import com.zorrix.advertbot.commands.CheckSubscriptionCommand;
import com.zorrix.advertbot.config.BotConfig;
import com.zorrix.advertbot.config.ChannelsConfig;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.jvnet.hk2.annotations.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
@Service
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ExecutorService {
    private final CheckSubscriptionCommand checkSubscriptionCommand;
    BotConfig botConfig;
    ChannelsConfig channelsConfig;

    @Value("${message.subscribe-message}")
    String subscribeMessage;

    @Autowired
    ExecutorService(BotConfig botConfig,
                    ChannelsConfig channelsConfig, CheckSubscriptionCommand checkSubscriptionCommand) {
        this.botConfig = botConfig;
        this.channelsConfig = channelsConfig;
        this.checkSubscriptionCommand = checkSubscriptionCommand;
    }

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

    //execute a message in which we will ask the user to subscribe to the desired channels
    public long executeSubscribeLabel(long chatId, @Autowired AdvertBot bot, Map<String, String> channels, String text) throws TelegramApiException {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(text);

        InlineKeyboardMarkup inlineKeyboardMarkup = getInlineKeyboardMarkup(channels);

        sendMessage.setReplyMarkup(inlineKeyboardMarkup);

        return bot.execute(sendMessage).getMessageId();
    }

    //getting InlineKeyboardMarkup (buttons under the text of sendMessage)
    public InlineKeyboardMarkup getInlineKeyboardMarkup(Map<String, String> channels){
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();

        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        int i = 0;
        System.out.println("channels size: " + channels.size());
        for (Map.Entry<String, String> channel : channels.entrySet()) {
                List<InlineKeyboardButton> row = new ArrayList<>();

                InlineKeyboardButton subscribeButton1 = new InlineKeyboardButton();
                subscribeButton1.setText("Channel " + (i+1) );
                subscribeButton1.setUrl(channel.getValue());

                row.add(subscribeButton1);
                rows.add(row);

                i++;
        }

        List<InlineKeyboardButton> row = new ArrayList<>();
        InlineKeyboardButton checkSubscriptionButton = new InlineKeyboardButton();
        checkSubscriptionButton.setText("✅" + "CHECK SUBSCRIPTION" + " ✅");
        checkSubscriptionButton.setCallbackData(checkSubscriptionCommand.getCommand());
        row.add(checkSubscriptionButton);
        rows.add(row);

        inlineKeyboardMarkup.setKeyboard(rows);
        return inlineKeyboardMarkup;
    }

    public void deleteMessage(@Autowired AdvertBot bot, int messageId, long chatId) throws TelegramApiException {
        DeleteMessage deleteMessage = new DeleteMessage();
        deleteMessage.setChatId(chatId);
        deleteMessage.setMessageId(messageId);

        bot.execute(deleteMessage);
    }
}

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

    public void executeSubscribeLabel(long chatId, long userId, @Autowired AdvertBot bot) throws TelegramApiException {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(subscribeMessage);

        InlineKeyboardMarkup inlineKeyboardMarkup = getInlineKeyboardMarkup(userId, bot);

        sendMessage.setReplyMarkup(inlineKeyboardMarkup);

        bot.execute(sendMessage);
    }

    private InlineKeyboardMarkup getInlineKeyboardMarkup(long userId, @Autowired AdvertBot bot) throws TelegramApiException {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();

        Map<String, String> channels = channelsConfig.getChannels();

        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        int i = 0;
        System.out.println("channels size: " + channels.size());
        for (Map.Entry<String, String> channel : channels.entrySet()) {
            if (!checkSubscriptionCommand.isUserSubscribed(userId, channel.getKey(), bot)){
                List<InlineKeyboardButton> row = new ArrayList<>();

                InlineKeyboardButton subscribeButton1 = new InlineKeyboardButton();
                subscribeButton1.setText("Channel " + (i+1) );
                subscribeButton1.setUrl(channel.getValue());

                row.add(subscribeButton1);
                rows.add(row);

                i++;
            }
        }

        List<InlineKeyboardButton> row = new ArrayList<>();
        InlineKeyboardButton checkSubscriptionButton = new InlineKeyboardButton();
        checkSubscriptionButton.setText("✅" + "CHECK SUBSCRIPTION" + " ✅");
        checkSubscriptionButton.setCallbackData("/checkSubscription");
        row.add(checkSubscriptionButton);
        rows.add(row);

        inlineKeyboardMarkup.setKeyboard(rows);
        return inlineKeyboardMarkup;
    }
}

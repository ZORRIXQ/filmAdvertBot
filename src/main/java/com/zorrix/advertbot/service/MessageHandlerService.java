package com.zorrix.advertbot.service;

import com.zorrix.advertbot.AdvertBot;
import com.zorrix.advertbot.commands.CheckSubscriptionCommand;
import com.zorrix.advertbot.commands.HelpCommand;
import com.zorrix.advertbot.commands.StartCommand;
import com.zorrix.advertbot.config.ChannelsConfig;
import com.zorrix.advertbot.exceptions.WrongCommandException;
import com.zorrix.advertbot.exceptions.WrongMessageException;
import org.jvnet.hk2.annotations.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramBot;

import java.util.Map;

@Service
@Component
public class MessageHandlerService {
    private final CheckSubscriptionCommand checkSubscriptionCommand;
    private final ChannelsConfig channelsConfig;
    ExecutorService executor;

    StartCommand startCommand;
    HelpCommand helpCommand;

    WrongCommandException wrongCommand;
    WrongMessageException wrongMessage;

    @Autowired
    MessageHandlerService(StartCommand startCommand,
                          WrongCommandException wrongCommand,
                          WrongMessageException wrongMessage,
                          HelpCommand helpCommand,
                          ExecutorService executor, CheckSubscriptionCommand checkSubscriptionCommand, ChannelsConfig channelsConfig){
        this.startCommand = startCommand;
        this.wrongCommand = wrongCommand;
        this.wrongMessage = wrongMessage;
        this.helpCommand = helpCommand;
        this.executor = executor;
        this.checkSubscriptionCommand = checkSubscriptionCommand;
        this.channelsConfig = channelsConfig;
    }

    public void handleMessage(Message message, @Autowired AdvertBot bot) throws TelegramApiException {
        SendMessage sendMessage = new SendMessage();
        StringBuilder textToSend = new StringBuilder();

        if (message.hasText()) {
            String text = message.getText().trim();
            long chatId = message.getChatId();

            sendMessage.setChatId(chatId);

            if (!text.startsWith("/")) {
                Map<String, String> channels = checkSubscriptionCommand.isUserSubscribedToAllChannels(message.getFrom().getId(), channelsConfig.getChannels(), bot);

                if (!channels.isEmpty()){
                    executor.executeSubscribeLabel(chatId, message.getFrom().getId(), bot, channels);
                    return;
                }
                else
                    sendMessage.setText("In progress");
            } else {
                if (text.equals(startCommand.getCommand())){
                    textToSend.append(startCommand.getResponse());
                } else if (text.equals(helpCommand.getCommand())){
                    textToSend.append(helpCommand.getResponse());
                }

                //more commands

                else{
                    textToSend.append(wrongCommand.getResponse());
                }
                if (textToSend.isEmpty()) {
                    textToSend.append("Unhandled exception has been occurred");
                }
                sendMessage.setText(textToSend.toString());
            }
            executor.executeSendMessage(sendMessage, bot);
        }
    }
}

package com.zorrix.advertbot.service;

import com.zorrix.advertbot.AdvertBot;
import com.zorrix.advertbot.commands.CheckSubscriptionCommand;
import com.zorrix.advertbot.commands.HelpCommand;
import com.zorrix.advertbot.commands.StartCommand;
import com.zorrix.advertbot.config.ChannelsConfig;
import com.zorrix.advertbot.exceptions.WrongCommandException;
import com.zorrix.advertbot.exceptions.WrongMessageException;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
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
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MessageHandlerService {
    //Loading all the variables we need
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

        String text = message.getText().trim();
        long chatId = message.getChatId();

        if (!text.startsWith("/")) {
            handleCommand(message, chatId, bot);
        } else {
            handleNonCommand(message, chatId, bot);
        }
    }

    private void handleCommand(Message message, long chatId,@Autowired AdvertBot bot) throws TelegramApiException {
        SendMessage sendMessage = new SendMessage();
        Map<String, String> channels = checkSubscriptionCommand.isUserSubscribedToAllChannels(message.getFrom().getId(), channelsConfig.getChannels(), bot);

        if (!channels.isEmpty()){
            executor.executeSubscribeLabel(chatId, message.getFrom().getId(), bot, channels);
        } else {
            sendMessage.setText("In progress");
            executor.executeSendMessage(sendMessage, bot);
        }
    }

    private void handleNonCommand(Message message, long chatId, @Autowired AdvertBot bot) throws TelegramApiException {
        String text = message.getText().trim();
        StringBuilder textToSend = new StringBuilder();
        SendMessage sendMessage = new SendMessage();

        if (text.equals(startCommand.getCommand())){
            textToSend.append(startCommand.getResponse());
        } else if (text.equals(helpCommand.getCommand())){
            textToSend.append(helpCommand.getResponse());
        }
        //Another commands

        else{
            textToSend.append(wrongMessage.getResponse());
        }
        sendMessage.setText(textToSend.toString());
        sendMessage.setChatId(chatId);
        executor.executeSendMessage(sendMessage, bot);
    }

}

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
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

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

    Queue<Integer> sentMessageIds;

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
        sentMessageIds = new LinkedList<>();
    }

    public void handleMessage(Message message, @Autowired AdvertBot bot) throws TelegramApiException {

        String text = message.getText().trim();
        long chatId = message.getChatId();

        if (!text.startsWith("/")) {
            handleNonCommand(message.getFrom().getId(), chatId, bot);
        } else {
            handleCommand(message, chatId, bot);
        }
    }

    //checking if user has already subscribed to channels or not and handling further actions
    private void handleNonCommand(long userId, long chatId, @Autowired AdvertBot bot) throws TelegramApiException {
        SendMessage sendMessage = new SendMessage();
        Map<String, String> channels = checkSubscriptionCommand
                .isUserSubscribedToAllChannels(userId, channelsConfig.getChannels(), bot);

        if (!channels.isEmpty()){
            String text = "You haven't subscribed for the channels: ";
            long sentMsgId = executor.executeSubscribeLabel(chatId, bot, channels, text);
            sentMessageIds.add((int) sentMsgId);
        } else {
            sendMessage.setText("You have subscribed to all the channels! in progress");
            sendMessage.setChatId(chatId);
            executor.executeSendMessage(sendMessage, bot);
            for (int sentMsgId : sentMessageIds){
                executor.deleteMessage(bot, sentMsgId, chatId);
                sentMessageIds.remove(sentMsgId);
            }
            //in progress
        }
    }


    private void handleSubscriptionDialog(Map<String, String> channels, long chatId, @Autowired AdvertBot bot) throws TelegramApiException {
        String text = "You haven't subscribed for the channels: ";

        long sentMsgId = executor.executeSubscribeLabel(chatId, bot, channels, text);
        sentMessageIds.add((int) sentMsgId);
    }

    //Handling all the messages starting with '/'
    private void handleCommand(Message message, long chatId, @Autowired AdvertBot bot) throws TelegramApiException {
        String text = message.getText().trim();
        StringBuilder textToSend = new StringBuilder();
        SendMessage sendMessage = new SendMessage();

        if (text.equals(startCommand.getCommand())){
            textToSend.append(startCommand.getResponse());

            Map<String, String> map =  checkSubscriptionCommand
                    .isUserSubscribedToAllChannels(message.getFrom().getId(), channelsConfig.getChannels(), bot);

            if (!map.isEmpty()){
                InlineKeyboardMarkup inlineKeyboardMarkup = executor.getInlineKeyboardMarkup(map);
                sendMessage.setReplyMarkup(inlineKeyboardMarkup);
            }
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

    //handling buttons with callBackData presses
    public void handleCallBackQuery(CallbackQuery callbackQuery, @Autowired AdvertBot bot) throws TelegramApiException {
        String callBackData = callbackQuery.getData();
        System.out.println("Received callback query: " + callBackData);

        if (callBackData.equals(checkSubscriptionCommand.getCommand())){
            Map<String, String> channels = checkSubscriptionCommand
                    .isUserSubscribedToAllChannels(callbackQuery.getFrom().getId(), channelsConfig.getChannels(), bot);
            if (!channels.isEmpty()){
                handleSubscriptionDialog(channels, callbackQuery.getFrom().getId(), bot);

                for (int i = 0; i < sentMessageIds.size() && !sentMessageIds.isEmpty(); i++) {
                    executor.deleteMessage(bot, sentMessageIds.poll(), callbackQuery.getMessage().getChatId());
                }
            } else {
                SendMessage sendMessage = new SendMessage();
                sendMessage.setText("You have subscribed to all the channels!");
                sendMessage.setChatId(callbackQuery.getFrom().getId());
                executor.executeSendMessage(sendMessage, bot);
            }

        }
    }

}

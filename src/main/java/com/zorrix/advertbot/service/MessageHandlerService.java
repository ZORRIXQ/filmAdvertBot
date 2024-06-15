package com.zorrix.advertbot.service;

import com.zorrix.advertbot.commands.HelpCommand;
import com.zorrix.advertbot.commands.StartCommand;
import com.zorrix.advertbot.exceptions.WrongCommandException;
import com.zorrix.advertbot.exceptions.WrongMessageException;
import org.jvnet.hk2.annotations.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

@Service
@Component
public class MessageHandlerService {
    StartCommand startCommand;
    HelpCommand helpCommand;

    WrongCommandException wrongCommand;
    WrongMessageException wrongMessage;

    @Autowired
    MessageHandlerService(StartCommand startCommand,
                          WrongCommandException wrongCommand,
                          WrongMessageException wrongMessage,
                          HelpCommand helpCommand){
        this.startCommand = startCommand;
        this.wrongCommand = wrongCommand;
        this.wrongMessage = wrongMessage;
        this.helpCommand = helpCommand;
    }

    public SendMessage handleMessage(Message message) {
        SendMessage sendMessage = new SendMessage();
        StringBuilder textToSend = new StringBuilder();

        if (message.hasText()) {
            String text = message.getText().trim();
            long chatId = message.getChatId();

            sendMessage.setChatId(chatId);

            if (text.toLowerCase().startsWith("/")) {
                if (text.equals(startCommand.getCommand())){
                    textToSend.append(startCommand.getResponse());
                } else if (text.equals(helpCommand.getCommand())){
                    textToSend.append(helpCommand.getResponse());
                }

                //more commands

                else{
                    textToSend.append(wrongCommand.getResponse());
                }
            } else {
                textToSend.append(wrongMessage.getResponse());
            }
        }
        sendMessage.setText(textToSend.toString());
        return sendMessage;
    }
}

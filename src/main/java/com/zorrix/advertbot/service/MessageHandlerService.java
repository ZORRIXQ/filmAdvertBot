package com.zorrix.advertbot.service;

import com.zorrix.advertbot.commands.StartCommand;
import com.zorrix.advertbot.exceptions.WrongCommandMessage;
import org.jvnet.hk2.annotations.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

@Service
@Component
public class MessageHandlerService {
    StartCommand startCommand;
    WrongCommandMessage wrongCommand;


    @Autowired
    MessageHandlerService(StartCommand startCommand, WrongCommandMessage wrongCommand){
        this.startCommand = startCommand;
        this.wrongCommand = wrongCommand;
    }

    public SendMessage handleMessage(Message message) {
        SendMessage sendMessage = new SendMessage();
        if (message.hasText()) {
            String text = message.getText().trim();
            long chatId = message.getChatId();

            sendMessage.setChatId(chatId);

            if (text.toLowerCase().startsWith("/")) {
                if (text.equals(startCommand.getCommand())){
                    sendMessage.setText("Hello, user!");
                }
                else
                    sendMessage.setText(wrongCommand.getResponse());
                //more commands
            }

            if (sendMessage.getText().isEmpty())
                sendMessage.setText("EBLAN");
        }
        return sendMessage;
    }
}

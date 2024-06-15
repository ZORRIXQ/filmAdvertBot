package com.zorrix.advertbot.commands;

import com.zorrix.advertbot.AdvertBot;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.groupadministration.GetChatMember;
import org.telegram.telegrambots.meta.api.objects.chatmember.ChatMember;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@Getter
@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CheckSubscriptionCommand implements Command{
    @Value("${command.check-subscription-command}")
    String command;

    @Value("${command.check-subscription-command}" + "   ____")
    String response;

    public boolean isUserSubscribed(long userId, String chatId, @Autowired AdvertBot bot) {
        GetChatMember getChatMember = new GetChatMember();
        getChatMember.setChatId(chatId);
        getChatMember.setUserId(userId);

        try {
            System.out.println("trying chatid: " + chatId);
            ChatMember chatMember = bot.execute(getChatMember);
            String status = chatMember.getStatus();
            boolean isMember = status.equals("member") || status.equals("administrator") || status.equals("creator");
            System.out.println("isMember: " + isMember);
            return isMember;
        } catch (TelegramApiException e) {
            e.printStackTrace();
            return false;
        }
    }

    public Map<String, String> isUserSubscribedToAllChannels(long userId, Map<String, String> channels, @Autowired AdvertBot bot) {
        Map<String, String> map = new LinkedHashMap<>();

        for (Map.Entry<String, String> channel : channels.entrySet()) {
            if (!isUserSubscribed(userId, channel.getKey(), bot)) {
                map.put(channel.getKey(), channel.getValue());
            }
        }
        return map;
    }
}

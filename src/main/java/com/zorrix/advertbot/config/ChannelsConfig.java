package com.zorrix.advertbot.config;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Component
@Getter
@PropertySource("classpath:application.properties")
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
public class ChannelsConfig {
    @Value("#{'${channels.channel-ids}'.split(',')}")
    ArrayList<String> channelIds;

    @Value("#{'${channels.channel-urls}'.split(';')}")
    ArrayList<String> channelUrls;

    public Map<String, String> getChannels() {
        Map<String, String> channels = new HashMap<>();
        for (int i = 0; i < channelIds.size(); i++) {
            String id = channelIds.get(i);
            if (channelIds.get(i).startsWith("-"))
                id = new StringBuilder(channelIds.get(i)).insert(1, "100").toString();
            channels.put(id, channelUrls.get(i));
        }

        return channels;
    }
}
package com.kharvoll.kharvollbot.listener;


import com.kharvoll.kharvollbot.KharvollBot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@Component
public class BotInitializer {

    private final KharvollBot kharvollBot;

    @Autowired
    public BotInitializer(KharvollBot kharvollBot) {
        this.kharvollBot = kharvollBot;
    }


    @EventListener({ApplicationStartedEvent.class})
    public void init() throws TelegramApiException {
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
        telegramBotsApi.registerBot(kharvollBot);
    }

}

package com.kharvoll.kharvollbot;

import com.kharvoll.kharvollbot.service.Dispatcher;
import java.io.Serializable;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;


@Component
public class KharvollBot extends TelegramLongPollingBot {

    private final String botUsername;
    private final Dispatcher dispatcher;

    public KharvollBot(@Value("${kharvoll-bot.bot.token}") String botToken,
                               @Value("${kharvoll-bot.bot.username}") String botUsername,
                               Dispatcher dispatcher) {
        super(botToken);
        this.dispatcher = dispatcher;
        this.botUsername = botUsername;
    }


    @Override
    public void onUpdateReceived(Update update) {
        List<BotApiMethod<?>> result = dispatcher.dispatch(update);

        for (BotApiMethod<?> method : result) {
            try {
                execute(method);
            } catch (TelegramApiException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public String getBotUsername() {
        return botUsername;
    }
}

package com.kharvoll.kharvollbot;

import com.kharvoll.kharvollbot.service.Dispatcher;
import com.kharvoll.kharvollbot.service.exception.KharvollBotException;
import java.io.Serializable;
import java.util.List;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;


@Log4j2
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
        try {
            List<BotApiMethod<?>> result = dispatcher.dispatch(update);

            for (BotApiMethod<?> method : result) {
                execute(method);
            }

        } catch (KharvollBotException e) {
            log.warn(e.getLocalizedMessage(), e);
            handleException(e, update);
        }

    }

    @Override
    public String getBotUsername() {
        return botUsername;
    }

    private void handleException(Exception exception, Update update) {
        String errorMessage = exception.getLocalizedMessage();

        SendMessage sendMessage = new SendMessage();
        sendMessage.setText(errorMessage);

        if (update.hasMessage()) {
            Message message = update.getMessage();
            sendMessage.setChatId(message.getChatId());
        } else if (update.hasCallbackQuery()) {
            CallbackQuery callbackQuery = update.getCallbackQuery();
            Message message = callbackQuery.getMessage();
            sendMessage.setChatId(message.getChatId());

        } else {
            log.warn("Cannot handle exception. Cannot resolve 'chatId;");
        }

        execute(sendMessage);
    }

    @Override
    public <T extends Serializable, Method extends BotApiMethod<T>> T execute(Method method) {
        try {
            return super.execute(method);
        } catch (TelegramApiException e) {
            log.error("Error during execution method", e);
            throw new UnsupportedOperationException();
        }

    }
}

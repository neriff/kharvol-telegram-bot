package com.kharvoll.kharvollbot.handler;

import java.util.List;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;

public interface CommandHandler {

    void preHandle(Update update);

    List<? extends BotApiMethod<?>> handle(Update update);

    void postHandle(Update update);

    boolean isApplicable(Update update);

}

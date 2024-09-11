package com.kharvoll.kharvollbot.service;

import com.kharvoll.kharvollbot.handler.CommandHandler;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
public class Dispatcher {

    private final List<CommandHandler> commandHandlers;

    @Autowired
    public Dispatcher(List<CommandHandler> commandHandlers) {
        this.commandHandlers = commandHandlers;
    }


    public List<BotApiMethod<?>> dispatch(Update update) {
        List<BotApiMethod<?>> responses = new ArrayList<>();

        List<CommandHandler> filteredHandlers = commandHandlers.stream()
                .filter(commandHandler -> commandHandler.isApplicable(update))
                .toList();

        filteredHandlers.forEach(commandHandler -> {
            commandHandler.preHandle(update);
            responses.addAll(commandHandler.handle(update));
            commandHandler.postHandle(update);
        });

        return responses;
    }

}

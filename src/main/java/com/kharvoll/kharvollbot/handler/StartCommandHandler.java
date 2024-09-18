package com.kharvoll.kharvollbot.handler;

import com.kharvoll.kharvollbot.domain.ConversationState;
import com.kharvoll.kharvollbot.service.UserSessionService;
import java.util.List;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

@Component
public class StartCommandHandler extends AbstractCommandHandler {

    public static final String COMMAND_START = "/start";

    protected StartCommandHandler(UserSessionService userSessionService) {
        super(userSessionService);
    }

    @Override
    public List<? extends BotApiMethod<?>> handle(Update update) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(getChatId(update));
        sendMessage.setText("Вітаю!");

        KeyboardButton pollSettingsButton = new KeyboardButton(ConfigurePollsHandler.COMMAND_CONFIGURE_POLLS);
        KeyboardRow keyboardRow = new KeyboardRow(List.of(pollSettingsButton));

        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup(List.of(keyboardRow));
        keyboardMarkup.setResizeKeyboard(true);
        keyboardMarkup.setSelective(true);
        keyboardMarkup.setOneTimeKeyboard(false);

        sendMessage.setReplyMarkup(keyboardMarkup);

        return List.of(sendMessage);
    }

    @Override
    public boolean isApplicable(Update update) {
        return chatTypeIs(update, CHAT_TYPE_PRIVATE) && isCommand(update) && hasText(update, COMMAND_START);
    }

    @Override
    public void preHandle(Update update) {
        super.preHandle(update);

        if (!userSessionService.isSessionExists(getChatId(update))) {
            userSessionService.createSession(getChatId(update));
        }

        userSessionService.setConversationState(getChatId(update), ConversationState.CONVERSATION_STARTED);
    }

    @Override
    public void postHandle(Update update) {
        userSessionService.setConversationState(getChatId(update), ConversationState.MAIN_MENU);
    }
}

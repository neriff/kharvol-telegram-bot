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
public class ConfigurePollsHandler extends AbstractCommandHandler {

    public static final String COMMAND_CONFIGURE_POLLS = "⚙\uFE0F Налаштування опитувань";

    protected ConfigurePollsHandler(UserSessionService userSessionService) {
        super(userSessionService);
    }


    @Override
    public List<? extends BotApiMethod<?>> handle(Update update) {

        KeyboardButton allPollsButton = new KeyboardButton(GetPollsCommandHandler.COMMAND_GET_POOLS);
        KeyboardButton addPollButton = new KeyboardButton(AddPollCommandHandler.COMMAND_ADD_POLL);
        KeyboardButton backButton = new KeyboardButton(BackCommandHandler.COMMAND_BACK);

        KeyboardRow keyboardRow1 = new KeyboardRow(List.of(allPollsButton, addPollButton));
        KeyboardRow keyboardRow2 = new KeyboardRow(List.of(backButton));

        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup(List.of(keyboardRow1, keyboardRow2));
        keyboardMarkup.setSelective(true);
        keyboardMarkup.setResizeKeyboard(true);
        keyboardMarkup.setOneTimeKeyboard(false);

        SendMessage sendMessage = new SendMessage();
        sendMessage.setText(COMMAND_CONFIGURE_POLLS);
        sendMessage.setChatId(getChatId(update));
        sendMessage.setReplyMarkup(keyboardMarkup);

        return List.of(sendMessage);
    }

    @Override
    public boolean isApplicable(Update update) {
        return hasText(update, COMMAND_CONFIGURE_POLLS);
    }

    @Override
    public void preHandle(Update update) {

    }

    @Override
    public void postHandle(Update update) {
        userSessionService.setConversationState(getChatId(update), ConversationState.POLL_CONFIGURATION);
    }
}

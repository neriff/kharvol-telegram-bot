package com.kharvoll.kharvollbot.handler;

import com.kharvoll.kharvollbot.domain.ConversationState;
import com.kharvoll.kharvollbot.service.UserSessionService;
import java.time.DayOfWeek;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

@Component
public class AddPollCommandHandler extends AbstractCommandHandler {

    public static final String COMMAND_ADD_POLL = "➕ Додати опитування";

    protected AddPollCommandHandler(UserSessionService userSessionService) {
        super(userSessionService);
    }

    @Override
    public List<? extends BotApiMethod<?>> handle(Update update) {
        KeyboardButton mondayButton = new KeyboardButton(StringUtils.capitalize(DayOfWeek.MONDAY.getDisplayName(TextStyle.FULL, new Locale("uk", "UA"))));
        KeyboardButton tuesdayButton = new KeyboardButton(StringUtils.capitalize(DayOfWeek.TUESDAY.getDisplayName(TextStyle.FULL, new Locale("uk", "UA"))));
        KeyboardButton wednesdayButton = new KeyboardButton(StringUtils.capitalize(DayOfWeek.WEDNESDAY.getDisplayName(TextStyle.FULL, new Locale("uk", "UA"))));
        KeyboardButton thursdayButton = new KeyboardButton(StringUtils.capitalize(DayOfWeek.THURSDAY.getDisplayName(TextStyle.FULL, new Locale("uk", "UA"))));
        KeyboardButton fridayButton = new KeyboardButton(StringUtils.capitalize(DayOfWeek.FRIDAY.getDisplayName(TextStyle.FULL, new Locale("uk", "UA"))));
        KeyboardButton saturdayButton = new KeyboardButton(StringUtils.capitalize(DayOfWeek.SATURDAY.getDisplayName(TextStyle.FULL, new Locale("uk", "UA"))));
        KeyboardButton sundayButton = new KeyboardButton(StringUtils.capitalize(DayOfWeek.SUNDAY.getDisplayName(TextStyle.FULL, new Locale("uk", "UA"))));

        KeyboardRow keyboardRow1 = new KeyboardRow(List.of(mondayButton, tuesdayButton));
        KeyboardRow keyboardRow2 = new KeyboardRow(List.of(wednesdayButton, thursdayButton));
        KeyboardRow keyboardRow3 = new KeyboardRow(List.of(fridayButton, saturdayButton));
        KeyboardRow keyboardRow4 = new KeyboardRow(List.of(sundayButton));

        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup(List.of(keyboardRow1, keyboardRow2, keyboardRow3, keyboardRow4));

        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(getChatId(update).toString());
        sendMessage.setText("Виберіть день коли опитування повинно бути створено для абонементів");
        sendMessage.setReplyMarkup(replyKeyboardMarkup);
        
        return List.of(sendMessage);
    }

    @Override
    public boolean isApplicable(Update update) {
        return hasText(update, COMMAND_ADD_POLL);
    }

    @Override
    public void preHandle(Update update) {

    }

    @Override
    public void postHandle(Update update) {
        userSessionService.setSessionParameter(getChatId(update), UserSessionService.SESSION_PARAMETER_CONVERSATION_STATE, ConversationState.WAITING_FOR_CREATION_DAY.name());
    }
}

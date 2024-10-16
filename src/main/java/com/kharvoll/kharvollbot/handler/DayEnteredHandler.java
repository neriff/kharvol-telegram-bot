package com.kharvoll.kharvollbot.handler;

import com.kharvoll.kharvollbot.domain.ConversationState;
import com.kharvoll.kharvollbot.service.UserSessionService;
import java.nio.file.WatchEvent;
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
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

@Component
public class DayEnteredHandler extends AbstractCommandHandler {
    public static final String DAY_MONDAY = StringUtils.capitalize(DayOfWeek.MONDAY.getDisplayName(TextStyle.FULL, new Locale("uk", "UA")));
    public static final String DAY_TUESDAY = StringUtils.capitalize(DayOfWeek.TUESDAY.getDisplayName(TextStyle.FULL, new Locale("uk", "UA")));
    public static final String DAY_WEDNESDAY = StringUtils.capitalize(DayOfWeek.WEDNESDAY.getDisplayName(TextStyle.FULL, new Locale("uk", "UA")));
    public static final String DAY_THURSDAY = StringUtils.capitalize(DayOfWeek.THURSDAY.getDisplayName(TextStyle.FULL, new Locale("uk", "UA")));
    public static final String DAY_FRIDAY = StringUtils.capitalize(DayOfWeek.FRIDAY.getDisplayName(TextStyle.FULL, new Locale("uk", "UA")));
    public static final String DAY_SATURDAY = StringUtils.capitalize(DayOfWeek.SATURDAY.getDisplayName(TextStyle.FULL, new Locale("uk", "UA")));
    public static final String DAY_SUNDAY = StringUtils.capitalize(DayOfWeek.SUNDAY.getDisplayName(TextStyle.FULL, new Locale("uk", "UA")));

    public DayEnteredHandler(UserSessionService userSessionService) {
        super(userSessionService);
    }

    @Override
    public List<? extends BotApiMethod<?>> handle(Update update) {
        String day = update.getMessage().getText();

        if (conversationStateIs(update, ConversationState.WAITING_FOR_CREATION_DAY)) {
            userSessionService.setSessionParameter(getChatId(update), UserSessionService.SESSION_PARAMETER_CREATION_DAY, day);

            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(getChatId(update).toString());
            sendMessage.setText("Виберіть день опитування повинно бути переслано до загальної групи");

            return List.of(sendMessage);
        } else if (conversationStateIs(update, ConversationState.WAITING_FOR_FORWARDING_DAY)) {
            userSessionService.setSessionParameter(getChatId(update), UserSessionService.SESSION_PARAMETER_FORWARDING_DAY, day);

            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(getChatId(update));
            sendMessage.setText("Введіть назву опитування");
            sendMessage.setReplyMarkup(new ReplyKeyboardRemove(true));

            return List.of(sendMessage);
        }

        return List.of();
    }

    @Override
    public boolean isApplicable(Update update) {
        boolean enteredDayOfWeek = hasAnyText(update, DAY_MONDAY, DAY_TUESDAY, DAY_WEDNESDAY, DAY_THURSDAY, DAY_FRIDAY, DAY_SATURDAY, DAY_SUNDAY);

        return enteredDayOfWeek &&
                (conversationStateIs(update, ConversationState.WAITING_FOR_CREATION_DAY) || conversationStateIs(update, ConversationState.WAITING_FOR_FORWARDING_DAY));
    }

    @Override
    public void postHandle(Update update) {
        Long chatId = getChatId(update);
        if (conversationStateIs(update, ConversationState.WAITING_FOR_CREATION_DAY)) {
            userSessionService.setConversationState(chatId, ConversationState.WAITING_FOR_FORWARDING_DAY);
            
        } else if (conversationStateIs(update, ConversationState.WAITING_FOR_FORWARDING_DAY)) {
            userSessionService.setConversationState(chatId, ConversationState.WAITING_FOR_QUESTION_TEMPLATE);
        }
    }
}

package com.kharvoll.kharvollbot.handler;

import com.kharvoll.kharvollbot.domain.ConversationState;
import com.kharvoll.kharvollbot.service.UserSessionService;
import java.util.AbstractMap;
import java.util.AbstractMap.SimpleEntry;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import org.glassfish.jersey.internal.util.Producer;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

@Component
public class BackCommandHandler extends AbstractCommandHandler {

    public static final String COMMAND_BACK = "\uD83D\uDD19 Назад";

    private final Map<ConversationState, Function<Update, SendMessage>> map = Map.ofEntries(
            new SimpleEntry<>(ConversationState.MAIN_MENU, update -> {
                SendMessage sendMessage = new SendMessage();
                sendMessage.setChatId(getChatId(update));
                sendMessage.setText(COMMAND_BACK);

                KeyboardButton pollSettingsButton = new KeyboardButton(ConfigurePollsHandler.COMMAND_CONFIGURE_POLLS);
                KeyboardRow keyboardRow = new KeyboardRow(List.of(pollSettingsButton));

                ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup(List.of(keyboardRow));
                keyboardMarkup.setResizeKeyboard(true);
                keyboardMarkup.setSelective(true);
                keyboardMarkup.setOneTimeKeyboard(false);

                sendMessage.setReplyMarkup(keyboardMarkup);

                return sendMessage;
            })
    );

    protected BackCommandHandler(UserSessionService userSessionService) {
        super(userSessionService);
    }

    @Override
    public void preHandle(Update update) {
    }

    @Override
    public List<? extends BotApiMethod<?>> handle(Update update) {

        ConversationState currentConversationState = userSessionService.getCurrentConversationState(getChatId(update));
        ConversationState previousState = currentConversationState.getPreviousState();

        SendMessage sendMessage = map.get(previousState).apply(update);

        return List.of(sendMessage);
    }

    @Override
    public void postHandle(Update update) {
        Long chatId = getChatId(update);

        ConversationState currentConversationState = userSessionService.getCurrentConversationState(chatId);
        ConversationState previousState = currentConversationState.getPreviousState();
        userSessionService.setConversationState(chatId, previousState);
    }

    @Override
    public boolean isApplicable(Update update) {
        return hasText(update, COMMAND_BACK)
                && userSessionService.getCurrentConversationState(getChatId(update)).getPreviousState() != null;
    }
}

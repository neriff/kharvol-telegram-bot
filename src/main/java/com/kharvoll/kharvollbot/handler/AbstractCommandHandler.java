package com.kharvoll.kharvollbot.handler;

import com.fasterxml.jackson.databind.JsonNode;
import com.kharvoll.kharvollbot.domain.ConversationState;
import com.kharvoll.kharvollbot.service.UserSessionService;
import com.kharvoll.kharvollbot.service.exception.ErrorMessage;
import com.kharvoll.kharvollbot.service.exception.UnauthorizedException;
import java.util.Arrays;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Value;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Update;

public abstract class AbstractCommandHandler implements CommandHandler {

    public static final String CHAT_TYPE_PRIVATE = "private";
    public static final String CHAT_TYPE_SUPERGROUP = "supergroup";

    @Value("${kharvoll-bot.bot.admin-id}")
    private Long adminId;

    protected final UserSessionService userSessionService;

    protected AbstractCommandHandler(UserSessionService userSessionService) {
        this.userSessionService = userSessionService;
    }


    @Override
    public void preHandle(Update update) {
        if (!isAdmin(update)) {
            throw new UnauthorizedException(ErrorMessage.PERMISSION_DONT_HAVE_PERMISSION_TO_EXECUTE_THIS_COMMAND);
        }

    }

    @Override
    public void postHandle(Update update) {

    }

    protected boolean isAdmin(Update update) {
        return getChatId(update).equals(adminId);
    }

    protected boolean hasText(Update update, String text) {
        return update.hasMessage()
                && update.getMessage().hasText()
                && update.getMessage().getText().equals(text);
    }

    protected boolean hasText(Update update) {
        return update.hasMessage() && update.getMessage().hasText();
    }

    protected boolean hasAnyText(Update update, String ... texts) {
        return hasText(update) && Arrays.stream(texts).toList()
                .contains(update.getMessage().getText());
    }

    protected boolean isCommand(Update update) {
        return update.hasMessage() && update.getMessage().isCommand();
    }

    protected boolean conversationStateIs(Update update, ConversationState state) {
        Long chatId = getChatId(update);
        Optional<JsonNode> conversationSateOptional = userSessionService.getSessionParameter(chatId, UserSessionService.SESSION_PARAMETER_CONVERSATION_STATE);

        return conversationSateOptional.isPresent() && conversationSateOptional.get().asText().equals(state.name());
    }

    protected Long getChatId(Update update) {
       return getChat(update).getId();
    }

    protected Chat getChat(Update update) {
        if (update.hasMessage()) {
            return update.getMessage().getChat();
        } else {
            return update.getCallbackQuery().getMessage().getChat();
        }
    }

    public boolean chatTypeIs(Update update, String type) {
        return getChat(update).getType().equalsIgnoreCase(type);
    }
}

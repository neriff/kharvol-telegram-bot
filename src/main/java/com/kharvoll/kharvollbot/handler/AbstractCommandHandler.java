package com.kharvoll.kharvollbot.handler;

import com.fasterxml.jackson.databind.JsonNode;
import com.kharvoll.kharvollbot.domain.ConversationState;
import com.kharvoll.kharvollbot.service.UserSessionService;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Value;
import org.telegram.telegrambots.meta.api.objects.Update;

public abstract class AbstractCommandHandler implements CommandHandler {

    @Value("${kharvoll-bot.bot.admin-id}")
    private Long adminId;

    protected final UserSessionService userSessionService;

    protected AbstractCommandHandler(UserSessionService userSessionService) {
        this.userSessionService = userSessionService;
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

    protected boolean isCommand(Update update) {
        return update.hasMessage() && update.getMessage().isCommand();
    }

    protected boolean conversationStateIs(Update update, ConversationState state) {
        Long chatId = getChatId(update);
        Optional<JsonNode> conversationSateOptional = userSessionService.getSessionParameter(chatId, UserSessionService.SESSION_PARAMETER_CONVERSATION_STATE);

        return conversationSateOptional.isPresent() && conversationSateOptional.get().asText().equals(state.name());
    }

    protected Long getChatId(Update update) {
        if (update.hasMessage()) {
            return update.getMessage().getChatId();
        } else {
            return update.getCallbackQuery().getMessage().getChatId();
        }
    }

}

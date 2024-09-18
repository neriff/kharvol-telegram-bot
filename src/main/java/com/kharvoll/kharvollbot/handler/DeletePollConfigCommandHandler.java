package com.kharvoll.kharvollbot.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.kharvoll.kharvollbot.persistence.repository.PollConfigRepository;
import com.kharvoll.kharvollbot.service.UserSessionService;
import java.util.List;
import lombok.SneakyThrows;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
@Transactional
@Scope(proxyMode = ScopedProxyMode.TARGET_CLASS)
public class DeletePollConfigCommandHandler extends AbstractCommandHandler {

    public static final String CALLBACK_DATA_PROPERTY_ID = "id";
    public static final String CALLBACK_DATA_PROPERTY_ACTION = "action";
    public static final String ACTION_DELETE_POLL_CONFIG = "deletePollConfig";
    public static final String CALLBACK_DATA_TEMPLATE = "{\"action\":\"%s\",\"id\":\"%s\"}";

    private final PollConfigRepository pollConfigRepository;

    protected DeletePollConfigCommandHandler(UserSessionService userSessionService,
                                             PollConfigRepository pollConfigRepository) {
        super(userSessionService);
        this.pollConfigRepository = pollConfigRepository;
    }

    @SneakyThrows
    @Override
    public List<? extends BotApiMethod<?>> handle(Update update) {
        JsonNode dataObject = new ObjectMapper().readTree(update.getCallbackQuery().getData());
        String pollConfigId = dataObject.get(CALLBACK_DATA_PROPERTY_ID).asText();

        pollConfigRepository.deleteById(pollConfigId);

        DeleteMessage deleteMessage = new DeleteMessage();
        deleteMessage.setChatId(update.getCallbackQuery().getMessage().getChatId());
        deleteMessage.setMessageId(update.getCallbackQuery().getMessage().getMessageId());

        return List.of(deleteMessage);
    }

    @Override
    public boolean isApplicable(Update update) {
        boolean isApplicable = chatTypeIs(update, CHAT_TYPE_PRIVATE) && update.hasCallbackQuery()
                && update.getCallbackQuery().getData() != null;

        if (isApplicable) {
            String data = update.getCallbackQuery().getData();
            JsonNode dataObject;

            try {
                dataObject = new ObjectMapper().readTree(data);
            } catch (JsonProcessingException e) {
                dataObject = JsonNodeFactory.instance.objectNode();
            }

            isApplicable = dataObject.has(CALLBACK_DATA_PROPERTY_ACTION)
                    && dataObject.get(CALLBACK_DATA_PROPERTY_ACTION).asText().equals(ACTION_DELETE_POLL_CONFIG);
        }

       return isApplicable;
    }
}

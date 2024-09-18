package com.kharvoll.kharvollbot.handler;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.kharvoll.kharvollbot.domain.ConversationState;
import com.kharvoll.kharvollbot.persistence.model.PollConfig;
import com.kharvoll.kharvollbot.persistence.repository.PollConfigRepository;
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
public class QuestionTemplateEnteredHandler extends AbstractCommandHandler {

    private final PollConfigRepository pollConfigRepository;

    protected QuestionTemplateEnteredHandler(UserSessionService userSessionService,
                                             PollConfigRepository pollConfigRepository) {
        super(userSessionService);
        this.pollConfigRepository = pollConfigRepository;
    }

    @Override
    public List<? extends BotApiMethod<?>> handle(Update update) {
        String questionTemplate = update.getMessage().getText();
        String creationDay = userSessionService.getSessionParameter(getChatId(update), "creationDay").orElse(JsonNodeFactory.instance.textNode("")).asText();
        String forwardingDay = userSessionService.getSessionParameter(getChatId(update), "forwardingDay").orElse(JsonNodeFactory.instance.textNode("")).asText();

        PollConfig pollConfig = PollConfig.builder()
                .creationDay(creationDay)
                .forwardingDay(forwardingDay)
                .questionTemplate(questionTemplate)
                .build();

        pollConfigRepository.save(pollConfig);

        KeyboardButton allPollsButton = new KeyboardButton(GetPollsCommandHandler.COMMAND_GET_POOLS);
        KeyboardButton addPollButton = new KeyboardButton(AddPollCommandHandler.COMMAND_ADD_POLL);
        KeyboardButton backButton = new KeyboardButton("\uD83D\uDD19 Назад");

        KeyboardRow keyboardRow1 = new KeyboardRow(List.of(allPollsButton, addPollButton));
        KeyboardRow keyboardRow2 = new KeyboardRow(List.of(backButton));

        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup(List.of(keyboardRow1, keyboardRow2));
        keyboardMarkup.setSelective(true);
        keyboardMarkup.setResizeKeyboard(true);
        keyboardMarkup.setOneTimeKeyboard(false);

        SendMessage sendMessage = new SendMessage();
        sendMessage.setText("Шаблон опитування успішно створений");
        sendMessage.setChatId(getChatId(update));
        sendMessage.setReplyMarkup(keyboardMarkup);


        return List.of(sendMessage);
    }

    @Override
    public boolean isApplicable(Update update) {
        return chatTypeIs(update, CHAT_TYPE_PRIVATE)
                && !isCommand(update)
                && hasText(update)
                && conversationStateIs(update, ConversationState.WAITING_FOR_QUESTION_TEMPLATE);
    }

    @Override
    public void postHandle(Update update) {
        userSessionService.setSessionParameter(getChatId(update), UserSessionService.SESSION_PARAMETER_CONVERSATION_STATE, ConversationState.POLL_CONFIGURATION.name());
    }
}

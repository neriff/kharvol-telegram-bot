package com.kharvoll.kharvollbot.handler;

import com.kharvoll.kharvollbot.persistence.model.PollConfig;
import com.kharvoll.kharvollbot.persistence.repository.PollConfigRepository;
import com.kharvoll.kharvollbot.service.UserSessionService;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

@Component
public class GetPollsCommandHandler extends AbstractCommandHandler {

    public static final String COMMAND_GET_POOLS = "\uD83D\uDCCA Усі опитування";

    private final PollConfigRepository pollConfigRepository;

    protected GetPollsCommandHandler(UserSessionService userSessionService,
                                     PollConfigRepository pollConfigRepository) {
        super(userSessionService);
        this.pollConfigRepository = pollConfigRepository;
    }


    @Override
    public List<? extends BotApiMethod<?>> handle(Update update) {
        List<PollConfig> pollConfigs = pollConfigRepository.findAll();

        if (pollConfigs.isEmpty()) {
            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(getChatId(update));
            sendMessage.setText("\uD83E\uDD37\u200D♂\uFE0F Наразі немає жодного шаблона");

            return List.of(sendMessage);
        }

        List<SendMessage> messages = new ArrayList<>();

        for (PollConfig pollConfig : pollConfigs) {
            SendMessage sendMessage = new SendMessage();
            sendMessage.setText(buildText(pollConfig));
            sendMessage.setChatId(getChatId(update));

            InlineKeyboardButton deleteButton = new InlineKeyboardButton();
            deleteButton.setText("❌ Видалити");
            deleteButton.setCallbackData(buildCallbackData(pollConfig));
            InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup(List.of(List.of(deleteButton)));

            sendMessage.setReplyMarkup(inlineKeyboardMarkup);
            messages.add(sendMessage);
        }


        return messages;
    }

    @Override
    public boolean isApplicable(Update update) {
        return chatTypeIs(update, CHAT_TYPE_PRIVATE) && hasText(update, COMMAND_GET_POOLS);
    }

    private String buildText(PollConfig pollConfig) {
        StringBuilder text = new StringBuilder();
        text.append("Creation day: ").append(pollConfig.getCreationDay()).append(System.lineSeparator());
        text.append("Forwarding day: ").append(pollConfig.getForwardingDay()).append(System.lineSeparator());
        text.append("Question template: ").append(pollConfig.getQuestionTemplate()).append(System.lineSeparator());
        text.append(System.lineSeparator());

        return text.toString();
    }

    private String buildCallbackData(PollConfig pollConfig) {
        return DeletePollConfigCommandHandler.CALLBACK_DATA_TEMPLATE
                .formatted(DeletePollConfigCommandHandler.ACTION_DELETE_POLL_CONFIG, pollConfig.getId());
    }
}

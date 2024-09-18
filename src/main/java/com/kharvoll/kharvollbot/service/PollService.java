package com.kharvoll.kharvollbot.service;

import com.kharvoll.kharvollbot.KharvollBot;
import com.kharvoll.kharvollbot.persistence.model.PollConfig;
import com.kharvoll.kharvollbot.persistence.model.PollInfo;
import com.kharvoll.kharvollbot.persistence.repository.PollConfigRepository;
import com.kharvoll.kharvollbot.persistence.repository.PollInfoRepository;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.ForwardMessage;
import org.telegram.telegrambots.meta.api.methods.pinnedmessages.PinChatMessage;
import org.telegram.telegrambots.meta.api.methods.polls.SendPoll;
import org.telegram.telegrambots.meta.api.objects.Message;

@Service
@Transactional
@Scope(proxyMode = ScopedProxyMode.TARGET_CLASS)
public class PollService {

    private static final List<String> AVAILABLE_POSITIVE_OPTION = List.of("Буду ➕", "Буду \uD83C\uDFD0");
    private static final List<String> AVAILABLE_NEGATIVE_OPTION = List.of("Мінус ❌", "Пас ❌", "Відпочиваю \uD83E\uDD42");

    private final KharvollBot kharvollBot;
    private final PollInfoRepository pollInfoRepository;
    private final PollConfigRepository pollConfigRepository;

    @Value("${kharvoll-bot.chat.admin-chat-id}")
    private String adminChatId;

    @Value("${kharvoll-bot.chat.kharvoll-chat-id}")
    private String kharvollChatId;

    @Value("${kharvoll-bot.chat.kharvoll-polls-thread-id}")
    private Integer kharvollPollsThreadId;

    public PollService(KharvollBot kharvollBot,
                       PollInfoRepository pollInfoRepository,
                       PollConfigRepository pollConfigRepository) {
        this.kharvollBot = kharvollBot;
        this.pollInfoRepository = pollInfoRepository;
        this.pollConfigRepository = pollConfigRepository;
    }

    public void createPolls() {
        LocalDate now = LocalDate.now();
        String creationDay = StringUtils.capitalize(now.getDayOfWeek().getDisplayName(TextStyle.FULL, new Locale("uk", "UA")));

        List<PollConfig> pollConfigs = pollConfigRepository.findAllByCreationDay(creationDay);

        for (PollConfig pollConfig : pollConfigs) {
            SendPoll sendPoll = new SendPoll();
            sendPoll.setQuestion(pollConfig.getQuestionTemplate());
            sendPoll.setOptions(List.of(getRandomOption(AVAILABLE_POSITIVE_OPTION), getRandomOption(AVAILABLE_NEGATIVE_OPTION)));
            sendPoll.setIsAnonymous(false);
            sendPoll.setAllowMultipleAnswers(false);
            sendPoll.setIsClosed(false);
            sendPoll.setChatId(adminChatId);

            Message executed = kharvollBot.execute(sendPoll);
            savePollInfo(executed, pollConfig);
        }
    }

    public void forwardPolls() {
        LocalDate now = LocalDate.now();
        String dayOfWeek = StringUtils.capitalize(now.getDayOfWeek().getDisplayName(TextStyle.FULL, new Locale("uk", "UA")));

        List<PollConfig> pollConfigs = pollConfigRepository.findAllByForwardingDay(dayOfWeek);

        for (PollConfig pollConfig : pollConfigs) {
            List<PollInfo> pollInfos = pollInfoRepository.findAllByPollConfigIdAndForwardedIsFalse(pollConfig.getId());

            for (PollInfo pollInfo : pollInfos) {
                Integer messageId = pollInfo.getMessageId();

                ForwardMessage forwardMessage = new ForwardMessage();
                forwardMessage.setMessageId(messageId);
                forwardMessage.setChatId(kharvollChatId);
                forwardMessage.setFromChatId(adminChatId);
                forwardMessage.setMessageThreadId(kharvollPollsThreadId);

                Message forwardedMessage = kharvollBot.execute(forwardMessage);

                pollInfo.setForwarded(true);

                PinChatMessage pinMessage = new PinChatMessage();
                pinMessage.setChatId(kharvollChatId);
                pinMessage.setMessageId(forwardedMessage.getMessageId());
                pinMessage.setDisableNotification(false);

                kharvollBot.execute(pinMessage);

            }
        }


    }

    private PollInfo savePollInfo(Message message, PollConfig pollConfig) {

        PollInfo pollInfo = PollInfo.builder()
                .messageId(message.getMessageId())
                .forwarded(false)
                .pollConfigId(pollConfig.getId())
                .build();

        return pollInfoRepository.save(pollInfo);
    }

    private String getRandomOption(List<String> options) {
        return options.get((int) (Math.random() * options.size()));
    }
}

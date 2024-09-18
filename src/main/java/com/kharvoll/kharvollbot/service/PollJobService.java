package com.kharvoll.kharvollbot.service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class PollJobService {

    private final PollService pollService;


    public PollJobService(PollService pollService) {
        this.pollService = pollService;
    }

    @Scheduled(cron = "${kharvoll-bot.poll.cron}")
    public void startCron() {
        pollService.createPolls();
        pollService.forwardPolls();
    }
}

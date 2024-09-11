package com.kharvoll.kharvollbot.service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ScheduledService {

    private final PollService pollService;


    public ScheduledService(PollService pollService) {
        this.pollService = pollService;
    }
//0 0 20 * * *
    @Scheduled(cron = "${kharvoll-bot.poll.cron}")
    public void startCron() {
        pollService.createPolls();
        pollService.forwardPolls();
    }
}

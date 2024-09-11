package com.kharvoll.kharvollbot.domain;

import lombok.Getter;

@Getter
public enum ConversationState {

    MAIN_MENU,
    CONVERSATION_STARTED,
    POLL_CONFIGURATION(MAIN_MENU),

    WAITING_FOR_CREATION_DAY(POLL_CONFIGURATION ),
    WAITING_FOR_FORWARDING_DAY(POLL_CONFIGURATION),
    WAITING_FOR_QUESTION_TEMPLATE(POLL_CONFIGURATION);

    private final ConversationState previousState;

    ConversationState(ConversationState previousState) {
        this.previousState = previousState;
    }

    ConversationState() {
        previousState = null;
    }

}

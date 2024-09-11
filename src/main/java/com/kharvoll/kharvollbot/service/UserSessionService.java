package com.kharvoll.kharvollbot.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.kharvoll.kharvollbot.domain.ConversationState;
import com.kharvoll.kharvollbot.persistence.model.UserSession;
import com.kharvoll.kharvollbot.persistence.repository.UserSessionRepository;
import jakarta.persistence.EntityNotFoundException;
import java.util.Optional;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@Scope(proxyMode = ScopedProxyMode.TARGET_CLASS)
public class UserSessionService {

    public static final String SESSION_PARAMETER_CREATION_DAY = "creationDay";
    public static final String SESSION_PARAMETER_FORWARDING_DAY = "forwardingDay";
    public static final String SESSION_PARAMETER_CONVERSATION_STATE = "conversationState";

    private final UserSessionRepository userSessionRepository;

    public UserSessionService(UserSessionRepository userSessionRepository) {
        this.userSessionRepository = userSessionRepository;
    }

    public void createSession(Long userId) {
        userSessionRepository.save(UserSession.builder().userId(userId).build());
    }

    public boolean isSessionExists(Long userId) {
        return userSessionRepository.findByUserId(userId).isPresent();
    }

    public Optional<JsonNode> getSessionParameter(Long userId, String parameter) {
        UserSession userSession = userSessionRepository.findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException("UserSession for user with id '%s' does not exist".formatted(userId)));

        ObjectNode sessionParameters = userSession.getSessionParameters();

        return Optional.ofNullable(sessionParameters.get(parameter));
    }

    public void setSessionParameter(Long userId, String parameter, String value) {
        UserSession userSession = userSessionRepository.findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException("UserSession for user with id '%s' does not exist".formatted(userId)));

        ObjectNode sessionParameters = userSession.getSessionParameters();

        sessionParameters.put(parameter, value);
    }

    public void setConversationState(Long userId, ConversationState conversationState) {
        setSessionParameter(userId, SESSION_PARAMETER_CONVERSATION_STATE, conversationState.toString());
    }

    public ConversationState getCurrentConversationState(Long userId) {
        String conversationStateText = getSessionParameter(userId, SESSION_PARAMETER_CONVERSATION_STATE).get().asText();
        return ConversationState.valueOf(conversationStateText);
    }

}

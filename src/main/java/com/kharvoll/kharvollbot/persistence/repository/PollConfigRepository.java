package com.kharvoll.kharvollbot.persistence.repository;

import com.kharvoll.kharvollbot.persistence.model.PollConfig;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PollConfigRepository extends JpaRepository<PollConfig, String> {

    List<PollConfig> findAllByCreationDay(String creationDay);
    List<PollConfig> findAllByForwardingDay(String forwardingDay);
}

package com.kharvoll.kharvollbot.persistence.repository;

import com.kharvoll.kharvollbot.persistence.model.PollInfo;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PollInfoRepository extends JpaRepository<PollInfo, String> {

    List<PollInfo> findAllByPollConfigIdAndForwardedIsFalse(String pollConfigId);

}

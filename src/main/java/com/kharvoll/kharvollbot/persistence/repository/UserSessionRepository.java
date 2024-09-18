package com.kharvoll.kharvollbot.persistence.repository;

import com.kharvoll.kharvollbot.persistence.model.UserSession;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserSessionRepository extends JpaRepository<UserSession, String> {

    Optional<UserSession> findByUserId(Long userId);

    boolean existsByUserId(Long userId);

}

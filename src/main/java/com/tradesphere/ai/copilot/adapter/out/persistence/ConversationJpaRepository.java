package com.tradesphere.ai.copilot.adapter.out.persistence;

import com.tradesphere.ai.copilot.domain.model.ConversationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data JPA Repository for Conversation
 */
@Repository
public interface ConversationJpaRepository extends JpaRepository<ConversationJpaEntity, UUID> {
    
    Optional<ConversationJpaEntity> findBySessionId(String sessionId);
    
    @Query("SELECT c FROM ConversationJpaEntity c WHERE c.userId = :userId AND c.status = :status ORDER BY c.lastMessageAt DESC")
    Optional<ConversationJpaEntity> findFirstByUserIdAndStatus(
        @Param("userId") UUID userId, 
        @Param("status") ConversationStatus status
    );
}


package com.tradesphere.ai.copilot.adapter.out.persistence;

import com.tradesphere.ai.copilot.application.port.out.ConversationRepository;
import com.tradesphere.ai.copilot.domain.model.Conversation;
import com.tradesphere.ai.copilot.domain.model.ConversationId;
import com.tradesphere.ai.copilot.domain.model.ConversationStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

/**
 * Adapter implementing ConversationRepository port
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class ConversationRepositoryAdapter implements ConversationRepository {

    private final ConversationJpaRepository jpaRepository;
    private final ConversationMapper mapper;

    @Override
    @Transactional
    public Conversation save(Conversation conversation) {
        log.debug("Saving conversation: {}", conversation.getId());
        ConversationJpaEntity entity = mapper.toEntity(conversation);
        ConversationJpaEntity saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Conversation> findById(ConversationId id) {
        log.debug("Finding conversation by ID: {}", id);
        return jpaRepository.findById(id.value())
                .map(mapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Conversation> findBySessionId(String sessionId) {
        log.debug("Finding conversation by session ID: {}", sessionId);
        return jpaRepository.findBySessionId(sessionId)
                .map(mapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Conversation> findActiveByUserId(UUID userId) {
        log.debug("Finding active conversation for user: {}", userId);
        return jpaRepository.findFirstByUserIdAndStatus(userId, ConversationStatus.ACTIVE)
                .map(mapper::toDomain);
    }

    @Override
    @Transactional
    public void delete(ConversationId id) {
        log.debug("Deleting conversation: {}", id);
        jpaRepository.deleteById(id.value());
    }
}


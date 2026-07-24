package com.tradesphere.ai.collection.adapter.out.persistence;

import com.tradesphere.ai.collection.domain.model.DocumentaryCollection;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper between DocumentaryCollection domain model and JPA entity
 * @deprecated This mapper is outdated and replaced by DocumentaryCollectionReflectionMapper.
 * It will be removed in a future version.
 */
@Deprecated
@Component
@RequiredArgsConstructor
@Slf4j
public class CollectionMapper {

    /**
     * @deprecated Use DocumentaryCollectionReflectionMapper instead
     */
    @Deprecated
    public DocumentaryCollectionJpaEntity toEntity(DocumentaryCollection collection) {
        throw new UnsupportedOperationException("This mapper is deprecated. Use DocumentaryCollectionReflectionMapper instead.");
    }

    /**
     * @deprecated Use DocumentaryCollectionReflectionMapper instead
     */
    @Deprecated
    public DocumentaryCollection toDomain(DocumentaryCollectionJpaEntity entity) {
        throw new UnsupportedOperationException("This mapper is deprecated. Use DocumentaryCollectionReflectionMapper instead.");
    }
}


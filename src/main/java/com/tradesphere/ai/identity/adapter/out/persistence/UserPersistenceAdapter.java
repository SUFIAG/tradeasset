package com.tradesphere.ai.identity.adapter.out.persistence;

import com.tradesphere.ai.identity.application.port.out.LoadUserPort;
import com.tradesphere.ai.identity.application.port.out.SaveUserPort;
import com.tradesphere.ai.identity.domain.model.User;
import com.tradesphere.ai.identity.domain.model.UserId;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * User Persistence Adapter
 * Implements user persistence using Spring Data JPA
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class UserPersistenceAdapter implements LoadUserPort, SaveUserPort {

    private final UserJpaRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public Optional<User> loadUserById(UserId userId) {
        log.debug("Loading user by ID: {}", userId.getValue());
        return userRepository.findById(userId.getValue())
                .map(userMapper::toDomainModel);
    }

    @Override
    public Optional<User> loadUserByUsername(String username) {
        log.debug("Loading user by username: {}", username);
        return userRepository.findByUsername(username)
                .map(userMapper::toDomainModel);
    }

    @Override
    public Optional<User> loadUserByEmail(String email) {
        log.debug("Loading user by email: {}", email);
        return userRepository.findByEmail(email)
                .map(userMapper::toDomainModel);
    }

    @Override
    public User saveUser(User user) {
        log.debug("Saving user: {}", user.getUsername());
        UserJpaEntity entity = userMapper.toJpaEntity(user);
        UserJpaEntity savedEntity = userRepository.save(entity);
        return userMapper.toDomainModel(savedEntity);
    }
}




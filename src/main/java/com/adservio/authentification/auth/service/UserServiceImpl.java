package com.adservio.authentification.auth.service;

import java.time.Instant;
import java.util.Optional;

import com.adservio.authentification.auth.domain.UserEntity;
import com.adservio.authentification.auth.repository.UserRepository;
import com.adservio.authentification.auth.util.RandomUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UserServiceImpl {

    private final Logger log = LoggerFactory.getLogger(UserService.class);
    @Autowired
    private UserRepository userRepository;
    public Optional<UserEntity> requestPasswordReset(String email) {

        return userRepository.findOneByEmailIgnoreCase(email)
        .filter(UserEntity::getActivated)
        .map(user -> {
            user.setResetKey(RandomUtil.generateResetKey());
            user.setResetDate(Instant.now());
           // cacheManager.getCache(USERS_CACHE).evict(user.getLogin());
            return user;
        });
    }
    
}

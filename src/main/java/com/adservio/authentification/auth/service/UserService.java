package com.adservio.authentification.auth.service;

import java.util.Optional;

import com.adservio.authentification.auth.domain.UserEntity;

public interface UserService {
    public Optional<UserEntity> requestPasswordReset(String email);
}

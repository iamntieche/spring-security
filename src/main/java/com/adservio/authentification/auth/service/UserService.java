package com.adservio.authentification.auth.service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import com.adservio.authentification.auth.domain.UserEntity;
import com.adservio.authentification.auth.service.dto.UserDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

public interface UserService {
    Optional<UserEntity> requestPasswordReset(String email);
    UserEntity saveUser(UserEntity user);
    void deleteUser(UserEntity user);

    Optional<UserEntity> completePasswordReset(String newPassword, String resetKey);

    List<UserEntity> findAllByActivatedIsFalseAndCreatedDateBefore(Instant createdDate);

    void removeNotActivatedUsers();

    Optional<UserEntity> findOneByLogin(String anonymousUser);

    long count();

   // Page<UserDTO> getAllManagedUser(String anonymousUser);
}

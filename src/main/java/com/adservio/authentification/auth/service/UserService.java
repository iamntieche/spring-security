package com.adservio.authentification.auth.service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import com.adservio.authentification.auth.domain.UserEntity;
import com.adservio.authentification.auth.service.dto.UserDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {
    Optional<UserDTO> requestPasswordReset(String email);
    UserDTO saveUser(UserEntity user);
    void deleteUser(UserDTO user);

    Optional<UserDTO> completePasswordReset(String newPassword, String resetKey);

    List<UserDTO> findAllByActivatedIsFalseAndCreatedDateBefore(Instant createdDate);

    void removeNotActivatedUsers();

    Optional<UserDTO> findOneByLogin(String anonymousUser);

    int count();

    Page<UserDTO> getAllManagedUser(String anonymousUser, Pageable pageable);
}

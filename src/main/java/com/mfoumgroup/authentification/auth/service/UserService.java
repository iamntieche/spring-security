package com.mfoumgroup.authentification.auth.service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import com.mfoumgroup.authentification.auth.domain.UserEntity;
import com.mfoumgroup.authentification.auth.dto.UserDTO;
import com.mfoumgroup.authentification.auth.dto.ManagedUserVM;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {
    Optional<UserDTO> requestPasswordReset(String email);
    UserDTO saveUser(UserEntity user);
    void deleteUser(String user);

    Optional<UserDTO> completePasswordReset(String newPassword, String resetKey);

    List<UserDTO> findAllByActivatedIsFalseAndCreatedDateBefore(Instant createdDate);

    void removeNotActivatedUsers();

    Optional<UserDTO> findOneByLogin(String anonymousUser);

    int count();

    Page<UserDTO> getAllManagedUser(String anonymousUser, Pageable pageable);

    UserDTO createUser(UserDTO user);

    Optional<UserEntity> getUserWithAuthorities();

    UserDTO registerUser(ManagedUserVM managedUserVM);

    Optional<UserDTO> updateUser(UserDTO userDTO);

    Optional<UserDTO> activateRegistration(String key);

    void changePassword(String currentPassword, String newPassword);

    Page<UserDTO> getAllManagedUsers(Pageable pageable);

    Optional<UserEntity> getUserWithAuthoritiesByLogin(String login);

    void updateUser(String firstName, String lastName, String email, String langKey, String imageUrl);
}

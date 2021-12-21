package com.mfoumgroup.authentification.auth.repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import com.mfoumgroup.authentification.auth.domain.UserEntity;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {

    String USERS_BY_LOGIN_CACHE = "usersByLogin";

    String USERS_BY_EMAIL_CACHE = "usersByEmail";
    
    Optional<UserEntity> findOneByEmailIgnoreCase(String email);

    Optional<UserEntity> findOneByResetKey(String resetKey);
    List<UserEntity> findAllByActivatedIsFalseAndCreatedDateBefore(Instant createdDate);

    Optional<UserEntity> findOneByLogin(String login);

   Page<UserEntity> findAllByLoginNot(String login, Pageable pageable);

    Optional<UserEntity> findOneWithAuthoritiesByEmailIgnoreCase(String email);

    Optional<UserEntity> findOneWithAuthoritiesByLogin(String login);

    Optional<UserEntity> findOneByActivationKey(String activationKey);
}

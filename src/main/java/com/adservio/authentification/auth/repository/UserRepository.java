package com.adservio.authentification.auth.repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import com.adservio.authentification.auth.domain.UserEntity;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
    
    
    Optional<UserEntity> findOneByEmailIgnoreCase(String email);

    Optional<UserEntity> findOneByResetKey(String resetKey);
    List<UserEntity> findAllByActivatedIsFalseAndCreatedDateBefore(Instant createdDate);

    Optional<UserEntity> findOneByLogin(String login);

   Page<UserEntity> findAllByLoginNot(String login, Pageable pageable);
}

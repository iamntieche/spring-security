package com.adservio.authentification.auth.repository;

import java.util.Optional;

import com.adservio.authentification.auth.domain.UserEntity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
    
    
    Optional<UserEntity> findOneByEmailIgnoreCase(String email);
}

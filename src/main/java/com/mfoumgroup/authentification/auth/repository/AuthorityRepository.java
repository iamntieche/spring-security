package com.mfoumgroup.authentification.auth.repository;

import com.mfoumgroup.authentification.auth.domain.AuthorityEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AuthorityRepository extends JpaRepository<AuthorityEntity, String> {

    Optional<AuthorityEntity> findByName(String name);
}

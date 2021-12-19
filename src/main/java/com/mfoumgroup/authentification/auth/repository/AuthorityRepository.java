package com.mfoumgroup.authentification.auth.repository;

import com.mfoumgroup.authentification.auth.domain.AuthorityEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthorityRepository extends JpaRepository<AuthorityEntity, String> {
}

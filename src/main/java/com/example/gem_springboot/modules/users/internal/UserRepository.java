package com.example.gem_springboot.modules.users.internal;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository
    extends
        JpaRepository<UserEntity, Long>,
        JpaSpecificationExecutor<UserEntity>
{
    boolean existsByEmail(String email);
    boolean existsByUsername(String username);
    java.util.Optional<UserEntity> findByEmail(String email);
    Optional<UserEntity> findByUsername(String username);

}

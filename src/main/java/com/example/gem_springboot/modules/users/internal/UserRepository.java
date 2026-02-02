package com.example.gem_springboot.modules.users.internal;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
    boolean existsByEmail(String email);

    @Query(
        //Seleziona gli oggetti UserEntity (alias 'u') filtrando i risultati in lower case
        "SELECT u FROM UserEntity u WHERE " +
            "LOWER(u.username) LIKE LOWER(CONCAT('%', :filter, '%')) OR " +
            "LOWER(u.email) LIKE LOWER(CONCAT('%', :filter, '%'))"
    )
    Page<UserEntity> searchByText(
        @Param("filter") String filter,
        Pageable pageable
    );
}

package com.example.gem_springboot.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.gem_springboot.models.entities.UserEntity;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
    
    boolean existsByEmail(String email);
}
package com.example.repository;

import com.example.model.UserEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserEntityRepository extends JpaRepository<UserEntity, Long> {

    @EntityGraph(attributePaths = {"authorities", "ips"})
    Optional<UserEntity> findByUsername(String username);

    Optional<UserEntity> findByEmail(String email);
}
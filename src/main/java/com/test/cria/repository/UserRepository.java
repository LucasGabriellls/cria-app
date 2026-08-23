package com.test.cria.repository;

import com.test.cria.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByEmail(String name);

    boolean existsById(int id);

    Optional<User> findByEmail(String email);
}

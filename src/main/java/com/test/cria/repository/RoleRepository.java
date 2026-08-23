package com.test.cria.repository;

import com.test.cria.entity.Role;
import com.test.cria.entity.enums.RoleEnum;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByRole(RoleEnum role);
}

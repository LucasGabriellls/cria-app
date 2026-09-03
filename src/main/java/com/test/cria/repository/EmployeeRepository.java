package com.test.cria.repository;

import com.test.cria.entity.Employee;
import com.test.cria.entity.enums.RoleEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    boolean existsByRegistrationNumber(String registrationNumber);

    @Query("SELECT DISTINCT e FROM Employee e " +
            "JOIN e.user u " +
            "JOIN u.roles r " +
            "WHERE r.role = :role")
    Page<Employee> findAllByRole(@Param("role") RoleEnum role, Pageable pageable);
}

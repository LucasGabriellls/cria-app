package com.test.cria.repository;

import com.test.cria.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    boolean existsByRegistrationNumber(String registrationNumber);
}

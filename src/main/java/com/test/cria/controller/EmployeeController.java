package com.test.cria.controller;

import com.test.cria.dto.employee.EmployeeCreateDTO;
import com.test.cria.dto.employee.EmployeeResponseDTO;
import com.test.cria.entity.User;
import com.test.cria.service.EmployeeService;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/employees")
public class EmployeeController {

    private final EmployeeService employeeService;

    @OneToOne
    @JoinColumn(name = "user_id", unique = true, nullable = false)
    private User user;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public EmployeeResponseDTO findById(@PathVariable Long id) {
        return this.employeeService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EmployeeResponseDTO create(EmployeeCreateDTO employeeCreateRequest) {
        return this.employeeService.create(employeeCreateRequest);
    }
}

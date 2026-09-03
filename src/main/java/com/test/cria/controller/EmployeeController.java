package com.test.cria.controller;

import com.test.cria.dto.employee.EmployeeCreateDTO;
import com.test.cria.dto.employee.EmployeePageResponseDTO;
import com.test.cria.dto.employee.EmployeeResponseDTO;
import com.test.cria.dto.employee.EmployeeUpdateDTO;
import com.test.cria.entity.enums.RoleEnum;
import com.test.cria.service.EmployeeService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/employees")
public class EmployeeController {

    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public EmployeeResponseDTO findById(@PathVariable  @NotNull @Min(1) Long id) {
        return employeeService.findById(id);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public EmployeePageResponseDTO list(@RequestParam(defaultValue = "0") @PositiveOrZero(message = "Page must be greater than or equal to zero") int page,
                                        @RequestParam(defaultValue = "10") @Positive(message = "Size must be greater than zero")
                                        @Max(value = 20, message = "Size must be less than or equal to 20") int size) {
        return employeeService.findAllPaginated(page, size);
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public EmployeeResponseDTO create(@Valid @RequestBody EmployeeCreateDTO employeeCreateRequest) {
        return employeeService.create(employeeCreateRequest);
    }

    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    public EmployeeResponseDTO update(@Valid @RequestBody EmployeeUpdateDTO employeeUpdateRequest) {
        return employeeService.update(employeeUpdateRequest);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable @NotNull @Min(1) Long id) {
        employeeService.delete(id);
    }

    @GetMapping("/role")
    @ResponseStatus(HttpStatus.OK)
    public EmployeePageResponseDTO listByRole(@RequestParam RoleEnum role) {
        return employeeService.listByRole(role);
    }
}

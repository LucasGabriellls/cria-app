package com.test.cria.controller;

import com.test.cria.dto.employee.EmployeeCreateDTO;
import com.test.cria.dto.employee.EmployeePageResponseDTO;
import com.test.cria.dto.employee.EmployeeResponseDTO;
import com.test.cria.dto.employee.EmployeeUpdateDTO;
import com.test.cria.service.EmployeeService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public EmployeeResponseDTO findById(@PathVariable Long id) {
        return this.employeeService.findById(id);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public EmployeePageResponseDTO list(@RequestParam(defaultValue = "0") @PositiveOrZero(message = "Page must be greater than or equal to zero") int page,
                                        @RequestParam(defaultValue = "10") @Positive(message = "Size must be greater than zero")
                                        @Max(value = 20, message = "Size must be less than or equal to 20") int size) {
        return this.employeeService.findAllPaginated(page, size);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EmployeeResponseDTO create(@Valid @RequestBody EmployeeCreateDTO employeeCreateRequest) {
        return this.employeeService.create(employeeCreateRequest);
    }

    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    public EmployeeResponseDTO update(@Valid @RequestBody EmployeeUpdateDTO employeeUpdateRequest) {
        return this.employeeService.update(employeeUpdateRequest);
    }
}

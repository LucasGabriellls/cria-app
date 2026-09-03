package com.test.cria.controller;

import com.test.cria.dto.classroom.ClassroomCreateDTO;
import com.test.cria.dto.classroom.ClassroomResponseDTO;
import com.test.cria.service.ClassroomService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/classrooms")
public class ClassroomController {

    private final ClassroomService classroomService;

    public ClassroomController(ClassroomService classroomService) {
        this.classroomService = classroomService;
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ClassroomResponseDTO findById(@PathVariable @NotNull(message = "ID is required") @Positive(message = "ID must be greater than zero") Long id) {
        return classroomService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ClassroomResponseDTO create(@Valid @RequestBody ClassroomCreateDTO classroomCreateRequest) {
        return classroomService.create(classroomCreateRequest);
    }
}

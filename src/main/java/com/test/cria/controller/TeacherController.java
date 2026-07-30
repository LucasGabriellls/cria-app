package com.test.cria.controller;

import com.test.cria.entity.Teacher;
import com.test.cria.service.TeacherService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class TeacherController {

    private final TeacherService teacherService;

    public TeacherController(TeacherService teacherService) {
        this.teacherService = teacherService;
    }

    @GetMapping("/teacher/{id}")
    public ResponseEntity<Teacher> findById(@PathVariable int id) {
        return ResponseEntity.status(HttpStatus.OK).body(teacherService.findById(id));
    }

    @GetMapping("/teachers")
    public ResponseEntity<List<Teacher>> findAll() {
        return ResponseEntity.status(HttpStatus.OK).body(teacherService.findAll());
    }

    @PostMapping("/teacher")
    public ResponseEntity<Teacher> create(@RequestBody Teacher teacher) {
        return ResponseEntity.status(HttpStatus.CREATED).body(teacherService.create(teacher));
    }

    @PutMapping("/teacher")
    public ResponseEntity<Teacher> update(@RequestBody Teacher teacher) {
        return ResponseEntity.status(HttpStatus.OK).body(teacherService.update(teacher));
    }

    @DeleteMapping("/teacher/{id}")
    public ResponseEntity<String> delete(@PathVariable int id) {
        teacherService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

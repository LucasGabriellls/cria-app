package com.test.cria.service;

import com.test.cria.entity.Teacher;
import com.test.cria.exception.UserNotFound;
import com.test.cria.repository.TeacherRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TeacherService {

    private TeacherRepository teacherRepository;

    public TeacherService(TeacherRepository teacherRepository) {
        this.teacherRepository = teacherRepository;
    }

    public Teacher findById(int id) {
        return teacherRepository.findById(id).orElseThrow(() -> new UserNotFound("Professor não encontrado!"));
    }
    
    public List<Teacher> findAll() {
        List<Teacher> teachers = teacherRepository.findAll();

        if  (teachers.isEmpty()) {
            throw new UserNotFound("Não possui nenhum professor");
        }
        return teachers;
    }

    @Transactional
    public Teacher create(Teacher teacher) {
        return teacherRepository.save(teacher);
    }

    @Transactional
    public Teacher update(Teacher teacher) {

        Teacher tempTeacher = findById(teacher.getId());

        if  (tempTeacher != null) {
                return teacherRepository.save(teacher);
        } else {
            throw new NullPointerException("Nenhum usuário encontrado");
        }
    }

    @Transactional
    public void delete(int id) {
        Teacher tempTeacher = findById(id);

        if  (tempTeacher != null) {
            teacherRepository.delete(tempTeacher);
        }  else {
            throw new NullPointerException("Nenhum usuário encontrado");
        }
    }
}

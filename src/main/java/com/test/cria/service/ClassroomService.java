package com.test.cria.service;

import com.test.cria.dto.classroom.ClassroomCreateDTO;
import com.test.cria.dto.classroom.ClassroomResponseDTO;
import com.test.cria.dto.employee.EmployeeResponseDTO;
import com.test.cria.entity.Classroom;
import com.test.cria.exception.classroom.ClassroomAlreadyExistsException;
import com.test.cria.exception.classroom.ClassroomNotFoundException;
import com.test.cria.mapper.ClassroomMapper;
import com.test.cria.repository.ClassroomRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClassroomService {

    private final ClassroomRepository classroomRepository;
    private final ClassroomMapper classroomMapper;

    public ClassroomService(ClassroomRepository classroomRepository, ClassroomMapper classroomMapper) {
        this.classroomRepository = classroomRepository;
        this.classroomMapper = classroomMapper;
    }

    public ClassroomResponseDTO findById(Long id) {
        Classroom classroom = classroomRepository.findByIdWithStaffAndEmployee(id)
                .orElseThrow(() -> new ClassroomNotFoundException("Classroom não encontrada com o ID: " + id));

        return classroomMapper.toResponseDTO(classroom);
    }

    @Transactional
    public ClassroomResponseDTO create(ClassroomCreateDTO createDTO) {
        validateDuplicate(createDTO);

        createDTO.stage().validate(createDTO.classLetter(), createDTO.shift());

        Classroom classroom = classroomMapper.toEntity(createDTO);
        Classroom savedClassroom = classroomRepository.save(classroom);

        return classroomMapper.toResponseDTO(savedClassroom);
    }

    private void validateDuplicate(ClassroomCreateDTO dto) {
        boolean exists = classroomRepository.existsByStageAndClassLetterAndShiftAndAcademicYear(
                dto.stage(),
                dto.classLetter(),
                dto.shift(),
                dto.academicYear()
        );

        if (exists) throw new ClassroomAlreadyExistsException(
                "A classroom with the specified stage, class letter, shift, and academic year already exists."
        );
    }
}

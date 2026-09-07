package com.test.cria.service;

import com.test.cria.dto.classroom.ClassroomCreateDTO;
import com.test.cria.dto.classroom.ClassroomResponseDTO;
import com.test.cria.entity.Classroom;
import com.test.cria.exception.classroom.ClassroomAlreadyExistsException;
import com.test.cria.exception.classroom.ClassroomNotFoundException;
import com.test.cria.mapper.ClassroomMapper;
import com.test.cria.repository.ClassroomRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ClassroomService {

    private final ClassroomRepository classroomRepository;
    private final ClassroomMapper classroomMapper;

    public ClassroomService(ClassroomRepository classroomRepository, ClassroomMapper classroomMapper) {
        this.classroomRepository = classroomRepository;
        this.classroomMapper = classroomMapper;
    }

    public ClassroomResponseDTO findById(Long id) {
        log.debug("Searching for classroom with ID: {} in database", id);

        Classroom classroom = classroomRepository.findByIdWithStaffAndEmployee(id)
                .orElseThrow(() -> {
                    log.warn("Classroom with ID: {} not found in database", id);
                    return new ClassroomNotFoundException("Classroom não encontrada com o ID: " + id);
                });

        log.debug("Classroom with ID: {} found in database", id);

        return classroomMapper.toResponseDTO(classroom);
    }

    @Transactional
    public ClassroomResponseDTO create(ClassroomCreateDTO createDTO) {
        log.debug("Starting classroom creation process [stage={}, shift={}, classLetter={}]",
                createDTO.stage(), createDTO.shift(), createDTO.classLetter());

        validateDuplicate(createDTO);

        createDTO.stage().validate(createDTO.classLetter(), createDTO.shift());

        Classroom classroom = classroomMapper.toEntity(createDTO);
        Classroom savedClassroom = classroomRepository.save(classroom);

        log.debug("Classroom created successfully with ID: {}", savedClassroom.getId());

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

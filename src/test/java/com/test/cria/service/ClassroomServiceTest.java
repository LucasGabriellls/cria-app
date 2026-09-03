package com.test.cria.service;

import com.test.cria.dto.classroom.ClassroomCreateDTO;
import com.test.cria.dto.classroom.ClassroomResponseDTO;
import com.test.cria.entity.Classroom;
import com.test.cria.entity.enums.ClassLetterEnum;
import com.test.cria.entity.enums.ShiftEnum;
import com.test.cria.entity.enums.StageEnum;
import com.test.cria.exception.classroom.ClassroomAlreadyExistsException;
import com.test.cria.exception.classroom.InvalidClassroomConfigurationException;
import com.test.cria.mapper.ClassroomMapper;
import com.test.cria.repository.ClassroomRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClassroomServiceTest {

    @Mock
    private ClassroomRepository classroomRepository;

    @Mock
    private ClassroomMapper classroomMapper;

    @InjectMocks
    private ClassroomService classroomService;

    @Test
    @DisplayName("Should create classroom successfully when there is no duplication and combination is valid")
    void shouldCreateSuccessfully() {
        ClassroomCreateDTO createDTO = buildValidCreateDTO();
        Classroom entity = buildValidEntity();
        ClassroomResponseDTO responseDTO = buildValidResponseDTO();

        when(classroomRepository.existsByStageAndClassLetterAndShiftAndAcademicYear(
                createDTO.stage(),
                createDTO.classLetter(),
                createDTO.shift(),
                createDTO.academicYear()
        )).thenReturn(false);

        when(classroomMapper.toEntity(createDTO)).thenReturn(entity);
        when(classroomRepository.save(entity)).thenReturn(entity);
        when(classroomMapper.toResponseDTO(entity)).thenReturn(responseDTO);

        ClassroomResponseDTO result = classroomService.create(createDTO);

        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(responseDTO);
        verify(classroomRepository).save(entity);
    }

    @Test
    @DisplayName("Should throw ClassroomAlreadyExistsException when classroom already exists")
    void shouldThrowExceptionWhenClassroomIsDuplicated() {
        ClassroomCreateDTO createDTO = buildValidCreateDTO();

        when(classroomRepository.existsByStageAndClassLetterAndShiftAndAcademicYear(
                createDTO.stage(),
                createDTO.classLetter(),
                createDTO.shift(),
                createDTO.academicYear()
        )
        ).thenReturn(true);

        assertThatThrownBy(() -> classroomService.create(createDTO))
                .isInstanceOf(ClassroomAlreadyExistsException.class);

        verify(classroomRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw InvalidClassroomConfigurationException when domain validation fails")
    void shouldThrowExceptionWhenDomainValidationFails() {
        ClassroomCreateDTO createDTO = buildInvalidCombinationCreateDTO();

        when(classroomRepository.existsByStageAndClassLetterAndShiftAndAcademicYear(
                createDTO.stage(),
                createDTO.classLetter(),
                createDTO.shift(),
                createDTO.academicYear()
        )
        ).thenReturn(false);

        assertThatThrownBy(() -> classroomService.create(createDTO))
                .isInstanceOf(InvalidClassroomConfigurationException.class)
                .hasMessage("For BERCARIO stage, class letter must be TURMA_A");

        verify(classroomRepository, never()).save(any());
    }

    private ClassroomCreateDTO buildValidCreateDTO() {
        return new ClassroomCreateDTO(
                StageEnum.BERCARIO,
                ClassLetterEnum.TURMA_A,
                ShiftEnum.MANHA,
                2026);
    }

    private ClassroomCreateDTO buildInvalidCombinationCreateDTO() {
        return new ClassroomCreateDTO(
                StageEnum.BERCARIO,
                ClassLetterEnum.TURMA_B,
                ShiftEnum.MANHA,
                2026);
    }

    private Classroom buildValidEntity() {
        return new Classroom();
    }

    private ClassroomResponseDTO buildValidResponseDTO() {
        return new ClassroomResponseDTO(
                1L,
                StageEnum.BERCARIO,
                ClassLetterEnum.TURMA_A,
                ShiftEnum.MANHA,
                2026,
                java.util.Collections.emptyList()
        );
    }
}
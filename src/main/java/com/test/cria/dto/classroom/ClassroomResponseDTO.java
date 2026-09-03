package com.test.cria.dto.classroom;

import com.test.cria.dto.classroomStaff.ClassroomStaffResponseDTO;
import com.test.cria.entity.enums.ClassLetterEnum;
import com.test.cria.entity.enums.ShiftEnum;
import com.test.cria.entity.enums.StageEnum;

import java.util.List;

public record ClassroomResponseDTO(
        Long id,
        StageEnum stage,
        ClassLetterEnum classLetter,
        ShiftEnum shift,
        Integer academicYear,
        List<ClassroomStaffResponseDTO> staffMembers
) {
}

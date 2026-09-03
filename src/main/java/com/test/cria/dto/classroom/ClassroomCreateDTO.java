package com.test.cria.dto.classroom;

import com.test.cria.entity.enums.ClassLetterEnum;
import com.test.cria.entity.enums.ShiftEnum;
import com.test.cria.entity.enums.StageEnum;
import jakarta.validation.constraints.NotNull;

import java.time.Year;

public record ClassroomCreateDTO(
        @NotNull(message = "Stage is required.")
        StageEnum stage,

        @NotNull(message = "Class letter is required.")
        ClassLetterEnum classLetter,

        @NotNull(message = "Shift is required.")
        ShiftEnum shift,

        @NotNull(message = "Academic year is required.")
        Integer academicYear
) {
    public ClassroomCreateDTO {
        if (academicYear != null) {
            int currentYear = Year.now().getValue();

            if (!academicYear.equals(currentYear)) {
                throw new IllegalArgumentException(
                        "Academic year must be equal to the current year (%d). Received: %d"
                                .formatted(currentYear, academicYear)
                );
            }
        }
    }
}

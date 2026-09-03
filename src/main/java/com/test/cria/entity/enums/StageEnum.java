package com.test.cria.entity.enums;

import com.test.cria.exception.classroom.InvalidClassroomConfigurationException;

import java.util.Set;

public enum StageEnum {
    BERCARIO {
        @Override
        public void validate(ClassLetterEnum classLetter, ShiftEnum shift) {
            if (classLetter != ClassLetterEnum.TURMA_A) throw new InvalidClassroomConfigurationException(
                    "For BERCARIO stage, class letter must be TURMA_A"
            );

            if (!Set.of(ShiftEnum.MANHA, ShiftEnum.TARDE).contains(shift)) throw new InvalidClassroomConfigurationException(
                    "For BERCARIO stage, shift must be MANHA or TARDE"
            );
        }
    },

    INFANTIL_1 {
        @Override
        public void validate(ClassLetterEnum classLetter, ShiftEnum shift) {
            if (!Set.of(ClassLetterEnum.TURMA_A, ClassLetterEnum.TURMA_B).contains(classLetter))throw new InvalidClassroomConfigurationException(
                    "For INFANTIL_1 stage, class letter must be TURMA_A or TURMA_B"
            );

            if (shift != ShiftEnum.INTEGRAL) throw new InvalidClassroomConfigurationException(
                    "For INFANTIL_1 stage, shift must be INTEGRAL"
            );
        }
    },

    INFANTIL_2 {
        @Override
        public void validate(ClassLetterEnum classLetter, ShiftEnum shift) {
            if (classLetter != ClassLetterEnum.TURMA_C) throw new InvalidClassroomConfigurationException(
                    "For INFANTIL_2 stage, class letter must be TURMA_C"
            );
        }
    },

    INFANTIL_3 {
        @Override
        public void validate(ClassLetterEnum classLetter, ShiftEnum shift) {
            if (!Set.of(ClassLetterEnum.TURMA_D, ClassLetterEnum.TURMA_E).contains(classLetter)) throw new InvalidClassroomConfigurationException(
                    "For INFANTIL_3 stage, class letter must be TURMA_D or TURMA_E"
            );
        }
    };

    public abstract void validate(ClassLetterEnum classLetter, ShiftEnum shift);
}

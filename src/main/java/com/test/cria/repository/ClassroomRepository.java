package com.test.cria.repository;

import com.test.cria.entity.Classroom;
import com.test.cria.entity.enums.ClassLetterEnum;
import com.test.cria.entity.enums.ShiftEnum;
import com.test.cria.entity.enums.StageEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClassroomRepository extends JpaRepository<Classroom, Long> {
    boolean existsByStageAndClassLetterAndShiftAndAcademicYear(
            StageEnum stage,
            ClassLetterEnum classLetter,
            ShiftEnum shift,
            int academicYear
    );

    @Query("""
        SELECT DISTINCT c 
        FROM Classroom c 
        LEFT JOIN FETCH c.staffMembers s 
        LEFT JOIN FETCH s.employee e 
        LEFT JOIN FETCH e.user u 
        WHERE c.id = :id
    """)
    Optional<Classroom> findByIdWithStaffAndEmployee(@Param("id") Long id);
}

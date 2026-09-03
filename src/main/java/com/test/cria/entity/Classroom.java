package com.test.cria.entity;

import com.test.cria.entity.enums.ClassLetterEnum;
import com.test.cria.entity.enums.ShiftEnum;
import com.test.cria.entity.enums.StageEnum;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "classroom")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Classroom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "stage", nullable = false, length = 30)
    private StageEnum stage;

    @Enumerated(EnumType.STRING)
    @Column(name = "class_letter", nullable = false, length = 10)
    private ClassLetterEnum classLetter;

    @Enumerated(EnumType.STRING)
    @Column(name = "shift", nullable = false, length = 20)
    private ShiftEnum shift;

    @Column(name = "academic_year", nullable = false)
    private Integer academicYear;

    @OneToMany(mappedBy = "classroom", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ClassroomStaff> staffMembers = new ArrayList<>();

    public void addStaff(Employee employee, LocalDate startDate) {
        ClassroomStaff staff = ClassroomStaff.builder()
                .classroom(this)
                .employee(employee)
                .startDate(startDate)
                .isActive(true)
                .build();

        this.staffMembers.add(staff);
    }
}

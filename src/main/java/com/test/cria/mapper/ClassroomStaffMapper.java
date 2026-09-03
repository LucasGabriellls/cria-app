package com.test.cria.mapper;

import com.test.cria.dto.classroomStaff.ClassroomStaffResponseDTO;
import com.test.cria.entity.ClassroomStaff;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {EmployeeMapper.class})
public interface ClassroomStaffMapper {

    ClassroomStaffResponseDTO toResponseDTO(ClassroomStaff staff);
}

package com.test.cria.mapper;

import com.test.cria.dto.classroom.ClassroomCreateDTO;
import com.test.cria.dto.classroom.ClassroomResponseDTO;
import com.test.cria.entity.Classroom;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {ClassroomStaffMapper.class})
public interface ClassroomMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "staffMembers", ignore = true)
    Classroom toEntity(ClassroomCreateDTO createDTO);

    ClassroomResponseDTO toResponseDTO(Classroom classroom);
}

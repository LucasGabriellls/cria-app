package com.test.cria.mapper;

import com.test.cria.dto.user.UserCreateRequestDTO;
import com.test.cria.dto.user.UserResponseDTO;
import com.test.cria.entity.Role;
import com.test.cria.entity.User;
import com.test.cria.entity.enums.RoleEnum;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserMapper {

    User toUserEntity(UserCreateRequestDTO userCreateRequestDTO);

    UserResponseDTO toUserResponseDTO(User userResponseDTO);

    default RoleEnum map(Role role) {
        return role != null ? role.getRole() : null;
    }
}

package com.test.cria.mapper;

import com.test.cria.DTO.request.UserRequestDTO;
import com.test.cria.DTO.response.UserResponseDTO;
import com.test.cria.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserMapper {

    User toUserEntity(UserRequestDTO userRequestDTO);

    UserResponseDTO toUserResponseDTO(User user);

    UserRequestDTO toUserRequestDTO(UserResponseDTO userResponseDTO);

    UserRequestDTO toUserRequestDTO(User user);
}

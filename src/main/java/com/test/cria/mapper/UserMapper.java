package com.test.cria.mapper;

import com.test.cria.dto.request.userRequest.UserCreateRequestDTO;
import com.test.cria.dto.response.userResponse.UserResponseDTO;
import com.test.cria.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserMapper {

    User toUserEntity(UserCreateRequestDTO userCreateRequestDTO);

    UserResponseDTO toUserResponseDTO(User userResponseDTO);
}

package com.test.cria.mapper;

import com.test.cria.dto.request.userRequest.UserRequestDTO;
import com.test.cria.dto.response.userResponse.UserResponseDTO;
import com.test.cria.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserMapper {

    User toUserEntity(UserRequestDTO userRequestDTO);

    UserResponseDTO toUserResponseDTO(User user);
}

package org.celebal.mapper;

import org.celebal.dto.UserDtos;
import org.celebal.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "userId", source = "userId")
    UserDtos.UserResponse toResponse(User user);
}



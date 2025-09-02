package org.celebal.mapper;

import org.celebal.dto.RideSessionDtos;
import org.celebal.model.RideSession;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RideSessionMapper {
    @Mapping(target = "groupId", source = "group.groupId")
    RideSessionDtos.SessionResponse toResponse(RideSession session);
}

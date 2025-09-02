package org.celebal.mapper;

import org.celebal.dto.GroupDtos;
import org.celebal.model.Group;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface GroupMapper {
    @Mapping(target = "adminUserId", source = "admin.userId")
    GroupDtos.GroupResponse toResponse(Group group);
}



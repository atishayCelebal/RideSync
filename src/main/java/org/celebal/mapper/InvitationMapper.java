package org.celebal.mapper;

import org.celebal.dto.InvitationDtos;
import org.celebal.model.Invitation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface InvitationMapper {
    @Mapping(target = "invitationId", source = "invitationId")
    @Mapping(target = "groupId", source = "group.groupId")
    InvitationDtos.InvitationResponse toResponse(Invitation invitation);
}



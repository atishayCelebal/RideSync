package org.celebal.mapper;

import org.celebal.dto.MembershipDtos;
import org.celebal.model.GroupMembership;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface MembershipMapper {
    @Mapping(target = "membershipId", source = "id")
    @Mapping(target = "userId", source = "user.userId")
    @Mapping(target = "username", source = "user.username")
    @Mapping(target = "role", expression = "java(m.getRole().name())")
    @Mapping(target = "status", expression = "java(m.getStatus().name())")
    MembershipDtos.MemberResponse toResponse(GroupMembership m);
}



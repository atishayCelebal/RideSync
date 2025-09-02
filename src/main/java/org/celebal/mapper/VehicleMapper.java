package org.celebal.mapper;

import org.celebal.dto.VehicleDtos;
import org.celebal.model.Vehicle;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface VehicleMapper {
    @Mapping(target = "ownerUserId", source = "owner.userId")
    VehicleDtos.VehicleResponse toResponse(Vehicle vehicle);
}



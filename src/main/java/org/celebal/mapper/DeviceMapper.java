package org.celebal.mapper;

import org.celebal.dto.DeviceDtos;
import org.celebal.model.Device;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface DeviceMapper {
    DeviceDtos.DeviceResponse toResponse(Device device);
}



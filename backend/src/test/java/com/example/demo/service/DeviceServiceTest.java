package com.example.demo.service;

import com.example.demo.dto.requestsDto.DeviceRegistrationDto;
import com.example.demo.repositories.DeviceRepository;
import com.example.demo.service.impl.DeviceServiceImpl;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DeviceServiceTest {

    @InjectMocks
    private DeviceServiceImpl deviceService;

    @Mock
    private DeviceRepository deviceRepository;

    public DeviceServiceTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testRegisterDevice() {
        DeviceRegistrationDto deviceDto = new DeviceRegistrationDto();
        deviceDto.setDeviceId("device-id");
        deviceDto.setVehicleId("vehicle-id");

        String response = deviceService.registerDevice(deviceDto);
        assertEquals("Device registered successfully.", response);
    }
}
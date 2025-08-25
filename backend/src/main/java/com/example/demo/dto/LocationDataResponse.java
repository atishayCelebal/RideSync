package com.example.demo.dto;

public class LocationDataResponse {
    private String deviceId;
    private double latitude;
    private double longitude;
    private String timestamp;

    public LocationDataResponse(String deviceId, double latitude, double longitude, String timestamp) {
        this.deviceId = deviceId;
        this.latitude = latitude;
        this.longitude = longitude;
        this.timestamp = timestamp;
    }

    // Getters
    public String getDeviceId() { return deviceId; }
    public double getLatitude() { return latitude; }
    public double getLongitude() { return longitude; }
    public String getTimestamp() { return timestamp; }
}

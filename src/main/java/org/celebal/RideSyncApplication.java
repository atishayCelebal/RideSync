package org.celebal;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Main Spring Boot application class for RideSync
 * 
 * This application provides:
 * - User and vehicle management
 * - Smartcar OAuth integration for vehicle tracking
 * - Real-time group location aggregation
 * - WebSocket broadcasting for live updates
 * - Kafka integration for data streaming
 * - Anomaly detection for group safety
 */
@SpringBootApplication
@EnableScheduling
@EnableConfigurationProperties
public class RideSyncApplication {

    public static void main(String[] args) {
        SpringApplication.run(RideSyncApplication.class, args);
        
        System.out.println("🚗 RideSync Application Started Successfully!");
        System.out.println("📍 Smartcar Integration: ENABLED");
        System.out.println("🌐 WebSocket Broadcasting: ENABLED");
        System.out.println("📊 Kafka Streaming: ENABLED");
        System.out.println("🔒 OAuth 2.0 Security: ENABLED");
        System.out.println("📱 Group Location Tracking: ENABLED");
    }
}

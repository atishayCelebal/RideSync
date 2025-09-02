package org.celebal.config;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import java.time.Duration;

@Configuration
@ConfigurationProperties(prefix = "smartcar")
@Data
@Slf4j
public class SmartcarConfig {
    
    private String clientId;
    private String clientSecret;
    private String redirectUri;
    private String scope;
    private String apiBaseUrl;
    
    // Add logging to verify properties are loaded
    public String getApiBaseUrl() {
        log.info("Getting API Base URL: {}", apiBaseUrl);
        return apiBaseUrl;
    }
    
    @Bean
    public WebClient smartcarWebClient() {
        log.info("Configuring Smartcar WebClient WITHOUT base URL to avoid connection issues");
        
        return WebClient.builder()
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(2 * 1024 * 1024)) // 2MB buffer
                .filter((request, next) -> {
                    log.info("Making request to: {} {}", request.method(), request.url());
                    return next.exchange(request);
                })
                .build();
    }
}

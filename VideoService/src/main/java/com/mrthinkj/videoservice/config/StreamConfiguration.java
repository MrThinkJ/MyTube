package com.mrthinkj.videoservice.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
public class StreamConfiguration {
    @Value("${server.stream.prefix:http://localhost:8081/api/v1/videos/}")
    private String streamPrefix;
}

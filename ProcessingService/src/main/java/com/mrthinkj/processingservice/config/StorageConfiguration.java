package com.mrthinkj.processingservice.config;

import io.minio.MinioClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StorageConfiguration {
    @Value("${minio.access-key}")
    private String accessKey;
    @Value("${minio.url}")
    private String url;
    @Value("${minio.secret-key}")
    private String secretKey;
    @Value("${minio.bucket.store}")
    private String bucketStore;
    @Value("${minio.bucket.stream}")
    private String bucketStream;

    @Bean
    MinioClient minioClient(){
        return MinioClient.builder()
                .endpoint(url)
                .credentials(accessKey, secretKey)
                .build();
    }
}

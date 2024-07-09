package com.mrthinkj.videoservice.config;

import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Data
public class StorageConfiguration {
    @Value("${minio.access-key}")
    private String accessKey;
    @Value("${minio.secret-key}")
    private String secretKey;
    @Value("${minio.bucket.stream}")
    private String bucketStream;
    @Value("${minio.bucket.store}")
    private String bucketStore;
    @Value("${minio.url}")
    private String url;
    private static final Logger logger = LoggerFactory.getLogger(StorageConfiguration.class);

    @Bean
    public MinioClient minioClient() throws Exception {
        MinioClient minioClient =  MinioClient.builder()
                .credentials(accessKey, secretKey)
                .endpoint(url)
                .build();
        initBucket(minioClient, bucketStream);
        initBucket(minioClient, bucketStore);
        return minioClient;
    }

    private void initBucket(MinioClient minioClient, String bucket) throws Exception{
        boolean exist = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucket).build());
        if (!exist){
            minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucket).build());
            logger.info("Create bucket {} successfully", bucket);
        }
    }
}

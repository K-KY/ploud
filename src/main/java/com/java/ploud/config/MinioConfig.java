package com.java.ploud.config;

import io.minio.MinioClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

//minio 설정
@Configuration
public class MinioConfig {
    @Value("${minio.endpoint}")
    private String endpoint;
    @Value("${minio.endpoint.internal}")
    private String internalEndpoint;
    @Value("${minio.access-key}")
    private String accessKey;
    @Value("${minio.secret-key}")
    private String secretKey;

    @Bean("externalMinioClient")
    public MinioClient minioClient() {
        return MinioClient.builder()
                .endpoint(endpoint)
                .region("us-east-1")//**이게 없으면 외부에서 조회 불가능한 경로를 접속해서 알아내려 시도 -> 접근 불가해서 에러
                .credentials(accessKey, secretKey)
                .build();
    }

    @Bean("internalMinioClient")
    public MinioClient internalMinioClient() {
        return MinioClient.builder()
                .endpoint(internalEndpoint)
                .credentials(accessKey, secretKey)
                .build();
    }
}



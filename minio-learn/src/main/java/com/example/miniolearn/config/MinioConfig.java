package com.example.miniolearn.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.minio.MinioClient;
import lombok.extern.slf4j.Slf4j;

/**
 * @author zhangkai
 * @version V2.1
 * @since 2021/9/5 0:06
 */
@Configuration
@Slf4j
public class MinioConfig {
    @Autowired
    private MinioProperties minioProperties;
    @Bean
    public MinioClient minioClient(){
        log.info("minioClient create start");
        MinioClient minioClient =
            MinioClient.builder()
                .endpoint(minioProperties.getEndpoint())
                .credentials(minioProperties.getAccessKey(), minioProperties.getSecretKey())
                .build();
            return minioClient;
    }
}

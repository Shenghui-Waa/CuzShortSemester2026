package com.cuzssp.campussecondhandtradingplatformbackend.common.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "s3.config")
public class S3Config {

    private String storageSupport;
    private String accountId;
    private String accessKey;
    private String secretKey;
    private String endpoint;
    private String bucketName;
    private String childFolder;
    private String cdnDomain;
    private String region;

}

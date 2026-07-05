package com.cuzssp.campussecondhandtradingplatform_backend.common.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "r2.s3")
public class S3Config {

    private String accountId;
    private String accessKey;
    private String secretKey;
    private String endpoint;
    private String bucketName;
    private String childFolder;
    private String cdnDomain;
    private String region;

}

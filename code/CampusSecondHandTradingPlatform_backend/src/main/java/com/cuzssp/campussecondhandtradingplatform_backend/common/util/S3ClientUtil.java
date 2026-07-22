package com.cuzssp.campussecondhandtradingplatform_backend.common.util;

import com.cuzssp.campussecondhandtradingplatform_backend.common.config.S3Config;
import com.cuzssp.campussecondhandtradingplatform_backend.common.constant.ConfigConstant;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;
import java.net.URI;

/*
 S3 兼容客户端
 */
@Component
@RequiredArgsConstructor
public class S3ClientUtil {

    private final S3Config s3Config;

    public S3Client getS3Client() {
        return buildS3Client();
    }

    private S3Client buildS3Client() {
        AwsBasicCredentials awsBasicCredentials = AwsBasicCredentials.create(
                s3Config.getAccessKey(),
                s3Config.getSecretKey()
        );
        S3Configuration serviceConfiguration = S3Configuration.builder()
                .pathStyleAccessEnabled(s3Config.getStorageSupport().equals(ConfigConstant.R2))
                .chunkedEncodingEnabled(false)
                .build();
        Region region = s3Config.getStorageSupport().equals(ConfigConstant.R2)
                ? Region.of(s3Config.getRegion())
                : Region.AWS_GLOBAL;
        return S3Client.builder()
                .endpointOverride(URI.create(s3Config.getEndpoint()))
                .credentialsProvider(StaticCredentialsProvider.create(awsBasicCredentials))
                .region(region)
                .serviceConfiguration(serviceConfiguration)
                .build();
    }

}
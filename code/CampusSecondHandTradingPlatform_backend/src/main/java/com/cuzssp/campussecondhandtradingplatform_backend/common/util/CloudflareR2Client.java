package com.cuzssp.campussecondhandtradingplatform_backend.common.util;

import com.cuzssp.campussecondhandtradingplatform_backend.common.config.S3Config;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.services.s3.model.*;

import java.net.URI;
import java.util.List;

/**
 * Cloudflare R2 S3 兼容客户端
 */
@Component
public class CloudflareR2Client {

    private final S3Config s3Config;

    public CloudflareR2Client(S3Config s3Config) {
        this.s3Config = s3Config;
    }

    public S3Client getS3Client() {
        return buildS3Client();
    }

    private S3Client buildS3Client() {
        AwsBasicCredentials credentials = AwsBasicCredentials.create(
                s3Config.getAccessKey(), s3Config.getSecretKey());
        S3Configuration serviceConfiguration = S3Configuration.builder()
                .pathStyleAccessEnabled(true).chunkedEncodingEnabled(false).build();
        return S3Client.builder()
                .endpointOverride(URI.create(s3Config.getEndpoint()))
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                .region(Region.of(s3Config.getRegion()))
                .serviceConfiguration(serviceConfiguration).build();
    }

    public List<Bucket> listBuckets() {
        try { return buildS3Client().listBuckets().buckets(); }
        catch (S3Exception e) { throw new RuntimeException("Failed to list buckets: " + e.getMessage(), e); }
    }

    public List<S3Object> listObjects(String bucketName) {
        try {
            ListObjectsV2Request request = ListObjectsV2Request.builder().bucket(bucketName).build();
            return buildS3Client().listObjectsV2(request).contents();
        } catch (S3Exception e) { throw new RuntimeException("Failed to list objects: " + e.getMessage(), e); }
    }

    public void putObject(String bucketName, String key, String content) {
        try {
            PutObjectRequest request = PutObjectRequest.builder().bucket(bucketName).key(key).build();
            buildS3Client().putObject(request, RequestBody.fromString(content));
        } catch (S3Exception e) { throw new RuntimeException("Failed to put object: " + e.getMessage(), e); }
    }
}
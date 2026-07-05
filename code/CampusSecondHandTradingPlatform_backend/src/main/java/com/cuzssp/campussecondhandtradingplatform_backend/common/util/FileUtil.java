package com.cuzssp.campussecondhandtradingplatform_backend.common.util;

import com.cuzssp.campussecondhandtradingplatform_backend.common.config.S3Config;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import org.springframework.beans.factory.annotation.Autowired;

import java.net.URI;
import java.util.UUID;

@Component
public class FileUtil {

    @Autowired
    private S3Config s3Config;

    public String uploadImage(MultipartFile file) {
        try {
            AwsBasicCredentials credentials = AwsBasicCredentials.create(
                    s3Config.getAccessKey(), s3Config.getSecretKey());
            S3Configuration serviceConfiguration = S3Configuration.builder()
                    .pathStyleAccessEnabled(true)
                    .chunkedEncodingEnabled(false)
                    .build();
            S3Client s3Client = S3Client.builder()
                    .endpointOverride(URI.create(s3Config.getEndpoint()))
                    .credentialsProvider(StaticCredentialsProvider.create(credentials))
                    .region(Region.of(s3Config.getRegion()))
                    .serviceConfiguration(serviceConfiguration)
                    .build();
            String originalFilename = file.getOriginalFilename();
            String extension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            String key = (s3Config.getChildFolder() != null ? s3Config.getChildFolder() : "")
                    + UUID.randomUUID() + extension;
            PutObjectRequest request = PutObjectRequest.builder()
                    .bucket(s3Config.getBucketName())
                    .key(key)
                    .contentType(file.getContentType())
                    .build();
            s3Client.putObject(request, RequestBody.fromBytes(file.getBytes()));
            String cdnDomain = s3Config.getCdnDomain();
            if (cdnDomain != null && !cdnDomain.isEmpty()) {
                return cdnDomain + "/" + key;
            }
            return s3Config.getEndpoint() + "/" + s3Config.getBucketName() + "/" + key;
        } catch (Exception e) {
            throw new RuntimeException("文件上传失败: " + e.getMessage(), e);
        }
    }
}

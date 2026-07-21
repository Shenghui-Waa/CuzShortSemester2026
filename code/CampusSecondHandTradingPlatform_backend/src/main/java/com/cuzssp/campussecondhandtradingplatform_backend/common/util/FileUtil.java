package com.cuzssp.campussecondhandtradingplatform_backend.common.util;

import com.cuzssp.campussecondhandtradingplatform_backend.common.config.S3Config;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class FileUtil {

    private final S3Config s3Config;
    private final S3ClientUtil s3ClientUtil;

    public String uploadImage(MultipartFile file) {
        try {
            S3Client s3Client = s3ClientUtil.getS3Client();
            String originalFilename = file.getOriginalFilename();
            String extension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            String key = (s3Config.getChildFolder() != null ? s3Config.getChildFolder() : "")
                    + "cuzssp-"
                    + UUID.randomUUID()
                    + extension;
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
            log.error("File upload failed: {}", e.getMessage(), e);
            throw new RuntimeException("File upload failed: " + e.getMessage(), e);
        }
    }
}
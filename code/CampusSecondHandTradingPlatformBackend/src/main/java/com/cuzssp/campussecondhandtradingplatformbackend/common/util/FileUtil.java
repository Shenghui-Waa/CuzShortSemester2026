package com.cuzssp.campussecondhandtradingplatformbackend.common.util;

import com.cuzssp.campussecondhandtradingplatformbackend.common.config.S3Config;
import com.cuzssp.campussecondhandtradingplatformbackend.common.dto.Result;
import com.cuzssp.campussecondhandtradingplatformbackend.common.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.util.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class FileUtil {

    private final S3Config s3Config;
    private final S3ClientUtil s3ClientUtil;

    private static final Set<String> ALLOWED_EXTENSIONS = new HashSet<>(Arrays.asList(
            "jpg", "jpeg", "png", "gif", "webp", "svg"
    ));

    private static final long MAX_FILE_SIZE = 2 * 1024 * 1024; // 2MB

    public String upload(MultipartFile file) {
        try (
                S3Client s3Client = s3ClientUtil.getS3Client()
        ) {
            // 获取原文件名
            String originalFilename = file.getOriginalFilename();
            // 获取后缀名
            String extension = "";
            if (originalFilename != null && originalFilename.contains("."))
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));

            // 获取新文件名
            String key = (
                    s3Config.getChildFolder() != null ? s3Config.getChildFolder() : ""
            )
                    + "cuzssp-"
                    + UUID.randomUUID()
                    + extension;
            // 创建上传请求
            PutObjectRequest request = PutObjectRequest.builder()
                    .bucket(s3Config.getBucketName())
                    .key(key)
                    .contentType(file.getContentType())
                    .build();
            // 上传
            s3Client.putObject(request, RequestBody.fromBytes(file.getBytes()));
            // 生成访问 url
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

    public void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty())
            throw new BusinessException("文件是空的");

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new BusinessException(Result.Code.REQUEST_ENTITY_TOO_LARGE,
                    "文件超过2MB限制");
        }
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || !originalFilename.contains(".")) {
            throw new BusinessException("文件格式无效");
        }
        String extension = originalFilename
                .substring(originalFilename.lastIndexOf(".") + 1)
                .toLowerCase(Locale.ROOT);
        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            throw new BusinessException(Result.Code.FORBIDDEN, "文件格式不接受: " + extension);
        }
    }

}
package com.cuzssp.campussecondhandtradingplatform_backend.controller;

import com.cuzssp.campussecondhandtradingplatform_backend.common.security.JwtTokenProvider;
import com.cuzssp.campussecondhandtradingplatform_backend.common.exception.BusinessException;
import com.cuzssp.campussecondhandtradingplatform_backend.service.FileService;
import com.cuzssp.campussecondhandtradingplatform_backend.common.dto.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
/*
  不提供文件删除 API
 */
public class FileController {

    private final FileService fileService;
    private final JwtTokenProvider jwtTokenProvider;

    private static final Set<String> ALLOWED_EXTENSIONS = new HashSet<>(Arrays.asList(
            "jpg", "jpeg", "png", "gif", "webp", "svg"
    ));

    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB

    /**
     * 单文件上传
     */
    @PostMapping("/upload")
    public Result<?> uploadFile(
            @RequestHeader("Authorization") String token,
            @RequestParam("file") MultipartFile file
    ) {
        validateToken(token);
        validateFile(file);
        return fileService.uploadFile(file);
    }

    /**
     * 多文件上传
     */
    @PostMapping("/uploads")
    public Result<?> uploadFiles(
            @RequestHeader("Authorization") String token,
            @RequestParam("files") List<MultipartFile> files
    ) {
        validateToken(token);
        for (MultipartFile file : files)
            validateFile(file);
        return fileService.uploadFiles(files);
    }

    private void validateToken(String token) {
        if (token == null || !token.startsWith("Bearer "))
            throw new BusinessException(401, "Unauthorized");
        String jwt = token.substring(7);
        if (!jwtTokenProvider.validateToken(jwt))
            throw new BusinessException(406, "Invalid token");
    }

    private void validateFile(MultipartFile file) {
        if (file.isEmpty())
            throw new BusinessException(403, "File is empty");
        if (file.getSize() > MAX_FILE_SIZE)
            throw new BusinessException(413, "File size exceeds 10MB limit");
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || !originalFilename.contains("."))
            throw new BusinessException(405, "Invalid file type");
        String extension = originalFilename
                .substring(originalFilename.lastIndexOf(".") + 1)
                .toLowerCase();
        if (!ALLOWED_EXTENSIONS.contains(extension))
            throw new BusinessException(405, "File type not allowed: " + extension);
    }

}

package com.cuzssp.campussecondhandtradingplatformbackend.controller;

import com.cuzssp.campussecondhandtradingplatformbackend.common.dto.Result;
import com.cuzssp.campussecondhandtradingplatformbackend.common.security.TokenProvider;
import com.cuzssp.campussecondhandtradingplatformbackend.service.FileService;
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
    private final TokenProvider tokenProvider;

    private static final Set<String> ALLOWED_EXTENSIONS = new HashSet<>(Arrays.asList(
            "jpg", "jpeg", "png", "gif", "webp", "svg"
    ));

    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB


}

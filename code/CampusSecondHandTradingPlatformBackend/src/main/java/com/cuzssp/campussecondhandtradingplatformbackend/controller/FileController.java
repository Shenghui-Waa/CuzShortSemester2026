package com.cuzssp.campussecondhandtradingplatformbackend.controller;

import com.cuzssp.campussecondhandtradingplatformbackend.service.FileService;
import com.cuzssp.campussecondhandtradingplatformbackend.common.dto.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
/*
  不提供文件删除 API
 */
public class FileController {

    private final FileService fileService;

    /**
     * 单文件上传
     */
    @PostMapping("/upload")
    public Result<?> uploadFile(
            @RequestParam("file") MultipartFile file
    ) {
        return Result.success(fileService.uploadFile(file));
    }

    /**
     * 多文件上传
     */
    @PostMapping("/uploads")
    public Result<?> uploadFiles(
            @RequestParam("files") List<MultipartFile> files
    ) {
        return Result.success(fileService.uploadFiles(files));
    }

}

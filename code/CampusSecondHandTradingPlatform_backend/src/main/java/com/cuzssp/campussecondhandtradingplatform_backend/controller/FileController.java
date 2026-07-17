package com.cuzssp.campussecondhandtradingplatform_backend.controller;

import com.cuzssp.campussecondhandtradingplatform_backend.service.FileService;

import com.cuzssp.campussecondhandtradingplatform_backend.common.vo.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
/**
 * 不提供文件删除 api
 */
public class FileController {

    private final FileService fileService;

    /**
     * 单文件上传
     * @param file
     * @return
     */
    @PostMapping("/upload")
    public Result<?> uploadFile(
            @RequestParam("file") MultipartFile file
    ) {
        return fileService.uploadFile(file);
    }

    /**
     * 多文件上传
     * @param files
     * @return
     */
    @PostMapping("/uploads")
    public Result<?> uploadFiles(
            @RequestParam("files") List<MultipartFile> files
    ) {
        return fileService.uploadFiles(files);
    }
}

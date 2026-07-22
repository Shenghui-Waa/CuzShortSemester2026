package com.cuzssp.campussecondhandtradingplatform_backend.service.impl;

import com.cuzssp.campussecondhandtradingplatform_backend.service.FileService;

import com.cuzssp.campussecondhandtradingplatform_backend.common.util.FileUtil;
import com.cuzssp.campussecondhandtradingplatform_backend.common.dto.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {

    private final FileUtil fileUtil;

    // 上传单个文件
    @Override
    public Result<String> uploadFile(
            MultipartFile file
    ) {
        String url = fileUtil.uploadImage(file);
        return Result.success(url);
    }

    // 上传多个文件
    @Override
    public Result<List<String>> uploadFiles(
            List<MultipartFile> files
    ) {
        List<String> urls = files.stream()
                .map(fileUtil::uploadImage)
                .collect(Collectors.toList());
        return Result.success(urls);
    }
}

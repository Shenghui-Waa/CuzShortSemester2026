package com.cuzssp.campussecondhandtradingplatformbackend.service.impl;

import com.cuzssp.campussecondhandtradingplatformbackend.service.FileService;

import com.cuzssp.campussecondhandtradingplatformbackend.common.util.FileUtil;
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
    public String uploadFile(
            MultipartFile file
    ) {
        fileUtil.validateFile(file);
        return fileUtil.upload(file);
    }

    // 上传多个文件
    @Override
    public List<String> uploadFiles(
            List<MultipartFile> files
    ) {
        for (MultipartFile file : files) {
            fileUtil.validateFile(file);
        }
        return files.stream()
                .map(fileUtil::upload)
                .collect(Collectors.toList());
    }
}

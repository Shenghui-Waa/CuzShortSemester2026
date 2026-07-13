package com.cuzssp.campussecondhandtradingplatform_backend.service.impl;

import com.cuzssp.campussecondhandtradingplatform_backend.service.FileService;

import com.cuzssp.campussecondhandtradingplatform_backend.common.util.FileUtil;
import com.cuzssp.campussecondhandtradingplatform_backend.common.vo.Result;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FileServiceImpl implements FileService {

    @Autowired private FileUtil fileUtil;

    /**
     * 单文件上传
     * @param file
     * @return
     */
    @Override
    public Result<String> uploadFile(
            MultipartFile file
    ) {
        String url = fileUtil.uploadImage(file);
        return Result.success(url);
    }

    /**
     * 多文件上传
     * @param files
     * @return
     */
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

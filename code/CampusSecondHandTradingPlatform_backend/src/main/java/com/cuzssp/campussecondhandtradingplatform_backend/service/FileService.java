package com.cuzssp.campussecondhandtradingplatform_backend.service;

import com.cuzssp.campussecondhandtradingplatform_backend.common.vo.Result;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

public interface FileService {
    Result<String> uploadFile(MultipartFile file);
    Result<List<String>> uploadFiles(List<MultipartFile> files);
}

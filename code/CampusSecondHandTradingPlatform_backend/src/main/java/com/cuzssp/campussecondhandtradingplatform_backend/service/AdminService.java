package com.cuzssp.campussecondhandtradingplatform_backend.service;

import com.cuzssp.campussecondhandtradingplatform_backend.common.dto.RegisterRequest;
import com.cuzssp.campussecondhandtradingplatform_backend.common.vo.*;
import jakarta.validation.Valid;

public interface AdminService {
    Result<DashboardVO> getDashboard();
}

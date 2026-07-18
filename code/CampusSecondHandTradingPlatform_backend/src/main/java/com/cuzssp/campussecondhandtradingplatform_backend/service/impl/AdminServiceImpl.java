package com.cuzssp.campussecondhandtradingplatform_backend.service.impl;

import com.cuzssp.campussecondhandtradingplatform_backend.common.constant.ProductConstant;
import com.cuzssp.campussecondhandtradingplatform_backend.common.constant.UserConstant;
import com.cuzssp.campussecondhandtradingplatform_backend.common.dto.RegisterRequest;
import com.cuzssp.campussecondhandtradingplatform_backend.common.entity.*;
import com.cuzssp.campussecondhandtradingplatform_backend.common.exception.BusinessException;
import com.cuzssp.campussecondhandtradingplatform_backend.common.security.Base64Provider;
import com.cuzssp.campussecondhandtradingplatform_backend.common.util.ToEntityUtil;
import com.cuzssp.campussecondhandtradingplatform_backend.common.util.ToVOUtil;
import com.cuzssp.campussecondhandtradingplatform_backend.common.vo.*;
import com.cuzssp.campussecondhandtradingplatform_backend.mapper.*;
import com.cuzssp.campussecondhandtradingplatform_backend.service.AdminService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final UserMapper userMapper;
    private final ProductMapper productMapper;
    private final OrderMapper orderMapper;

    @Override
    public Result<DashboardVO> getDashboard() {
        return Result.success(ToVOUtil.toDashboardVO(userMapper, productMapper, orderMapper));
    }

}

package com.cuzssp.campussecondhandtradingplatform_backend.service;

import com.cuzssp.campussecondhandtradingplatform_backend.common.dto.ReviewRequest;
import com.cuzssp.campussecondhandtradingplatform_backend.common.vo.PageResult;
import com.cuzssp.campussecondhandtradingplatform_backend.common.vo.Result;
import com.cuzssp.campussecondhandtradingplatform_backend.common.vo.ReviewVO;

public interface ReviewService {
    Result<Void> createReview(Long reviewerId, ReviewRequest request);
    Result<PageResult<ReviewVO>> getUserReviews(Long userId, Integer page, Integer pageSize);
}

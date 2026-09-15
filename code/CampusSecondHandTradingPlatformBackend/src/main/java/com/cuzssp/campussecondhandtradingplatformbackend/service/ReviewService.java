package com.cuzssp.campussecondhandtradingplatformbackend.service;

import com.cuzssp.campussecondhandtradingplatformbackend.common.dto.request.ReviewRequest;
import com.cuzssp.campussecondhandtradingplatformbackend.common.dto.PageResult;
import com.cuzssp.campussecondhandtradingplatformbackend.common.vo.ReviewVO;

public interface ReviewService {

    PageResult<ReviewVO> getUserReviews(Long userId, Integer page, Integer pageSize);
    Void createReview(Long reviewerId, ReviewRequest request);

}

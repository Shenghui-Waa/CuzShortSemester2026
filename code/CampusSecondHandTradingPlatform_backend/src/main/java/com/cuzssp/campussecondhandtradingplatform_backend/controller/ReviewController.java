package com.cuzssp.campussecondhandtradingplatform_backend.controller;

import com.cuzssp.campussecondhandtradingplatform_backend.common.dto.ReviewRequest;
import com.cuzssp.campussecondhandtradingplatform_backend.common.security.SecurityUtil;
import com.cuzssp.campussecondhandtradingplatform_backend.service.ReviewService;
import com.cuzssp.campussecondhandtradingplatform_backend.common.dto.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;
    private final SecurityUtil securityUtil;

    /**
     * 创建评价
     */
    @PostMapping
    public Result<?> createReview(
            @RequestHeader("Authorization") String token,
            @RequestBody ReviewRequest request
    ) {
        Long currentUserId = securityUtil.getCurrentUserId(token);
        return reviewService.createReview(currentUserId, request);
    }

    /**
     * 获取评价记录
     */
    @GetMapping("/user/{userId}")
    public Result<?> getUserReviews(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize
    ) {
        return reviewService.getUserReviews(userId, page, pageSize);
    }

}

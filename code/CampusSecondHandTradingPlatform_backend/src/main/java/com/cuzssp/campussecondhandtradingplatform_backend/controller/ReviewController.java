package com.cuzssp.campussecondhandtradingplatform_backend.controller;

import com.cuzssp.campussecondhandtradingplatform_backend.common.dto.ReviewRequest;

import com.cuzssp.campussecondhandtradingplatform_backend.common.security.JwtTokenProvider;
import com.cuzssp.campussecondhandtradingplatform_backend.service.ReviewService;
import com.cuzssp.campussecondhandtradingplatform_backend.common.vo.Result;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    @Autowired private ReviewService reviewService;
    @Autowired private JwtTokenProvider jwtTokenProvider;

    /**
     * 创建评价
     * @param token
     * @param request
     * @return
     */
    @PostMapping
    public Result<?> createReview(
            @RequestHeader("Authorization") String token,
            @RequestBody ReviewRequest request
    ) {
        Long currentUserId = getCurrentUserId(token);
        return reviewService.createReview(currentUserId, request);
    }

    /**
     * 获取评价记录
     * @param userId
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("/user/{userId}")
    public Result<?> getUserReviews(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize
    ) {
        return reviewService.getUserReviews(userId, page, pageSize);
    }

    private Long getCurrentUserId(String token) {
        if (token == null || token.isEmpty())
            return null;
        try {
            return jwtTokenProvider.getUserIdFromToken(token.replace("Bearer ", ""));
        } catch (Exception e) {
            return null;
        }
    }
}

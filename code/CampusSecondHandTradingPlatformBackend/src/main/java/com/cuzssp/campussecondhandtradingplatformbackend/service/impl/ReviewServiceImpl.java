package com.cuzssp.campussecondhandtradingplatformbackend.service.impl;
import com.cuzssp.campussecondhandtradingplatformbackend.common.dto.Result;
import com.cuzssp.campussecondhandtradingplatformbackend.common.entity.Review;
import com.cuzssp.campussecondhandtradingplatformbackend.common.entity.OrderInfo;
import com.cuzssp.campussecondhandtradingplatformbackend.common.util.ToEntityUtil;
import com.cuzssp.campussecondhandtradingplatformbackend.common.util.ToVOUtil;
import com.cuzssp.campussecondhandtradingplatformbackend.mapper.ReviewMapper;
import com.cuzssp.campussecondhandtradingplatformbackend.mapper.OrderInfoMapper;
import com.cuzssp.campussecondhandtradingplatformbackend.mapper.UserMapper;
import com.cuzssp.campussecondhandtradingplatformbackend.service.ReviewService;
import com.cuzssp.campussecondhandtradingplatformbackend.common.vo.ReviewVO;
import com.cuzssp.campussecondhandtradingplatformbackend.common.dto.PageResult;
import com.cuzssp.campussecondhandtradingplatformbackend.common.dto.request.ReviewRequest;
import com.cuzssp.campussecondhandtradingplatformbackend.common.exception.BusinessException;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.cuzssp.campussecondhandtradingplatformbackend.common.constant.OrderInfoConstant;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReviewMapper reviewMapper;
    private final OrderInfoMapper orderMapper;
    private final UserMapper userMapper;

    // 获取评价
    @Override
    public PageResult<ReviewVO> getUserReviews(
            Long userId, Integer page, Integer pageSize
    ) {
        validatePagination(page, pageSize);
        PageHelper.startPage(page, pageSize);
        try {
            List<Review> reviews = reviewMapper.selectByTargetId(userId);
            PageInfo<Review> reviewPageInfo = new PageInfo<>(reviews);
            List<ReviewVO> reviewVOs = reviews
                    .stream()
                    .map(review -> ToVOUtil.toReviewVO(
                            review, userMapper.selectById(review.getReviewerId())
                    ))
                    .collect(Collectors.toList());
            return new PageResult<>(
                    reviewVOs, reviewPageInfo.getTotal(),
                    reviewPageInfo.getPageNum(), reviewPageInfo.getPageSize()
            );
        } finally {
            PageHelper.clearPage();
        }
    }

    // 创建评价
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Void createReview(
            Long reviewerId, ReviewRequest request
    ) {
        if (request == null || request.getOrderId() == null || request.getTargetId() == null
                || request.getRating() == null || request.getRating() < 1 || request.getRating() > 5)
            throw new BusinessException("Invalid review details");

        // Acquire a write lock without database-specific FOR UPDATE syntax.
        orderMapper.lockForReview(request.getOrderId());
        OrderInfo order = orderMapper.selectById(request.getOrderId());
        if (order == null)
            throw new BusinessException(Result.Code.NOT_FOUND, "Order not found");

        if (!Objects.equals(order.getBuyerId(), reviewerId)
                && !Objects.equals(order.getSellerId(), reviewerId))
            throw new BusinessException(Result.Code.FORBIDDEN, "Not a participant of this order");

        Long expectedTarget = Objects.equals(order.getBuyerId(), reviewerId)
                ? order.getSellerId() : order.getBuyerId();
        if (!Objects.equals(expectedTarget, request.getTargetId()))
            throw new BusinessException("Review target must be the other order participant");

        if (!Objects.equals(order.getStatus(), OrderInfoConstant.Status.COMPLETED))
            throw new BusinessException("Only completed orders can be reviewed");

        if (reviewMapper.countByOrderIdAndReviewerId(request.getOrderId(), reviewerId) > 0)
            throw new BusinessException(Result.Code.FORBIDDEN, "Already reviewed");

        Review review = ToEntityUtil.toReviewEntity(reviewerId, request);
        reviewMapper.insert(review);
        int adjustment = request.getRating() >= 3 ? 1 : -1;
        userMapper.adjustCreditScore(request.getTargetId(), adjustment, LocalDateTime.now());
        return null;
    }


    private void validatePagination(Integer page, Integer pageSize) {
        if (page == null || page < 1
                || pageSize == null || pageSize < 1 || pageSize > 100)
            throw new BusinessException("Invalid pagination");

    }

}

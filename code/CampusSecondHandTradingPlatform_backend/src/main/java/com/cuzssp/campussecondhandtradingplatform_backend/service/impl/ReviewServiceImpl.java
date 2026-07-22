package com.cuzssp.campussecondhandtradingplatform_backend.service.impl;
import com.cuzssp.campussecondhandtradingplatform_backend.common.entity.Review;
import com.cuzssp.campussecondhandtradingplatform_backend.common.entity.OrderInfo;
import com.cuzssp.campussecondhandtradingplatform_backend.common.entity.User;
import com.cuzssp.campussecondhandtradingplatform_backend.common.util.ToEntityUtil;
import com.cuzssp.campussecondhandtradingplatform_backend.common.util.ToVOUtil;
import com.cuzssp.campussecondhandtradingplatform_backend.mapper.ReviewMapper;
import com.cuzssp.campussecondhandtradingplatform_backend.mapper.OrderMapper;
import com.cuzssp.campussecondhandtradingplatform_backend.mapper.UserMapper;
import com.cuzssp.campussecondhandtradingplatform_backend.service.ReviewService;
import com.cuzssp.campussecondhandtradingplatform_backend.common.dto.Result;
import com.cuzssp.campussecondhandtradingplatform_backend.common.vo.ReviewVO;
import com.cuzssp.campussecondhandtradingplatform_backend.common.vo.PageResult;
import com.cuzssp.campussecondhandtradingplatform_backend.common.dto.ReviewRequest;
import com.cuzssp.campussecondhandtradingplatform_backend.common.exception.BusinessException;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReviewMapper reviewMapper;
    private final OrderMapper orderMapper;
    private final UserMapper userMapper;

    // 获取评价
    @Override
    public Result<PageResult<ReviewVO>> getUserReviews(
            Long userId, Integer page, Integer pageSize
    ) {
        PageHelper.startPage(page, pageSize);
        List<Review> reviews = reviewMapper.selectByTargetId(userId);
        PageInfo<Review> reviewPageInfo = new PageInfo<>(reviews);
        List<ReviewVO> reviewVOs = reviews.stream()
                .map(review -> ToVOUtil.toReviewVO(
                        review, userMapper.selectById(review.getReviewerId())
                )).collect(Collectors.toList());
        return Result.success(new PageResult<>(
                reviewVOs, reviewPageInfo.getTotal(),
                reviewPageInfo.getPageNum(), reviewPageInfo.getPageSize()
        ));
    }

    // 创建评价
    @Override
    public Result<Void> createReview(
            Long reviewerId, ReviewRequest request
    ) {
        OrderInfo order = orderMapper.selectById(request.getOrderId());
        if (order == null)
            throw new BusinessException(404, "Order not found");
        if (!Objects.equals(order.getBuyerId(), reviewerId)
                && !Objects.equals(order.getSellerId(), reviewerId))
            throw new BusinessException(403, "Not a participant of this order");
        if (reviewMapper.countByOrderIdAndReviewerId(request.getOrderId(), reviewerId) > 0)
            throw new BusinessException(403, "Already reviewed");
        Review review = ToEntityUtil.toReviewEntity(reviewerId, request);
        reviewMapper.insert(review);
        User target = userMapper.selectById(request.getTargetId());
        if (target != null) {
            target.setCreditScore(
                    target.getCreditScore() + (request.getRating() >= 3 ? 1 : -1)
            );
            target.setUpdatedAt(LocalDateTime.now());
            userMapper.updateById(target);
        }
        return Result.success();
    }


}

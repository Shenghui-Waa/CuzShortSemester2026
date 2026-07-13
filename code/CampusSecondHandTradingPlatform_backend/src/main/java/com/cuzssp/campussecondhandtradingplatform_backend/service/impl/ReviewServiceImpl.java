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
import com.cuzssp.campussecondhandtradingplatform_backend.common.vo.Result;
import com.cuzssp.campussecondhandtradingplatform_backend.common.vo.ReviewVO;
import com.cuzssp.campussecondhandtradingplatform_backend.common.vo.PageResult;
import com.cuzssp.campussecondhandtradingplatform_backend.common.dto.ReviewRequest;
import com.cuzssp.campussecondhandtradingplatform_backend.common.exception.BusinessException;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReviewServiceImpl implements ReviewService {

    @Autowired private ReviewMapper reviewMapper;
    @Autowired private OrderMapper orderMapper;
    @Autowired private UserMapper userMapper;

    /**
     * 创建评价
     * @param reviewerId
     * @param request
     * @return
     */
    @Override
    public Result<Void> createReview(
            Long reviewerId, ReviewRequest request
    ) {
        OrderInfo order = orderMapper.selectById(request.getOrderId());
        if (order == null)
            throw new BusinessException("Order not found");
        if (reviewMapper.countByOrderIdAndReviewerId(request.getOrderId(), reviewerId) > 0)
            throw new BusinessException("Already reviewed");
        Review review = ToEntityUtil.toReviewEntity(reviewerId, request);
        reviewMapper.insert(review);
        User target = userMapper.selectById(request.getTargetId());
        if (target != null) {
            target.setCreditScore(
                    target.getCreditScore() + (request.getRating() >= 3 ? 1 : -1)
            );
            userMapper.updateById(target);
        }
        return Result.success();
    }

    /**
     * 获取评价记录
     * @param userId
     * @param page
     * @param pageSize
     * @return
     */
    @Override
    public Result<PageResult<ReviewVO>> getUserReviews(
            Long userId, Integer page, Integer pageSize
    ) {
        PageHelper.startPage(page, pageSize);
        List<Review> reviews = reviewMapper.selectByTargetId(userId);
        PageInfo<Review> reviewPageInfo = new PageInfo<>(reviews);
        List<ReviewVO> reviewVOs = reviews.stream()
                .map(review -> {
                    return ToVOUtil.toReviewVO(
                            review, userMapper.selectById(review.getReviewerId())
                    );
                }).collect(Collectors.toList());
        return Result.success(
                new PageResult<>(
                        reviewVOs,
                        reviewPageInfo.getTotal(),
                        reviewPageInfo.getPageNum(),
                        reviewPageInfo.getPageSize()
                )
        );
    }
}

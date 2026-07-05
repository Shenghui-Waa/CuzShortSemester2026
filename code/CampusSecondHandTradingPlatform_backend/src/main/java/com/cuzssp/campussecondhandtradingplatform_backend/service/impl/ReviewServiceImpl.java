package com.cuzssp.campussecondhandtradingplatform_backend.service.impl;
import com.cuzssp.campussecondhandtradingplatform_backend.common.entity.Review;
import com.cuzssp.campussecondhandtradingplatform_backend.common.entity.OrderInfo;
import com.cuzssp.campussecondhandtradingplatform_backend.common.entity.User;
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

    @Override
    public Result<Void> createReview(Long reviewerId, ReviewRequest request) {
        OrderInfo order = orderMapper.selectById(request.getOrderId());
        if (order == null) throw new BusinessException("Order not found");
        if (reviewMapper.countByOrderIdAndReviewerId(request.getOrderId(), reviewerId) > 0)
            throw new BusinessException("Already reviewed");
        Review review = new Review();
        review.setOrderId(request.getOrderId()); review.setReviewerId(reviewerId);
        review.setTargetId(request.getTargetId()); review.setRating(request.getRating());
        review.setContent(request.getContent());
        reviewMapper.insert(review);
        User target = userMapper.selectById(request.getTargetId());
        if (target != null) {
            target.setCreditScore(target.getCreditScore() + (request.getRating() >= 3 ? 1 : -1));
            userMapper.updateById(target);
        }
        return Result.success();
    }

    @Override
    public Result<PageResult<ReviewVO>> getUserReviews(Long userId, Integer page, Integer pageSize) {
        PageHelper.startPage(page, pageSize);
        List<Review> pg = reviewMapper.selectByTargetId(userId);
        PageInfo<Review> pgInfo = new PageInfo<>(pg);
        List<ReviewVO> vos = pg.stream().map(r -> {
            ReviewVO vo = new ReviewVO();
            vo.setId(r.getId()); vo.setOrderId(r.getOrderId()); vo.setReviewerId(r.getReviewerId());
            vo.setTargetId(r.getTargetId()); vo.setRating(r.getRating()); vo.setContent(r.getContent());
            vo.setCreatedAt(r.getCreatedAt());
            User reviewer = userMapper.selectById(r.getReviewerId());
            if (reviewer != null) { vo.setReviewerName(reviewer.getNickname()); vo.setReviewerAvatar(reviewer.getAvatar()); }
            return vo;
        }).collect(Collectors.toList());
        return Result.success(new PageResult<>(vos, pgInfo.getTotal(), pgInfo.getPageNum(), pgInfo.getPageSize()));
    }
}

package com.cuzssp.campussecondhandtradingplatformbackend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cuzssp.campussecondhandtradingplatformbackend.common.entity.Review;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import java.util.List;

@Mapper
public interface ReviewMapper extends BaseMapper<Review> {
    @Select("SELECT * FROM review WHERE target_id = #{targetId} ORDER BY created_at DESC, id DESC")
    List<Review> selectByTargetId(@Param("targetId") Long targetId);

    @Select("SELECT COUNT(*) FROM review WHERE order_id = #{orderId} AND reviewer_id = #{reviewerId}")
    Long countByOrderIdAndReviewerId(@Param("orderId") Long orderId,
                                    @Param("reviewerId") Long reviewerId);
}

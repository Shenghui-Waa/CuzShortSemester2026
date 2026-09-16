package com.cuzssp.campussecondhandtradingplatformbackend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cuzssp.campussecondhandtradingplatformbackend.common.entity.OrderInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import java.util.List;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Mapper
public interface OrderInfoMapper extends BaseMapper<OrderInfo> {

    List<OrderInfo> selectByUserIdOrStatus(@Param("userId") Long userId,
                                         @Param("status") Integer status);

    int transitionIfStatusMatches(@Param("id") Long id,
                                  @Param("expectedStatus") Integer expectedStatus,
                                  @Param("targetStatus") Integer targetStatus,
                                  @Param("updatedAt") LocalDateTime updatedAt,
                                  @Param("paidAt") LocalDateTime paidAt,
                                  @Param("shippedAt") LocalDateTime shippedAt,
                                  @Param("completedAt") LocalDateTime completedAt);

    int cancelIfStatusMatches(@Param("id") Long id,
                              @Param("expectedStatus") Integer expectedStatus,
                              @Param("refundStatus") Integer refundStatus,
                              @Param("refundedAt") LocalDateTime refundedAt,
                              @Param("updatedAt") LocalDateTime updatedAt);

    @Update("UPDATE order_info SET updated_at = updated_at WHERE id = #{id}")
    int lockForReview(@Param("id") Long id);

    @Select("SELECT COUNT(*) FROM order_info WHERE created_at >= #{start} AND created_at < #{end}")
    Long countCreatedBetween(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Select("SELECT COALESCE(SUM(total_amount), 0) FROM order_info WHERE status IN (2, 3)")
    BigDecimal sumFulfilledAmount();

}

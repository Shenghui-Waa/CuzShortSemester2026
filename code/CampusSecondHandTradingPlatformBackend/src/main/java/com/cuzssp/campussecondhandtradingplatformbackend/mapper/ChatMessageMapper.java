package com.cuzssp.campussecondhandtradingplatformbackend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cuzssp.campussecondhandtradingplatformbackend.common.entity.ChatMessage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface ChatMessageMapper extends BaseMapper<ChatMessage> {

    @Select("""
            SELECT * FROM chat_message
            WHERE sender_id = #{userId} OR receiver_id = #{userId}
            ORDER BY created_at DESC, id DESC
            """)
    List<ChatMessage> selectByUserId(@Param("userId") Long userId);

    @Select("""
            SELECT * FROM chat_message
            WHERE (sender_id = #{userId} AND receiver_id = #{contactId})
               OR (sender_id = #{contactId} AND receiver_id = #{userId})
            ORDER BY created_at DESC, id DESC
            """)
    List<ChatMessage> selectByConversation(@Param("userId") Long userId, @Param("contactId") Long contactId);

    @Update("""
            UPDATE chat_message SET is_read = 1
            WHERE receiver_id = #{receiverId} AND sender_id = #{senderId} AND is_read = 0
            """)
    int markAsRead(@Param("receiverId") Long receiverId, @Param("senderId") Long senderId);

}

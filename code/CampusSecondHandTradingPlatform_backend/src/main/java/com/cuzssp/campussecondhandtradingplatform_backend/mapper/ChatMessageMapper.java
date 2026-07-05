package com.cuzssp.campussecondhandtradingplatform_backend.mapper;

import com.cuzssp.campussecondhandtradingplatform_backend.common.entity.ChatMessage;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface ChatMessageMapper {
    @Select("SELECT * FROM chat_message WHERE sender_id = #{senderId} ORDER BY created_at DESC")
    List<ChatMessage> selectBySenderId(@Param("senderId") Long senderId);

    @Select("SELECT * FROM chat_message WHERE receiver_id = #{receiverId} ORDER BY created_at DESC")
    List<ChatMessage> selectByReceiverId(@Param("receiverId") Long receiverId);

    @Select("SELECT * FROM chat_message WHERE ((sender_id = #{userId1} AND receiver_id = #{userId2}) OR (sender_id = #{userId2} AND receiver_id = #{userId1})) ORDER BY created_at DESC")
    List<ChatMessage> selectByConversation(@Param("userId1") Long userId1, @Param("userId2") Long userId2);

    @Insert("INSERT INTO chat_message (sender_id, receiver_id, product_id, content, is_read, created_at) VALUES (#{senderId}, #{receiverId}, #{productId}, #{content}, #{isRead}, #{createdAt})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(ChatMessage message);

    @Update("UPDATE chat_message SET is_read = 1 WHERE receiver_id = #{receiverId} AND sender_id = #{senderId} AND is_read = 0")
    int markAsRead(@Param("receiverId") Long receiverId, @Param("senderId") Long senderId);
}

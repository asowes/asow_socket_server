package com.young.asow.repository;

import com.young.asow.entity.Conversation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ConversationRepository extends JpaRepository<Conversation, Long> {

    /**
     * 获取当前用户的所有会话列表
     */
//    @Query("select cc from chat_conversation cc where cc.from.id = ?1 or cc.to.id = ?1")
    @Query("select cc from chat_conversation cc where cc.to.id = ?1")
    List<Conversation> findByUserId(Long userId);

//    /**
//     * 获取当前用户的所有会话列表
//     */
//    @Query("select c, uc.unread " +
//            "from chat_conversation c " +
//            "join chat_user_conversation uc " +
//            "on uc.conversation_id = c.id " +
//            "join user u " +
//            "on uc.user_id = u.id " +
//            "where u.id = 1")
//    List<Conversation> findConversationsByUserId(Long userId);
}

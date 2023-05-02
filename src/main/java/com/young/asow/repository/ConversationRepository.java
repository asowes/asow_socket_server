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
    @Query("select cc from chat_conversation cc where cc.fromId = ?1 or cc.toId = ?1")
    List<Conversation> findByUserId(String userId);

    Optional<Conversation> findByConversationId(String conversationId);
}

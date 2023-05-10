package com.young.asow.repository;

import com.young.asow.entity.Conversation;
import com.young.asow.entity.UserConversation;
import com.young.asow.entity.UserConversationId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;


public interface UserConversationRepository extends JpaRepository<UserConversation, UserConversationId> {

    List<UserConversation> findByUserId(Long userId);

    UserConversation findByUserIdAndConversationId(Long userId, Long cId);
}

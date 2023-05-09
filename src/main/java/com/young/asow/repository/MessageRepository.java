package com.young.asow.repository;

import com.young.asow.entity.Message;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface MessageRepository extends JpaRepository<Message, Long> {
    Optional<Message> findByMessageId(String messageId);

    @Query(value = "select cm from chat_message cm where cm.conversation.id = ?1")
    List<Message> findMessagesByConversationId(Pageable pageable, String conversationId);

    Optional<Message> findByConversationIdAndIsLatest(Long conversationId, Boolean islast);
}

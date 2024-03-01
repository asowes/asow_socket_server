package com.young.asow.repository;


import com.young.asow.entity.UserRelationship;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UserRelationshipRepository extends JpaRepository<UserRelationship, Long> {
    List<UserRelationship> findAllByUserId(Long userId);

    @Query(value = "select cur from chat_user_relationship cur where cur.user.id = ?1 or cur.friend.id = ?1")
    List<UserRelationship> findUserRelationships(Long userId);
}

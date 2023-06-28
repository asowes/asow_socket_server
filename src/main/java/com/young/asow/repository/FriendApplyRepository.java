package com.young.asow.repository;


import com.young.asow.entity.FriendApply;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface FriendApplyRepository extends JpaRepository<FriendApply, Long> {

    @Query("select cfa from chat_friend_apply cfa where cfa.sender.id = ?1 and cfa.accepter.id = ?2")
    List<FriendApply> findFriendApply(Long senderId, Long accepterId);

    @Query("select cfa from chat_friend_apply cfa where cfa.sender.id = ?1 and cfa.accepter.id = ?2 and cfa.status= 1")
    Optional<FriendApply> findApplying(Long senderId, Long accepterId);

    @Query("select cfa from chat_friend_apply cfa " +
            "where cfa.accepter.id = ?1 " +
            "and cfa.status = 1 " +
            "and cfa.operateTime = (select max(c.operateTime) " +
            "from chat_friend_apply c " +
            "where c.accepter.id = cfa.accepter.id and c.sender.id = cfa.sender.id)")
    List<FriendApply> findLatestByAccepterId(Long accepterId);

    @Query("select cfa from chat_friend_apply cfa " +
            "where cfa.sender.id = ?1 " +
            "and cfa.accepter.id = ?2 " +
            "and cfa.operateTime = (select max(c.operateTime) " +
            "from chat_friend_apply c " +
            "where c.accepter.id = cfa.accepter.id and c.sender.id = cfa.sender.id)")
    Optional<FriendApply> relationship(Long senderId, Long accepterId);
}

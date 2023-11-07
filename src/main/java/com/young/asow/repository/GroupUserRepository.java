package com.young.asow.repository;


import com.young.asow.entity.GroupUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GroupUserRepository extends JpaRepository<GroupUser, Long> {

    List<GroupUser> findByUserId(Long userId);

    List<GroupUser> findByChatGroupId(Long chatGroupId);

    GroupUser findByChatGroupIdAndUserId(Long chatGroupId, Long userId);
}

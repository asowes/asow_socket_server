package com.young.asow.repository;

import com.young.asow.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    @Query(value = "select u from user u where u.username = ?1")
    Optional<User> findByUsername(String username);

    @Query(value = "select u from user u where u.id = ?1")
    Optional<User> findById(Long userId);

    @Query("select u from user u where u.username like %?1% or u.nickname like %?1%")
    List<User> findByKeyword(String keyword);

    List<User> findAllByIdIn(List<Long> ids);
}

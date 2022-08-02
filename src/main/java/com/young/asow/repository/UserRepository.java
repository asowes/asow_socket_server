package com.young.asow.repository;

import com.young.asow.entity.LoginUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<LoginUser, Long> {

    Optional<LoginUser> findByUsername(String username);
}

package com.young.asow.repository;

import com.young.asow.entity.LoginUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<LoginUser, Long> {
    LoginUser findByUsername(String username);
}

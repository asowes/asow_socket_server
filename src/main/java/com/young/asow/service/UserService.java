package com.young.asow.service;

import com.young.asow.entity.LoginUser;
import com.young.asow.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void addUser(LoginUser user) {
        userRepository.save(user);
    }

}

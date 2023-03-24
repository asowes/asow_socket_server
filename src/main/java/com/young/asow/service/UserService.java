package com.young.asow.service;

import com.young.asow.entity.Authority;
import com.young.asow.entity.User;
import com.young.asow.modal.UserModal;
import com.young.asow.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;

@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void addUser(UserModal modal) {
        userRepository.findByUsername(modal.getUsername()).ifPresentOrElse(
                loginUser -> {
                    throw new RuntimeException("[ " + modal.getUsername() + " ] 已经注册过了");
                }, () -> {
                    BCryptPasswordEncoder bcryptPassword = new BCryptPasswordEncoder();
                    User user = new User();
                    user.setUsername(modal.getUsername());
                    user.setPassword(bcryptPassword.encode(user.getPassword()));
                    user.addAuthority(new Authority(Authority.ROLE.USER.value()));
                    userRepository.save(user);
                });
    }

    public Optional<User> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException(username));
        return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), Collections.emptyList());
    }
}

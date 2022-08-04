package com.young.asow.service;

import com.young.asow.entity.Authority;
import com.young.asow.entity.LoginUser;
import com.young.asow.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.security.core.userdetails.User;

import java.util.Collections;
import java.util.Optional;

@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void addUser(LoginUser user) {
        userRepository.findByUsername(user.getUsername()).ifPresentOrElse(
                loginUser -> {
                    throw new RuntimeException("[ " + user.getUsername() + " ] 已经注册过了");
                }, () -> {
                    BCryptPasswordEncoder bcryptPassword = new BCryptPasswordEncoder();
                    user.addAuthority(new Authority(Authority.ROLE.USER.value()));
                    user.setPassword(bcryptPassword.encode(user.getPassword()));
                    userRepository.save(user);
                });
    }

    public Optional<LoginUser> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        LoginUser user = userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException(username));
        return new User(user.getUsername(), user.getPassword(), Collections.emptyList());
    }
}

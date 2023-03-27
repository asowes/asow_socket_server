package com.young.asow.filter;

import com.alibaba.fastjson.JSONObject;
import com.young.asow.entity.User;
import com.young.asow.modal.UserModal;
import com.young.asow.service.UserService;
import com.young.asow.util.auth.JWTUtil;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.Collections;

@Log4j2
public class JWTLoginFilter extends UsernamePasswordAuthenticationFilter {
    private final AuthenticationManager authenticationManager;
    private final UserService userService;

    public JWTLoginFilter(
            AuthenticationManager authenticationManager,
            UserService userService
    ) {
        this.authenticationManager = authenticationManager;
        this.userService = userService;
    }


    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        UserModal user = JSONObject.parseObject(getBodyJsonStrByJson(request), UserModal.class);

        return authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        user.getUsername(),
                        user.getPassword(),
                        Collections.emptyList()
                )
        );
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        String username = (String) authResult.getPrincipal();
        User user = userService.getUserByUsername(username).orElseThrow(() -> new UsernameNotFoundException(username));
        JWTUtil.issueToken(user, response);
    }

    private String getBodyJsonStrByJson(ServletRequest request) {
        StringBuilder json = new StringBuilder();
        String line;
        try {
            BufferedReader reader = request.getReader();
            while ((line = reader.readLine()) != null) {
                json.append(line);
            }
        } catch (Exception e) {
            log.error("请求参数转换错误!", e);
        }
        return json.toString();
    }
}

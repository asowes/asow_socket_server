package com.young.asow.filter;

import com.auth0.jwt.exceptions.TokenExpiredException;
import com.young.asow.entity.Authority;
import com.young.asow.util.auth.JWTToken;
import com.young.asow.util.auth.JWTUtil;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static com.young.asow.config.WebSecurityConfig.AUTH_WHITELIST;

@Log4j2
public class JWTAuthenticationFilter extends BasicAuthenticationFilter {
    private static final List<String> WHITE_LIST = Arrays.asList(AUTH_WHITELIST);

    public JWTAuthenticationFilter(AuthenticationManager authenticationManager) {
        super(authenticationManager);
    }

    @Override
    protected void doFilterInternal(
            final HttpServletRequest request,
            final HttpServletResponse response,
            final FilterChain chain
    ) throws IOException, ServletException {
        String requestURI = request.getRequestURI();
        if (WHITE_LIST.contains(requestURI) || antMatches(requestURI)) {
            chain.doFilter(request, response);
            return;
        }

        String token = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (token == null || !token.startsWith(JWTToken.TOKEN_PREFIX_BEARER)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied");
            return;
        }
        String tokenStr = token.substring(JWTToken.TOKEN_PREFIX_BEARER.length()).trim();

        try {
            JWTToken decoded = JWTUtil.decode(tokenStr);
            // todo fixme 需要校验用户状态以及用户Id是否存在（如果数据库被清空了，token中附带的信息仍然能够通过以下校验）
            if (decoded == null) {
                throw new TokenExpiredException("Invalid token");
            }

            Set<Authority> authorities = new HashSet<>();
            for (String role : decoded.getRoles()) {
                authorities.add(new Authority(role));
            }

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            JWTToken.JWT_TOKEN,
                            decoded,
                            authorities
                    );

            SecurityContextHolder.getContext().setAuthentication(authentication);
            chain.doFilter(request, response);
        } catch (TokenExpiredException e) {
            log.warn("Invalid token: " + tokenStr + ". Error:" + e.getMessage());
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied");
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Server errors");
            e.printStackTrace();
        }
    }


    private boolean antMatches(String requestURI) {
        for (String whiteUrlPattern : WHITE_LIST) {
            if (whiteUrlPattern.endsWith("**") &&
                    requestURI.replace("/api/", "/").startsWith(
                            whiteUrlPattern.replace("**", ""))) {
                return true;
            }
        }
        return false;
    }

}

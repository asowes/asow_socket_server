package com.young.asow.util.auth;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.young.asow.constant.ConstantKey;
import com.young.asow.entity.Authority;
import com.young.asow.entity.User;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.util.Strings;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;
import java.util.stream.Collectors;

@Log4j2
public class JWTUtil {
    private static final Algorithm algorithm;
    private static final JWTVerifier verifier;

    static {
        try {
            algorithm = Algorithm.HMAC256(ConstantKey.SIGNING_KEY);

            verifier = JWT.require(algorithm).build();
        } catch (Exception e) {
            throw new RuntimeException("UnsupportedEncodingException", e);
        }
    }

    private static final String _JSON = "json";

    private static String encode(
            JWTToken token,
            long expiredAtMills
    ) {
        return JWT.create().
                withClaim(_JSON, JSON.toJSONString(token)).
                withExpiresAt(
                        new Date(expiredAtMills)).
                sign(algorithm);
    }

    public static JWTToken decode(String token) throws TokenExpiredException {
        Optional<JWTToken> jwtToken = decodeMayOptional(token);
        return jwtToken.orElseThrow(() -> new TokenExpiredException("JWTToken expired"));
    }

    public static Optional<JWTToken> decodeMayOptional(String tokenMayContainsBearer) {
        if (Strings.isBlank(tokenMayContainsBearer)) {
            return Optional.empty();
        }

        String token = tokenMayContainsBearer.replace("Bearer ", "");
        try {
            DecodedJWT jwt = verifier.verify(token);
            String json = jwt.getClaim(_JSON).asString();
            if (json == null) {
                log.warn("Never, JWTToken invalid. token=" + token);
                return Optional.empty();
            }
            return Optional.of(JSON.toJavaObject(JSONObject.parseObject(json), JWTToken.class));
        } catch (TokenExpiredException e) {
            return Optional.empty();
        } catch (Exception e) {
            log.warn("Never, JWTToken format NG." + e.getMessage() + " token=" + token);
            return Optional.empty();
        }
    }


    public static void issueToken(
            final User user,
            final HttpServletResponse response
    ) throws IOException {
        String token;
        long currentTime = System.currentTimeMillis();
//        long exp = currentTime + 1000 * 25;
//        long exp = currentTime + 1000 * 60 * 15;
        long exp = currentTime + 1000L * 60 * 60 * 24 * 180;
        log.info("过期时间:" + new Date(exp));

        Set<String> authorities = user.getAuthorities()
                .stream()
                .map(Authority::getAuthority)
                .collect(Collectors.toSet());

        token = encode(
                JWTToken.builder()
                        .userId(user.getId())
                        .token(user.getUsername())
                        .roles(authorities)
                        .build(),
                exp
        );

        Map<String, String> params = new HashMap<>();
        params.put("token", token);
        params.put("userId", String.valueOf(user.getId()));
        response.setContentType("application/json; charset=utf-8");
        PrintWriter out = response.getWriter();
        out.write(JSON.toJSONString(params));
        out.close();
    }

    public static Long getUserId(String token) {
        String tokenStr = token.substring(JWTToken.TOKEN_PREFIX_BEARER.length()).trim();
        JWTToken decoded = JWTUtil.decode(tokenStr);
        return decoded.getUserId();
    }
}

package com.xmxe.auth;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

@Configuration
public class AccessTokenConfig {
    private String SIGNING_KEY = "xmxe";

    @Bean
    TokenStore tokenStore() {
        // JwtTokenStore 本质上并不是做存储。不像其他的如new RedisTokenStore()将access_token存储到内存或者redis里面
        return new JwtTokenStore(jwtAccessTokenConverter());
    }

    @Bean
    JwtAccessTokenConverter jwtAccessTokenConverter() {
        // JwtAccessTokenConverter 可以实现将用户信息和 JWT 进行转换（将用户信息转为 jwt 字符串，或者从 jwt 字符串提取出用户信息
        JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
        // 在 JWT 字符串生成的时候，我们需要一个签名
        converter.setSigningKey(SIGNING_KEY);
        return converter;
    }
}

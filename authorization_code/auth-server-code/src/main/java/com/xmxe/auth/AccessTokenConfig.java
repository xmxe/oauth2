package com.xmxe.auth;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.InMemoryTokenStore;


@Configuration
public class AccessTokenConfig {

    /**
     * 配置令牌的存储位置
     */
    @Bean
    TokenStore tokenStore() {
        return new InMemoryTokenStore();
    }
}

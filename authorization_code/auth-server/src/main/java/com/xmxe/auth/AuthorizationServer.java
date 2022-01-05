package com.xmxe.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.code.AuthorizationCodeServices;
import org.springframework.security.oauth2.provider.code.InMemoryAuthorizationCodeServices;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;

// 开启授权服务器的自动化配置
@EnableAuthorizationServer
@Configuration
public class AuthorizationServer extends AuthorizationServerConfigurerAdapter {
    @Autowired
    TokenStore tokenStore;
    @Autowired
    ClientDetailsService clientDetailsService;

    @Bean
    AuthorizationServerTokenServices tokenServices() {
        DefaultTokenServices services = new DefaultTokenServices();
        services.setClientDetailsService(clientDetailsService);
        services.setSupportRefreshToken(true);
        services.setTokenStore(tokenStore);
        services.setAccessTokenValiditySeconds(60 * 60 * 2);
        services.setRefreshTokenValiditySeconds(60 * 60 * 24 * 3);
        return services;
    }

    /**
     * 用来配置令牌端点的安全约束，也就是这个端点谁能访问，谁不能访问。
     * @param security
     * @throws Exception
     */
    @Override
    public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
//        checkTokenAccess 是指一个 Token 校验的端点，这个端点我们设置为可以直接访问
//        （在后面，当资源服务器收到 Token 之后，需要去校验 Token 的合法性，就会访问这个端点）
        security.checkTokenAccess("permitAll()")
                .allowFormAuthenticationForClients();
    }

    /**
     * 配置客户端的详细信息
     * @param clients
     * @throws Exception
     */
    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        clients.inMemory()
                .withClient("javaboy")
                .secret(new BCryptPasswordEncoder().encode("123"))
                .resourceIds("res1")
                .authorizedGrantTypes("authorization_code","refresh_token")
                .scopes("all")
                .redirectUris("http://localhost:8082/index.html");
    }

    /**
     * 配置令牌的访问端点和令牌服务
     * @param endpoints
     * @throws Exception
     */
    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        endpoints.authorizationCodeServices(authorizationCodeServices())
                .tokenServices(tokenServices());
    }

    /**
     * 首先我们提供了一个 TokenStore 的实例，这个是指你生成的 Token 要往哪里存储，我们可以存在 Redis 中，也可以存在内存中，
     * 也可以结合 JWT 等等，这里，我们就先把它存在内存中，所以提供一个 InMemoryTokenStore 的实例即可。
     * @return
     */
    @Bean
    AuthorizationCodeServices authorizationCodeServices() {
        return new InMemoryAuthorizationCodeServices();
    }
}

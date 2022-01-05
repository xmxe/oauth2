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


    /**
     * access_token是临时的，有一定有效期。这是因为，access token 在使用的过程中可能会泄露。给 access token 限定一个较短的有效期可以
     * 降低因 access token 泄露而带来的风险。然而引入了有效期之后，使用起来就不那么方便了，因为每当 access token 过期，使用方就必须
     * 获取新的 access token。怎么获取呢？那就是重新授权，也就是要打开鉴权页面。这样用户可能每隔几天，甚至每天都需要进行授权操作。
     * 这是一件非常影响用户体验的事情。希望有一种方法，可以避免这种情况。于是 Oauth2.0 引入了 refresh token 机制。当鉴权服务器发送
     * access token 给使用方时，同时也发送一个 refresh token。 这个 refresh token 的有效期很长，作用是可以用来刷新 access token。
     * 鉴权服务器提供一个刷新接口，例如：POST /refresh
     * 参数： refreshtoken
     * 返回：新的 access token
     * 注：这个例子仅限于说明，实际当中获取 access token 和刷新 access token 是同一个接口，不会单独为刷新 access token 做一个接口
     * 传入 refresh token，鉴权服务器验证 refresh token 是合法的之后，返回一个新的 access token。这样，当 access token 过期后，
     * 使用方就使用 refresh token 来获取一个新的 access token。
     * 问题来了，这样做难道不会破坏安全性吗？前面说 access token 可能会泄漏，于是设置较短的有效期，可是现在又同时给一个 refresh token，
     * 那 refresh token 是怎么保证安全的呢？答案是，为了 refresh token 的安全，Oauth2.0 要求，refresh token 一定要保存在使用方的
     * 服务器上，而绝不能存放在移动 app、PC端软件、浏览器上，也不能在网络上随便传递。调用 refresh 接口的时候，一定是从使用方服务器到鉴权
     * 服务器的 https 访问。所以，refresh token 比 access token 隐蔽得多，也安全得多。当然，这需要使用方正确的遵守 Oauth2.0 的要求。
     *
     */
    @Bean
    AuthorizationServerTokenServices tokenServices() {
        DefaultTokenServices services = new DefaultTokenServices();
        services.setClientDetailsService(clientDetailsService);
        // 支持使用refresh token刷新access token
        services.setSupportRefreshToken(true);
        services.setTokenStore(tokenStore);
        // access token有效期2个小时
        services.setAccessTokenValiditySeconds(60 * 60 * 2);
        // refresh token有效期30天
        services.setRefreshTokenValiditySeconds(60 * 60 * 24 * 3);
        // 允许重复使用refresh token
        services.setReuseRefreshToken(true);
        return services;
    }

    /**
     * 用来配置令牌端点的安全约束，也就是这个端点谁能访问，谁不能访问。
     */
    @Override
    public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
        // checkTokenAccess 是指一个 Token 校验的端点，这个端点我们设置为可以直接访问
        // （在后面，当资源服务器收到 Token 之后，需要去校验 Token 的合法性，就会访问这个端点）
        security.checkTokenAccess("permitAll()")
                .allowFormAuthenticationForClients();
    }

    /**
     * 配置客户端的详细信息
     * 允许哪些客户端可以进行授权，并不是所有的客户端都能随意接入认证中心
     * 不是所有客户端都有权限请求令牌的，需要事先在认证中心申请，比如微信并不是所有网站都能直接接入，而是要去微信后台开通这个权限
     */
    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        clients.inMemory()
                // 指定客户端唯一ID
                .withClient("xmxe")

                // 指定秘钥，使用加密算法加密
                .secret(new BCryptPasswordEncoder().encode("123"))

                // 给客户端分配的资源权限，对应的是资源服务，比如订单这个微服务就可以看成一个资源，作为客户端肯定不是所有资源都能访问。
                .resourceIds("res1")

                // authorizedGrantTypes()：定义认证中心支持的授权类型，总共支持五种
                // 授权码模式：authorization_code
                // 密码模式：password
                // 客户端模式：client_credentials
                // 简化模式：implicit
                // 令牌刷新：refresh_token，这并不是OAuth2的模式，定义这个表示认证中心支持令牌刷新
                .authorizedGrantTypes("authorization_code","refresh_token","password")

                // autoApprove：是否需要授权，设置为true则不需要用户点击确认授权直接返回授权码
                .autoApprove(false)

                // 定义客户端的权限，这里只是一个标识，资源服务可以根据这个权限进行鉴权。
                .scopes("all")
                .redirectUris("http://localhost:8082/index.html");
    }

    /**
     * 配置令牌的访问端点和令牌服务
     */
    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        endpoints.authorizationCodeServices(authorizationCodeServices())// 获取授权码相关配置
                .tokenServices(tokenServices()); // 获取令牌相关配置
    }

    /**
     * 用来配置授权码的存储，这里我们是存在在内存中，tokenServices 用来配置令牌的存储，即 access_token 的存储位置，
     * 这里我们也先存储在内存中。授权码和令牌有什么区别？授权码是用来获取令牌的，使用一次就失效，令牌则是用来获取资源的
     */
    @Bean
    AuthorizationCodeServices authorizationCodeServices() {
        return new InMemoryAuthorizationCodeServices();
    }
}

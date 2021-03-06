package com.xmxe.authserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;

@SpringBootApplication
@EnableResourceServer// 表示一个资源服务器
public class AuthServerSSOApplication {

    public static void main(String[] args) {
        SpringApplication.run(AuthServerSSOApplication.class, args);
    }

}

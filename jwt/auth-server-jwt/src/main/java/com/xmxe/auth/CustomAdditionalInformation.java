package com.xmxe.auth;

import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class CustomAdditionalInformation implements TokenEnhancer {
    /**
     * JWT 默认生成的用户信息
     * @param accessToken 已经生成的 access_token 信息
     * @param authentication
     * 我们可以从 OAuth2AccessToken 中取出已经生成的额外信息，然后在此基础上追加自己的信息。
     *
     */
    @Override
    public OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {

        Map<String, Object> info = accessToken.getAdditionalInformation();
        info.put("author", "qrqrqr");
        ((DefaultOAuth2AccessToken) accessToken).setAdditionalInformation(info);
        return accessToken;
    }
}

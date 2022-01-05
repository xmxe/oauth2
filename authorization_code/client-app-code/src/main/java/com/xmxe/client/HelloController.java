package com.xmxe.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.client.RestTemplate;

import java.util.Map;


@Controller
public class HelloController {
    @Autowired
    RestTemplate restTemplate;

    @GetMapping("/index.html")
    public String hello(String code, Model model) {
        if (code != null) {
            MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
            map.add("code", code);
            map.add("client_id", "xmxe");
            map.add("client_secret", "123");
            map.add("redirect_uri", "http://localhost:8082/index.html");
            map.add("grant_type", "authorization_code");
            // 访问授权中心 携带授权码获取token
            Map<String,String> resp = restTemplate.postForObject("http://localhost:8080/oauth/token", map, Map.class);
            String access_token = resp.get("access_token");
            String refresh_token = resp.get("refresh_token");
            System.out.println("access_token="+access_token+"refresh_token="+refresh_token);

            // 根据refresh_token刷新access_token --start 测试客户端模式resp没有返回refresh_token
            // map.add("grant_type","refresh_token");
            // map.add("refresh_token",refresh_token);
            // Map<String,String> resp2 = restTemplate.postForObject("http://localhost:8080/oauth/token", map, Map.class);
            // 根据refresh_token刷新access_token --end

            HttpHeaders headers = new HttpHeaders();
            headers.add("Authorization", "Bearer " + access_token);
            HttpEntity<Object> httpEntity = new HttpEntity<>(headers);
            // 请求资源服务器
            ResponseEntity<String> entity = restTemplate.exchange("http://localhost:8081/admin/hello", HttpMethod.GET, httpEntity, String.class);
            model.addAttribute("msg", entity.getBody());
        }
        return "index";
    }
}
/**
 * /oauth/authorize	这个是授权的端点
 * /oauth/token	这个是用来获取令牌的端点
 * /oauth/confirm_access	用户确认授权提交的端点（就是 auth-server 询问用户是否授权那个页面的提交地址）
 * /oauth/error	授权出错的端点
 * /oauth/check_token	校验 access_token 的端点
 * /oauth/token_key	提供公钥的端点
 */
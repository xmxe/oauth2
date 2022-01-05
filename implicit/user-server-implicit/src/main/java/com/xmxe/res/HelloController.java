package com.xmxe.res;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
// 简化模式没有服务端，我们只能通过 js 来请求资源服务器上的数据，所以资源服务器需要支持跨域
@CrossOrigin(value = "*")
public class HelloController {
    @GetMapping("/hello")
    public String hello() {
        return "hello";
    }
    @GetMapping("/admin/hello")
    public String admin() {
        return "admin";
    }
}

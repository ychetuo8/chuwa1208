package com.demo.aopdemo.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class DemoService {

    private final RestTemplate restTemplate;

    public DemoService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String helloAndCallExternal() {
        // External call: will be logged by AOP (RestTemplate bean)
        String external = restTemplate.getForObject("https://httpbin.org/get", String.class);
        int len = (external == null) ? 0 : external.length();
        return "hello | external length=" + len;
    }

    public String throwError() {
        throw new RuntimeException("demo exception");
    }
}

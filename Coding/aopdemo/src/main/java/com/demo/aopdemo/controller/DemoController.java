package com.demo.aopdemo.controller;

import com.demo.aopdemo.service.DemoService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DemoController {

    private final DemoService demoService;

    public DemoController(DemoService demoService) {
        this.demoService = demoService;
    }

    @GetMapping("/hello")
    public String hello() {
        return demoService.helloAndCallExternal();
    }

    @GetMapping("/boom")
    public String boom() {
        return demoService.throwError();
    }
}

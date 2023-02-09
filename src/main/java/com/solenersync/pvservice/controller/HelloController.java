package com.solenersync.pvservice.controller;

import org.springframework.web.bind.annotation.*;

@RequestMapping("/v1/pv")
@RestController
public class HelloController {

    @GetMapping("/test")
    public String test() {
        System.out.println("sending test response");
        return "Testing 1...2...";
    }

    @GetMapping("/")
    public String index() {
        System.out.println("loading homepage");
        return "Hello from Solenersync..";
    }
}

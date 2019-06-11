package com.lamb7758.demo.controller;

import com.lamb7758.demo.aop.annotation.RequestLimit;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DemoController {
    @RequestMapping("/iplimit")
    @RequestLimit
    public String ipLimit(){
        System.out.println("...............");
        return "success";
    }
}

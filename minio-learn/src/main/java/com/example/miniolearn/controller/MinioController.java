package com.example.miniolearn.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.miniolearn.facade.MinioInterface;

/**
 * @author zhangkai
 * @version V2.1
 * @since 2021/9/5 0:26
 */
@RestController
@RequestMapping("/file")
public class MinioController {

    @PostMapping("/queryFiles")
    public void queryFiles() {

    }

    @GetMapping("/test")
    public String test(){
        System.out.println("源码环境构建成功！");
        return "源码环境构建成功！";
    }
}

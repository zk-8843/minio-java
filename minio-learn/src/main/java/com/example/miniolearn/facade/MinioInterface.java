package com.example.miniolearn.facade;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author zhangkai
 * @version V2.1
 * @since 2021/9/5 0:28
 */
// @RestController
// @RequestMapping("/file")
public interface MinioInterface {
    /**
     * 查询文件列表
     */
    @PostMapping("/queryFiles")
    public void queryFiles();

    /**
     * 测试
     * @return
     */
    @GetMapping("/test")
    public String test();
}

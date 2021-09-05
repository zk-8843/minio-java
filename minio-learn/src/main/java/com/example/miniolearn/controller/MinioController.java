package com.example.miniolearn.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.miniolearn.facade.MinioInterface;
import com.example.miniolearn.service.IMinioService;
import com.example.miniolearn.util.MinIoUtil;

import io.minio.messages.Bucket;
import lombok.SneakyThrows;

/**
 * @author zhangkai
 * @version V2.1
 * @since 2021/9/5 0:26
 */
@RestController
@RequestMapping("/file")
public class MinioController {
    @Autowired
    private MinIoUtil minIoUtil;
    @Autowired
    private IMinioService iMinioService;

    @SneakyThrows
    @GetMapping("/queryFiles")
    public String queryFiles() {
        List<Bucket> allBuckets = minIoUtil.getAllBuckets();
        return allBuckets.toString();
    }

    @GetMapping("/uploader")
    public String uploader(){
        return "源码环境构建成功！";
    }

    @GetMapping("/downLoader")
    public void downLoader(){
        // iMinioService.
    }
}

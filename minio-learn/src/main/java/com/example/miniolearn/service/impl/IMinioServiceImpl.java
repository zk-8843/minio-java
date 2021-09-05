package com.example.miniolearn.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.miniolearn.service.IMinioService;
import com.example.miniolearn.util.MinIoUtil;

import io.minio.messages.Bucket;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

/**
 * @author zhangkai
 * @version V2.1
 * @since 2021/9/5 13:47
 */
@Slf4j
@Service
public class IMinioServiceImpl implements IMinioService {
    @Autowired
    private MinIoUtil minIoUtil;

    @SneakyThrows
    @Override
    public void queryFile(String buckName) {
        List<Bucket> allBuckets = minIoUtil.getAllBuckets();
        allBuckets.toString();
    }

    @Override
    public void uploader(String buckName, String fileName) {
        // minIoUtil.uploadFile();
    }

    @Override
    public void downLoader(String bucketName, String fileName) {

    }
}

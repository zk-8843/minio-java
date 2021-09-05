package com.example.miniolearn.service;

/**
 * @author zhangkai
 * @version V2.1
 * @since 2021/9/5 12:48
 */
public interface IMinioService {
    /**
     * 查询当前bucket所有文件
     * @param buckName
     */
    public void queryFile(String buckName);
    /**
     * 上传文件
     * @param buckName
     * @param fileName
     */
    public void uploader(String buckName,String fileName);

    /**
     * 下载文件
     * @param bucketName
     * @param fileName
     */
    public void downLoader(String bucketName,String fileName);
}

package com.example.miniolearn.util;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import org.junit.platform.commons.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.example.miniolearn.config.MinioProperties;

import io.minio.BucketExistsArgs;
import io.minio.ListObjectsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.ObjectWriteResponse;
import io.minio.PutObjectArgs;
import io.minio.RemoveBucketArgs;
import io.minio.Result;
import io.minio.StatObjectArgs;
import io.minio.UploadObjectArgs;
import io.minio.errors.ErrorResponseException;
import io.minio.errors.InsufficientDataException;
import io.minio.errors.InternalException;
import io.minio.errors.InvalidResponseException;
import io.minio.errors.ServerException;
import io.minio.errors.XmlParserException;
import io.minio.messages.Bucket;
import io.minio.messages.Item;
import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONObject;

/**
 * @author zhangkai
 * @version V2.1
 * @since 2021/9/5 13:04
 */

@Component
@Slf4j
public class MinIoUtil {

    @Autowired
    private MinioProperties minioProperties;
    @Autowired
    private MinioClient minioClient;


    public void createBucket(String bucketName) {
        try {
            if (minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build())) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
            }
        } catch (Exception e) {
            log.error("创建桶失败:{}", e);
        }
    }


    /**
     * 初始化Bucket
     *
     * @throws Exception 异常
     */
    private void createBucket()
    throws IOException, InvalidKeyException, InvalidResponseException, InsufficientDataException,
        NoSuchAlgorithmException, ServerException, InternalException, XmlParserException,
        ErrorResponseException {
        if (!minioClient.bucketExists(BucketExistsArgs.builder().bucket(minioProperties.getBucketName()).build())) {
            minioClient.makeBucket(MakeBucketArgs.builder().bucket(minioProperties.getBucketName()).build());
        }
    }


    /**
     * 判断文件是否存在
     *
     * @param bucketName 存储桶
     * @param objectName 对象
     * @return true：存在
     */
    public  boolean doesObjectExist(String bucketName, String objectName) {
        boolean exist = true;
        try {
            minioClient
                .statObject(StatObjectArgs.builder().bucket(bucketName).object(objectName).build());
        } catch (Exception e) {
            exist = false;
        }
        return exist;
    }


    /**
     * 判断文件夹是否存在
     *
     * @param bucketName 存储桶
     * @param objectName 文件夹名称（去掉/）
     * @return true：存在
     */
    public  boolean doesFolderExist(String bucketName, String objectName) {
        boolean exist = false;
        try {
            Iterable<Result<Item>> results = minioClient.listObjects(
                ListObjectsArgs.builder().bucket(bucketName).prefix(objectName).recursive(false).build());
            for (Result<Item> result : results) {
                Item item = result.get();
                if (item.isDir() && objectName.equals(item.objectName())) {
                    exist = true;
                }
            }
        } catch (Exception e) {
            exist = false;
        }
        return exist;
    }


    /**
     * 通过MultipartFile，上传文件
     *
     * @param bucketName 存储桶
     * @param file       文件
     * @param objectName 对象名
     */
    public ObjectWriteResponse putObject(String bucketName, MultipartFile file,
        String objectName, String contentType)
    throws IOException, InvalidKeyException, InvalidResponseException, InsufficientDataException, NoSuchAlgorithmException, ServerException, InternalException, XmlParserException,
        ErrorResponseException {
        InputStream inputStream = file.getInputStream();
        return minioClient.putObject(
            PutObjectArgs.builder().bucket(bucketName).object(objectName).contentType(contentType)
                .stream(
                    inputStream, inputStream.available(), -1)
                .build());
    }

    /**
     * 上传本地文件
     *
     * @param bucketName 存储桶
     * @param objectName 对象名称
     * @param fileName   本地文件路径
     */
    public  ObjectWriteResponse putObject(String bucketName, String objectName,
        String fileName)
    throws IOException, InvalidKeyException, InvalidResponseException, InsufficientDataException, NoSuchAlgorithmException, ServerException, InternalException, XmlParserException,
        ErrorResponseException {
        return minioClient.uploadObject(
            UploadObjectArgs.builder()
                .bucket(bucketName).object(objectName).filename(fileName).build());
    }

    /**
     * 通过流上传文件
     *
     * @param bucketName  存储桶
     * @param objectName  文件对象
     * @param inputStream 文件流
     */
    public  ObjectWriteResponse putObjectbyStream(String bucketName, String objectName,
        InputStream inputStream)
    throws IOException, InvalidKeyException, InvalidResponseException, InsufficientDataException, NoSuchAlgorithmException, ServerException, InternalException, XmlParserException,
        ErrorResponseException {
        return minioClient.putObject(
            PutObjectArgs.builder().bucket(bucketName).object(objectName).stream(
                    inputStream, inputStream.available(), -1)
                .build());
    }

    /**
     * 创建文件夹或目录
     *
     * @param bucketName 存储桶
     * @param objectName 目录路径
     */
    public  ObjectWriteResponse putDirObject(String bucketName, String objectName)
    throws IOException, InvalidKeyException, InvalidResponseException, InsufficientDataException, NoSuchAlgorithmException, ServerException, InternalException, XmlParserException,
        ErrorResponseException {
        return minioClient.putObject(
            PutObjectArgs.builder().bucket(bucketName).object(objectName).stream(
                    new ByteArrayInputStream(new byte[]{}), 0, -1)
                .build());
    }


    /**
     * @Title: uploadFile
     * @Description: (获取上传文件信息上传文件)
     * @author 云诺
     * [file 上传文件（MultipartFile）, bucketName 桶名]
     */
    public JSONObject uploadFile(MultipartFile file, String bucketName) throws Exception {
        JSONObject res = new JSONObject();
        res.put("code", 0);
        //判断文件是否为空
        if (null == file || 0 == file.getSize()) {
            res.put("msg", "上传文件不能为空");
            return res;
        }
        //判断存储桶是否存在  不存在则创建
        createBucket(bucketName);
        //文件名
        String originalFilename = file.getOriginalFilename();
        //新的文件名 = 存储桶文件名_时间戳.后缀名
        String fileName = bucketName + "_" +
            System.currentTimeMillis() +
            originalFilename.substring(originalFilename.lastIndexOf("."));
        //开始上传
        putObjectbyStream(bucketName, fileName, file.getInputStream());
        res.put("code", 1);
        res.put("fileName", fileName);
        res.put("msg", minioProperties.getEndpoint() + "/" + bucketName + "/" + fileName);
        return res;
    }

    /**
     * @param filepath   文件路径，bucketName 桶名 Uuid UUID
     * @param uuid       文件UUID
     * @param bucketName com.alibaba.fastjson.JSONObject
     * @Title: uploadFile
     * @Description: 获取文件路径上传文件
     * @Title: uploadFile
     * @Description:
     */
    public JSONObject uploadFile(String filepath, String uuid, String bucketName) throws Exception {
        //        MultipartFile file = null;
        File oldFile = new File(filepath);
        FileInputStream fileInputStream = new FileInputStream(oldFile);
        //        file = new MockMultipartFile(oldFile.getName(), oldFile.getName(), fileInputStream);
        JSONObject res = new JSONObject();
        res.put("code", 0);
        //判断文件是否为空
        if (oldFile == null) {
            res.put("msg", "上传文件不能为空");
            return res;
        }
        //判断存储桶是否存在  不存在则创建
        createBucket(bucketName);
        //文件名
        String originalFilename = oldFile.getName();
        //新的文件名 = 存储桶文件名_时间戳_UUID.后缀名
        String fileName = bucketName + "_" +
            System.currentTimeMillis() + "_" + uuid +
            originalFilename.substring(originalFilename.lastIndexOf("."));
        //开始上传
        putObjectbyStream(bucketName, fileName, fileInputStream);
        res.put("code", 1);
        res.put("fileName", fileName);
        res.put("msg", minioProperties.getEndpoint() + "/" + bucketName + "/" + fileName);
        return res;
    }

    /**
     * @param bucketName com.alibaba.fastjson.JSONObject
     * @Title: uploadFile
     * @Description: 获取文件路径上传文件
     * *@param filepath 文件路径，bucketName 桶名
     */
    public JSONObject uploadFile(String filepath, String bucketName) throws Exception {
        //        MultipartFile file = null;
        File oldFile = new File(filepath);
        FileInputStream fileInputStream = new FileInputStream(oldFile);
        //        file = new MockMultipartFile(oldFile.getName(), oldFile.getName(), ContentType.APPLICATION_OCTET_STREAM.toString(), fileInputStream);
        JSONObject res = new JSONObject();
        res.put("code", 0);
        //判断文件是否为空
        if (oldFile == null) {
            res.put("msg", "上传文件不能为空");
            return res;
        }
        //判断存储桶是否存在  不存在则创建
        createBucket(bucketName);
        //文件名
        String originalFilename = oldFile.getName();
        //新的文件名 = 存储桶文件名_时间戳_UUID.后缀名
        String fileName = bucketName + "_" +
            System.currentTimeMillis() +
            originalFilename.substring(originalFilename.lastIndexOf("."));
        //开始上传
        putObjectbyStream(bucketName, fileName, fileInputStream);
        res.put("code", 1);
        res.put("fileName", fileName);
        res.put("msg", minioProperties.getEndpoint() + "/" + bucketName + "/" + fileName);
        return res;
    }

    /**
     * @Title: getAllBuckets
     * @Description: (获取全部bucket)
     * []
     */
    public List<Bucket> getAllBuckets() throws Exception {
        return minioClient.listBuckets();
    }

    /**
     * @param bucketName bucket名称
     * @Title: removeBucket
     * @Description: (根据bucketName删除信息)
     * [bucketName] 桶名
     */
    public void removeBucket(String bucketName) throws IOException, InvalidKeyException, InvalidResponseException, InsufficientDataException, NoSuchAlgorithmException, ServerException, InternalException, XmlParserException,
        ErrorResponseException {
        minioClient.removeBucket(RemoveBucketArgs.builder().bucket(bucketName).build());
    }


    /**
     * @param bucketName bucket名称
     * @param objectName ⽂件名称
     * @param stream     ⽂件流
     * @throws Exception https://docs.minio.io/cn/java-client-api-reference.html#putObject
     * @Title: putObject
     * @Description: (上传文件)
     * [bucketName 桶名, objectName 文件名, stream ⽂件流]
     */
    public void putObject(String bucketName, String objectName, InputStream stream) throws
        Exception {
        putObjectbyStream(bucketName,objectName,stream);
    }

    /**
     * 上传⽂件
     *
     * @param bucketName  bucket名称
     * @param objectName  ⽂件名称
     * @param stream      ⽂件流
     * @param size        ⼤⼩
     * @param contextType 类型
     * @throws Exception https://docs.minio.io/cn/java-client-api-reference.html#putObject
     * @Title: putObject
     * @Description: $(文件流上传文件)
     * [bucketName, objectName, stream, size, contextType]
     */
    public void putObject(String bucketName, String objectName, InputStream stream, long
        size, String contextType) throws Exception {
        putObjectbyStream(bucketName,objectName,stream);
    }

}



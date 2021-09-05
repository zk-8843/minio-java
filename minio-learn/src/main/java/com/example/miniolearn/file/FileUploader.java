package com.example.miniolearn.file;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.UploadObjectArgs;
import io.minio.errors.MinioException;

/**
 * @author zhangkai
 * @version V2.1
 * @since 2021/9/4 23:31
 */
public class FileUploader {
    public static void main(String[] args)
         throws IOException, NoSuchAlgorithmException, InvalidKeyException {
            try {
                // Create a minioClient with the MinIO server playground, its access key and secret key.
                MinioClient minioClient =
                    MinioClient.builder()
                        .endpoint("http://47.93.8.59:9000")
                        .credentials("minioadmin", "minioadmin")
                        .build();

                // Make 'asiatrip' bucket if not exist.
                String bucketName = "test-zk";
                boolean found =
                    minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
                if (!found) {
                    // Make a new bucket called 'asiatrip'.
                    minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
                } else {
                    System.out.println("Bucket 'test' already exists.");
                }

                // Upload '/home/user/Photos/asiaphotos.zip' as object name 'asiaphotos-2015.zip' to bucket
                // 'asiatrip'.
                minioClient.uploadObject(
                    UploadObjectArgs.builder()
                        .bucket(bucketName)
                        .object("tt-20210904234110.jpg")
                        //本地磁盘路径
                        .filename("F:/picture/tt-20210904234110.jpg")
                        .build());
                System.out.println("文件上传成功！");
            } catch (MinioException e) {
                System.out.println("Error occurred: " + e);
                System.out.println("HTTP trace: " + e.httpTrace());
            }
    }
}

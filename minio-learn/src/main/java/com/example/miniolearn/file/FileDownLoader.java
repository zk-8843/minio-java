package com.example.miniolearn.file;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import io.minio.BucketExistsArgs;
import io.minio.DownloadObjectArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.UploadObjectArgs;
import io.minio.errors.MinioException;

/**
 * @author zhangkai
 * @version V2.1
 * @since 2021/9/4 23:52
 */
public class FileDownLoader {
    public static void main(String[] args) throws IOException, NoSuchAlgorithmException, InvalidKeyException {
        try {
            // Create a minioClient with the MinIO server playground, its access key and secret key.
            MinioClient minioClient =
                MinioClient.builder()
                    .endpoint("http://47.93.8.59:9000")
                    .credentials("minioadmin", "minioadmin")
                    .build();
            String bucketName = "test-zk";
            {
                // Download 'my-objectname' from 'my-bucketname' to 'my-filename'
                minioClient.downloadObject(
                    DownloadObjectArgs.builder()
                        .bucket(bucketName)
                        .object("tt-20210904234110.jpg")
                        //文件下载本地路径
                        .filename("F:/picture/test-gg.jpg")
                        .build());
                System.out.println("my-objectname is successfully downloaded to my-filename");
            }
        } catch (MinioException e) {
            System.out.println("Error occurred: " + e);
            System.out.println("HTTP trace: " + e.httpTrace());
        }
    }
}

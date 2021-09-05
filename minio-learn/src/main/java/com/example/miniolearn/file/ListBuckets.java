package com.example.miniolearn.file;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import io.minio.MinioClient;
import io.minio.errors.MinioException;
import io.minio.messages.Bucket;

/**
 * @author zhangkai
 * @version V2.1
 * @since 2021/9/5 13:57
 */
public class ListBuckets {
    /** MinioClient.listBuckets() example. */
    public static void main(String[] args)
    throws IOException, NoSuchAlgorithmException, InvalidKeyException {
        try {
            /* play.min.io for test and development. */
            MinioClient minioClient =
                MinioClient.builder()
                    .endpoint("http://47.93.8.59:9000")
                    .credentials("minioadmin", "minioadmin")
                    .build();

            /* Amazon S3: */
            // MinioClient minioClient =
            //     MinioClient.builder()
            //         .endpoint("https://s3.amazonaws.com")
            //         .credentials("YOUR-ACCESSKEY", "YOUR-SECRETACCESSKEY")
            //         .build();

            // List buckets we have atleast read access.
            List<Bucket> bucketList = minioClient.listBuckets();
            for (Bucket bucket : bucketList) {
                System.out.println(bucket.creationDate() + ", " + bucket.name());
            }
        } catch (MinioException e) {
            System.out.println("Error occurred: " + e);
        }
    }
}

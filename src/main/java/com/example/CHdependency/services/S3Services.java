package com.example.CHdependency.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Service
public class S3Services {

    @Value("${aws.bucket.name}")
    private String awsBucket;

    private final S3Client s3Client;

    public S3Services(S3Client s3Client) {
        this.s3Client = s3Client;
    }

    public void awsBucketManager(byte[] bytes, String filename) {

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(awsBucket)
                .key("images/" + filename)
                .build();


        s3Client.putObject(putObjectRequest, RequestBody.fromBytes(bytes));
    }


}

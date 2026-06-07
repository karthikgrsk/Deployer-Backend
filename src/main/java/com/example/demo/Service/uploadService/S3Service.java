package com.example.demo.Service.uploadService;

import java.io.File;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import jakarta.annotation.PostConstruct;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Service
public class S3Service {

    @Value("${aws.secretKey}")
    private String secretKey;

    @Value("${aws.accessKey}")
    private String accessKey;

    @Value("${aws.region}")
    private String region;

    @Value("${aws.s3.bucket}")
    private String bucket;

    //Create connection objects
    //Only one S3client is created for all users
    // private static final S3Client s3 = S3Client.builder().build();
    private S3Client s3;

    @PostConstruct
    public void init(){
        //login
        AwsBasicCredentials credentials = AwsBasicCredentials.create(accessKey, secretKey);

        s3 = S3Client.builder()
                .region(Region.of(region)) // connects to the aws region
                .credentialsProvider(
                      StaticCredentialsProvider.create(credentials)) // Provides credentials to AWS SDK.
                .forcePathStyle(true) // Use path-style addressing instead of virtual-hosted-style
                .build(); // create final s3client object
    }


    //upload files to s3 bucket
    public void uploadFiles(String key,File file){
        try {
            System.out.println("DEBUG: Uploading to bucket: " + bucket + ", region: " + region + ", key: " + key);
            
            //create upload request object
            PutObjectRequest request = 

                   PutObjectRequest.builder()
                        .bucket(bucket) // bucket name
                        .key(key) // key stores in the bucket
                        .build();

             s3.putObject(request, file.toPath());  //uploads files to s3 
             
             System.out.println("Upload successful!");
        } catch (Exception e) {
            System.err.println("ERROR: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    
}

package com.example.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

@Configuration
public class s3ClientConfig {


    @Value("${aws.secretKey}")
    private String secretKey;

    @Value("${aws.accessKey}")
    private String accessKey;

    @Value("${aws.region}")
    private String region;

    @Value("${aws.s3.bucket}")
    private String bucket;

    @Bean
    public S3Client s3Client(){
        //login
        AwsBasicCredentials credentials = AwsBasicCredentials.create(accessKey, secretKey);

        return S3Client.builder()
                .region(Region.of(region)) // connects to the aws region
                .credentialsProvider(
                      StaticCredentialsProvider.create(credentials)) // Provides credentials to AWS SDK.
                .forcePathStyle(true) // Use path-style addressing instead of virtual-hosted-style
                .build(); // create final s3client object
    }
    
}

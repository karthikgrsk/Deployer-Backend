package com.example.demo.Service.buildWorkerService;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


import software.amazon.awssdk.core.sync.ResponseTransformer;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Response;
import software.amazon.awssdk.services.s3.model.S3Object;

@Service
public class s3DowloadService {

    @Value("${aws.s3.bucket}")
    private String bucket;

    @Autowired
    private S3Client s3Client;

    public void downloadFolder(String key) throws Exception{

        System.out.println("Downloading files for:" + key);

        //creating localFolder so where it should be download folder
        String localFolder = "workspace/" + key;

        //if folder is missing create new folder
        Files.createDirectories(Paths.get(localFolder));

        String prefix = key + "/";
        ListObjectsV2Request listrequest = ListObjectsV2Request.builder()
                               .bucket(bucket)         
                               .prefix(prefix)
                               .build();

        ListObjectsV2Response response  = s3Client.listObjectsV2(listrequest);

        for (S3Object object : response.contents()) {
            String objectKey = object.key();

            if (objectKey.equals(prefix)) {
                continue;
            }

            String relativePath = objectKey.startsWith(prefix)
                    ? objectKey.substring(prefix.length())
                    : objectKey.replaceFirst("^.*?/" + key + "/", "");

            if (relativePath.isEmpty()) {
                continue;
            }

            //workspace/k23lb/src/app.jsx
            Path destinationPath  = Paths.get(localFolder, relativePath);

            //create the missing folder
            Files.createDirectories(destinationPath.getParent());

            GetObjectRequest objectRequest = GetObjectRequest.builder()
                                                .bucket(bucket)
                                                .key(objectKey)
                                                .build();

            s3Client.getObject(
                objectRequest,
                ResponseTransformer.toFile(destinationPath)
            );

            System.out.println("save to:"+ destinationPath);

        }

        System.out.println("download completed");
    }
    
}

package com.example.demo.Service.buildWorkerService;

import java.io.File;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.example.demo.Service.uploadService.fileService;

import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Service
public class UploadBuildService {

    @Value("${aws.s3.bucket}")
    private String bucket;

    private S3Client s3Client;

    public UploadBuildService(S3Client s3Client) {
        this.s3Client = s3Client;
    }

    @Autowired
    private fileService fService;

    public void uploadBuildFolder(String id) {

        // after react npm run build
        // structure is workspace/abc12/dist/src/index.html

        File buildFolder = new File("workspace/" + id + "/build");
        File distFolder = new File("workspace/" + id + "/dist");

        File outputFolder;

        if (distFolder.exists()) {
            outputFolder = distFolder;

            System.out.println("vite project detected");
        }else if(buildFolder.exists()){
            outputFolder = buildFolder;
            System.out.println("cra project detected");
        }else{
            throw new RuntimeException(
                "No build found"
            );
        }

        List<File> files = fService.readFiles(outputFolder);

        for (File file : files) {

            String key = file.getPath().replace(outputFolder.getPath() + File.separator,
                    "") // removing workspace to dist from
                                                                                   // the path
                    .replace("\\", "/"); // format for s3 from "\" to "/"

            PutObjectRequest request = PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(id + "/" + key)
                    .build();

            s3Client.putObject(request, file.toPath());

            System.out.println("Uploaded: " + id + "/" + key);
        }

        System.out.println("Uploaded successfully");
    }

}

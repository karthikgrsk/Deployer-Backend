package com.example.demo.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
//import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;

@RestController
public class RequestHandlerController {

    @Value("${aws.s3.bucket}")
    private String bucket;

    // @Autowired
    // private StringRedisTemplate redisTemplate;

    @Autowired
    private S3Client s3Client;

    @GetMapping("/**")
    public ResponseEntity<byte[]> handleRequest(@RequestHeader("Host") String host,
            HttpServletRequest path) {

        try {

            String id = host.split("\\.")[0]; // gives it abc12

            String filePath = path.getRequestURI();

            String s3key = id + filePath;

            System.out.println("Host = " + host);
            System.out.println("File Path = " + filePath);
            System.out.println("S3 Key = " + s3key);

            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(bucket)
                    .key(s3key)
                    .build();

            ResponseBytes<GetObjectResponse> s3ResponseBytes = s3Client.getObjectAsBytes(getObjectRequest);

            String contentType = getContentType(filePath);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_TYPE, contentType)
                    .body(s3ResponseBytes.asByteArray()); // return contentype and response of html/css/js

        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }

    }

    private String getContentType(String filePath) {

        if (filePath.endsWith(".html")) {
            return "text/html";

        } else if (filePath.endsWith(".css")) {
            return "text/css";

        } else if (filePath.endsWith(".js")) {
            return "application/javascript";

        } else if (filePath.endsWith(".png")) {
            return "image/png";

        } else if (filePath.endsWith(".jpg")
                || filePath.endsWith(".jpeg")) {

            return "image/jpeg";

        } else if (filePath.endsWith(".svg")) {
            return "image/svg+xml";

        } else if (filePath.endsWith(".json")) {
            return "application/json";

        } else if (filePath.endsWith(".ico")) {
            return "image/x-icon";

        } else if (filePath.endsWith(".webp")) {
            return "image/webp";
        }

        return "application/octet-stream";
    }
}

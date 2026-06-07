package com.example.demo.Controller;

import java.io.File;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.DTO.DeployRequest;
import com.example.demo.Service.uploadService.GitService;
import com.example.demo.Service.uploadService.IDGenerator;
import com.example.demo.Service.uploadService.S3Service;
import com.example.demo.Service.uploadService.fileService;

import jakarta.validation.Valid;

@RestController
@CrossOrigin(origins = "*")
public class DeployController {

    @Autowired
    private S3Service s3Service;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private fileService fService;

    @PostMapping("/deploy")
    public ResponseEntity<?> deploy(@Valid @RequestBody DeployRequest request) throws Exception {

        String id = IDGenerator.idGenerate();

        String filePath = "output/" + id;

        GitService.cloneRepo(request.getRepoUrl(), filePath);

        List<File> files = fService.readFiles(new File(filePath));

        for (File file : files) {

            if (file.getPath().contains(".git") ||
                    file.getPath().contains("node_modules") ||
                    file.getPath().contains("dist")) {
                continue;
            }

            String s3Key = file.getPath().replace("\\", "/");
            System.out.println("Uploading: " + s3Key);

            s3Service.uploadFiles(s3Key, file);
        }

        redisTemplate.opsForList().leftPush("build-queue", id);

        redisTemplate.opsForHash().put("status", id, "uploaded");

        return ResponseEntity.ok(
                Map.of(
                        "id", id,
                        "status", "uploaded"));
    }

    @GetMapping("/status")
    public ResponseEntity<?> getStatus(@RequestParam String id) {

        String status = (String) redisTemplate.opsForHash().get("status", id);
        return ResponseEntity.ok(

                Map.of("status", status == null ? "not found" : status)

        );
    }

}

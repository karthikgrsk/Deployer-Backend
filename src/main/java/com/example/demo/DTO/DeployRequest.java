package com.example.demo.DTO;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;

@Data
public class DeployRequest{
    @NotBlank(message = "repoUrl must not be blank")
    private String repoUrl;
}
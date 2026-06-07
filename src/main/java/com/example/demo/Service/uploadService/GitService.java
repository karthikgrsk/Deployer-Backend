package com.example.demo.Service.uploadService;

import java.io.File;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.TransportException;

//import com.example.demo.DTO.DeployRequest;


public class GitService{
    
    public static void cloneRepo(String url,String folderPath) throws Exception, TransportException, GitAPIException{
        if (url == null || url.isBlank()) {
            throw new IllegalArgumentException("repoUrl is null or empty");
        }

        Git.cloneRepository()
           .setURI(url)
           .setDirectory(new File(folderPath))
           .call();
    }
}

package com.example.demo.Service.uploadService;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class fileService {
    
    public List<File> readFiles(File folder){
        List<File> files = new ArrayList<>();

        File[] list = folder.listFiles();

        if(list == null){
            return files;
        }

        for (File file : list) {
            if(file.isDirectory()){
                files.addAll(readFiles(file));
            }else{
                files.add(file);
            }
        }

        return files;
    }
}

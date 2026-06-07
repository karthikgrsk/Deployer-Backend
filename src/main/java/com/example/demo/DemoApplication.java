package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

//import com.example.demo.Service.uploadService.S3Service;

// import com.example.demo.Service.IDGenerator;

@SpringBootApplication(scanBasePackages = "com.example")
public class DemoApplication{
	
	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

	// @Override
	// public void run(String... args) throws Exception {
		

	// 	File file = new File("/home/karthik/Downloads/Profile_Picture.jpeg");

	// 	s3Service.uploadFiles("users/karthik/Profile_Picture", file);

	// 	System.out.println("uploded");
	// }

}

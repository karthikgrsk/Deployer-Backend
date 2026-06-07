package com.example.demo.Service.uploadService;

import java.util.Random;

public class IDGenerator {
    
    private static final String charSET = "123456789qwertyuiopasdfghjklzxcvbnm";

    public static String idGenerate(){

        Random random  = new Random();
        StringBuilder id = new StringBuilder();


        for (int i = 0; i < 5; i++) {
            id.append(charSET.charAt(random.nextInt(charSET.length())));
        }
        return id.toString();
    }
}

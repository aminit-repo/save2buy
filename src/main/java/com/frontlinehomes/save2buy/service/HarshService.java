package com.frontlinehomes.save2buy.service;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.security.SecureRandom;

public class HarshService {

    public static String getSecuredPassword(String password){
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(16);
        String result = encoder.encode(password);
        return result;
    }

    public static Boolean isEqual(String password, String encodedPassword){
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(16);
        return  encoder.matches(password, encodedPassword);
    }


}

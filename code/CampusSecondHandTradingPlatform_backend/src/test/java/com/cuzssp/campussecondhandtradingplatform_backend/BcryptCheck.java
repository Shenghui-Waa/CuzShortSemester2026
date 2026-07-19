package com.cuzssp.campussecondhandtradingplatform_backend;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class BcryptCheck {
    public static void main(String[] args) {
        String hash = "$2a$10$6gC4IBBis.AmFWBnVDKGcOJWcvCZ2MsI2N9bIlz8kE5nj9KGIW.ie";
        String[] candidates = {"admin123","123456","password","admin","cuzssp","cuzssp2026","admin12345","test","root"};
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        for (String pw : candidates) {
            if (encoder.matches(pw, hash)) {
                System.out.println("MATCH: " + pw);
                return;
            }
        }
        System.out.println("NO MATCH in common passwords");
    }
}
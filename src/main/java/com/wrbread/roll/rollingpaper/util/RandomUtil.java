package com.wrbread.roll.rollingpaper.util;

import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class RandomUtil {
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
    public static String generateRandomString() {
        int length = 6; // 원하는 문자열 길이
        Random random = new Random();
        StringBuilder stringBuilder = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            int randomIndex = random.nextInt(CHARACTERS.length());
            char randomChar = CHARACTERS.charAt(randomIndex);
            stringBuilder.append(randomChar);
        }

        return stringBuilder.toString();
    }
}

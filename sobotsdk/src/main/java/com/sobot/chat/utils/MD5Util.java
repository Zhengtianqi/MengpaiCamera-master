package com.sobot.chat.utils;

import java.security.MessageDigest;

public class MD5Util {
    public static String encode(String str) {
        StringBuilder sb = new StringBuilder();

        try {
            MessageDigest instance = MessageDigest.getInstance("MD5");
            byte[] digest = instance.digest(str.getBytes());
            for (byte b : digest) {
                int num = b & 0xff;
                String hex= Integer.toHexString(num);
                if(hex.length()<2){
                    sb.append("0");
                }
                sb.append(hex);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return sb.toString();
    }
}
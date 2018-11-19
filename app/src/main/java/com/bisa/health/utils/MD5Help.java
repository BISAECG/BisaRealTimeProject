package com.bisa.health.utils;

import java.security.MessageDigest;

/**
 * Created by Administrator on 2017/8/16.
 */

public class MD5Help {

    public static String md5EnBit32(String sourceStr) {
        try {
            // 获得MD5摘要算法的 MessageDigest对象
            MessageDigest mdInst = MessageDigest.getInstance("MD5");
            // 使用指定的字节更新摘要
            mdInst.update(sourceStr.getBytes());
            // 获得密文
            byte[] md = mdInst.digest();
            // 把密文转换成十六进制的字符串形式
            StringBuffer buf = new StringBuffer();
            for (int i = 0; i < md.length; i++) {
                int tmp = md[i];
                if (tmp < 0)
                    tmp += 256;
                if (tmp < 16)
                    buf.append("0");
                buf.append(Integer.toHexString(tmp));
            }
            return buf.toString().toLowerCase();// 32位加密
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String md5EnBit16(String sourceStr) {
        try {
            // 获得MD5摘要算法的 MessageDigest对象
            MessageDigest mdInst = MessageDigest.getInstance("MD5");
            // 使用指定的字节更新摘要
            mdInst.update(sourceStr.getBytes());
            // 获得密文
            byte[] md = mdInst.digest();
            // 把密文转换成十六进制的字符串形式
            StringBuffer buf = new StringBuffer();
            for (int i = 0; i < md.length; i++) {
                int tmp = md[i];
                if (tmp < 0)
                    tmp += 256;
                if (tmp < 16)
                    buf.append("0");
                buf.append(Integer.toHexString(tmp));
            }
            return buf.toString().substring(8, 24).toLowerCase();// 16位加密
            // return buf.toString();// 32位加密
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String getRandomString(int length){
        String string = "a1b2c3d4e5f6g7h8i9jklmnopqrstuvwxyz";
        StringBuffer sb = new StringBuffer();
        int len = string.length();
        for (int i = 0; i < length; i++) {
            int ranNumber= (int) Math.round(Math.random() * (len-1));
            sb.append(string.charAt(ranNumber));
        }
        return sb.toString();
    }

}

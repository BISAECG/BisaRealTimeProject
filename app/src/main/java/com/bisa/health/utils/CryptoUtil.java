package com.bisa.health.utils;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by Administrator on 2018/7/30.
 */

public abstract class CryptoUtil {

    // HMAC 加密算法名称
    public static final String HMAC_MD5 = "HmacMD5";// 128位
    public static final String HMAC_SHA1 = "HmacSHA1";// 126
    public static final String HMAC_SHA256 = "HmacSHA256";// 256
    public static final String HMAC_SHA512 = "HmacSHA512";// 512


    /**
     * 生成HMAC摘要
     *
     * @param plaintext 明文
     * @param secretKey 安全秘钥
     * @param algName 算法名称
     * @return 摘要
     */
    public static String hmacDigest(String plaintext,String secretKey,String algName) {
        try {
            Mac mac = Mac.getInstance(algName);
            byte[] secretByte = secretKey.getBytes();
            byte[] dataBytes = plaintext.getBytes();
            SecretKey secret = new SecretKeySpec(secretByte,algName);
            mac.init(secret);
            byte[] doFinal = mac.doFinal(dataBytes);
            return byte2HexStr(doFinal);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * 字节数组转字符串
     *
     * @param bytes 字节数组
     * @return 字符串
     */
    private static String byte2HexStr(byte[] bytes) {
        StringBuilder hs = new StringBuilder();
        String stmp;
        for (int n = 0; bytes!=null && n < bytes.length; n++) {
            stmp = Integer.toHexString(bytes[n] & 0XFF);
            if (stmp.length() == 1)
                hs.append('0');
            hs.append(stmp);
        }
        return hs.toString().toUpperCase();
    }
}

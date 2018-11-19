package com.bisa.health.utils;

import android.util.Log;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by Administrator on 2017/8/10.
 */

public class AESHelp {

    public static final byte[] key={36,23,01,19,83,8,2,25,17,41,98,60,31,70,1,8};

    static public byte[] generatorKey(){
        try{
            KeyGenerator keyGen = KeyGenerator.getInstance("AES");//密钥生成器
            keyGen.init(128);  //默认128，获得无政策权限后可为192或256
            SecretKey secretKey = keyGen.generateKey();//生成密钥
            byte[] key = secretKey.getEncoded();//密钥字节数组
            return key;
        }catch (Exception e){
            return null;
        }

    }


    static  public String encryption(byte[] key,byte[] data){

        try{

            SecretKey secretKey = new SecretKeySpec(key, "AES");//恢复密钥
            Cipher cipher = Cipher.getInstance("AES");//Cipher完成加密或解密工作类
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);//对Cipher初始化，解密模式
            byte[] cipherByte = cipher.doFinal(data);//加密data
            String enStr=bytesToHexString(cipherByte);
            Log.i("----",enStr);
            return enStr;
        }catch (Exception e){
            return null;
        }

    }

    static  public String decryption(byte[] key,String vStr){
            byte[] data=hexStringToBytes(vStr);
        try{
            SecretKey secretKey = new SecretKeySpec(key, "AES");//恢复密钥
            Cipher cipher = Cipher.getInstance("AES");//Cipher完成加密或解密工作类
            cipher.init(Cipher.DECRYPT_MODE, secretKey);//对Cipher初始化，解密模式
            byte[] cipherByte = cipher.doFinal(data);//解密data
            return new String(cipherByte);
        }catch (Exception e){
            return null;
        }

    }
    public static String bytesToHexString(byte[] src){
        StringBuilder stringBuilder = new StringBuilder("");
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }

    public static byte[] hexStringToBytes(String hexString) {
        if (hexString == null || hexString.equals("")) {
            return null;
        }
        hexString = hexString.toUpperCase();
        int length = hexString.length() / 2;
        char[] hexChars = hexString.toCharArray();
        byte[] d = new byte[length];
        for (int i = 0; i < length; i++) {
            int pos = i * 2;
            d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
        }
        return d;
    }
    private static byte charToByte(char c) {
        return (byte) "0123456789ABCDEF".indexOf(c);
    }
}

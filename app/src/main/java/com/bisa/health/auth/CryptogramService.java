package com.bisa.health.auth;

import android.util.Base64;
import android.util.Log;

import com.bisa.health.auth.config.SecretConfig;
import com.bisa.health.utils.CryptoUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.apache.tools.ant.taskdefs.Antlib.TAG;

/**
 * Created by Administrator on 2018/7/30.
 */

public class CryptogramService {

    private static CryptogramService instance;
    private CryptogramService() {}
    public static CryptogramService getInstance() {
        if (instance == null) {
            synchronized (CryptogramService.class) {
                if (instance == null) {
                    instance = new CryptogramService();
                }
            }
        }
        return instance;
    }


    /**
     * 生成HMAC摘要
     *
     * @param plaintext 明文
     */
    public String hmacDigest(String plaintext) {
        return hmacDigest(plaintext, SecretConfig.HMAC_SECRET);
    }

    public String hmacDigest(Map<String,String> param) {

        List<String> keys=new ArrayList<String>();
        for (Map.Entry<String, String> entry : param.entrySet()) {
            keys.add(entry.getKey());
        }
        Collections.sort(keys);//对请求参数进行排序参数->自然顺序

        StringBuffer baseString = new StringBuffer();
        for (String key : keys) {
            baseString.append(param.get(key));
        }
        String serverDigest =  hmacDigest(baseString.toString(), SecretConfig.HMAC_SECRET);
        Log.i(TAG, "hmacDigest: "+serverDigest);
        String base64Digest= Base64.encodeToString(serverDigest.getBytes(),Base64.NO_WRAP);
        return base64Digest;
    }


    /**
     * 生成HMAC摘要
     *
     * @param plaintext 明文
     */
    public String hmacDigest(String plaintext,String appKey) {

        return CryptoUtil.hmacDigest(plaintext,appKey,SecretConfig.HMAC_ALGORITHM_NAME);
    }



}

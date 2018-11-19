package com.bisa.health.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Created by Administrator on 2018/10/16.
 */

public class HttpUtil {
    public static String packParamsToWebView(Map<String,String> params){
        try{
            if(params.size() ==0){
                return null;
            }
            StringBuilder builder = new StringBuilder();
            Set<String> keys = params.keySet();
            Iterator<String> iterator = keys.iterator();
            while (iterator.hasNext()){
                String key = iterator.next();
                String value = URLEncoder.encode(params.get(key), "UTF-8");
                builder.append(String.format("%s=%s&",key, value));
            }
            builder.deleteCharAt(builder.lastIndexOf("&"));
            return builder.toString();
        }catch (UnsupportedEncodingException e){
            return null;
        }

    }

}

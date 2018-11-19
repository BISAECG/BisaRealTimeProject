package com.bisa.health.cache;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Map;

/**
 * Created by Administrator on 2017/10/24.
 */

public class SharedPersistor  implements  ObjectPersistor{

    private final SharedPreferences sharedPreferences;

    public SharedPersistor(Context context) {
        this(context.getSharedPreferences("HealthPersistence", Context.MODE_PRIVATE));
    }

    public SharedPersistor(SharedPreferences sharedPreferences) {
        this.sharedPreferences = sharedPreferences;
    }

    public void init(){

       Map<String,?> map=sharedPreferences.getAll();

        for (Map.Entry<String,?> entry : map.entrySet()) {
            String wordBase64= (String) entry.getValue();
            if (wordBase64 != null && !wordBase64.equals("")) { // 不可少，否则在下面会报java.io.StreamCorruptedException
                try {
                    byte[] objBytes = Base64.decode(wordBase64.getBytes(),
                            Base64.DEFAULT);
                    ByteArrayInputStream bais = new ByteArrayInputStream(objBytes);
                    ObjectInputStream ois = new ObjectInputStream(bais);
                    Object obj = ois.readObject();
                    bais.close();
                    ois.close();
                    CacheManage.getInstance().push(entry.getKey(),obj);
                }catch (Exception ex){
                    continue;
                }
            }

        }

    }

    @Override
    public <N extends Object> N loadObject(String key) {

        Object mObject=CacheManage.getInstance().pull(key);
        if(mObject!=null){
            return (N)mObject;
        }


        try {
            String wordBase64 = sharedPreferences.getString(key, "");
// 将base64格式字符串还原成byte数组
            if (wordBase64 == null || wordBase64.equals("")) { // 不可少，否则在下面会报java.io.StreamCorruptedException
                return null;
            }
            byte[] objBytes = Base64.decode(wordBase64.getBytes(),
                    Base64.DEFAULT);
            ByteArrayInputStream bais = new ByteArrayInputStream(objBytes);
            ObjectInputStream ois = new ObjectInputStream(bais);
            Object obj = ois.readObject();
            bais.close();
            ois.close();
            return (N)obj;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public <N extends Object> N saveObject(N n) {


        CacheManage.getInstance().push(n);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = null;
        try {
            oos = new ObjectOutputStream(baos);
            oos.writeObject(n);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        String objectStr = new String(Base64.encode(baos.toByteArray(),
                Base64.DEFAULT));
        try {
            baos.close();
            oos.close();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString(n.getClass().getName(), objectStr);
        editor.commit();

        return n;
    }

    @Override
    public <N> N saveObject(String key, N n) {

        CacheManage.getInstance().push(key,n);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = null;
        try {
            oos = new ObjectOutputStream(baos);
            oos.writeObject(n);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        String objectStr = new String(Base64.encode(baos.toByteArray(),
                Base64.DEFAULT));
        try {
            baos.close();
            oos.close();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(n.getClass().getName(), objectStr);
        editor.commit();
        return n;
    }


    @Override
    public void  delObject(String key) {
        sharedPreferences.edit().remove(key).commit();
        CacheManage.getInstance().del(key);
    }

    @Override
    public void clearAll() {
        CacheManage.getInstance().clearCache();
        sharedPreferences.edit().clear().commit();
    }

    @Override
    public boolean isCacheExist(String key) {

        if(CacheManage.getInstance().pull(key)!=null){
            return true;
        }
        return false;
    }

    @Override
    public boolean contain(String key) {
        String wordBase64 = sharedPreferences.getString(key, "");
        if(wordBase64!=null){
            return true;
        }
        return false;
    }

    @Override
    public <N> N flashSave(N n) {
        Object mObject=CacheManage.getInstance().push(n);
        return (N)mObject;
    }

    @Override
    public <N> N flashLoad(String key,boolean isDel) {
        Object mObject=CacheManage.getInstance().pull(key);
        if(isDel) {
            CacheManage.getInstance().del(key);
        }
        return (N)mObject;
    }


}

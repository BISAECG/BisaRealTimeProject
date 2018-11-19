package com.bisa.health.cache;

import android.util.Log;
import android.util.LruCache;

/**
 * Created by Administrator on 2018/4/12.
 */

public  class CacheManage {

    private static CacheManage instance=null;
    public static LruCache<String, Object> mRetainedCache = null;
    private CacheManage(){

        int maxMemory = (int) (Runtime.getRuntime().totalMemory()/1024);
        int cacheSize = maxMemory/8;
        mRetainedCache = new LruCache<String,Object>(cacheSize);
    }
    public static CacheManage getInstance() {
        if (instance == null) {
            synchronized (CacheManage.class) {
                if (instance == null) {//2
                    instance = new CacheManage();
                }
            }
        }
        return instance;
    }

    private static final String TAG = "CacheManage";
    public  <N extends Object> N  push(N n){
        Log.i(TAG, "push: "+n.getClass().getName());
        mRetainedCache.put(n.getClass().getName(),n);
        return n;
    }

    public  <N extends Object> N  push(String key,N n){
        mRetainedCache.put(key,n);
        return n;
    }

    public  <N extends Object> N  pull(String key){

        return (N) mRetainedCache.get(key);
    }

    public  void del(String key){
        mRetainedCache.remove(key);
    }

    public void clearCache(){
            mRetainedCache.evictAll();
    }
}

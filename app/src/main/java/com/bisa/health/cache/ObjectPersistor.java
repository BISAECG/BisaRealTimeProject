package com.bisa.health.cache;

/**
 * Created by Administrator on 2017/10/26.
 */

public interface ObjectPersistor {
    public <N extends Object> N loadObject(String key);
    public <N extends Object> N saveObject(N n);
    public <N extends Object> N saveObject(String key,N n);
    public void  delObject(String key);
    public void clearAll();
    public boolean isCacheExist(String key);
    public boolean contain(String key);

    public <N> N flashSave(N n);
    public <N> N flashLoad(String key,boolean isDel);
}

package com.bisa.health.model;

/**
 * Created by hd on 2016/10/14.
 */
public class StorageInfo {
    public String path;
    public String state;
    public boolean isRemoveable;
    public StorageInfo(String path) {
        this.path = path;
    }
    public boolean isMounted() {
        return "mounted".equals(state);
    }
    @Override
    public String toString() {
        return "StorageInfo [path=" + path + ", state=" + state
                + ", isRemoveable=" + isRemoveable + "]";
    }


}

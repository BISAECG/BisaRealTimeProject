package com.bisa.health.usb.filesys;

import com.bisa.health.usb.fat.Fat16BootSector;
import com.github.mjdev.libaums.driver.BlockDeviceDriver;

/**
 * Created by Administrator on 2017/12/28.
 */

public class FileSystemInfo {

    private static FileSystemInfo fileSystemInfo;

    private FileSystemInfo(){

    }
    private FileSystemInfo(Fat16BootSector fat16BootSector){

    }
    public static FileSystemInfo getInstance(Fat16BootSector fat16BootSector) {
        if (fileSystemInfo == null) {
            synchronized (FileSystemInfo.class) {
                if (fileSystemInfo == null) {
                    fileSystemInfo = new FileSystemInfo(fat16BootSector);
                }
            }
        }
        return fileSystemInfo;
    }
    public static void readRootFs(BlockDeviceDriver blockDevice){


    }


}

package com.bisa.health.utils;

import org.apache.tools.zip.ZipOutputStream;
import org.apache.tools.zip.ZipEntry;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


/**
 * Created by Administrator on 2018/5/15.
 */

public class ZipUtil {
    private static final String CHINESE_CHARSET = "utf-8";
    private static final int CACHE_SIZE = 1024;

    /**
     * 压缩后zip放到和源文件同一目录下
     * @param sourceFolder
     */
    public static String zip(String sourceFolder) {

        OutputStream os = null;
        BufferedOutputStream bos = null;
        ZipOutputStream zos = null;
        try {

            File file = new File(sourceFolder);
            String zipFilePath = cutOutPath(file);
            String filename = cutOutFilename(sourceFolder);
            zipFilePath +=filename+".zip";

            os = new FileOutputStream(zipFilePath);
            bos = new BufferedOutputStream(os);
            zos = new ZipOutputStream(bos);

            zos.setEncoding(CHINESE_CHARSET);

            String basePath = null;
            if (file.isDirectory()) {//压缩文件�?
                basePath = file.getPath();
            } else {
                basePath = file.getParent();
            }
            zipFile(file, basePath, zos);

            return zipFilePath;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally{
            try {
                if (zos != null) {
                    zos.closeEntry();
                    zos.close();
                }
                if (bos != null) {
                    bos.close();
                }
                if (os != null) {
                    os.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * 压缩文件
     * @param sourceFolder 压缩文件
     * @param zipFilePath 压缩文件输出路径
     */
    public static String zip(String sourceFolder, String zipFilePath) {
        OutputStream os = null;
        BufferedOutputStream bos = null;
        ZipOutputStream zos = null;
        try {
            os = new FileOutputStream(zipFilePath);
            bos = new BufferedOutputStream(os);
            zos = new ZipOutputStream(bos);

            zos.setEncoding(CHINESE_CHARSET);
            File file = new File(sourceFolder);
            String basePath = null;
            if (file.isDirectory()) {//压缩文件�?
                basePath = file.getPath();
            } else {
                basePath = file.getParent();
            }
            zipFile(file, basePath, zos);

            return zipFilePath;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally{
            try {
                if (zos != null) {
                    zos.closeEntry();
                    zos.close();
                }
                if (bos != null) {
                    bos.close();
                }
                if (os != null) {
                    os.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    /**
     * 递归压缩文件
     * @param parentFile
     * @param basePath
     * @param zos
     * @throws Exception
     */
    private static void zipFile(File parentFile, String basePath, ZipOutputStream zos) throws Exception {
        File[] files = new File[0];
        if (parentFile.isDirectory()) {
            files = parentFile.listFiles();
        } else {
            files = new File[1];
            files[0] = parentFile;
        }
        String pathName;
        InputStream is;
        BufferedInputStream bis;
        byte[] cache = new byte[CACHE_SIZE];
        for (File file : files) {
            if (file.isDirectory()) {
                pathName = file.getPath().substring(basePath.length() + 1) + File.separator;
                zos.putNextEntry(new ZipEntry(pathName));
                zipFile(file, basePath, zos);
            } else {
                pathName = file.getPath().substring(basePath.length() + 1);
                is = new FileInputStream(file);
                bis = new BufferedInputStream(is);
                zos.putNextEntry(new ZipEntry(pathName));
                int nRead = 0;
                while ((nRead = bis.read(cache, 0, CACHE_SIZE)) != -1) {
                    zos.write(cache, 0, nRead);
                }
                bis.close();
                is.close();
            }
        }
    }

    /**
     *
     * 	 截获文件路径
     * @param
     * @return
     */
    private static String cutOutPath(File file) {
        if(file.exists()){
            String folder = file.getParent();
            return folder;
        }else{
            return null;
        }
    }

    /**
     * 截获文件名
     * @param file_path
     * @return
     */
    private static String cutOutFilename(String file_path) {
        if(file_path.length()>1){
            int last_index = file_path.lastIndexOf("/");
            file_path = file_path.substring(last_index);
            int index = file_path.indexOf(".");
            String file_name = file_path.substring(0,index);
            return file_name;
        }else{
            return null;
        }
    }

}

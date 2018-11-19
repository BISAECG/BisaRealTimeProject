package com.bisa.health.utils;

import android.graphics.Bitmap;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;


public class FileIOKit {


	public static boolean is_file_status(File file){

		
		FileLock outlock=null;
		FileChannel fout=null;
		try {
			  RandomAccessFile rafout = new RandomAccessFile(file, "rws");
			  fout = rafout.getChannel();
			  outlock = fout.tryLock();
			  if(outlock.isValid()){
				  return true;
			  }else{
				  return false;
			  }
			 
		} catch (Exception e) {
			return false;
		}finally{
			if(outlock!=null){
				 try {
					 outlock.release();
				} catch (IOException e) {
				}
			}
			
			if(fout!=null){
				 try {
					 fout.close();
				} catch (IOException e) {
				}
			}
			
		
		}
	
	
	}
	
	
	@SuppressWarnings("resource")
	public static boolean FromByteToFile(byte[] inbyte,File outFile){
		
		FileLock outlock=null;
		FileChannel fout=null;
		try {
			  RandomAccessFile rafout = new RandomAccessFile(outFile, "rws");
			  
			  fout = rafout.getChannel();
			  outlock = fout.tryLock();
			  if(outlock.isValid()){
				  rafout.write(inbyte); fout.force(true);
				  return true;
			  }else{
				  return false;
			  }
			 
		} catch (Exception e) {
			return false;
		}finally{
			if(outlock!=null){
				 try {
					 outlock.release();
				} catch (IOException e) {
				}
			}
			
			if(fout!=null){
				 try {
					 fout.close();
				} catch (IOException e) {
				}
			}
		}
	
	}

	public static byte[] FromInputStreamToByte(InputStream is){

		byte[] bt=null;
		try {
			bt=new byte[is.available()];
			is.read(bt);
			return bt;
		} catch (Exception e) {
			return null;
		}finally{
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}


	}
	
	
	@SuppressWarnings("resource")
	public static byte[] FromFileToByte(File inFile){

		if(!inFile.exists()){
			return null;
		}

		FileLock inlock=null;
		FileChannel fin=null;
		byte[] bt=null;
		try {
			RandomAccessFile rafin = new RandomAccessFile(inFile, "rws");

			fin = rafin.getChannel();
			inlock = fin.tryLock();
			if(inlock.isValid()){
				bt=new byte[(int) rafin.length()];
				int rcout=rafin.read(bt);
				if(rcout!=-1){
					return bt;
				}else{
					return null;
				}

			}else{
				return null;
			}

		} catch (Exception e) {
			return bt;
		}finally{
			if(inlock!=null){
				try {
					inlock.release();
				} catch (IOException e) {
				}
			}

			if(fin!=null){
				try {
					fin.close();
				} catch (IOException e) {
				}
			}
		}

	}
	
	@SuppressWarnings("resource")
	public static boolean copyFileUTF8(InputStream is,File outFile){
		
		FileLock outlock=null;
		FileChannel fcout=null;
		try {
			  RandomAccessFile rafout = new RandomAccessFile(outFile, "rws");
			  
			   fcout = rafout.getChannel();
			   outlock = fcout.tryLock();
			  if(outlock.isValid()){
				  byte[] bt=new byte[is.available()];
				  is.read(bt);
				  ByteBuffer bbf=ByteBuffer.wrap(bt,0,bt.length); 
				  fcout.write(bbf);fcout.force(true);
			  }else{
				  return false;
			  }
			 
			  return true;
		} catch (Exception e) {
			return false;
		}finally{
			if(outlock!=null){
				 try {
					outlock.release();
				} catch (IOException e) {
				}
			}
			
			if(fcout!=null){
				 try {
					fcout.close();
				} catch (IOException e) {
				}
			}
		}
	
	}
	
	
	@SuppressWarnings("resource")
	public static boolean copyFile(InputStream is,File outFile){
		
		FileLock outlock=null;
		FileChannel fcout=null;
		try {
			  RandomAccessFile rafout = new RandomAccessFile(outFile, "rws");
			  
			   fcout = rafout.getChannel();
			   outlock = fcout.tryLock();
			   
			  if(outlock.isValid()){
				  byte[] bt=new byte[is.available()];
				  is.read(bt);
				  ByteBuffer bbf=ByteBuffer.wrap(bt,0,bt.length); 
				  fcout.write(bbf);fcout.force(true);
			  }else{
				  return false;
			  }
			 
			  return true;
		} catch (Exception e) {
			return false;
		}finally{
			if(outlock!=null){
				 try {
					outlock.release();
				} catch (IOException e) {
				}
			}
			
			if(fcout!=null){
				 try {
					fcout.close();
				} catch (IOException e) {
				}
			}
		}
	
	}
	
	@SuppressWarnings("resource")
	public static boolean copyFileToFile(File inFile,File outFile){


		FileChannel fcin = null;
		FileLock inlock = null;
		FileChannel fcout = null;
		FileLock outlock = null;
		try {
			RandomAccessFile rafin = new RandomAccessFile(inFile, "rws");
			fcin = rafin.getChannel();
			inlock = fcin.tryLock();
			if (inlock.isValid()) {
				RandomAccessFile rafout = new RandomAccessFile(outFile, "rws");
				fcout = rafout.getChannel();
				outlock = fcout.tryLock();
				if (outlock.isValid()) {
					fcin.transferTo(0, fcin.size(), fcout);
					fcin.force(true);
					fcout.force(true);
				} else {
					return false;
				}

			} else {
				return false;
			}

			return true;
		} catch (Exception e) {
			return false;
		} finally {
			if (inlock != null) {
				try {
					inlock.release();
				} catch (IOException e) {
				}
			}

			if (fcin != null) {
				try {
					fcin.close();
				} catch (IOException e) {
				}
			}
			if (outlock != null) {
				try {
					outlock.release();
				} catch (IOException e) {
				}
			}

			if (fcout != null) {
				try {
					fcout.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

	}

	//从InputStream中读取数据，转换成byte数组，最后关闭InputStream
	public byte[] readByteByInputStream(InputStream is) {
		byte[] bytes = null;
		BufferedInputStream bis = new BufferedInputStream(is);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		BufferedOutputStream bos = new BufferedOutputStream(baos);
		byte[] buffer = new byte[1024 * 8];
		int length = 0;
		try {
			while ((length = bis.read(buffer)) > 0) {
				bos.write(buffer, 0, length);
			}
			bos.flush();
			bytes = baos.toByteArray();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				bos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				bis.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return bytes;
	}


	/**
	 * 转图片
	 * @param file
	 * @param bitmap
	 * @return
	 */

	public static File saveFile(File file, Bitmap bitmap) {
		byte[] bytes = bitmapToBytes(bitmap);
		if(bytes==null){
			return null;
		}
		return saveFile(file, bytes);
	}



	public static byte[] bitmapToBytes(Bitmap bm) {
		try{
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);
			return baos.toByteArray();
		}catch (Exception e){
			return null;
		}

	}

	private static File saveFile(File file, byte[] bytes) {
		boolean an=FromByteToFile(bytes,file);
		if(an){
			return file;
		}else{
			return null;
		}
	}

	public static String QueueFileName(File[] datFile) {

		long tempTime=0;
		int mindex=0;
		for(int i=0;i<datFile.length;i++){
			int sp=datFile[i].getName().indexOf('_');
			int ep=datFile[i].getName().indexOf('.');
			long curLongTime=Long.parseLong((datFile[i].getName().substring(sp+1, ep)));
			if(tempTime==0){
				mindex=i;
				tempTime=curLongTime;
			}else{
				if(curLongTime<tempTime){
					mindex=i;
					tempTime=curLongTime;
				}
			}
		}
		if(datFile[mindex]!=null){
			return datFile[mindex].getName().split("\\.")[0];
		}
		return null;
	}
}

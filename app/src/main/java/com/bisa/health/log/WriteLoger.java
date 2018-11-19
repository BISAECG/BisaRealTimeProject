package com.bisa.health.log;

import android.os.Environment;
import android.text.format.DateFormat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class WriteLoger {
	static String dir = "SpyderLog";
	static boolean blwrite = true;
	private static final String TAG = "WriteLoger";
	public static void WriteLog(String conent) {

		
		long dateTaken = System.currentTimeMillis();
		String datetime = DateFormat.format("yyyy-MM-dd kk:mm:ss", dateTaken)
				.toString();
		String prename = DateFormat.format("yyyy-MM-dd", dateTaken).toString();
		String fileName = prename + ".txt";
		conent = "@~:" + datetime + "->" + conent + "\r\n";
		File realTimeFile=new File(Environment.getExternalStorageDirectory().getPath()+"/log");

		FileOutputStream realTimelogfos=null;
		FileChannel realTimelogfc=null;	

		if (!realTimeFile.exists())
			realTimeFile.mkdir();
		
		if (realTimeFile.exists()) {
			
			try {

				realTimelogfos=new FileOutputStream(realTimeFile + "/" + fileName,true);
				realTimelogfc=realTimelogfos.getChannel();
				ByteBuffer realTimebbf=ByteBuffer.wrap(conent.getBytes());
				realTimelogfc.write(realTimebbf);
				
				
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					if(realTimelogfos!=null)
						realTimelogfos.close();
					if(realTimelogfc!=null)
						realTimelogfc.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	
	}

}

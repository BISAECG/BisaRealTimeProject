package com.bisa.health.camera.sdk;

import android.content.Context;

import com.bisa.health.R;
import com.lib.SDKCONST;


public class FileDataUtils {
	// "B" 连拍 "L" 延时拍
	/**
	 * 获取录像文件类型
	 * 
	 * @param context
	 * @param fileName
	 * @return
	 */



	public static int getIntFileType(String fileName) {
		int fileType = 0;
		if (fileName.endsWith(".h264")) {
			int pos = fileName.indexOf('[');
			if (pos > 0 && pos < fileName.length()) {
				String type = fileName.substring(pos + 1, pos + 2);
				if (type.equals("A"))
					fileType = SDKCONST.VidoFileType.VI_DETECT;
				else if (type.equals("M"))
					fileType = SDKCONST.VidoFileType.VI_MANUAL;
				else if (type.equals("R"))
					fileType = SDKCONST.VidoFileType.VI_MANUAL;
				else if (type.equals("H"))
					fileType = SDKCONST.VidoFileType.VI_REGULAR;
				else if (type.equals("K"))
					fileType = SDKCONST.VidoFileType.VI_KEY;
			}
		} else if (fileName.endsWith(".jpg")) {
			int pos = fileName.indexOf('[');
			if (pos > 0 && pos < fileName.length()) {
				String type = fileName.substring(pos + 1, pos + 2);
				if (type.equals("A"))
					fileType = SDKCONST.PicFileType.PIC_DETECT;
				else if (type.equals("M"))
					fileType = SDKCONST.PicFileType.PIC_MANUAL;
				else if (type.equals("R"))
					fileType = SDKCONST.PicFileType.PIC_MANUAL;
				else if (type.equals("H"))
					fileType = SDKCONST.PicFileType.PIC_REGULAR;
				else if (type.equals("K"))
					fileType = SDKCONST.PicFileType.PIC_KEY;
				else if (type.equals("B"))
					fileType = SDKCONST.PicFileType.PIC_BURST_SHOOT;
				else if (type.equals("L"))
					fileType = SDKCONST.PicFileType.PIC_TIME_LAPSE;
			}
		}

		return fileType;
	}

	/**
	 * 获取录像码流类型
	 *
	 * @param fileName
	 * @return
	 */
	public static int getStreamType(String fileName) {
		if (StringUtils.isStringNULL(fileName)) {
			return SDKCONST.StreamType.Main;
		} else {
			int index_0 = fileName.indexOf("(", 0);
			int index_1 = fileName.indexOf(")", index_0);
			if (index_0 == index_1) {
				return SDKCONST.StreamType.Main;
			}
			String type = fileName.substring(index_0 + 1, index_1);
			try {
				return Integer.parseInt(type);
			} catch (Exception e) {
				return SDKCONST.StreamType.Main;
			}
		}
	}

	/**
	 * @param fileName
	 *            文件名
	 * @param type
	 *            0: 唯一的序号 1:相同时间下的序号
	 * @Title: getOrderNum
	 * @Description: TODO(获取序号)
	 */
	public static int getOrderNum(String fileName, int type) {
		int index_0, index_1, index_2;
		String num = "";
		if (StringUtils.isStringNULL(fileName)) {
			return 0;
		} else {
			switch (type) {
			case 0:
				index_0 = fileName.indexOf('[');
				index_1 = fileName.indexOf('[', index_0 + 1);
				index_2 = fileName.indexOf("]", index_1);
				if (index_1 == index_2) {
					return 0;
				}
				num = fileName.substring(index_1 + 1, index_2);
				break;
			case 1:
				index_0 = fileName.lastIndexOf("]");
				index_1 = fileName.lastIndexOf("[");
				if (index_0 == index_1) {
					return 0;
				}
				num = fileName.substring(index_1 + 1, index_0);
				break;
			default:
				break;
			}
			
			try {
				return Integer.parseInt(num);
			} catch (Exception e) {
				return 0;
			}
		}
	}
}

package com.bisa.health.ecg.encoder;


public class ECGBuildHelper {

	
	private static ECGBuildHelper ecgBuildHelper = null;

	public static ECGBuildHelper getInstance() {
		if (ecgBuildHelper == null) {
			synchronized (ECGBuildHelper.class) {
				if (ecgBuildHelper == null) {
					ecgBuildHelper = new ECGBuildHelper();
				}
			}
		}
		return ecgBuildHelper;
	}
	private ECGBuildHelper() {
		
	}
	public byte Ch1b[] = new byte[4];
	public byte[] Ch2b = new byte[4]; 
	public int[] Ch1d = {0,0,0,0};
	public int[] Ch2d = {0,0,0,0}; 
	private static int ecg_wk1, abs_wk16, abs_wk161, abs_wk162, fg0;
	
	public void init(){
		ecg_wk1=0;
		abs_wk16=0;
		abs_wk161=0;
		abs_wk162=0;
		fg0=0;
		Ch1b = new byte[4];
		Ch2b = new byte[4];
		Ch1d =new int[4];
		Ch2d =new int[4];
	}

	public int[] ECGBuildTo316(int b2c4){

		int[] E316=new int[5];
		E316[4]=-1;

		//************第一通道变换开始**********************************
		Ch1b[3] = (byte) (b2c4 / 16);
		Ch2b[3] = (byte)(b2c4 & 0x0F);
		ecg_wk1 = Ch1b[1] & 15;
		fg0 = 0;
		if (Ch1b[1] < 16)
		{  //需要计算
			if (ecg_wk1 < 8)
			{  //f4
				if (ecg_wk1 < 4)
				{  // f4+
					Ch1d[1] = Ch1d[0] + ecg_wk1;
				}
				else
				{  //f4-
					Ch1d[1] = Ch1d[0] - 8 + ecg_wk1;
				}
			}
			else
			{
				if (ecg_wk1 < 0x0C)
				{  // f8
					abs_wk16 = Ch1b[1] & 1;
					abs_wk16 <<= 4;
					abs_wk161 = Ch1b[2] & 0x0F;
					abs_wk16 += abs_wk161;         // 得到增量值
					if (ecg_wk1 > 0x09)
					{
						abs_wk16 = abs_wk16 - 32;         //f8-
					}
					abs_wk161 = abs_wk16 / 2;       // 得到1/2增量值
					Ch1d[2] = Ch1d[0] + abs_wk16;
					Ch1d[1] = Ch1d[0] + abs_wk161;
					Ch1b[2] += 16;
				}
				else
				{  //f12
					abs_wk16 = Ch1b[1] & 3;                      //Ch1b[1]高9,10位
					abs_wk16 <<= 8;
					abs_wk161 = Ch1b[2] & 0x0F;
					abs_wk161 <<= 4;
					abs_wk162 = abs_wk16 + abs_wk161 + Ch1b[3];
					if (abs_wk162 < 1001)
					{                              // 正常ECG 数据
						abs_wk16 = abs_wk162 - Ch1d[0];
						abs_wk161 = abs_wk16 / 3;
						Ch1d[1] = Ch1d[0] + abs_wk161;
						Ch1d[2] = Ch1d[1] + abs_wk161;
						Ch1d[3] = abs_wk162;

						Ch1b[2] += 16;
						Ch1b[3] += 16;
					}
					else
					{
						fg0 = abs_wk162;
						if ((abs_wk162 == 1001) || (abs_wk162 == 1002))
						{
							if ((Ch2b[1] == 0) && (Ch2b[2] == 0) && (Ch2b[3] == 0))
							{
								Ch1d[1] = abs_wk162;
							}
							else
							{
								Ch1d[1] = Ch1d[0];
							};
						}
						else
						if (abs_wk162 == 1010)
						{
							Ch1d[1] = abs_wk162;
						}
						else
						{
							Ch1d[1] = Ch1d[0];
						}
						Ch1d[2] = Ch1d[0];
						Ch1d[3] = Ch1d[0];
					}
					Ch1b[2] += 16;
					Ch1b[3] += 16;
				}
			}
		}
		//************第一通道变换结束***********************************

		//************第二通道变换开始***********************************
		ecg_wk1 = Ch2b[1] & 0x0F;
		if (fg0 == 0)
		{     // 正常数据
			if (Ch2b[1]<16)
			{  //需要计算
				if (ecg_wk1 < 8)
				{  //f4
					if (ecg_wk1 < 4)
					{  // f4+
						Ch2d[1] = Ch2d[0] + ecg_wk1;
					}
					else
					{  //f4-
						Ch2d[1] = Ch2d[0] - 8 + ecg_wk1;
					}
				}
				else
				{
					if (ecg_wk1 < 0x0C)
					{  // f8
						abs_wk16 = Ch2b[1] & 1;
						abs_wk16 <<= 4;
						abs_wk161 = Ch2b[2] & 0x0F;
						abs_wk16 += abs_wk161;         // 得到增量值
						if (ecg_wk1 > 0x09)
						{
							abs_wk16 = abs_wk16 - 32;         //f8-
						}
						abs_wk161 = abs_wk16 / 2;       // 得到1/2增量值
						Ch2d[2] = Ch2d[0] + abs_wk16;
						Ch2d[1] = Ch2d[0] + abs_wk161;
						Ch2b[2] += 16;
					}
					else
					{  //f12
						abs_wk16 = Ch2b[1] & 3;
						abs_wk16 <<= 8;
						abs_wk161 = Ch2b[2] & 0x0F;
						abs_wk161 <<= 4;
						abs_wk162 = abs_wk16 + abs_wk161 + Ch2b[3];
						Ch2d[3] = abs_wk162;
						abs_wk16 = abs_wk162 - Ch2d[0];
						abs_wk161 = abs_wk16 / 3;
						Ch2d[1] = Ch2d[0] + abs_wk161;
						Ch2d[2] = Ch2d[1] + abs_wk161;
						Ch2b[2] += 16;
						Ch2b[3] += 16;
					}
				}
			}
		}
		else
		{               // 特殊数据
			if (fg0 != 1010)
			{
				Ch2d[1] = Ch2d[0];
			}
			else if((Ch2b[1] &3)==0)
			{
				abs_wk161 = Ch2b[2] & 0x0F;
				abs_wk161 <<= 4;
				abs_wk162 = abs_wk161 + Ch2b[3];
				Ch2d[1] = abs_wk162;
			}
			else
				Ch2d[1] = Ch2d[0];
			Ch2d[2] = Ch2d[0];
			Ch2d[3] = Ch2d[0];
			Ch2b[2] += 16;
			Ch2b[3] += 16;
		}
		Ch1b[0] = Ch1b[1];
		Ch1b[1] = Ch1b[2];
		Ch1b[2] = Ch1b[3];

		Ch1d[0] = Ch1d[1];
		Ch1d[1] = Ch1d[2];
		Ch1d[2] = Ch1d[3];

		Ch2b[0] = Ch2b[1];
		Ch2b[1] = Ch2b[2];
		Ch2b[2] = Ch2b[3];

		Ch2d[0] = Ch2d[1];
		Ch2d[1] = Ch2d[2];
		Ch2d[2] = Ch2d[3];


		if (Ch1d[0] >1000)
		{
			E316[0] = 0;
			E316[1] = Ch1d[1];
			E316[2] = Ch2d[1];
			E316[3] = E316[2] - E316[1]+500;
			if (Ch1d[0] < 1003)
			{
				E316[0] = Ch1d[0];
			}
			else
			{
				if (Ch1d[0] == 1010)
				{
					E316[4] = ((Ch2d[0] - 139) * 100 / 31);
				}
			}
		}
		else
		{

			E316[0] = 0;
			E316[1] = Ch1d[0] ;
			E316[2] = Ch2d[0];
			E316[3] = E316[2] - E316[1];
		}

		return E316;

	}

}

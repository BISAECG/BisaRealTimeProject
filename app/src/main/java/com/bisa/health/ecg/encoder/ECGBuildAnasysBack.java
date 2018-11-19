package com.bisa.health.ecg.encoder;

public class ECGBuildAnasysBack {

	private static ECGBuildAnasysHelperBack v2Help;

	private static ECGBuildAnasysBack instance = null;

	private static int[] AVG_ECG;


	public static ECGBuildAnasysBack getInstance() {
		if (instance == null) {
			synchronized (ECGBuildHelper.class) {
					instance = new ECGBuildAnasysBack();
			}
		}
		return instance;
	}

	private ECGBuildAnasysBack() {
		super();
	}

	public ECGBuildAnasysBack init(){

		if(v2Help==null)
		v2Help=new ECGBuildAnasysHelperBack();
		v2Help.aDatainit();
		AVG_ECG=new int[2];
		return this;
	}


	private static final String TAG = "ECGBuildAnasysBack";
	public int[] build(int v2,int v1,int v3){
		int[] ECGV2=v2Help.ecgDataAnasys(v2);
		return ECGV2;
	}
	
}

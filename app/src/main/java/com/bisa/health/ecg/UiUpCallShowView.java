package com.bisa.health.ecg;


public interface UiUpCallShowView {
	public void uiBpmCallBack(int val2, int val1,int val3);
	
	/* define Null Adapter class for that interface */
	public static class Null implements UiUpCallShowView {

		@Override
		public void uiBpmCallBack(int val2,int val1,int val3) {
			
			
		}
		
	}
}

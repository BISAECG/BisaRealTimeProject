package com.bisa.health.cust.view;


import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

public class CustomFontTextView extends TextView {
	public CustomFontTextView(Context context) {
		super(context);
		init(context);
	}

	public CustomFontTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public CustomFontTextView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init(context);
	}

	public void init(Context context) {
		float rate = (float) 720/this.getTextSize();
		float text_size=this.getTextSize()*160/480;
		if(text_size<12){
			text_size=12;
		}
		setTextSize(rate*8);
	}

}

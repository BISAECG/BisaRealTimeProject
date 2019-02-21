package com.bisa.health.cust.view;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.bisa.health.R;

/**
 * Created by Administrator on 2017/7/27.
 */

public class CustBindDefaultPopView extends PopupWindow {

    private IOnClickCallInterFace onClickCallInterFace;
    private Context context;
    private LinearLayout rlLeft;
    private LinearLayout rlRight;
    private TextView tv_title;
    private TextView tv_body;

    public CustBindDefaultPopView(Context context) {
        super(context);
        init(context);
    }

    public CustBindDefaultPopView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CustBindDefaultPopView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public void setItemOnClickListener(IOnClickCallInterFace onClickCallInterFace){
        this.onClickCallInterFace=onClickCallInterFace;
    }

    public void setTitle(String text){
        if(tv_title!=null){
            tv_title.setText(text);
        }
    }
    public void setMessage(String text){
        if(tv_body!=null){
            tv_body.setText(text);
        }
    }


    public void init(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_pop_default_layout, null);
        setContentView(view);
        setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        setFocusable(true);
        setOutsideTouchable(true);
        ColorDrawable dw = new ColorDrawable(0xff);
        setBackgroundDrawable(dw);
        setAnimationStyle(R.style.PopupWindow);
        tv_title=(TextView) view.findViewById(R.id.tv_title);
        tv_body=(TextView)view.findViewById(R.id.txt_body);

        rlLeft=(LinearLayout) view.findViewById(R.id.ll_left);
        rlLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                onClickCallInterFace.onItemClick(false);
            }
        });
        rlRight=(LinearLayout)view.findViewById(R.id.ll_right);
        rlRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                onClickCallInterFace.onItemClick(true);
            }
        });
    }



}

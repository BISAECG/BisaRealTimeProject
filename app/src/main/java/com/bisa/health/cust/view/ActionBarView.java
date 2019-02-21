package com.bisa.health.cust.view;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bisa.health.R;


public class ActionBarView extends FrameLayout {


    private TextView tvTitle;
    private ImageButton ibtn_back;
    private ImageButton ibtn_next;
    private RelativeLayout rl_back;
    private RelativeLayout rl_next;
    private boolean isPre=true;
    private boolean isNext=false;
    private Drawable preIco;
    private Drawable nextIco;
    private String title;
    private static final String TAG = "ActionBarView";

    //定义一个接口对象listerner
    private OnActionClickListener listenerNext;

    private OnActionClickListener listenerBack;

    //定义一个接口
    public interface  OnActionClickListener{
        public void onActionClick();
    }

    public void setOnActionClickListenerNext(OnActionClickListener listener) {
        this.listenerNext = listener;
    }

    public void setOnItemSelectListenerBack(OnActionClickListener listener) {
        this.listenerBack = listener;
    }


    private View.OnClickListener r;
    public ActionBarView(@NonNull Context context) {
        super(context);
        init(context, null,0);
    }

    public ActionBarView(@NonNull Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs,0);
    }

    public ActionBarView(@NonNull Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs,defStyleAttr);
    }


    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        LayoutInflater.from(context).inflate(R.layout.fragment_action_setting_bar, this, true);

        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.ActionBarView, defStyleAttr, 0);
        title = a.getString(R.styleable.ActionBarView_title);
        isPre=a.getBoolean(R.styleable.ActionBarView_isPre,true);
        isNext=a.getBoolean(R.styleable.ActionBarView_isNext,false);
        preIco=a.getDrawable(R.styleable.ActionBarView_preIco);
        nextIco=a.getDrawable(R.styleable.ActionBarView_nextIco);
        a.recycle();
        rl_back=findViewById(R.id.rl_back);
        rl_back.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if(listenerBack!=null){
                    listenerBack.onActionClick();
                }else{
                    ((Activity)getContext()).finish();
                }

            }
        });

        rl_next=findViewById(R.id.rl_next);
        rl_next.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if(listenerNext!=null){
                    listenerNext.onActionClick();
                }
            }
        });

        ibtn_back=(ImageButton)findViewById(R.id.ibtn_back);
        ibtn_next=findViewById(R.id.ibtn_next);
        tvTitle=(TextView)findViewById(R.id.tv_title);

        if(isPre){
            ibtn_back.setBackground(preIco);
        }else{
            ibtn_back.setVisibility(GONE);
        }

        if(isNext){
            ibtn_next.setBackground(nextIco);
        }else{
            ibtn_next.setVisibility(GONE);
        }

        tvTitle.setText(title);

    }

}

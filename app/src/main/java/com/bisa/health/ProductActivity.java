package com.bisa.health;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.RectF;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.bisa.health.cache.CacheManage;
import com.bisa.health.camera.CameraAddActivity;
import com.bisa.health.model.enumerate.ActionEnum;
import com.bisa.health.utils.ActivityUtil;

import zhy.com.highlight.HighLight;
import zhy.com.highlight.interfaces.HighLightInterface;
import zhy.com.highlight.position.OnBaseCallback;
import zhy.com.highlight.shape.RectLightShape;

public class ProductActivity extends BaseActivity implements View.OnClickListener {

    public HighLight mHightLight=null;
    public ImageView iv_pecg;
    public ImageView iv_camera;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.product_activity);
        iv_pecg=this.findViewById(R.id.iv_pecg);
        iv_pecg.setOnClickListener(this);
        iv_camera=this.findViewById(R.id.iv_camera);
        iv_camera.setOnClickListener(this);

        SharedPreferences setting =getSharedPreferences(CacheManage.SHARE_APP_TAG,  Context.MODE_PRIVATE);
        Boolean user_first = setting.getBoolean(this.getClass().getName(),true);
        if(user_first){//第一次
            setting.edit().putBoolean(this.getClass().getName(), false).commit();
            showNextTipViewOnCreated();
        }

	}
    public void remove(View view)
    {
        mHightLight.remove();
    }
    public  void showNextTipViewOnCreated(){
        mHightLight = new HighLight(this)//
                //.anchor(findViewById(R.id.id_container))//如果是Activity上增加引导层，不需要设置anchor
                .autoRemove(false)
                .setOnLayoutCallback(new HighLightInterface.OnLayoutCallback() {
                    @Override
                    public void onLayouted() {
                        //界面布局完成添加tipview
                      //  mHightLight.addHighLight(R.id.iv_pecg,R.layout.info_null,new OnTopPosCallback(0),new RectLightShape());
                        mHightLight.addHighLight(R.id.llayout_product,R.layout.info_product,new OnBaseCallback(40){

                            @Override
                            public void getPosition(float rightMargin, float bottomMargin, RectF rectF, HighLight.MarginInfo marginInfo) {
                                marginInfo.rightMargin = 0;
                                marginInfo.topMargin = rectF.top + rectF.height()+offset;
                            }
                        },new RectLightShape());
                        //然后显示高亮布局
                        //然后显示高亮布局
                        mHightLight.show();

                        View decorLayout = mHightLight.getHightLightView();
                        Button knownView = (Button) decorLayout.findViewById(R.id.btn_iknown);
                        knownView.setOnClickListener(new View.OnClickListener()
                        {
                            @Override
                            public void onClick(View view) {
                                remove(null);
                            }
                        });
                    }
                })
                .setClickCallback(new HighLight.OnClickCallback() {
                    @Override
                    public void onClick() {
                        remove(null);
                    }
                });

    }

	@Override
	protected void onDestroy() {

		super.onDestroy();

	}


    @Override
    public void onClick(View v) {
        if(v==iv_pecg){
            ActivityUtil.startActivity(this,AddActivity.class,ActionEnum.NULL);
        }
        else if(v == iv_camera) {
            ActivityUtil.startActivity(this, CameraAddActivity.class, ActionEnum.NULL);
        }
    }
}

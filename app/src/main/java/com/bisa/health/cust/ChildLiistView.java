package com.bisa.health.cust;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

/**
 * Created by Administrator on 2018/5/10.
 */

public class ChildLiistView extends ListView {

    public ChildLiistView(Context context) {
        super(context);

    }

    public ChildLiistView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

    }

    public ChildLiistView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
                MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }
}

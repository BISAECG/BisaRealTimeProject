package com.bisa.health.cust.anim;

import android.view.animation.Animation;
import android.view.animation.Transformation;

/**
 * Created by Administrator on 2018/8/30.
 */

public class CircleAnimation extends Animation {

    private int radii;

    public CircleAnimation(int radii) {
        this.radii = radii;
    }

    /**
     * @param interpolatedTime 取值范围 0-1
     * @param t
     */
    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {
        //根据取值范围 确定圆周运动的角度范围。0-360
        float d = 360 * interpolatedTime ;//+ 180;  //算法一
//        float d = 360 * interpolatedTime + 180;       //算法二
        if (d > 360) { //算法二
            d = d - 360;
        }
        //根据取值范围 确定圆周运动的角度范围。0-360
        int[] ps = getNewLocation((int) d, radii, 0, 0);//
        t.getMatrix().setTranslate(ps[0], ps[1] - radii);
    }

    /**
     * @param newAngle 当前临时角度
     * @param r        半径
     * @param width    控件的宽
     * @param height   控件的高
     * @return
     */
    public int[] getNewLocation(int newAngle, int r, int width, int height) {
        int newAngle1;
        int newX = 0, newY = 0;
        /**
         * 0-90
         */
        if (newAngle == 0) {
            newX = width;
            newY = height - r;
        } else if (newAngle == 90) {
            newX = width + r;
            newY = height;
        } else if (newAngle == 180) {
            newX = width;
            newY = height + r;
        } else if (newAngle == 270) {
            newX = width - r;
            newY = height;
        } else if (newAngle == 360) {
            newX = width;
            newY = height - r;
        } else if (newAngle > 360) {
            newX = width;
            newY = height - r;
        } else if (newAngle > 0 && newAngle < 90) {
            // 0-90  baidu：就拿sin30°为列：Math.sin(30*Math.PI/180)
            // 思路为PI相当于π，而此时的PI在角度值里相当于180°，
            // 所以Math.PI/180得到的结果就是1°，然后再乘以30就得到相应的30°
            newX = (int) (width + (r * Math.sin(newAngle * Math.PI / 180)));
            newY = (int) (height - (r * Math.cos(newAngle * Math.PI / 180)));
        } else if (newAngle > 90 && newAngle < 180) {// 90-180
            newAngle1 = 180 - newAngle;
            newX = (int) (width + (r * Math.sin(newAngle1 * Math.PI / 180)));
            newY = (int) (height + (r * Math.cos(newAngle1 * Math.PI / 180)));
        } else if (newAngle > 180 && newAngle < 270) {//180-270
            newAngle1 = 270 - newAngle;
            newX = (int) (width - (r * Math.cos(newAngle1 * Math.PI / 180)));
            newY = (int) (height + (r * Math.sin(newAngle1 * Math.PI / 180)));
        } else if (newAngle > 270 && newAngle < 360) {//270-360
            newAngle1 = 360 - newAngle;
            newX = (int) (width - (r * Math.sin(newAngle1 * Math.PI / 180)));
            newY = (int) (height - (r * Math.cos(newAngle1 * Math.PI / 180)));
        }
        return new int[]{newX, newY};
    }
}


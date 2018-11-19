package com.bisa.health.cust;

import com.bisa.health.utils.DisplayUtil;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class CustomBgView extends View {

	Paint paint = null;
	private int TIME_TEXT_OFFSET = 10;

	public CustomBgView(Context context) {
		super(context);
	}

	public CustomBgView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public CustomBgView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);
		canvas.drawColor(Color.BLACK);
		if (paint == null) {
			paint = new Paint();
			paint.setColor(0xffEBEBEB);
			paint.setAntiAlias(true);
		}

		int width = this.getWidth();
		int height = this.getHeight();
		int indexTime = 1;
		float heightAvg = height / DisplayUtil.CHCOUNT;

		for (int i = 0, j = 1; i < DisplayUtil.CHCOUNT; i++, j++) {
			paint.setStrokeWidth(3f);
			// 三通道边框
			paint.setAlpha(150);
			canvas.drawLine(0, heightAvg * (j), width, heightAvg * (j), paint);// 长方形
			// 画刻度
			float scaleAVG_Y = heightAvg / DisplayUtil.SCALE_Y;
			paint.setStrokeWidth(1f);
			int scale_mv = 3;
			String scale_mv_text = "";
			for (int y = 0; y < DisplayUtil.SCALE_Y; y++) {
				paint.setAlpha(100);
				canvas.drawLine(0, scaleAVG_Y * y + (i * heightAvg), width, scaleAVG_Y * y + (i * heightAvg), paint);
				/**
				 * 画刻度
				 */
				paint.setAlpha(150);
				paint.setColor(Color.WHITE);
				paint.setTextSize(DisplayUtil.px2sp(getContext(), 50));

				if (y % 2 == 0) {
					if (Math.abs(--scale_mv) != 2) {

						paint.setTextSize(DisplayUtil.px2sp(getContext(), 50));
						if (scale_mv >= 0) {
							scale_mv_text = " ";
						} else {
							scale_mv_text = "";
						}
						canvas.drawText(scale_mv_text + (scale_mv), 0, scaleAVG_Y * y + (i * heightAvg), paint);

					}
					if (scale_mv == 2) {
						paint.setTextSize(DisplayUtil.px2sp(getContext(), 60));
						canvas.drawText("CH" + j, 40, scaleAVG_Y * y + (i * heightAvg) + 40, paint);
					}
				}
				

			}

			float SCALE_X = width / scaleAVG_Y;// x轴数量

			for (int x = 0; x < SCALE_X; x++) {
				paint.setAlpha(50);
				canvas.drawLine(scaleAVG_Y * x, 0, scaleAVG_Y * x, height, paint);

				/**
				 * 画秒数
				 */
				if (i == 2 & x != 0 & x % 5 == 0) {
					paint.setAlpha(150);
					paint.setColor(Color.WHITE);
					paint.setTextSize(DisplayUtil.px2sp(getContext(), 60));
					canvas.drawText((indexTime++) + "s", scaleAVG_Y * x, height - TIME_TEXT_OFFSET, paint);

				}

				paint.setColor(0xffEBEBEB);

			}

		}

	}

}

package com.dyc.factorymode;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

public class CompassView extends ImageView {

    private float mDirection;  //方向旋转的度数
    private Drawable compass;  //指针图片资源

	public CompassView(Context context ,AttributeSet attr) {
		super(context,attr);
		// TODO Auto-generated constructor stub
		mDirection = 0.0f;  
		compass = null;
	}
    
	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);
		if(null==compass){
			compass = getDrawable();
			compass.setBounds(0, 0, getWidth(), getHeight());
		}
		canvas.save();
		canvas.rotate(mDirection,getWidth()/2, getHeight()/2);
		compass.draw(canvas);
		canvas.restore();
	}
	public void updateDirection(float direction){
		mDirection = direction;
		invalidate();  //重绘，更新指针方向
	}
}


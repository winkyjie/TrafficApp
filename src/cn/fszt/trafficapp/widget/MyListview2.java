package cn.fszt.trafficapp.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ListView;

/**
 * 禁用点击事件
 * @author AeiouKong
 *
 */
public class MyListview2 extends ListView{

	public MyListview2(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public MyListview2(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public MyListview2(Context context) {
		super(context);
	}
	
	@Override 
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) { 

        int expandSpec = MeasureSpec.makeMeasureSpec( 
                Integer.MAX_VALUE >> 3, MeasureSpec.AT_MOST); 
        super.onMeasure(widthMeasureSpec, expandSpec); 
    } 
	
	@Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
            return true;// true 拦截事件自己处理，禁止向下传递
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
            return false;// false 自己不处理此次事件以及后续的事件，那么向上传递给外层view
    }
	
}

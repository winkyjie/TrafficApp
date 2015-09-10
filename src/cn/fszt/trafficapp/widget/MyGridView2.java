package cn.fszt.trafficapp.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.GridView;

/**
 * 实现在listview scrollview嵌套gridview，可以单击事件
 * @author AeiouKong
 *
 */
public class MyGridView2 extends GridView {
	
	public boolean hasScrollBar = true;
	
	public MyGridView2(Context context) {
		super(context);

	}

	public MyGridView2(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//		int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
//				MeasureSpec.AT_MOST);
//		super.onMeasure(widthMeasureSpec, expandSpec);
		
		int expandSpec = heightMeasureSpec;
        if (hasScrollBar) {
            expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
                    MeasureSpec.AT_MOST);
            super.onMeasure(widthMeasureSpec, expandSpec);// 注意这里,这里的意思是直接测量出GridView的高度
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
	}
	
//	@Override
//    public boolean onInterceptTouchEvent(MotionEvent ev) {
//            return true;// true 拦截事件自己处理，禁止向下传递
//    }
//
//    @Override
//    public boolean onTouchEvent(MotionEvent ev) {
//            return false;// false 自己不处理此次事件以及后续的事件，那么向上传递给外层view
//    }
}

package cn.fszt.trafficapp.ui.activity;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import me.imid.swipebacklayout.lib.SwipeBackLayout;
import me.imid.swipebacklayout.lib.app.SwipeBackActivityBase;
import me.imid.swipebacklayout.lib.app.SwipeBackActivityHelper;

/**
 * 有返回操作的基类
 */
public class BaseBackActivity extends BaseActivity implements SwipeBackActivityBase, SwipeBackLayout.SwipeListener {

    private SwipeBackActivityHelper mHelper;
    private SwipeBackLayout swipeBackLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mHelper = new SwipeBackActivityHelper(this);
        mHelper.onActivityCreate();
        swipeBackLayout = getSwipeBackLayout();
        enableGesture(false);
        swipeBackLayout.setEdgeTrackingEnabled(SwipeBackLayout.EDGE_LEFT);
        swipeBackLayout.addSwipeListener(this);
    }

    /**
     * 设置是否开启滑动返回手势
     * @param enableGesture 是否启动
     */
    public void enableGesture(boolean enableGesture){
        swipeBackLayout.setEnableGesture(enableGesture);
    }

    @Override
    protected void onResume() {
        super.onResume();
        swipeBackLayout.setEdgeTrackingEnabled(SwipeBackLayout.EDGE_LEFT);
    }

    //设置标题图标，启用滑动返回时显示图标，不启用滑动返回时显示返回箭头
    protected void setTitleDrawable(TextView titleView, int drawableId) {
        Drawable drawable = null;
        if (drawableId != 0) {
            drawable = getResources().getDrawable(drawableId);
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        }
        titleView.setCompoundDrawables(drawable, null, null, null);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    protected void finishActivity() {
        finish();
    }

    @Override
    public void onBackPressed() {
        finishActivity();
        super.onBackPressed();
    }

    @Override
    public void onScrollStateChange(int state, float scrollPercent) {
//        if (!swipeBackLayout.isNormalHeight()){
//            CommonUtils.hideKeyboard(this);
//        }
    }

    @Override
    public void onEdgeTouch(int edgeFlag) {
//        CommonUtils.vibrate(20);
    }

    @Override
    public void onScrollOverThreshold() {

    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mHelper.onPostCreate();
    }

    @Override
    public View findViewById(int id) {
        View v = super.findViewById(id);
        if (v != null)
            return v;
        return mHelper.findViewById(id);
    }

    @Override
    public SwipeBackLayout getSwipeBackLayout() {
        return mHelper.getSwipeBackLayout();
    }

    @Override
    public void setSwipeBackEnable(boolean enable) {
        getSwipeBackLayout().setEnableGesture(enable);
    }

    @Override
    public void scrollToFinishActivity() {
        getSwipeBackLayout().scrollToFinishActivity();
    }
}

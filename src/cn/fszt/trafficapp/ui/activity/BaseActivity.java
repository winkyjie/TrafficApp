package cn.fszt.trafficapp.ui.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;

import com.umeng.analytics.MobclickAgent;


/**
 * Activity基类
 * Created by devil on 14-7-26.
 */
public class BaseActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            //透明状态栏
//            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//            //透明导航栏
//            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
//        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    @Override
    public boolean onMenuOpened(int featureId, Menu menu) {
        //屏蔽menu键
        return false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


}

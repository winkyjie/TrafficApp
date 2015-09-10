
package cn.fszt.trafficapp.ui.activity;

import java.util.ArrayList;
import java.util.List;

import cn.fszt.trafficapp.R;
import cn.fszt.trafficapp.util.AppClickCount;
import cn.fszt.trafficapp.widget.PagerSlidingTabStrip;
import android.app.LocalActivityManager;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;


public class MultimediaActivity extends BaseBackActivity{

	private PagerSlidingTabStrip tabs;
	private ViewPager pager;
	private MyPagerAdapter adapter;
	private final String[] TITLES = { "    音频    ", "    视频    "};
	LocalActivityManager manager = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.traffictotaltabpager);

		manager = new LocalActivityManager(this , true);
        manager.dispatchCreate(savedInstanceState);
        
		final ArrayList<View> list = new ArrayList<View>();
        Intent intent = new Intent(this, AudioListActivity.class);
        list.add(getView("AudioListActivity", intent));
        Intent intent2 = new Intent(this,VideoListActivity.class);
        list.add(getView("VideoListActivity", intent2));
        
		tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
		pager = (ViewPager) findViewById(R.id.pager);
		new AppClickCount(this, TITLES[0]);
		tabs.setOnPageChangeListener(new MyOnPageChangeListener());
		adapter = new MyPagerAdapter(list);
		pager.setAdapter(adapter);

		final int pageMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 0, getResources()
				.getDisplayMetrics());
		pager.setPageMargin(pageMargin);

		tabs.setViewPager(pager);
		getActionBar().setTitle(getResources().getString(R.string.n_video));
		getActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.titlebar)));
		getActionBar().setDisplayHomeAsUpEnabled(true);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		if(item.getItemId() == android.R.id.home)
        {
            finish();
            return true;
        }
		return super.onOptionsItemSelected(item);
	}


	private View getView(String id, Intent intent) {
        return manager.startActivity(id, intent).getDecorView();
    }
	
	public class MyPagerAdapter extends PagerAdapter {
		
		List<View> list =  new ArrayList<View>();
        public MyPagerAdapter(ArrayList<View> list) {
            this.list = list;
        }

		@Override
        public void destroyItem(ViewGroup container, int position,
                Object object) {
            ViewPager pViewPager = ((ViewPager) container);
            pViewPager.removeView(list.get(position));
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        @Override
        public int getCount() {
            return list.size();
        }
        @Override
        public Object instantiateItem(View arg0, int position) {
            ViewPager pViewPager = ((ViewPager) arg0);
            pViewPager.addView(list.get(position));
            return list.get(position);
        }

		@Override
		public CharSequence getPageTitle(int position) {
			return TITLES[position];
		}


	}

	public class MyOnPageChangeListener implements OnPageChangeListener {


        @Override
        public void onPageSelected(int arg0) {
        	new AppClickCount(MultimediaActivity.this, TITLES[arg0]);
        }

        @Override
        public void onPageScrollStateChanged(int arg0) {
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
        }
    }

}
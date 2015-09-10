
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
import android.util.Log;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

public class TrafficTotalActivity extends BaseBackActivity {

	private PagerSlidingTabStrip tabs;
	private ViewPager pager;
	private MyPagerAdapter adapter;

	LocalActivityManager manager = null;
	private final String[] TITLES = { "最新路况", "路面快照", "车友报料" };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.traffictotaltabpager);

		manager = new LocalActivityManager(this, true);
		manager.dispatchCreate(savedInstanceState);

		final ArrayList<View> list = new ArrayList<View>();
		Intent intent = new Intent(this, TrafficInfoActivity.class);
		list.add(getView("TrafficInfoActivity", intent));
		Intent intent4 = new Intent(this, RoadImageActivity.class);
		list.add(getView("RoadInfoImage", intent4));
		Intent intent3 = new Intent(this, ReportListActivity.class);
		list.add(getView("Baoliaolist", intent3));

		tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
		pager = (ViewPager) findViewById(R.id.pager);
		adapter = new MyPagerAdapter(list);
		pager.setAdapter(adapter);
		final int pageMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 0,
				getResources().getDisplayMetrics());
		pager.setPageMargin(pageMargin);
		tabs.setOnPageChangeListener(new MyOnPageChangeListener());
		new AppClickCount(TrafficTotalActivity.this, TITLES[0]);
		tabs.setViewPager(pager);
		getActionBar().setTitle(getResources().getString(R.string.n_text));
		getActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.titlebar)));
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setDisplayShowHomeEnabled(false);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		if (item.getItemId() == android.R.id.home) {
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private View getView(String id, Intent intent) {
		return manager.startActivity(id, intent).getDecorView();
	}

	public class MyPagerAdapter extends PagerAdapter {
		List<View> list = new ArrayList<View>();

		public MyPagerAdapter(ArrayList<View> list) {
			this.list = list;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
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
			new AppClickCount(TrafficTotalActivity.this, TITLES[arg0]);
		}

		@Override
		public void onPageScrollStateChanged(int arg0) {
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
		}
	}

}
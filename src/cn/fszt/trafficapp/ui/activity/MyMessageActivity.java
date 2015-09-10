
package cn.fszt.trafficapp.ui.activity;

import java.util.ArrayList;
import java.util.List;

import cn.fszt.trafficapp.R;
import cn.fszt.trafficapp.util.AppClickCount;
import cn.fszt.trafficapp.widget.PagerSlidingTabStrip;
import android.app.LocalActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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

public class MyMessageActivity extends BaseBackActivity {

	private PagerSlidingTabStrip tabs;
	private ViewPager pager;
	private MyPagerAdapter adapter;
	private final String[] TITLES = { "最新通知", "中奖通知", "代办通知" };
	LocalActivityManager manager = null;
	private SharedPreferences sp_push;
	private int current_page = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.traffictotaltabpager);

		manager = new LocalActivityManager(this, true);
		manager.dispatchCreate(savedInstanceState);
		sp_push = getSharedPreferences("PUSH", Context.MODE_PRIVATE);
		////////////////////////////////////////////////////////////////
		//根据通知推送类型跳转到对应页面
		Boolean latest = sp_push.getBoolean("LatestNotification", false);
		if(latest){
			current_page = 0;
		}
		Boolean award = sp_push.getBoolean("AwardNotification", false);
		if(award){
			current_page = 1;
		}
		Boolean agent = sp_push.getBoolean("AgentNotification", false);
		if (agent) {
			current_page = 2;
		}
		///////////////////////////////////////////////////////////////
		final ArrayList<View> list = new ArrayList<View>();
		Intent intent = new Intent(this, MsgMsgListActivity.class);
		list.add(getView("M_msglist", intent));
		Intent intent2 = new Intent(this, MsgAwardListActivity.class);
		list.add(getView("M_awardlist", intent2));
		Intent intent3 = new Intent(this, MsgCarTidingsListActivity.class);
		list.add(getView("M_cartidingslist", intent3));
		tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
		pager = (ViewPager) findViewById(R.id.pager);
		adapter = new MyPagerAdapter(list);
		pager.setAdapter(adapter);
		new AppClickCount(MyMessageActivity.this, TITLES[0]);

		final int pageMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 0,
				getResources().getDisplayMetrics());
		pager.setPageMargin(pageMargin);
		tabs.setOnPageChangeListener(new OnPageChangeListener() {
			@Override
			public void onPageSelected(int arg0) {
				new AppClickCount(MyMessageActivity.this, TITLES[arg0]);
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
			}
		});
		pager.setCurrentItem(current_page); //设置加载第几个页面
		tabs.setViewPager(pager);
		getActionBar().setTitle(getResources().getString(R.string.mymessage));
		getActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.titlebar)));
		getActionBar().setDisplayHomeAsUpEnabled(true);
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
		public void setPrimaryItem(ViewGroup container, int position, Object object) {
			// TODO Auto-generated method stub
			super.setPrimaryItem(container, position, object);
		}

		@Override
		public int getCount() {
			// return list.size();
			return TITLES.length;
		}

		@Override
		public Object instantiateItem(View container, int position) {
			ViewPager pViewPager = ((ViewPager) container);
			pViewPager.addView(list.get(position));
			return list.get(position);
		}

		@Override
		public CharSequence getPageTitle(int position) {
			return TITLES[position];
		}

	}

}
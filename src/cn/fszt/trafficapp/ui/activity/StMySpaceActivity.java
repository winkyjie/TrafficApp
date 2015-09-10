
package cn.fszt.trafficapp.ui.activity;

import java.util.ArrayList;
import java.util.List;

import cn.fszt.trafficapp.R;
import cn.fszt.trafficapp.widget.PagerSlidingTabStrip;
import android.app.LocalActivityManager;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;


public class StMySpaceActivity extends BaseBackActivity {

	private PagerSlidingTabStrip tabs;
	private ViewPager pager;
	private MyPagerAdapter adapter;

	LocalActivityManager manager = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.traffictotaltabpager);

		manager = new LocalActivityManager(this , true);
        manager.dispatchCreate(savedInstanceState);
        
		final ArrayList<View> list = new ArrayList<View>();
        Intent intent = new Intent(this, SpCarlifeActivity.class);
        list.add(getView("S_carlife", intent));
        Intent intent2 = new Intent(this, SpActActivity.class);
        list.add(getView("S_huodong", intent2));
//        Intent intent3 = new Intent(this, S_news.class);
//        list.add(getView("S_news", intent3));
        Intent intent4 = new Intent(this, SpRadioCommentActivity.class);
        list.add(getView("S_radiohudong", intent4));
        Intent intent5 = new Intent(this, SpBaomingActivity.class);
        list.add(getView("S_baoming", intent5));
        Intent intent6 = new Intent(this, SpTuangouActivity.class);
        list.add(getView("S_tuangou", intent6));
        Intent intent7 = new Intent(this, SpVoteActivity.class);
        list.add(getView("S_vote", intent7));
        
        
		tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
		pager = (ViewPager) findViewById(R.id.pager);
		adapter = new MyPagerAdapter(list);
		pager.setAdapter(adapter);

		final int pageMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 0, getResources()
				.getDisplayMetrics());
		pager.setPageMargin(pageMargin);

		tabs.setViewPager(pager);
		getActionBar().setTitle(getResources().getString(R.string.myspace));
		getActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.titlebar)));
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setDisplayShowHomeEnabled(false);
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

		private final String[] TITLES = { "车生活", "精选·互动", "节目评论", "报名", "团购", "投票" };
		
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
		public void setPrimaryItem(ViewGroup container, int position,
				Object object) {
			// TODO Auto-generated method stub
			super.setPrimaryItem(container, position, object);
		}

		@Override
        public int getCount() {
//            return list.size();
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
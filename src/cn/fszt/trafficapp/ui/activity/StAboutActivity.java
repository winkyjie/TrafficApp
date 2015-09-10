package cn.fszt.trafficapp.ui.activity;

import cn.fszt.trafficapp.R;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.MenuItem;

/**
 * 关于我们
 * @author AeiouKong
 *
 */
public class StAboutActivity extends BaseBackActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		 setContentView(R.layout.activity_about);
		 
		 getActionBar().setTitle(getResources().getString(R.string.aboutus));
		 getActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.titlebar)));
		 getActionBar().setDisplayHomeAsUpEnabled(true);
		 getActionBar().setDisplayShowHomeEnabled(false);
}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch(item.getItemId()){
			case android.R.id.home:
				finish();
				break;
		}
		return true;
	}
}

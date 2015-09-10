package cn.fszt.trafficapp.adapter;

import cn.fszt.trafficapp.R;
import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MainAdapter extends BaseAdapter {
	Activity act;
	int[] res;
	String[] titles;
	int visible = View.GONE;

	public MainAdapter(Activity act) {
		this.act = act;
		res = new int[] { R.drawable.c_chalukuang, R.drawable.c_fuwu,
				R.drawable.c_fenxiang, R.drawable.c_ditu,
				R.drawable.c_baolukuang, R.drawable.c_jingxuan,
				R.drawable.c_cheshenghuo, R.drawable.c_yinshipin };
		titles = new String[] { act.getString(R.string.n_text),
				act.getString(R.string.n_weizhang),
				act.getString(R.string.n_baoguang),
				act.getString(R.string.n_map),
				act.getString(R.string.n_baoliao),
				act.getString(R.string.n_qingbao),
				act.getString(R.string.n_carlife),
				act.getString(R.string.n_video) };
	}

	public void setCircleVisibile(int enable){
		if(enable == View.VISIBLE){
			visible = View.VISIBLE;
		}else{
			visible = View.GONE;
		}
	}
	
	@Override
	public int getCount() {
		return res.length;

	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Holder holder = null;
		if (convertView == null) {
			holder = new Holder();
			convertView = View.inflate(act, R.layout.item_newmain, null);
			holder.iv_main = (ImageView) convertView.findViewById(R.id.iv_main);
			holder.iv_circle = (ImageView) convertView.findViewById(R.id.iv_circle);
			holder.tv_title = (TextView) convertView.findViewById(R.id.tv_title);
			convertView.setTag(holder);
		} else {
			holder = (Holder) convertView.getTag();
		}
		holder.iv_main.setImageResource(res[position]);
		holder.tv_title.setText(titles[position]);
		if(position == 2){
			holder.iv_circle.setVisibility(visible);
		}
		return convertView;
	}

	static class Holder {
		private ImageView iv_main;
		private ImageView iv_circle;
		private TextView tv_title;
	}
}

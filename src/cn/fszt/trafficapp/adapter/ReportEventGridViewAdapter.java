package cn.fszt.trafficapp.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import cn.fszt.trafficapp.R;
import cn.fszt.trafficapp.domain.TcEventData;
import android.app.Activity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 报路况--交通事件
 * @author AeiouKong
 *
 */
public class ReportEventGridViewAdapter extends BaseAdapter{
	Activity act;
	List<TcEventData> dataList;
	private int selectTotal = 0;
	public Map<String, String> map = new HashMap<String, String>();
	
	public ReportEventGridViewAdapter(Activity act){
		this.act = act;
		dataList = new ArrayList<TcEventData>();
		TcEventData item = new TcEventData();
		item.imagePath = R.drawable.bl_shigong;
		item.eventName = act.getString(R.string.shigong);
		dataList.add(item);
		TcEventData item1 = new TcEventData();
		item1.imagePath = R.drawable.bl_huaiche;
		item1.eventName = act.getString(R.string.huaiche);
		dataList.add(item1);
		TcEventData item2 = new TcEventData();
		item2.imagePath = R.drawable.bl_zhuangche;
		item2.eventName = act.getString(R.string.zhuangche);
		dataList.add(item2);
		TcEventData item3 = new TcEventData();
		item3.imagePath = R.drawable.bl_fenglu;
		item3.eventName = act.getString(R.string.fenglu);
		dataList.add(item3);
		TcEventData item4 = new TcEventData();
		item4.imagePath = R.drawable.bl_jishui;
		item4.eventName = act.getString(R.string.jishui);
		dataList.add(item4);
		TcEventData item5 = new TcEventData();
		item5.imagePath = R.drawable.bl_guanzhi;
		item5.eventName = act.getString(R.string.guanzhi);
		dataList.add(item5);
	}
	
	@Override
	public int getCount() {
		return dataList.size();
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
	public View getView(final int position, View convertView, ViewGroup parent) {
		final Holder holder;
		if(convertView == null){
			holder = new Holder();
			convertView = View.inflate(act, R.layout.item_bl_event, null);
			holder.iv_main = (ImageView) convertView.findViewById(R.id.iv_main);
			holder.isselected = (ImageView) convertView.findViewById(R.id.isselected);
			holder.text = (TextView) convertView.findViewById(R.id.item_image_grid_text);
			convertView.setTag(holder);
		}else{
			holder = (Holder) convertView.getTag();
		}
		final TcEventData item = dataList.get(position);
		holder.iv_main.setImageResource(dataList.get(position).imagePath);
		holder.iv_main.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (selectTotal < 2) {
					item.isSelected = !item.isSelected;
					if (item.isSelected) {
						holder.isselected.setVisibility(View.VISIBLE);
						holder.isselected.setImageResource(R.drawable.icon_data_select);
						holder.text.setBackgroundResource(R.drawable.bgd_relatly_line);
						selectTotal++;
						map.put(item.eventName, item.eventName);

					} else if (!item.isSelected) {
						holder.isselected.setVisibility(View.GONE);
						holder.text.setBackgroundColor(0x00000000);
						selectTotal--;
						map.remove(item.eventName);
					}
				}else if (selectTotal >= 2) {
					if (item.isSelected) {
						item.isSelected = !item.isSelected;
						holder.isselected.setVisibility(View.GONE);
						holder.text.setBackgroundColor(0x00000000);
						selectTotal--;
						map.remove(item.eventName);

					} else {
//						Message message = Message.obtain(mHandler, 0);
//						message.sendToTarget();
					}
				}
			}
		});
		return convertView;
	}
	
	class Holder {
		private ImageView iv_main;
		private ImageView isselected;
		private TextView text;
	}
}

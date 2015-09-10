package cn.fszt.trafficapp.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.fszt.trafficapp.R;
import cn.fszt.trafficapp.domain.TcFlowData;
import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ReportFlowGridViewAdapter extends BaseAdapter{
	Activity act;
	List<TcFlowData> dataList;
	private int selectTotal = 0;
	public Map<String, String> map = new HashMap<String, String>();
	
	public ReportFlowGridViewAdapter(Activity act){
		this.act = act;
		dataList = new ArrayList<TcFlowData>();
		TcFlowData item = new TcFlowData();
		item.imagePath = R.drawable.bl_yongji;
		item.eventName = act.getString(R.string.zuse);
		dataList.add(item);
		TcFlowData item1 = new TcFlowData();
		item1.imagePath = R.drawable.bl_huanman;
		item1.eventName = act.getString(R.string.huanman);
		dataList.add(item1);
		TcFlowData item2 = new TcFlowData();
		item2.imagePath = R.drawable.bl_changtong;
		item2.eventName = act.getString(R.string.changtong);
		dataList.add(item2);
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
			convertView = View.inflate(act, R.layout.item_bl_flow, null);
			holder.iv_main = (ImageView) convertView.findViewById(R.id.iv_main);
			holder.isselected = (ImageView) convertView.findViewById(R.id.isselected);
			holder.text = (TextView) convertView.findViewById(R.id.item_image_grid_text);
			convertView.setTag(holder);
		}else{
			holder = (Holder) convertView.getTag();
		}
		final TcFlowData item = dataList.get(position);
		holder.iv_main.setImageResource(dataList.get(position).imagePath);
		holder.iv_main.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (selectTotal < 1) {
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
				}else if (selectTotal >= 1) {
					
					if (item.isSelected) {
						item.isSelected = !item.isSelected;
						holder.isselected.setVisibility(View.GONE);
						holder.text.setBackgroundColor(0x00000000);
						selectTotal--;
						map.remove(item.eventName);

					} else {
						map.clear();
						map.put(item.eventName, item.eventName);
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

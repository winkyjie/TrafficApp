package cn.fszt.trafficapp.adapter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import cn.fszt.trafficapp.R;
import cn.fszt.trafficapp.domain.MsgCarTidingsData;
import cn.fszt.trafficapp.widget.msg.AppMsg;

/**
 * 代办通知适配
 */
public class MsgCarTidingsListAdapter extends BaseAdapter {
	private Context mContext;
	private List<MsgCarTidingsData> arrays;
	private LayoutInflater inflater;
	AppMsg.Style style = AppMsg.STYLE_INFO;
	AppMsg appMsg;

	public MsgCarTidingsListAdapter(Context mContext, List<MsgCarTidingsData> arrays, LayoutInflater inflater) {
		this.mContext = mContext;
		this.arrays = arrays;
		this.inflater = inflater;
	}

	@Override
	public int getCount() {
		return arrays == null ? 0 : arrays.size();
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
		String status = arrays.get(position).getAgentStatus();
		String content = arrays.get(position).getContent();
		String time = arrays.get(position).getAgentLatestDate();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			Date ddate = sdf.parse(time);
			SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd HH:mm");
			time = dateFormat.format(ddate);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		BaomingHolder mholder = null;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.item_carnotice, null);
			mholder = new BaomingHolder();
			mholder.tv_carnotice_status = (TextView) convertView.findViewById(R.id.tv_carnotice_status);
			mholder.tv_carnotice_content = (TextView) convertView.findViewById(R.id.tv_carnotice_content);
			mholder.tv_carnotice_time = (TextView) convertView.findViewById(R.id.tv_carnotice_time);
			convertView.setTag(mholder);
		} else {
			mholder = (BaomingHolder) convertView.getTag();
		}
		mholder.tv_carnotice_status.setText(status);
		mholder.tv_carnotice_content.setText(content);
		mholder.tv_carnotice_time.setText(time);
		return convertView;
	}

	private class BaomingHolder {
		TextView tv_carnotice_status;
		TextView tv_carnotice_content;
		TextView tv_carnotice_time;
	}
}

package cn.fszt.trafficapp.adapter;

import java.util.List;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import cn.fszt.trafficapp.R;
import cn.fszt.trafficapp.domain.SurveyListData;

public class VoteSurveyListAdapter extends BaseAdapter{
	
	private List<SurveyListData> arrays;
	private LayoutInflater inflater;

	public VoteSurveyListAdapter(List<SurveyListData> arrays,LayoutInflater inflater){
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
	public View getView(int position, View convertView, ViewGroup parent) {
		HuodongHolder hdholder = null;
		if (convertView == null) {
			convertView  = inflater.inflate(R.layout.item_survey_list, null);
			
			hdholder = new HuodongHolder();
			hdholder.tv_surveytitle = (TextView) convertView.findViewById(R.id.tv_surveytitle);
			convertView.setTag(hdholder);
		} else {
			hdholder = (HuodongHolder) convertView.getTag();
		}
            	
        hdholder.tv_surveytitle.setText(arrays.get(position).getHdsurveyvote_votetitle());
            	
		return convertView;
	}
	
	private class HuodongHolder {
		TextView tv_surveytitle;
	}
}

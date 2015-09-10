package cn.fszt.trafficapp.adapter;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import cn.fszt.trafficapp.R;
import cn.fszt.trafficapp.domain.RadioListData;

/**
 * 听广播--节目时表
 * @author AeiouKong
 * 2014-5-14
 */
public class RadioLiveListAdapter extends BaseAdapter{
	
	private List<RadioListData> arrays;
	private LayoutInflater inflater;
	private String id;
	private Activity act;

	public RadioLiveListAdapter(Activity act,List<RadioListData> arrays,LayoutInflater inflater,String hdmenuid){
		this.arrays = arrays;
		this.inflater = inflater;
		this.id = hdmenuid;
		this.act = act;
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
	public int getItemViewType(int position) {
		
		String hdmenuid = arrays.get(position).getHdmenuid();
		if(id!=null&&id.equals(hdmenuid)){
			return 0;
		}else{
			return 1;
		}
	}
	
	@Override
	public int getViewTypeCount() {
		return 4;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		RadioListHolder hdholder = null;
		int type = getItemViewType(position); 
		String starttime = arrays.get(position).getStarttime();
		String endtime = arrays.get(position).getEndtime();
		String programname = arrays.get(position).getProgramname();
		String host = arrays.get(position).getHost();
		String hdmenuid = arrays.get(position).getHdmenuid();
		long startT = fromDateStringToLong(starttime); 
		long endT = fromDateStringToLong(endtime);  
		int time = (int) ((endT - startT)/1000/60);
		
		if (convertView == null) {
			switch(type){
			case 0:
				convertView  = inflater.inflate(R.layout.item_radiolist, null);
				
				hdholder = new RadioListHolder();
				hdholder.tv_starttime = (TextView) convertView.findViewById(R.id.tv_starttime);
				hdholder.tv_endtime = (TextView) convertView.findViewById(R.id.tv_endtime);
				hdholder.tv_programname = (TextView) convertView.findViewById(R.id.tv_programname);
				hdholder.tv_host = (TextView) convertView.findViewById(R.id.tv_host);
				hdholder.tv_time = (TextView) convertView.findViewById(R.id.tv_time);
				hdholder.iv_playing = (ImageView) convertView.findViewById(R.id.iv_playing);
				convertView.setTag(hdholder);
				break;
			case 1:
				convertView  = inflater.inflate(R.layout.item_radiolist, null);
				
				hdholder = new RadioListHolder();
				hdholder.tv_starttime = (TextView) convertView.findViewById(R.id.tv_starttime);
				hdholder.tv_endtime = (TextView) convertView.findViewById(R.id.tv_endtime);
				hdholder.tv_programname = (TextView) convertView.findViewById(R.id.tv_programname);
				hdholder.tv_host = (TextView) convertView.findViewById(R.id.tv_host);
				hdholder.tv_time = (TextView) convertView.findViewById(R.id.tv_time);
				hdholder.iv_playing = (ImageView) convertView.findViewById(R.id.iv_playing);
				convertView.setTag(hdholder);
				break;
			}
				
			
		} else {
			switch(type){
			case 0:
				hdholder = (RadioListHolder) convertView.getTag();
				break;
			case 1:
				hdholder = (RadioListHolder) convertView.getTag();
				break;
			}
		}
		switch(type){
		case 0:
			hdholder.tv_programname.setTextColor(act.getResources().getColor(R.color.titlebar));
			hdholder.tv_starttime.setTextColor(act.getResources().getColor(R.color.titlebar));
			hdholder.tv_endtime.setTextColor(act.getResources().getColor(R.color.titlebar));
			hdholder.iv_playing.setVisibility(View.VISIBLE);
			hdholder.tv_starttime.setText(starttime+"-");
	        hdholder.tv_endtime.setText(endtime);
	        hdholder.tv_programname.setText(programname);
	        hdholder.tv_host.setText(host);
	        hdholder.tv_time.setText("时长:"+time+"分钟");
			break;
		case 1:
			hdholder.tv_starttime.setText(starttime+"-");
	        hdholder.tv_endtime.setText(endtime);
	        hdholder.tv_programname.setText(programname);
	        hdholder.tv_host.setText(host);
	        hdholder.tv_time.setText("时长:"+time+"分钟");
			break;
		}
		return convertView;
	}
	
	private class RadioListHolder {
		TextView tv_starttime;
		TextView tv_endtime;
		TextView tv_programname;
		TextView tv_host;
		TextView tv_time;
		ImageView iv_playing;
	}
	
	public long fromDateStringToLong(String inVal) { //此方法计算时间毫秒
		  Date date = null;   //定义时间类型       
		  SimpleDateFormat inputFormat = new SimpleDateFormat("HH:mm"); 
		  try { 
			  date = inputFormat.parse(inVal); //将字符型转换成日期型
		  } catch (Exception e) { 
			  e.printStackTrace(); 
		  } 
		  	return date.getTime();   //返回毫秒数
	} 
	
}

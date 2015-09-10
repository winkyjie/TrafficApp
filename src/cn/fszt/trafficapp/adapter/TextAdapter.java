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
import android.widget.ImageView;
import android.widget.TextView;
import cn.fszt.trafficapp.R;
import cn.fszt.trafficapp.domain.TextData;

public class TextAdapter extends BaseAdapter {
	private Context mContext;
	private List<TextData> arrays;
	private LayoutInflater inflater;
	private int densityDpi;
	
	public TextAdapter(Context mContext, List<TextData> arrays,LayoutInflater inflater,int densityDpi){
		this.mContext = mContext;
		this.arrays = arrays;
		this.inflater = inflater;
		this.densityDpi = densityDpi;
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
		return super.getItemViewType(position);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();
			if(densityDpi == 240){
				convertView = inflater.inflate(R.layout.item_list_hdpi, null);
			}else{
				convertView = inflater.inflate(R.layout.item_list, null);
			}
			holder.tv_address = (TextView) convertView.findViewById(R.id.text);
			holder.tv_createtime = (TextView) convertView.findViewById(R.id.tv_createtime);
			holder.iv_event = (ImageView) convertView.findViewById(R.id.img_event);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		if(arrays.size()>0){
			  holder.tv_address.setText(arrays.get(position).getAddress()+"， "+arrays.get(position).getEvent());
			  
			  //服务器拿到的发布交通信息时间
			  String createtime = arrays.get(position).getCreatetime();
			  SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
			  String sysdate = df.format(new Date());
			  
			  //与系统时间的时间差(分钟)
			  long minutes = 0;
			try {
				 Date d1 = df.parse(sysdate);
				 Date d2 = df.parse(createtime);
				    long diff = d1.getTime() - d2.getTime();
				    minutes = diff / (1000 * 60);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			
			   //判断不同时间差显示的文字
			  if(minutes<1){
				  holder.tv_createtime.setText("刚刚");
			  }else if(60-minutes>=1){
				  holder.tv_createtime.setText(minutes+"分钟前");
			  }else if(minutes-60>=0&&minutes-1440<0){
				  holder.tv_createtime.setText(minutes/60+"小时前");
			  }else if(minutes-1440>=0&&minutes-2880<0){
				  holder.tv_createtime.setText("昨天");
			  }else if(minutes-2880>=0&&minutes-10080<0){
				  holder.tv_createtime.setText(minutes/60/24+"天前");
			  }else if(minutes-10080>=0&&minutes-43200<0){
				  holder.tv_createtime.setText(minutes/60/24/7+"周前");
			  }else if(minutes-43200>=0&&minutes-518400<0){
				  holder.tv_createtime.setText(minutes/60/24/7/4+"个月前");
			  }
			  
			  //判断不同事件显示的图标
//			if(arrays.get(position).getEvent().equals(mContext.getResources().getString(R.string.huaiche))){
//				holder.iv_event.setBackgroundResource(R.drawable.z_huaiche);
//			}else if(arrays.get(position).getEvent().equals(mContext.getResources().getString(R.string.changtong))){
//				holder.iv_event.setBackgroundResource(R.drawable.z_changtong);
//			}else if(arrays.get(position).getEvent().equals(mContext.getResources().getString(R.string.zuse))){
//				holder.iv_event.setBackgroundResource(R.drawable.z_zuse);
//			}else if(arrays.get(position).getEvent().equals(mContext.getResources().getString(R.string.huanman))){
//				holder.iv_event.setBackgroundResource(R.drawable.z_huanman);
//			}else if(arrays.get(position).getEvent().equals(mContext.getResources().getString(R.string.guanzhi))){
//				holder.iv_event.setBackgroundResource(R.drawable.z_guanzhi);
//			}else if(arrays.get(position).getEvent().equals(mContext.getResources().getString(R.string.gaidao))){
//				holder.iv_event.setBackgroundResource(R.drawable.z_gaidao);
//			}else if(arrays.get(position).getEvent().equals(mContext.getResources().getString(R.string.shigong))){
//				holder.iv_event.setBackgroundResource(R.drawable.z_shigong);
//			}else if(arrays.get(position).getEvent().equals(mContext.getResources().getString(R.string.jishui))){
//				holder.iv_event.setBackgroundResource(R.drawable.z_jishui);
//			}
		}else{
			holder = null;
		}
		return convertView;
	}
}

class ViewHolder {
	TextView tv_address;
	TextView tv_createtime;
	ImageView iv_event;
}

package cn.fszt.trafficapp.adapter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import cn.fszt.trafficapp.R;
import cn.fszt.trafficapp.domain.ActListData;
import cn.fszt.trafficapp.widget.photoview.DisplayImageOptionsUtil;

public class VideoListAdapter extends BaseAdapter{
	
	private List<ActListData> arrays;
	private LayoutInflater inflater;
	private ImageLoader imageLoader;
	private DisplayImageOptions options;

	public VideoListAdapter(List<ActListData> arrays,LayoutInflater inflater){
		this.arrays = arrays;
		this.inflater = inflater;
		
		imageLoader = ImageLoader.getInstance();
		
		options = DisplayImageOptionsUtil.getOptions(
				R.drawable.ic_empty, R.drawable.ic_error,R.drawable.default_image);
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
		
		if(position==0){
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
		HuodongHolder hdholder = null;
		int type = getItemViewType(position); 
		String imageUrl = arrays.get(position).getNewimage();  
		String title = arrays.get(position).getTitle();  
		String commentcount = arrays.get(position).getCommentcount();  
		String views = arrays.get(position).getViews();  
		
		String date = arrays.get(position).getCreatedate();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			Date ddate = sdf.parse(date);
			SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd");
			date = dateFormat.format(ddate);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		if (convertView == null) {
			switch(type){
			case 0:
				convertView  = inflater.inflate(R.layout.item_huodong_list, null);
				
				hdholder = new HuodongHolder();
				hdholder.iv_huodong = (ImageView) convertView.findViewById(R.id.iv_huodong);
				hdholder.tv_huodong = (TextView) convertView.findViewById(R.id.tv_huodong);
				hdholder.tv_hd_createtime = (TextView) convertView.findViewById(R.id.tv_hd_createtime);
				hdholder.tv_hd_commentcount = (TextView) convertView.findViewById(R.id.tv_hd_commentcount);
				hdholder.tv_hd_views = (TextView) convertView.findViewById(R.id.tv_hd_views);
				convertView.setTag(hdholder);
				break;
				
			case 1:
				convertView  = inflater.inflate(R.layout.item_huodong_list, null);
				
				hdholder = new HuodongHolder();
				hdholder.iv_huodong = (ImageView) convertView.findViewById(R.id.iv_huodong);
				hdholder.tv_huodong = (TextView) convertView.findViewById(R.id.tv_huodong);
				hdholder.tv_hd_createtime = (TextView) convertView.findViewById(R.id.tv_hd_createtime);
				hdholder.tv_hd_commentcount = (TextView) convertView.findViewById(R.id.tv_hd_commentcount);
				hdholder.tv_hd_views = (TextView) convertView.findViewById(R.id.tv_hd_views);
				convertView.setTag(hdholder);
				break;
			}
			
		} else {
			switch(type){
				case 0:
					hdholder = (HuodongHolder) convertView.getTag();
				break;
				case 1:
					hdholder = (HuodongHolder) convertView.getTag();
				break;
			}
		}
          
		switch(type){
			case 0:
				imageLoader.displayImage(imageUrl, hdholder.iv_huodong, options, null);
		        hdholder.tv_huodong.setText(title);
		        hdholder.tv_hd_createtime.setText(date);
		        if(commentcount.length()>4){
		        	hdholder.tv_hd_commentcount.setText(commentcount.substring(0, commentcount.length()-4)+"万+");
		        }else{
		        	hdholder.tv_hd_commentcount.setText(commentcount);
		        }
		        
		        if(views.length()>4){
		        	hdholder.tv_hd_views.setText(views.substring(0, views.length()-4)+"万+");
		        }else{
		        	hdholder.tv_hd_views.setText(views);
		        }
			break;
			case 1:
				imageLoader.displayImage(imageUrl, hdholder.iv_huodong, options, null);
		        hdholder.tv_huodong.setText(title);
		        hdholder.tv_hd_createtime.setText(date);
		        if(commentcount.length()>4){
		        	hdholder.tv_hd_commentcount.setText(commentcount.substring(0, commentcount.length()-4)+"万+");
		        }else{
		        	hdholder.tv_hd_commentcount.setText(commentcount);
		        }
		        
		        if(views.length()>4){
		        	hdholder.tv_hd_views.setText(views.substring(0, views.length()-4)+"万+");
		        }else{
		        	hdholder.tv_hd_views.setText(views);
		        }
		        
			break;
		}
        
		return convertView;
	}
	
	private class HuodongHolder {
		TextView tv_huodong;
		TextView tv_hd_createtime;
		TextView tv_hd_commentcount;
		TextView tv_hd_views;
		ImageView iv_huodong;
	}
}

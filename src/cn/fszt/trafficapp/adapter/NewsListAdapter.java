package cn.fszt.trafficapp.adapter;

import java.util.List;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import cn.fszt.trafficapp.R;
import cn.fszt.trafficapp.domain.NewsData;
import cn.fszt.trafficapp.widget.photoview.DisplayImageOptionsUtil;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class NewsListAdapter extends BaseAdapter{
	
	private List<NewsData> arrays;
	private LayoutInflater inflater;
	private ImageLoader imageLoader;
	private DisplayImageOptions options;
	
	public NewsListAdapter(List<NewsData> arrays,LayoutInflater inflater){
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
		
		String istop = arrays.get(position).getIstop();
		if(istop != null && !"".equals(istop)){
			if("0".equals(istop)){
				return 0;
			}else if("1".equals(istop)){
				return 1;
			}else if("2".equals(istop)){
				return 2;
			}else if("3".equals(istop)){
				return 3;
			}else{
				return 1;
				}
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
		int type = getItemViewType(position); 
		PnewsHolder pholder = null;
		CnewsHolder cholder = null;
		CreatedateHolder dholder = null;
		if (convertView == null) {
			switch(type){
			case 0:
				convertView  = inflater.inflate(R.layout.item_pnews_list, null);
				pholder = new PnewsHolder();
				pholder.iv_pnews = (ImageView) convertView.findViewById(R.id.iv_pnews);
				pholder.tv_pnews = (TextView) convertView.findViewById(R.id.tv_pnews);
				convertView.setTag(pholder);
			break;
			case 1:
				convertView  = inflater.inflate(R.layout.item_cnews_list, null);
				cholder = new CnewsHolder();
				cholder.iv_cnews = (ImageView) convertView.findViewById(R.id.iv_cnews);
				cholder.tv_cnews = (TextView) convertView.findViewById(R.id.tv_cnews);
				convertView.setTag(cholder);
			break;
			case 2:
				convertView  = inflater.inflate(R.layout.item_cnews_list, null);
				cholder = new CnewsHolder();
				cholder.iv_cnews = (ImageView) convertView.findViewById(R.id.iv_cnews);
				cholder.tv_cnews = (TextView) convertView.findViewById(R.id.tv_cnews);
				convertView.setTag(cholder);
				convertView.setBackgroundResource(R.drawable.bg_list_news_last);
			break;
			case 3:
				convertView  = inflater.inflate(R.layout.item_news_createdate, null);
				dholder = new CreatedateHolder();
				dholder.tv_createdate = (TextView) convertView.findViewById(R.id.tv_news_createdate);
				convertView.setTag(dholder);
			break;
			}
			
		} else {
			switch(type){
				case 0:
					pholder = (PnewsHolder) convertView.getTag();
				break;
				case 1:
					cholder = (CnewsHolder) convertView.getTag();
				break;
				case 2:
					cholder = (CnewsHolder) convertView.getTag();
				break;
				case 3:
					dholder = (CreatedateHolder) convertView.getTag();
				break;
			}
		}
		switch(type){  
		
            case 0:  
	        	String imageUrl = arrays.get(position).getNewimage();  
	        	imageLoader.displayImage(imageUrl, pholder.iv_pnews, options, null);
            	pholder.tv_pnews.setText(arrays.get(position).getTitle());
            break;  
            case 1:  
            	String imageUrl_cnews = arrays.get(position).getNewimage();  
            	imageLoader.displayImage(imageUrl_cnews, cholder.iv_cnews, options, null);
            	cholder.tv_cnews.setText(arrays.get(position).getTitle());
            break;  
            case 2:
            	String imageUrl_cnews2 = arrays.get(position).getNewimage();  
            	imageLoader.displayImage(imageUrl_cnews2, cholder.iv_cnews, options, null);
            	cholder.tv_cnews.setText(arrays.get(position).getTitle());
            break;
            case 3:
            	String s_date = arrays.get(position).getCreatedate();
            	if(s_date!=null){
            		String[] a_date = s_date.trim().split(" ");
                	dholder.tv_createdate.setText(a_date[0]);
            	}
            break;
        }  
		
		return convertView;
	}

	private class PnewsHolder {
		TextView tv_pnews;
		ImageView iv_pnews;
	}
	private class CnewsHolder {
		TextView tv_cnews;
		ImageView iv_cnews;
	}
	private class CreatedateHolder{
		TextView tv_createdate;
	}
}

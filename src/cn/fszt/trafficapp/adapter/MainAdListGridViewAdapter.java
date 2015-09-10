package cn.fszt.trafficapp.adapter;

import java.util.List;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import cn.fszt.trafficapp.R;
import cn.fszt.trafficapp.domain.MainAdListData;
import cn.fszt.trafficapp.widget.photoview.DisplayImageOptionsUtil;

/**
 * 首页广告区
 * @author AeiouKong
 *
 */
public class MainAdListGridViewAdapter extends BaseAdapter {
	
	private LayoutInflater inflater; 
	private ImageLoader imageLoader;
	private DisplayImageOptions options;
	private List<MainAdListData> arrays;
	
	public MainAdListGridViewAdapter(){
	}
	
	public MainAdListGridViewAdapter(Context context,List<MainAdListData> arrays) {
		inflater = LayoutInflater.from(context);
		imageLoader = ImageLoader.getInstance();
		options = DisplayImageOptionsUtil.getOptions(
				R.drawable.ic_empty, R.drawable.ic_error,R.drawable.default_image);
		this.arrays = arrays;
	}

	public int getCount() {
		return arrays==null ? 0 : arrays.size();
	}
	public Object getItem(int arg0) {

		return null;
	}
	public long getItemId(int arg0) {

		return 0;
	}

	/**
	 * ListView Item设置
	 */
	public View getView(int position, View convertView, ViewGroup parent) {
		
		ViewHolder holder = null;
		String content = arrays.get(position).getTitle();
		String price = arrays.get(position).getPrice();
		String imgurl = arrays.get(position).getImagepath();
		
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.item_img_mainadlistgridview,
					parent, false);
			holder = new ViewHolder();
			holder.iv_img = (ImageView) convertView
					.findViewById(R.id.iv_img);
			holder.tv_content = (TextView) convertView
					.findViewById(R.id.tv_content);
			holder.tv_price = (TextView) convertView
					.findViewById(R.id.tv_price);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		imageLoader.displayImage(imgurl, holder.iv_img, options);
		holder.tv_content.setText(content);
		holder.tv_price.setText(price);
		return convertView;
	}

	static class ViewHolder {
		public ImageView iv_img;
		public TextView tv_content;
		public TextView tv_price;
	}

}

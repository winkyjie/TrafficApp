package cn.fszt.trafficapp.adapter;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import cn.fszt.trafficapp.R;
import cn.fszt.trafficapp.widget.photoview.DisplayImageOptionsUtil;

public class MyGridViewAdapter extends BaseAdapter {
	private LayoutInflater inflater; // 视图容器
	private String[] urls_small;
	private ImageLoader imageLoader;
	private DisplayImageOptions options;

	public MyGridViewAdapter(){
		
	}
	
	public MyGridViewAdapter(Context context) {
		inflater = LayoutInflater.from(context);
		imageLoader = ImageLoader.getInstance();
		
		options = DisplayImageOptionsUtil.getOptions(
				R.drawable.ic_empty, R.drawable.ic_error,R.drawable.default_image);
	}
	
	
	public void setUrls(String urls){
		urls_small = urls.split(",");
	}

	public int getCount() {
		return urls_small==null ? 0 : urls_small.length;
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
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.item_img_gridview,
					parent, false);
			holder = new ViewHolder();
			holder.image = (ImageView) convertView
					.findViewById(R.id.item_grida_image);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		imageLoader.displayImage(urls_small[position], holder.image, options);
		
		return convertView;
	}

	static class ViewHolder {
		public ImageView image;
	}

}

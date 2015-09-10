package cn.fszt.trafficapp.adapter;

import java.util.List;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

import cn.fszt.trafficapp.R;
import cn.fszt.trafficapp.domain.ReplaysData;
import cn.fszt.trafficapp.widget.photoview.DisplayImageOptionsUtil;
import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class RadioProgrammeListAdapter extends BaseAdapter{

	private Activity act;
	private List<ReplaysData> arrays;
	private ImageLoader imageLoader;
	private DisplayImageOptions options;
	private String pgname = "节目名称";
	
	public RadioProgrammeListAdapter(Activity act,List<ReplaysData> arrays){
		this.act = act;
		this.arrays = arrays;
		imageLoader = ImageLoader.getInstance();
        options = DisplayImageOptionsUtil.getOptions(
				R.drawable.ic_empty, R.drawable.ic_error,R.drawable.default_image,
				new RoundedBitmapDisplayer(90));
	}
	
	public void setPgname(String pgname){
		if(pgname!=null){
			this.pgname = pgname;
		}
	}
	
	@Override
	public int getCount() {
		return arrays.size();
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
		
		Holder holder;
		if(convertView == null){
			holder = new Holder();
			convertView = View.inflate(act, R.layout.item_choosereplayhudong, null);
			holder.iv_headimg = (ImageView) convertView.findViewById(R.id.iv_headimg);
			holder.iv_circle = (ImageView) convertView.findViewById(R.id.iv_circle);
			holder.tv_title = (TextView) convertView.findViewById(R.id.tv_title);
			convertView.setTag(holder);
		}else{
			holder = (Holder) convertView.getTag();
		}
		imageLoader.displayImage(arrays.get(position).getImagepath(), holder.iv_headimg, options);
		String typename = arrays.get(position).getTypename();
		holder.tv_title.setText(typename);
		if(arrays.get(position).isVisiable()){
			holder.iv_circle.setVisibility(View.VISIBLE);
		}
//		holder.tv_title.setTextColor(pgname.contains(typename) 
//											? act.getResources().getColor(R.color.orange_bg)
//											: act.getResources().getColor(R.color.name_text));
		return convertView;
	}

	class Holder {
		private ImageView iv_headimg;
		private ImageView iv_circle;
		private TextView tv_title;
	}
}

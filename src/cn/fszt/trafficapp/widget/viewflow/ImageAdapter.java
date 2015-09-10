
package cn.fszt.trafficapp.widget.viewflow;

import java.util.ArrayList;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import cn.fszt.trafficapp.R;
import cn.fszt.trafficapp.domain.HomeInfoData;
import cn.fszt.trafficapp.ui.activity.ActDetailActivity;
import cn.fszt.trafficapp.util.AppClickCount;
import cn.fszt.trafficapp.widget.photoview.DisplayImageOptionsUtil;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 首页飞图
 * @author AeiouKong
 *
 */
public class ImageAdapter extends BaseAdapter {

	private Context mContext;
	private LayoutInflater mInflater;
	private ArrayList<HomeInfoData> ids;
	private ImageLoader imageLoader;
	private DisplayImageOptions options;
	private String uid;
	
	public ImageAdapter(Context context,ArrayList<HomeInfoData> ids, String uid) {
		mContext = context;
		this.ids = ids;
		mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		imageLoader = ImageLoader.getInstance();
		
		options = DisplayImageOptionsUtil.getOptions(
				R.drawable.ic_empty, R.drawable.ic_error,R.drawable.default_image);
		
		this.uid = uid;
	}

	@Override
	public int getCount() {
		return Integer.MAX_VALUE;  
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.textview_item, null);
		}
		((TextView) convertView.findViewById(R.id.texView)).setText(ids.get(position%ids.size()).getTitle());
		imageLoader.displayImage(ids.get(position%ids.size()).getNewimage(), ((ImageView) convertView.findViewById(R.id.iv_main)), options);
		
		convertView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				new AppClickCount(mContext, "飞图文章");
				String hdtype = ids.get(position%ids.size()).getHdtype();
				String hdinfoid = ids.get(position%ids.size()).getHdinfoid();
				//精选活动
				if(hdtype!=null && hdtype.equals("0")){
					Intent intent = new Intent(mContext,ActDetailActivity.class);
					intent.putExtra("hdinfoid", hdinfoid);
					intent.putExtra("contenttype", "huodong");
					intent.putExtra("uid", uid);
					mContext.startActivity(intent);
				//车生活，目前app没有区分，都是跳转到ActDetailActivity
				}else if(hdtype!=null && hdtype.equals("1")){
					Intent intent = new Intent(mContext,ActDetailActivity.class);
					intent.putExtra("hdinfoid", hdinfoid);
					intent.putExtra("contenttype", "carlife");
					intent.putExtra("uid", uid);
					mContext.startActivity(intent);
				}
			}
		});
		return convertView;
	}

}

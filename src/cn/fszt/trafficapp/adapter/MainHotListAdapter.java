package cn.fszt.trafficapp.adapter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import cn.fszt.trafficapp.R;
import cn.fszt.trafficapp.domain.MainHotListData;
import cn.fszt.trafficapp.widget.expression.ExpressionUtil;
import cn.fszt.trafficapp.widget.photoview.DisplayImageOptionsUtil;
import android.content.Context;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 首页热点区
 * @author AeiouKong
 *
 */
public class MainHotListAdapter extends BaseAdapter{
	
	private List<MainHotListData> arrays;
	private LayoutInflater inflater;
	private ImageLoader imageLoader;
	private DisplayImageOptions options;
	private Context mContext;
	private int densityDpi;
	
	public MainHotListAdapter(Context mContext, List<MainHotListData> arrays,LayoutInflater inflater,int densityDpi){
		this.arrays = arrays;
		this.inflater = inflater;
		this.mContext = mContext;
		this.densityDpi = densityDpi;
		
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
	public View getView(int position, View convertView, ViewGroup parent) {

		HotListHolder hdholder = null;
		String imageUrl = arrays.get(position).getImagepath();
		String title = arrays.get(position).getTitle();  
		String zhengze = mContext.getString(R.string.expression_matches); // 正则表达式，用来判断消息内是否有表情
		SpannableString spannableString = ExpressionUtil.getExpressionString(
				mContext, title, zhengze, densityDpi);
		String createdate = arrays.get(position).getCreatedate();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			Date ddate = sdf.parse(createdate);
			SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd");
			createdate = dateFormat.format(ddate);
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (NullPointerException e) {
			e.printStackTrace();
		}
		if (convertView == null) {
			convertView  = inflater.inflate(R.layout.item_main_hotlist, null);
			
			hdholder = new HotListHolder();
			hdholder.iv_huodong = (ImageView) convertView.findViewById(R.id.iv_huodong);
			hdholder.tv_huodong = (TextView) convertView.findViewById(R.id.tv_huodong);
			hdholder.tv_hd_createtime = (TextView) convertView.findViewById(R.id.tv_hd_createtime);
			convertView.setTag(hdholder);
		} else {
			hdholder = (HotListHolder) convertView.getTag();
		}
		
		imageLoader.displayImage(imageUrl, hdholder.iv_huodong, options, null);
        hdholder.tv_huodong.setText(spannableString);
        hdholder.tv_hd_createtime.setText(createdate);
        
		return convertView;
	}
	
	static class HotListHolder {
		TextView tv_huodong;
		TextView tv_hd_createtime;
		ImageView iv_huodong;
	}

}

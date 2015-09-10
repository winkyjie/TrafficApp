package cn.fszt.trafficapp.adapter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

import android.content.Context;
import android.content.Intent;
import android.text.SpannableString;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import cn.fszt.trafficapp.R;
import cn.fszt.trafficapp.domain.RadioCommentListData;
import cn.fszt.trafficapp.widget.MyGridView2;
import cn.fszt.trafficapp.widget.MyListview2;
import cn.fszt.trafficapp.widget.expression.ExpressionUtil;
import cn.fszt.trafficapp.widget.photoview.DisplayImageOptionsUtil;
import cn.fszt.trafficapp.widget.photoview.ViewPagerActivity;

public class RadioSecretListAdapter extends BaseAdapter {
	private Context context;
	private List<RadioCommentListData> list;
	private LayoutInflater inflater;
	private int densityDpi;
	private ImageLoader imageLoader;
	private DisplayImageOptions options_head;
	private String urls[] = null;
	private String uid;
	private int visible;

	public RadioSecretListAdapter(Context con, List<RadioCommentListData> arrays, String uid, int densityDpi) {
		this.context = con;
		this.list = arrays;
		this.uid = uid;
		this.densityDpi = densityDpi;
		inflater = LayoutInflater.from(con);
		imageLoader = ImageLoader.getInstance();
		options_head = DisplayImageOptionsUtil.getOptions(R.drawable.default_head, R.drawable.default_head,
				R.drawable.default_image, new RoundedBitmapDisplayer(90));
	}

	@Override
	public int getCount() {
		return list == null ? 0 : list.size();
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public void setCircleVisibile(int enable) {
		if (enable == View.VISIBLE) {
			visible = View.VISIBLE;
		} else {
			visible = View.GONE;
		}
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		SecretLetterHolder holder = null;
		if (convertView == null) {
			holder = new SecretLetterHolder();
			convertView = inflater.inflate(R.layout.item_baoguang_list, null);
			holder.tv_baoliaoandbaoguang_nickname = (TextView) convertView
					.findViewById(R.id.tv_baoliaoandbaoguang_nickname);
			holder.tv_baoliaoandbaoguang_content = (TextView) convertView
					.findViewById(R.id.tv_baoliaoandbaoguang_content);
			holder.tv_baoliaoandbaoguang_createtime = (TextView) convertView
					.findViewById(R.id.tv_baoliaoandbaoguang_createtime);
			holder.iv_baoliaoandbaoguang_headimg = (ImageView) convertView
					.findViewById(R.id.iv_baoliaoandbaoguang_headimg);
			holder.gv_img = (MyGridView2) convertView.findViewById(R.id.gv_img);
			holder.imgAdapter = new MyGridViewAdapter(context);
			holder.iv_circle = (ImageView) convertView.findViewById(R.id.iv_circle);
			holder.iv_bg_good = (ImageView) convertView.findViewById(R.id.iv_bg_good);
			holder.iv_bg_comment = (ImageView) convertView.findViewById(R.id.iv_bg_comment);
			holder.iv_bg_good.setVisibility(View.GONE);
			holder.iv_bg_comment.setVisibility(View.GONE);
			convertView.setTag(holder);
		} else {
			holder = (SecretLetterHolder) convertView.getTag();
		}
		String date = list.get(position).getCreatedate();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			Date ddate = sdf.parse(date);
			SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd HH:mm");
			date = dateFormat.format(ddate);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		String zhengze = context.getString(R.string.expression_matches); // 正则表达式，用来判断消息内是否有表情
		SpannableString spannableString = ExpressionUtil.getExpressionString(context,
				list.get(position).getTextcontent(), zhengze, densityDpi);
		String imagepath_small = list.get(position).getImagepath_small();
		holder.tv_baoliaoandbaoguang_nickname.setText(list.get(position).getNickname());
		imageLoader.displayImage(list.get(position).getHeadimg(), holder.iv_baoliaoandbaoguang_headimg, options_head,
				null);
		holder.tv_baoliaoandbaoguang_content.setText(spannableString);
		holder.tv_baoliaoandbaoguang_createtime.setText(date);
		if (list.get(position).isVisiable()) {
			holder.iv_circle.setVisibility(visible); // 设置推送消息提醒
		}

		if (!imagepath_small.isEmpty()) {
			urls = imagepath_small.split(",");
			final Intent intent = new Intent(context, ViewPagerActivity.class);
			intent.putExtra("imgurl", urls);
			holder.imgAdapter.setUrls(imagepath_small);

			holder.gv_img.setAdapter(holder.imgAdapter);
			holder.gv_img.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					intent.putExtra("ID", position);
					context.startActivity(intent);
				}
			});
		}
		return convertView;
	}

	final class SecretLetterHolder {
		TextView tv_baoliaoandbaoguang_nickname;
		ImageView iv_baoliaoandbaoguang_headimg;
		TextView tv_baoliaoandbaoguang_content;
		TextView tv_baoliaoandbaoguang_createtime;
		MyGridView2 gv_img;
		MyGridViewAdapter imgAdapter;
		ImageView iv_circle;
		ImageView iv_bg_good;
		ImageView iv_bg_comment;
	}
}

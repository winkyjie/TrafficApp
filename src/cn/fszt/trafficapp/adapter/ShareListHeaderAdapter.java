package cn.fszt.trafficapp.adapter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

import cn.fszt.trafficapp.R;
import cn.fszt.trafficapp.domain.ShareListHeaderData;
import cn.fszt.trafficapp.util.Constant;
import cn.fszt.trafficapp.util.HttpUtil;
import cn.fszt.trafficapp.widget.expression.ExpressionUtil;
import cn.fszt.trafficapp.widget.msg.AppMsg;
import cn.fszt.trafficapp.widget.photoview.DisplayImageOptionsUtil;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.text.SpannableString;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 车友分享header来自节目互动同步的内容
 * @author AeiouKong
 *
 */
public class ShareListHeaderAdapter extends BaseAdapter{
	
	private Context mContext;
	private List<ShareListHeaderData> arrays;
	private LayoutInflater inflater;
	private ImageLoader imageLoader;
	private DisplayImageOptions options,options_head;
	private int densityDpi;
	private String deldetail_url, uid;
	AppMsg.Style style = AppMsg.STYLE_INFO;
	AppMsg appMsg;
	
	public ShareListHeaderAdapter(Context mContext,List<ShareListHeaderData> arrays,LayoutInflater inflater,
			int densityDpi, String deldetail_url, String uid){
		this.mContext = mContext;
		this.arrays = arrays;
		this.inflater = inflater;
		this.densityDpi = densityDpi;
		this.deldetail_url = deldetail_url;
		this.uid = uid;
		
		imageLoader = ImageLoader.getInstance();
		
		options_head = DisplayImageOptionsUtil.getOptions(
				R.drawable.default_head_sq, R.drawable.default_head_sq,R.drawable.default_image, new RoundedBitmapDisplayer(15));
		
		options = DisplayImageOptionsUtil.getOptions(
				R.drawable.ic_transparent, R.drawable.ic_transparent,R.drawable.ic_transparent);
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
		return Constant.LISTVIEWHEADER_ITEMID;
	}
	
	@Override
	public int getItemViewType(int position) {
		
		String connected_uid = arrays.get(position).getConnected_uid();
		// 非自己
		if (!connected_uid.equals(uid)) {
			return 1;
		}// 是自己
		else if (connected_uid.equals(uid)) {
			return 2;
		}
		return super.getItemViewType(position);
	}
	
	@Override
	public int getViewTypeCount() {
		return 3;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {

		HotListHolder hdholder = null;
		int type = getItemViewType(position);
		String imageUrl = arrays.get(position).getHeadimg();
		String content = arrays.get(position).getContent();
		String nickname = arrays.get(position).getNickname();
		String date = arrays.get(position).getCreatedate();
		String isdjpath = arrays.get(position).getIsdjpath();
		String istoppath = arrays.get(position).getIstoppath();
		String good = arrays.get(position).getLikecount();
		String reply = arrays.get(position).getReplycount();
		String hdmenuid = arrays.get(position).getHdmenuid();
		String zhengze = mContext.getString(R.string.expression_matches); // 正则表达式，用来判断消息内是否有表情
		SpannableString spannableString = ExpressionUtil.getExpressionString(
				mContext, "#"+hdmenuid+"# "+content, zhengze, densityDpi);
//		String c = "#"+hdmenuid+"#"+spannableString;
//		SpannableString s = new SpannableString(c);
//		if (c.contains("#") && getOccur(c, "#") > 1) {
//			int start = c.indexOf("#");
//			int end = c.lastIndexOf("#") + 1;
//			s.setSpan(new ForegroundColorSpan(mContext
//					.getResources().getColor(R.color.orange_bg)), start, end,
//					Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
//		}
		final String hdcommentid = arrays.get(position).getHdcommentid();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			Date ddate = sdf.parse(date);
			SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd HH:mm");
			date = dateFormat.format(ddate);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		if (convertView == null) {
			
			hdholder = new HotListHolder();
			switch (type) {
			case 1:
				convertView  = inflater.inflate(R.layout.item_sharelist_header, null);
				hdholder.iv_sharehead_headimg = (ImageView) convertView.findViewById(R.id.iv_sharehead_headimg);
				hdholder.iv_sharehead_dj = (ImageView) convertView.findViewById(R.id.iv_sharehead_dj);
				hdholder.iv_sharehead_top = (ImageView) convertView.findViewById(R.id.iv_sharehead_top);
				hdholder.tv_sharehead_content = (TextView) convertView.findViewById(R.id.tv_sharehead_content);
				hdholder.tv_sharehead_nickname = (TextView) convertView.findViewById(R.id.tv_sharehead_nickname);
				hdholder.tv_sharehead_createtime = (TextView) convertView.findViewById(R.id.tv_sharehead_createtime);
				hdholder.tv_sharehead_good = (TextView) convertView.findViewById(R.id.tv_sharehead_good);
				hdholder.tv_sharehead_inpinglun = (TextView) convertView.findViewById(R.id.tv_sharehead_inpinglun);
				hdholder.tv_sharehead_deldetail = (Button) convertView.findViewById(R.id.tv_sharehead_deldetail);
				break;
			case 2:
				convertView  = inflater.inflate(R.layout.item_sharelist_header, null);
				hdholder.iv_sharehead_headimg = (ImageView) convertView.findViewById(R.id.iv_sharehead_headimg);
				hdholder.iv_sharehead_dj = (ImageView) convertView.findViewById(R.id.iv_sharehead_dj);
				hdholder.iv_sharehead_top = (ImageView) convertView.findViewById(R.id.iv_sharehead_top);
				hdholder.tv_sharehead_content = (TextView) convertView.findViewById(R.id.tv_sharehead_content);
				hdholder.tv_sharehead_nickname = (TextView) convertView.findViewById(R.id.tv_sharehead_nickname);
				hdholder.tv_sharehead_createtime = (TextView) convertView.findViewById(R.id.tv_sharehead_createtime);
				hdholder.tv_sharehead_good = (TextView) convertView.findViewById(R.id.tv_sharehead_good);
				hdholder.tv_sharehead_inpinglun = (TextView) convertView.findViewById(R.id.tv_sharehead_inpinglun);
				hdholder.tv_sharehead_deldetail = (Button) convertView.findViewById(R.id.tv_sharehead_deldetail);
				break;
			}
			convertView.setTag(hdholder);
		} else {
			hdholder = (HotListHolder) convertView.getTag();
		}
		switch(type){
			case 1:
				imageLoader.displayImage(imageUrl, hdholder.iv_sharehead_headimg, options_head, null);
				imageLoader.displayImage(isdjpath, hdholder.iv_sharehead_dj, options, null);
				imageLoader.displayImage(istoppath, hdholder.iv_sharehead_top, options, null);
		        hdholder.tv_sharehead_content.setText(spannableString);
		        hdholder.tv_sharehead_nickname.setText(nickname);
		        hdholder.tv_sharehead_createtime.setText(date);
		        hdholder.tv_sharehead_good.setText(good);
		        hdholder.tv_sharehead_inpinglun.setText(reply);
				break;
			case 2:
				imageLoader.displayImage(imageUrl, hdholder.iv_sharehead_headimg, options_head, null);
				imageLoader.displayImage(isdjpath, hdholder.iv_sharehead_dj, options, null);
				imageLoader.displayImage(istoppath, hdholder.iv_sharehead_top, options, null);
		        hdholder.tv_sharehead_content.setText(spannableString);
		        hdholder.tv_sharehead_nickname.setText(nickname);
		        hdholder.tv_sharehead_createtime.setText(date);
		        hdholder.tv_sharehead_good.setText(good);
		        hdholder.tv_sharehead_inpinglun.setText(reply);
		        hdholder.tv_sharehead_deldetail.setVisibility(View.VISIBLE);
		        hdholder.tv_sharehead_deldetail.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						dialog(hdcommentid,position);
					}
				});
				break;
		}
        
		return convertView;
	}
	
	static class HotListHolder {
		TextView tv_sharehead_content;
		TextView tv_sharehead_nickname;
		TextView tv_sharehead_createtime;
		TextView tv_sharehead_good;
		TextView tv_sharehead_inpinglun;
		ImageView iv_sharehead_headimg;
		ImageView iv_sharehead_dj;
		ImageView iv_sharehead_top;
		Button tv_sharehead_deldetail;
	}
	
	private void dialog(final String hdcommentid, final int position) {
		AlertDialog.Builder builder = new Builder(mContext);
		builder.setMessage("确认删除吗?");
		builder.setTitle("提示");
		builder.setPositiveButton(
				mContext.getResources().getString(R.string.dialog_confirm_yes),
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();

						new Thread() {
							public void run() {
								JSONObject jsonObject;
								String resultString = HttpUtil
										.GetStringFromUrl(deldetail_url
												+ "&hdcommentid=" + hdcommentid);
								try {
									if (resultString != null) {
										jsonObject = new JSONObject(
												resultString);
										String code = jsonObject
												.getString("code");
										if (code.equals("200")) {
											handler.obtainMessage(1, position)
													.sendToTarget();
										} else {
											handler.sendEmptyMessage(0);
										}
									} else {
										handler.sendEmptyMessage(0);
									}
								} catch (JSONException e) {
									handler.sendEmptyMessage(0);
									e.printStackTrace();
								}
							}
						}.start();
					}
				});
		builder.setNegativeButton(
				mContext.getResources().getString(R.string.dialog_no),
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
		builder.create().show();
	}

	
	Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 0:
				showMsg(mContext.getResources().getString(R.string.del_fail),
						"info", "bottom");
				break;
			case 1:
				int position = (Integer) msg.obj;
				arrays.remove(position);
				notifyDataSetChanged();
				showMsg(mContext.getResources().getString(R.string.del_success),
						"info", "bottom");
				break;
			case 2:
				String message = (String) msg.obj;
				if (message != null) {
					showMsg(message, "info", "bottom");
				}
				break;
			}
		}
	};

	private void showMsg(String msg, String styletype, String gravity) {

		if (styletype.equals("alert")) {
			style = AppMsg.STYLE_ALERT;
		} else if (styletype.equals("info")) {
			style = AppMsg.STYLE_INFO;
		}

		appMsg = AppMsg.makeText((Activity) mContext, msg, style);

		if (gravity.equals("bottom")) {
			appMsg.setLayoutGravity(Gravity.BOTTOM);
		} else if (gravity.equals("top")) {
		}
		appMsg.show();
	}
	
	/**
	 * 获取某字符出现次数
	 * 
	 * @param src
	 * @param find
	 * @return
	 */
	public int getOccur(String src, String find) {
		int o = 0;
		int index = -1;
		while ((index = src.indexOf(find, index)) > -1) {
			++index;
			++o;
		}
		return o;
	}
}

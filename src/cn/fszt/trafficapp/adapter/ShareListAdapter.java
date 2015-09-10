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
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.text.SpannableString;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cn.fszt.trafficapp.R;
import cn.fszt.trafficapp.domain.BaoliaoAndBaoguangData;
import cn.fszt.trafficapp.util.HttpUtil;
import cn.fszt.trafficapp.widget.MyGridView2;
import cn.fszt.trafficapp.widget.MyListview2;
import cn.fszt.trafficapp.widget.expression.ExpressionUtil;
import cn.fszt.trafficapp.widget.msg.AppMsg;
import cn.fszt.trafficapp.widget.photoview.DisplayImageOptionsUtil;
import cn.fszt.trafficapp.widget.photoview.ViewPagerActivity;

/**
 * 车友分享 适配器
 * 
 * @author AeiouKong
 *
 */
@SuppressLint("InflateParams")
public class ShareListAdapter extends BaseAdapter {
	private Context mContext;
	private List<BaoliaoAndBaoguangData> arrays;
	private LayoutInflater inflater;
	private ImageLoader imageLoader;
	private DisplayImageOptions options_head,options;
	private int densityDpi;
	AppMsg.Style style = AppMsg.STYLE_INFO;
	AppMsg appMsg;
	private String deldetail_url, uid;

	public ShareListAdapter(Context mContext,
			List<BaoliaoAndBaoguangData> arrays, LayoutInflater inflater,
			int densityDpi, String deldetail_url, String uid) {

		this.mContext = mContext;
		this.arrays = arrays;
		this.inflater = inflater;
		this.densityDpi = densityDpi;
		this.deldetail_url = deldetail_url;
		this.uid = uid;

		imageLoader = ImageLoader.getInstance();

		options_head = DisplayImageOptionsUtil.getOptions(
				R.drawable.default_head_sq, R.drawable.default_head_sq,
				R.drawable.default_image, new RoundedBitmapDisplayer(15));
		
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
		return 0;
	}

	@Override
	public int getItemViewType(int position) {

		String imagepath = arrays.get(position).getImagepath();
		String connected_uid = arrays.get(position).getConnected_uid();

		if (!"".equals(imagepath)) {
			// 有图片非自己
			if (!connected_uid.equals(uid)) {
				return 1;
			}// 有图片是自己
			else {
				return 2;
			}
		} else{
			// 没有图片非自己
			if (!connected_uid.equals(uid)) {
				return 3;
			}// 没有图片是自己
			else {
				return 4;
			}
		}
	}

	@Override
	public int getViewTypeCount() {
		return 10;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		BaoliaoandbaoguangHolder mholder = null;
		int type = getItemViewType(position);
		String[] urls = null;
		String imagepath = arrays.get(position).getImagepath();
		String imagepath_small = arrays.get(position).getImagepath_small();
		String nickname = arrays.get(position).getNickname();
		String replynum = arrays.get(position).getReplycount();
		String likenum = arrays.get(position).getLikecount();
		String imageUrl_head = arrays.get(position).getHeadimg();
		String isdjpath = arrays.get(position).getIsdjpath();
		String istoppath = arrays.get(position).getIstoppath();
		String date = arrays.get(position).getCreatetime();
		final String stcollectid = arrays.get(position).getStcollectid();
		String zhengze = mContext.getString(R.string.expression_matches); // 正则表达式，用来判断消息内是否有表情
		String content = arrays.get(position).getContent();
		SpannableString spannableString = ExpressionUtil.getExpressionString(
				mContext, content, zhengze, densityDpi);
//		if (content.contains("#") && getOccur(content, "#") > 1) {
//			int start = content.indexOf("#");
//			int end = content.lastIndexOf("#") + 1;
//			spannableString.setSpan(new ForegroundColorSpan(mContext
//					.getResources().getColor(R.color.orange_bg)), start, end,
//					Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
//		}
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			Date ddate = sdf.parse(date);
			SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd HH:mm");
			date = dateFormat.format(ddate);
		} catch (ParseException e) {
			e.printStackTrace();
		}

		if (convertView == null) {
			mholder = new BaoliaoandbaoguangHolder();
			switch (type) {
			case 1:
				convertView = inflater.inflate(R.layout.item_baoguang_list,
						null);
				mholder.tv_baoliaoandbaoguang_nickname = (TextView) convertView
						.findViewById(R.id.tv_baoliaoandbaoguang_nickname);
				mholder.tv_baoliaoandbaoguang_content = (TextView) convertView
						.findViewById(R.id.tv_baoliaoandbaoguang_content);
				mholder.tv_baoliaoandbaoguang_createtime = (TextView) convertView
						.findViewById(R.id.tv_baoliaoandbaoguang_createtime);
				mholder.iv_baoliaoandbaoguang_headimg = (ImageView) convertView
						.findViewById(R.id.iv_baoliaoandbaoguang_headimg);
				mholder.iv_baoliaoandbaoguang_top = (ImageView) convertView
						.findViewById(R.id.iv_baoliaoandbaoguang_top);
				mholder.gv_img = (MyGridView2) convertView
						.findViewById(R.id.gv_img);
				mholder.imgAdapter = new MyGridViewAdapter(mContext);
				mholder.list_pl = (MyListview2) convertView
						.findViewById(R.id.list_pl);
				mholder.shareAdapter = new ShareListReplyAdapter(mContext,zhengze,densityDpi);
				// TODO 举报或者置顶的时候要用长按
				mholder.rl_baoliaoandbaoguang = (RelativeLayout) convertView
						.findViewById(R.id.rl_baoliaoandbaoguang);
				mholder.tv_bg_inpinglun = (TextView) convertView
						.findViewById(R.id.tv_bg_inpinglun);
				mholder.tv_bg_good = (TextView) convertView
						.findViewById(R.id.tv_bg_good);
				mholder.tv_baoliaoandbaoguang_deldetail = (Button) convertView
						.findViewById(R.id.tv_baoliaoandbaoguang_deldetail);
				mholder.iv_bg_dj = (ImageView) convertView
				.findViewById(R.id.iv_bg_dj);
				break;
			case 2:
				convertView = inflater.inflate(R.layout.item_baoguang_list,
						null);
				mholder.tv_baoliaoandbaoguang_nickname = (TextView) convertView
						.findViewById(R.id.tv_baoliaoandbaoguang_nickname);
				mholder.tv_baoliaoandbaoguang_content = (TextView) convertView
						.findViewById(R.id.tv_baoliaoandbaoguang_content);
				mholder.tv_baoliaoandbaoguang_createtime = (TextView) convertView
						.findViewById(R.id.tv_baoliaoandbaoguang_createtime);
				mholder.iv_baoliaoandbaoguang_headimg = (ImageView) convertView
						.findViewById(R.id.iv_baoliaoandbaoguang_headimg);
				mholder.iv_baoliaoandbaoguang_top = (ImageView) convertView
						.findViewById(R.id.iv_baoliaoandbaoguang_top);
				mholder.gv_img = (MyGridView2) convertView
						.findViewById(R.id.gv_img);
				mholder.imgAdapter = new MyGridViewAdapter(mContext);
				mholder.list_pl = (MyListview2) convertView
						.findViewById(R.id.list_pl);
				mholder.shareAdapter = new ShareListReplyAdapter(mContext,zhengze,densityDpi);
				mholder.rl_baoliaoandbaoguang = (RelativeLayout) convertView
						.findViewById(R.id.rl_baoliaoandbaoguang);
				mholder.tv_bg_inpinglun = (TextView) convertView
						.findViewById(R.id.tv_bg_inpinglun);
				mholder.tv_bg_good = (TextView) convertView
						.findViewById(R.id.tv_bg_good);
				mholder.tv_baoliaoandbaoguang_deldetail = (Button) convertView
						.findViewById(R.id.tv_baoliaoandbaoguang_deldetail);
				mholder.iv_bg_dj = (ImageView) convertView
				.findViewById(R.id.iv_bg_dj);
				break;
			case 3:
				convertView = inflater.inflate(R.layout.item_baoguang_list,
						null);
				mholder.tv_baoliaoandbaoguang_nickname = (TextView) convertView
						.findViewById(R.id.tv_baoliaoandbaoguang_nickname);
				mholder.tv_baoliaoandbaoguang_content = (TextView) convertView
						.findViewById(R.id.tv_baoliaoandbaoguang_content);
				mholder.list_pl = (MyListview2) convertView
						.findViewById(R.id.list_pl);
				mholder.shareAdapter = new ShareListReplyAdapter(mContext,zhengze,densityDpi);
				mholder.tv_baoliaoandbaoguang_createtime = (TextView) convertView
						.findViewById(R.id.tv_baoliaoandbaoguang_createtime);
				mholder.iv_baoliaoandbaoguang_headimg = (ImageView) convertView
						.findViewById(R.id.iv_baoliaoandbaoguang_headimg);
				mholder.iv_baoliaoandbaoguang_top = (ImageView) convertView
						.findViewById(R.id.iv_baoliaoandbaoguang_top);
				mholder.rl_baoliaoandbaoguang = (RelativeLayout) convertView
						.findViewById(R.id.rl_baoliaoandbaoguang);
				mholder.tv_bg_inpinglun = (TextView) convertView
						.findViewById(R.id.tv_bg_inpinglun);
				mholder.tv_bg_good = (TextView) convertView
						.findViewById(R.id.tv_bg_good);
				mholder.tv_baoliaoandbaoguang_deldetail = (Button) convertView
						.findViewById(R.id.tv_baoliaoandbaoguang_deldetail);
				mholder.iv_bg_dj = (ImageView) convertView
				.findViewById(R.id.iv_bg_dj);
				break;
			case 4:
				convertView = inflater.inflate(R.layout.item_baoguang_list,
						null);
				mholder.tv_baoliaoandbaoguang_nickname = (TextView) convertView
						.findViewById(R.id.tv_baoliaoandbaoguang_nickname);
				mholder.tv_baoliaoandbaoguang_content = (TextView) convertView
						.findViewById(R.id.tv_baoliaoandbaoguang_content);
				mholder.list_pl = (MyListview2) convertView
						.findViewById(R.id.list_pl);
				mholder.shareAdapter = new ShareListReplyAdapter(mContext,zhengze,densityDpi);
				mholder.tv_baoliaoandbaoguang_createtime = (TextView) convertView
						.findViewById(R.id.tv_baoliaoandbaoguang_createtime);
				mholder.iv_baoliaoandbaoguang_headimg = (ImageView) convertView
						.findViewById(R.id.iv_baoliaoandbaoguang_headimg);
				mholder.iv_baoliaoandbaoguang_top = (ImageView) convertView
						.findViewById(R.id.iv_baoliaoandbaoguang_top);
				mholder.rl_baoliaoandbaoguang = (RelativeLayout) convertView
						.findViewById(R.id.rl_baoliaoandbaoguang);
				mholder.tv_bg_inpinglun = (TextView) convertView
						.findViewById(R.id.tv_bg_inpinglun);
				mholder.tv_bg_good = (TextView) convertView
						.findViewById(R.id.tv_bg_good);
				mholder.tv_baoliaoandbaoguang_deldetail = (Button) convertView
						.findViewById(R.id.tv_baoliaoandbaoguang_deldetail);
				mholder.iv_bg_dj = (ImageView) convertView
				.findViewById(R.id.iv_bg_dj);
				break;
			}
			convertView.setTag(mholder);
		} else {
			mholder = (BaoliaoandbaoguangHolder) convertView.getTag();
		}

		switch (type) {
		case 1:
			imageLoader.displayImage(imageUrl_head,
					mholder.iv_baoliaoandbaoguang_headimg, options_head, null);
			imageLoader.displayImage(isdjpath,
					mholder.iv_bg_dj, options, null);
			imageLoader.displayImage(istoppath,
					mholder.iv_baoliaoandbaoguang_top, options, null);
			mholder.tv_baoliaoandbaoguang_nickname.setText(nickname);
			mholder.tv_baoliaoandbaoguang_createtime.setText(date);
			mholder.tv_bg_inpinglun.setText(replynum);
			mholder.tv_bg_good.setText(likenum);
			mholder.tv_baoliaoandbaoguang_content.setText(spannableString);
			
			if (imagepath_small != null) {
				urls = imagepath.split(",");
				final Intent intent = new Intent(mContext,
						ViewPagerActivity.class);
				intent.putExtra("imgurl", urls);
				mholder.imgAdapter.setUrls(imagepath_small);
				mholder.gv_img.setAdapter(mholder.imgAdapter);
				mholder.gv_img
						.setOnItemClickListener(new OnItemClickListener() {
							@Override
							public void onItemClick(AdapterView<?> parent,
									View view, int position, long id) {
								intent.putExtra("ID", position);
								mContext.startActivity(intent);
							}
						});
			}
			mholder.shareAdapter.setReplydetailList(arrays, position);
			mholder.list_pl.setAdapter(mholder.shareAdapter);
			break;
		case 2:
			imageLoader.displayImage(imageUrl_head,
					mholder.iv_baoliaoandbaoguang_headimg, options_head, null);
			imageLoader.displayImage(isdjpath,
					mholder.iv_bg_dj, options, null);
			imageLoader.displayImage(istoppath,
					mholder.iv_baoliaoandbaoguang_top, options, null);
			mholder.tv_baoliaoandbaoguang_nickname.setText(nickname);
			mholder.tv_baoliaoandbaoguang_createtime.setText(date);
			mholder.tv_bg_inpinglun.setText(replynum);
			mholder.tv_bg_good.setText(likenum);
			mholder.tv_baoliaoandbaoguang_content.setText(spannableString);
			
			if (imagepath_small != null) {
				urls = imagepath.split(",");
				final Intent intent = new Intent(mContext,
						ViewPagerActivity.class);
				intent.putExtra("imgurl", urls);
				mholder.imgAdapter.setUrls(imagepath_small);
				mholder.gv_img.setAdapter(mholder.imgAdapter);
				mholder.gv_img
						.setOnItemClickListener(new OnItemClickListener() {
							@Override
							public void onItemClick(AdapterView<?> parent,
									View view, int position, long id) {
								intent.putExtra("ID", position);
								mContext.startActivity(intent);
							}
						});
			}
			mholder.tv_baoliaoandbaoguang_deldetail.setVisibility(View.VISIBLE);
    		mholder.tv_baoliaoandbaoguang_deldetail.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					dialog(stcollectid,position);
				}
			});
    		mholder.shareAdapter.setReplydetailList(arrays, position);
			mholder.list_pl.setAdapter(mholder.shareAdapter);
			break;
		case 3:
			imageLoader.displayImage(imageUrl_head,
					mholder.iv_baoliaoandbaoguang_headimg, options_head, null);
			imageLoader.displayImage(isdjpath,
					mholder.iv_bg_dj, options, null);
			imageLoader.displayImage(istoppath,
					mholder.iv_baoliaoandbaoguang_top, options, null);
			mholder.tv_baoliaoandbaoguang_nickname.setText(nickname);
			mholder.tv_baoliaoandbaoguang_createtime.setText(date);
			mholder.tv_bg_inpinglun.setText(replynum);
			mholder.tv_bg_good.setText(likenum);
			mholder.tv_baoliaoandbaoguang_content.setText(spannableString);
			mholder.shareAdapter.setReplydetailList(arrays, position);
			mholder.list_pl.setAdapter(mholder.shareAdapter);
			break;
		case 4:
			imageLoader.displayImage(imageUrl_head,
					mholder.iv_baoliaoandbaoguang_headimg, options_head, null);
			imageLoader.displayImage(isdjpath,
					mholder.iv_bg_dj, options, null);
			imageLoader.displayImage(istoppath,
					mholder.iv_baoliaoandbaoguang_top, options, null);
			mholder.tv_baoliaoandbaoguang_nickname.setText(nickname);
			mholder.tv_baoliaoandbaoguang_createtime.setText(date);
			mholder.tv_bg_inpinglun.setText(replynum);
			mholder.tv_bg_good.setText(likenum);
			mholder.tv_baoliaoandbaoguang_content.setText(spannableString);
			mholder.tv_baoliaoandbaoguang_deldetail.setVisibility(View.VISIBLE);
    		mholder.tv_baoliaoandbaoguang_deldetail.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					dialog(stcollectid,position);
				}
			});
    		mholder.shareAdapter.setReplydetailList(arrays, position);
			mholder.list_pl.setAdapter(mholder.shareAdapter);
			break;

		}
		return convertView;
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

	static class BaoliaoandbaoguangHolder {
		TextView tv_baoliaoandbaoguang_nickname;
		ImageView iv_baoliaoandbaoguang_headimg;
		ImageView iv_baoliaoandbaoguang_top;
		TextView tv_baoliaoandbaoguang_content;
		TextView tv_baoliaoandbaoguang_createtime;
		RelativeLayout rl_baoliaoandbaoguang;
		TextView tv_bg_inpinglun;
		TextView tv_bg_good;
		Button tv_baoliaoandbaoguang_deldetail;
		ImageView iv_bg_dj;
		MyGridView2 gv_img;
		MyListview2 list_pl;
		MyGridViewAdapter imgAdapter;
		ShareListReplyAdapter shareAdapter;
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

	private void dialog(final String stcollectid, final int position) {
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
												+ "&stcollectid=" + stcollectid);
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
}

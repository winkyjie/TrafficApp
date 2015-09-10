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
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Handler;
import android.text.SpannableString;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import cn.fszt.trafficapp.R;
import cn.fszt.trafficapp.domain.RadioCommentListData;
import cn.fszt.trafficapp.ui.activity.RadioCommentEditActivity;
import cn.fszt.trafficapp.util.HttpUtil;
import cn.fszt.trafficapp.widget.MyGridView2;
import cn.fszt.trafficapp.widget.MyListview2;
import cn.fszt.trafficapp.widget.expression.ExpressionUtil;
import cn.fszt.trafficapp.widget.msg.AppMsg;
import cn.fszt.trafficapp.widget.photoview.DisplayImageOptionsUtil;
import cn.fszt.trafficapp.widget.photoview.ViewPagerActivity;

/**
 * 节目互动每个节目评论列表界面Adapter
 * 
 * @author AeiouKong
 *
 */
public class RadioCommentListAdapter extends BaseAdapter {

	private Context mContext;
	private List<RadioCommentListData> arrays;
	private LayoutInflater inflater;
	private String delmpl_url, reportychat_url, id, uid;
	private MediaPlayer mPlayer = null;
	private int densityDpi;
	private ImageLoader imageLoader;
	private DisplayImageOptions  options_head,options_dj;
	AppMsg.Style style = AppMsg.STYLE_INFO;
	AppMsg appMsg;
	private static final int REPORT = 1;
	private static final int SUBMITPINGLUN = 2;

	public RadioCommentListAdapter(Context mContext, List<RadioCommentListData> arrays,
			LayoutInflater inflater, String delmpl_url, String reportychat_url,
			String uid, int densityDpi, String id) {
		this.mContext = mContext;
		this.arrays = arrays;
		this.inflater = inflater;
		this.delmpl_url = delmpl_url;
		this.reportychat_url = reportychat_url;
		this.uid = uid;
		this.densityDpi = densityDpi;
		this.id = id;
		mPlayer = new MediaPlayer();

		imageLoader = ImageLoader.getInstance();

		options_head = DisplayImageOptionsUtil.getOptions(
				R.drawable.default_head, R.drawable.default_head,
				R.drawable.default_image, new RoundedBitmapDisplayer(90));
		options_dj = DisplayImageOptionsUtil.getOptions(
				R.drawable.ic_transparent, R.drawable.ic_transparent,
				R.drawable.ic_transparent);
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
		} else {
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
		final String hdcommentid = arrays.get(position).getHdcommentid();
		String content = arrays.get(position).getContent();
		String zhengze = mContext.getString(R.string.expression_matches); // 正则表达式，用来判断消息内是否有表情
		SpannableString spannableString = ExpressionUtil.getExpressionString(
				mContext, content, zhengze, densityDpi);
		String date = arrays.get(position).getCreatedate();
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
				mholder.radioAdapter = new RadioCommentListReplyAdapter(mContext,zhengze,densityDpi);
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
				mholder.radioAdapter = new RadioCommentListReplyAdapter(mContext,zhengze,densityDpi);
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
				mholder.radioAdapter = new RadioCommentListReplyAdapter(mContext,zhengze,densityDpi);
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
				mholder.radioAdapter = new RadioCommentListReplyAdapter(mContext,zhengze,densityDpi);
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
					mholder.iv_bg_dj, options_dj, null);
			imageLoader.displayImage(istoppath,
					mholder.iv_baoliaoandbaoguang_top, options_dj, null);
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
			mholder.radioAdapter.setReplydetailList(arrays, position);
			mholder.list_pl.setAdapter(mholder.radioAdapter);
			break;
		case 2:
			imageLoader.displayImage(imageUrl_head,
					mholder.iv_baoliaoandbaoguang_headimg, options_head, null);
			imageLoader.displayImage(isdjpath,
					mholder.iv_bg_dj, options_dj, null);
			imageLoader.displayImage(istoppath,
					mholder.iv_baoliaoandbaoguang_top, options_dj, null);
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
					delDialog(hdcommentid,position);
				}
			});
    		mholder.radioAdapter.setReplydetailList(arrays, position);
			mholder.list_pl.setAdapter(mholder.radioAdapter);
			break;
		case 3:
			imageLoader.displayImage(imageUrl_head,
					mholder.iv_baoliaoandbaoguang_headimg, options_head, null);
			imageLoader.displayImage(isdjpath,
					mholder.iv_bg_dj, options_dj, null);
			imageLoader.displayImage(istoppath,
					mholder.iv_baoliaoandbaoguang_top, options_dj, null);
			mholder.tv_baoliaoandbaoguang_nickname.setText(nickname);
			mholder.tv_baoliaoandbaoguang_createtime.setText(date);
			mholder.tv_bg_inpinglun.setText(replynum);
			mholder.tv_bg_good.setText(likenum);
			mholder.tv_baoliaoandbaoguang_content.setText(spannableString);
			mholder.radioAdapter.setReplydetailList(arrays, position);
			mholder.list_pl.setAdapter(mholder.radioAdapter);
			break;
		case 4:
			imageLoader.displayImage(imageUrl_head,
					mholder.iv_baoliaoandbaoguang_headimg, options_head, null);
			imageLoader.displayImage(isdjpath,
					mholder.iv_bg_dj, options_dj, null);
			imageLoader.displayImage(istoppath,
					mholder.iv_baoliaoandbaoguang_top, options_dj, null);
			mholder.tv_baoliaoandbaoguang_nickname.setText(nickname);
			mholder.tv_baoliaoandbaoguang_createtime.setText(date);
			mholder.tv_bg_inpinglun.setText(replynum);
			mholder.tv_bg_good.setText(likenum);
			mholder.tv_baoliaoandbaoguang_content.setText(spannableString);
			mholder.tv_baoliaoandbaoguang_deldetail.setVisibility(View.VISIBLE);
    		mholder.tv_baoliaoandbaoguang_deldetail.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					delDialog(hdcommentid,position);
				}
			});
    		mholder.radioAdapter.setReplydetailList(arrays, position);
			mholder.list_pl.setAdapter(mholder.radioAdapter);
			break;


		}
		return convertView;
	}

	Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 1:
				int position = (Integer) msg.obj;
				arrays.remove(position);
				notifyDataSetChanged();
				showMsg(mContext.getResources().getString(R.string.del_success),
						"info", "bottom");
				break;
			case 0:
				showMsg(mContext.getResources().getString(R.string.del_fail),
						"info", "bottom");
				break;
			case 2:
				showMsg(mContext.getResources().getString(
						R.string.report_success), "info", "bottom");
				break;
			case 3:
				showMsg(mContext.getResources().getString(R.string.report_fail),
						"info", "bottom");
				break;
			}
		}
	};

	/**
	 * 删除评论对话框
	 * 
	 * @param hdcommentid
	 * @param position
	 */
	private void delDialog(final String hdcommentid, final int position) {
		AlertDialog.Builder builder = new Builder(mContext);
		builder.setMessage("确认删除吗?");
		builder.setTitle("提示");
		builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				new Thread() {
					public void run() {
						JSONObject jsonObject;
						String resultString = HttpUtil
								.GetStringFromUrl(delmpl_url + hdcommentid);
						try {
							if (resultString != null) {
								jsonObject = new JSONObject(resultString);
								String code = jsonObject.getString("code");
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
		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		builder.create().show();
	}

//	private void reportdialog(final String hdcommentid) {
//		AlertDialog.Builder builder = new Builder(mContext);
//		builder.setMessage(mContext.getResources().getString(
//				R.string.report_tips));
//		builder.setTitle("提示");
//		builder.setPositiveButton(
//				mContext.getResources().getString(R.string.dialog_confirm_yes),
//				new DialogInterface.OnClickListener() {
//					@Override
//					public void onClick(DialogInterface dialog, int which) {
//						dialog.dismiss();
//						new Thread() {
//							public void run() {
//								JSONObject jsonObject;
//								String resultString = HttpUtil
//										.GetStringFromUrl(reportychat_url
//												+ hdcommentid);
//								try {
//									if (resultString != null) {
//										jsonObject = new JSONObject(
//												resultString);
//										String code = jsonObject
//												.getString("code");
//										if (code.equals("200")) {
//											handler.sendEmptyMessage(2);
//										} else {
//											handler.sendEmptyMessage(3);
//										}
//									} else {
//										handler.sendEmptyMessage(3);
//									}
//								} catch (JSONException e) {
//									handler.sendEmptyMessage(3);
//									e.printStackTrace();
//								}
//							}
//						}.start();
//					}
//				});
//		builder.setNegativeButton(
//				mContext.getResources().getString(R.string.dialog_no),
//				new DialogInterface.OnClickListener() {
//					@Override
//					public void onClick(DialogInterface dialog, int which) {
//						dialog.dismiss();
//					}
//				});
//		builder.create().show();
//	}

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
	 * 跳转发评论界面
	 * 
	 * @param nickname
	 */
	private void reply(String nickname) {
		Intent intent = new Intent(mContext, RadioCommentEditActivity.class);
		intent.putExtra("nickname", nickname);
		intent.putExtra("id", id);
		intent.putExtra("islive", "1"); // 0=直播，1=点播
		((Activity) mContext).startActivityForResult(intent, SUBMITPINGLUN);
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
		RadioCommentListReplyAdapter radioAdapter;
	}

}

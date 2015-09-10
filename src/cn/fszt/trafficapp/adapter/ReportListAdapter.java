package cn.fszt.trafficapp.adapter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
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
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import cn.fszt.trafficapp.R;
import cn.fszt.trafficapp.domain.BaoliaoAndBaoguangData;
import cn.fszt.trafficapp.util.Constant;
import cn.fszt.trafficapp.util.HttpUtil;
import cn.fszt.trafficapp.util.ShareCountUtil;
import cn.fszt.trafficapp.widget.MyGridView2;
import cn.fszt.trafficapp.widget.msg.AppMsg;
import cn.fszt.trafficapp.widget.photoview.DisplayImageOptionsUtil;
import cn.fszt.trafficapp.widget.photoview.ViewPagerActivity;
import cn.fszt.trafficapp.widget.sharesdk.onekeyshare.OnekeyShare;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;

/**
 * 路况信息-车友报料 适配器
 * 
 * @author AeiouKong
 *
 */
public class ReportListAdapter extends BaseAdapter {
	private Context mContext;
	private List<BaoliaoAndBaoguangData> arrays;
	private LayoutInflater inflater;
	private ImageLoader imageLoader;
	private DisplayImageOptions options, options_head;
	private int densityDpi;
	AppMsg.Style style = AppMsg.STYLE_INFO;
	AppMsg appMsg;
	private MediaPlayer mPlayer = null;
	private String delbl_url, uid, api_url;
	int int_num;

	public ReportListAdapter(Context mContext,
			List<BaoliaoAndBaoguangData> arrays, LayoutInflater inflater,
			int densityDpi, String uid, String delbl_url) {
		this.mContext = mContext;
		this.arrays = arrays;
		this.inflater = inflater;
		this.densityDpi = densityDpi;
		this.uid = uid;
		this.delbl_url = delbl_url;
		mPlayer = new MediaPlayer();
		api_url = mContext.getString(R.string.api_url);
		imageLoader = ImageLoader.getInstance();

		options = DisplayImageOptionsUtil.getOptions(R.drawable.ic_transparent,
				R.drawable.ic_transparent, R.drawable.ic_transparent);

		options_head = DisplayImageOptionsUtil.getOptions(
				R.drawable.default_head, R.drawable.default_head,
				R.drawable.default_image, new RoundedBitmapDisplayer(90));
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
		String videopath = arrays.get(position).getVideopath();
		String connected_uid = arrays.get(position).getConnected_uid();

		if (!"".equals(videopath)) {
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
		} else {
			if (!"".equals(imagepath)) {
				// 有图片非自己
				if (!connected_uid.equals(uid)) {
					return 5;
				}// 有图片是自己
				else {
					return 6;
				}
			} else {
				// 没有图片非自己
				if (!connected_uid.equals(uid)) {
					return 7;
				}// 没有图片是自己
				else {
					return 8;
				}
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
		final String transmiturl = arrays.get(position).getTransmiturl();
		final String content = arrays.get(position).getContent();
		String[] urls = null;
		final String imagepath = arrays.get(position).getImagepath();
		String isdjpath = arrays.get(position).getIsdjpath();
		final String likenum = arrays.get(position).getLikecount();
		String likecount = "(" + likenum + ")";
		final String videopath = arrays.get(position).getVideopath();
		String nickname = arrays.get(position).getNickname();
		String imageUrl_head = arrays.get(position).getHeadimg();
		String istoppath = arrays.get(position).getIstoppath();
		String date = arrays.get(position).getCreatetime();
		final String stcollectid = arrays.get(position).getStcollectid();
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
				convertView = inflater.inflate(
						R.layout.item_baoliaoandbaoguang_list, null);
				mholder.tv_baoliaoandbaoguang_nickname = (TextView) convertView
						.findViewById(R.id.tv_baoliaoandbaoguang_nickname);
				mholder.ll_bl_share = (LinearLayout) convertView
						.findViewById(R.id.ll_bl_share);
				mholder.ll_bl_good = (LinearLayout) convertView
						.findViewById(R.id.ll_bl_good);
				mholder.tv_bl_good = (TextView) convertView
						.findViewById(R.id.tv_bl_good);
				mholder.tv_baoliaoandbaoguang_content = (TextView) convertView
						.findViewById(R.id.tv_baoliaoandbaoguang_content);
				mholder.tv_baoliaoandbaoguang_createtime = (TextView) convertView
						.findViewById(R.id.tv_baoliaoandbaoguang_createtime);
				mholder.iv_baoliaoandbaoguang_headimg = (ImageView) convertView
						.findViewById(R.id.iv_baoliaoandbaoguang_headimg);
				mholder.iv_baoliaoandbaoguang_top = (ImageView) convertView
						.findViewById(R.id.iv_baoliaoandbaoguang_top);
				mholder.rl_baoliaoandbaoguang = (RelativeLayout) convertView
						.findViewById(R.id.rl_baoliaoandbaoguang);
				mholder.tv_baoliaoandbaoguang_deldetail = (Button) convertView
						.findViewById(R.id.tv_baoliaoandbaoguang_deldetail);
				mholder.gv_img = (MyGridView2) convertView
						.findViewById(R.id.gv_img);
				mholder.imgAdapter = new MyGridViewAdapter(mContext);
				mholder.iv_baoliaoandbaoguang_laba = (ImageView) convertView
						.findViewById(R.id.iv_baoliaoandbaoguang_laba);
				mholder.iv_baoliaoandbaoguang_dj = (ImageView) convertView
						.findViewById(R.id.iv_baoliaoandbaoguang_dj);
				convertView.setTag(mholder);
				break;
			case 2:
				convertView = inflater.inflate(
						R.layout.item_baoliaoandbaoguang_list, null);
				mholder.tv_baoliaoandbaoguang_nickname = (TextView) convertView
						.findViewById(R.id.tv_baoliaoandbaoguang_nickname);
				mholder.ll_bl_share = (LinearLayout) convertView
						.findViewById(R.id.ll_bl_share);
				mholder.ll_bl_good = (LinearLayout) convertView
						.findViewById(R.id.ll_bl_good);
				mholder.tv_bl_good = (TextView) convertView
						.findViewById(R.id.tv_bl_good);
				mholder.tv_baoliaoandbaoguang_content = (TextView) convertView
						.findViewById(R.id.tv_baoliaoandbaoguang_content);
				mholder.tv_baoliaoandbaoguang_createtime = (TextView) convertView
						.findViewById(R.id.tv_baoliaoandbaoguang_createtime);
				mholder.iv_baoliaoandbaoguang_headimg = (ImageView) convertView
						.findViewById(R.id.iv_baoliaoandbaoguang_headimg);
				mholder.iv_baoliaoandbaoguang_top = (ImageView) convertView
						.findViewById(R.id.iv_baoliaoandbaoguang_top);
				mholder.rl_baoliaoandbaoguang = (RelativeLayout) convertView
						.findViewById(R.id.rl_baoliaoandbaoguang);
				mholder.tv_baoliaoandbaoguang_deldetail = (Button) convertView
						.findViewById(R.id.tv_baoliaoandbaoguang_deldetail);
				mholder.gv_img = (MyGridView2) convertView
						.findViewById(R.id.gv_img);
				mholder.imgAdapter = new MyGridViewAdapter(mContext);
				mholder.iv_baoliaoandbaoguang_laba = (ImageView) convertView
						.findViewById(R.id.iv_baoliaoandbaoguang_laba);
				mholder.iv_baoliaoandbaoguang_dj = (ImageView) convertView
						.findViewById(R.id.iv_baoliaoandbaoguang_dj);
				convertView.setTag(mholder);
				break;
			case 3:
				convertView = inflater.inflate(
						R.layout.item_baoliaoandbaoguang_list, null);
				mholder.tv_baoliaoandbaoguang_nickname = (TextView) convertView
						.findViewById(R.id.tv_baoliaoandbaoguang_nickname);
				mholder.ll_bl_share = (LinearLayout) convertView
						.findViewById(R.id.ll_bl_share);
				mholder.ll_bl_good = (LinearLayout) convertView
						.findViewById(R.id.ll_bl_good);
				mholder.tv_bl_good = (TextView) convertView
						.findViewById(R.id.tv_bl_good);
				mholder.tv_baoliaoandbaoguang_content = (TextView) convertView
						.findViewById(R.id.tv_baoliaoandbaoguang_content);
				mholder.tv_baoliaoandbaoguang_createtime = (TextView) convertView
						.findViewById(R.id.tv_baoliaoandbaoguang_createtime);
				mholder.iv_baoliaoandbaoguang_headimg = (ImageView) convertView
						.findViewById(R.id.iv_baoliaoandbaoguang_headimg);
				mholder.iv_baoliaoandbaoguang_top = (ImageView) convertView
						.findViewById(R.id.iv_baoliaoandbaoguang_top);
				mholder.rl_baoliaoandbaoguang = (RelativeLayout) convertView
						.findViewById(R.id.rl_baoliaoandbaoguang);
				mholder.tv_baoliaoandbaoguang_deldetail = (Button) convertView
						.findViewById(R.id.tv_baoliaoandbaoguang_deldetail);
				mholder.gv_img = (MyGridView2) convertView
						.findViewById(R.id.gv_img);
				mholder.imgAdapter = new MyGridViewAdapter(mContext);
				mholder.iv_baoliaoandbaoguang_laba = (ImageView) convertView
						.findViewById(R.id.iv_baoliaoandbaoguang_laba);
				mholder.iv_baoliaoandbaoguang_dj = (ImageView) convertView
						.findViewById(R.id.iv_baoliaoandbaoguang_dj);
				convertView.setTag(mholder);
				break;
			case 4:
				convertView = inflater.inflate(
						R.layout.item_baoliaoandbaoguang_list, null);
				mholder.tv_baoliaoandbaoguang_nickname = (TextView) convertView
						.findViewById(R.id.tv_baoliaoandbaoguang_nickname);
				mholder.ll_bl_share = (LinearLayout) convertView
						.findViewById(R.id.ll_bl_share);
				mholder.ll_bl_good = (LinearLayout) convertView
						.findViewById(R.id.ll_bl_good);
				mholder.tv_bl_good = (TextView) convertView
						.findViewById(R.id.tv_bl_good);
				mholder.tv_baoliaoandbaoguang_content = (TextView) convertView
						.findViewById(R.id.tv_baoliaoandbaoguang_content);
				mholder.tv_baoliaoandbaoguang_createtime = (TextView) convertView
						.findViewById(R.id.tv_baoliaoandbaoguang_createtime);
				mholder.iv_baoliaoandbaoguang_headimg = (ImageView) convertView
						.findViewById(R.id.iv_baoliaoandbaoguang_headimg);
				mholder.iv_baoliaoandbaoguang_top = (ImageView) convertView
						.findViewById(R.id.iv_baoliaoandbaoguang_top);
				mholder.rl_baoliaoandbaoguang = (RelativeLayout) convertView
						.findViewById(R.id.rl_baoliaoandbaoguang);
				mholder.tv_baoliaoandbaoguang_deldetail = (Button) convertView
						.findViewById(R.id.tv_baoliaoandbaoguang_deldetail);
				mholder.gv_img = (MyGridView2) convertView
						.findViewById(R.id.gv_img);
				mholder.imgAdapter = new MyGridViewAdapter(mContext);
				mholder.iv_baoliaoandbaoguang_laba = (ImageView) convertView
						.findViewById(R.id.iv_baoliaoandbaoguang_laba);
				mholder.iv_baoliaoandbaoguang_dj = (ImageView) convertView
						.findViewById(R.id.iv_baoliaoandbaoguang_dj);
				convertView.setTag(mholder);
				break;
			case 5:
				convertView = inflater.inflate(
						R.layout.item_baoliaoandbaoguang_list, null);
				mholder.tv_baoliaoandbaoguang_nickname = (TextView) convertView
						.findViewById(R.id.tv_baoliaoandbaoguang_nickname);
				mholder.ll_bl_share = (LinearLayout) convertView
						.findViewById(R.id.ll_bl_share);
				mholder.ll_bl_good = (LinearLayout) convertView
						.findViewById(R.id.ll_bl_good);
				mholder.tv_bl_good = (TextView) convertView
						.findViewById(R.id.tv_bl_good);
				mholder.tv_baoliaoandbaoguang_content = (TextView) convertView
						.findViewById(R.id.tv_baoliaoandbaoguang_content);
				mholder.tv_baoliaoandbaoguang_createtime = (TextView) convertView
						.findViewById(R.id.tv_baoliaoandbaoguang_createtime);
				mholder.iv_baoliaoandbaoguang_headimg = (ImageView) convertView
						.findViewById(R.id.iv_baoliaoandbaoguang_headimg);
				mholder.iv_baoliaoandbaoguang_top = (ImageView) convertView
						.findViewById(R.id.iv_baoliaoandbaoguang_top);
				mholder.rl_baoliaoandbaoguang = (RelativeLayout) convertView
						.findViewById(R.id.rl_baoliaoandbaoguang);
				mholder.tv_baoliaoandbaoguang_deldetail = (Button) convertView
						.findViewById(R.id.tv_baoliaoandbaoguang_deldetail);
				mholder.gv_img = (MyGridView2) convertView
						.findViewById(R.id.gv_img);
				mholder.imgAdapter = new MyGridViewAdapter(mContext);
				mholder.iv_baoliaoandbaoguang_dj = (ImageView) convertView
						.findViewById(R.id.iv_baoliaoandbaoguang_dj);
				convertView.setTag(mholder);
				break;
			case 6:
				convertView = inflater.inflate(
						R.layout.item_baoliaoandbaoguang_list, null);
				mholder.tv_baoliaoandbaoguang_nickname = (TextView) convertView
						.findViewById(R.id.tv_baoliaoandbaoguang_nickname);
				mholder.ll_bl_share = (LinearLayout) convertView
						.findViewById(R.id.ll_bl_share);
				mholder.ll_bl_good = (LinearLayout) convertView
						.findViewById(R.id.ll_bl_good);
				mholder.tv_bl_good = (TextView) convertView
						.findViewById(R.id.tv_bl_good);
				mholder.tv_baoliaoandbaoguang_content = (TextView) convertView
						.findViewById(R.id.tv_baoliaoandbaoguang_content);
				mholder.tv_baoliaoandbaoguang_createtime = (TextView) convertView
						.findViewById(R.id.tv_baoliaoandbaoguang_createtime);
				mholder.iv_baoliaoandbaoguang_headimg = (ImageView) convertView
						.findViewById(R.id.iv_baoliaoandbaoguang_headimg);
				mholder.iv_baoliaoandbaoguang_top = (ImageView) convertView
						.findViewById(R.id.iv_baoliaoandbaoguang_top);
				mholder.rl_baoliaoandbaoguang = (RelativeLayout) convertView
						.findViewById(R.id.rl_baoliaoandbaoguang);
				mholder.tv_baoliaoandbaoguang_deldetail = (Button) convertView
						.findViewById(R.id.tv_baoliaoandbaoguang_deldetail);
				mholder.gv_img = (MyGridView2) convertView
						.findViewById(R.id.gv_img);
				mholder.imgAdapter = new MyGridViewAdapter(mContext);
				mholder.iv_baoliaoandbaoguang_dj = (ImageView) convertView
						.findViewById(R.id.iv_baoliaoandbaoguang_dj);
				convertView.setTag(mholder);
				break;
			case 7:
				convertView = inflater.inflate(
						R.layout.item_baoliaoandbaoguang_list, null);
				mholder.tv_baoliaoandbaoguang_nickname = (TextView) convertView
						.findViewById(R.id.tv_baoliaoandbaoguang_nickname);
				mholder.ll_bl_share = (LinearLayout) convertView
						.findViewById(R.id.ll_bl_share);
				mholder.ll_bl_good = (LinearLayout) convertView
						.findViewById(R.id.ll_bl_good);
				mholder.tv_bl_good = (TextView) convertView
						.findViewById(R.id.tv_bl_good);
				mholder.tv_baoliaoandbaoguang_content = (TextView) convertView
						.findViewById(R.id.tv_baoliaoandbaoguang_content);
				mholder.tv_baoliaoandbaoguang_createtime = (TextView) convertView
						.findViewById(R.id.tv_baoliaoandbaoguang_createtime);
				mholder.iv_baoliaoandbaoguang_headimg = (ImageView) convertView
						.findViewById(R.id.iv_baoliaoandbaoguang_headimg);
				mholder.iv_baoliaoandbaoguang_top = (ImageView) convertView
						.findViewById(R.id.iv_baoliaoandbaoguang_top);
				mholder.rl_baoliaoandbaoguang = (RelativeLayout) convertView
						.findViewById(R.id.rl_baoliaoandbaoguang);
				mholder.tv_baoliaoandbaoguang_deldetail = (Button) convertView
						.findViewById(R.id.tv_baoliaoandbaoguang_deldetail);
				mholder.gv_img = (MyGridView2) convertView
						.findViewById(R.id.gv_img);
				mholder.imgAdapter = new MyGridViewAdapter(mContext);
				mholder.iv_baoliaoandbaoguang_dj = (ImageView) convertView
						.findViewById(R.id.iv_baoliaoandbaoguang_dj);
				convertView.setTag(mholder);
				break;
			case 8:
				convertView = inflater.inflate(
						R.layout.item_baoliaoandbaoguang_list, null);
				mholder.tv_baoliaoandbaoguang_nickname = (TextView) convertView
						.findViewById(R.id.tv_baoliaoandbaoguang_nickname);
				mholder.ll_bl_share = (LinearLayout) convertView
						.findViewById(R.id.ll_bl_share);
				mholder.ll_bl_good = (LinearLayout) convertView
						.findViewById(R.id.ll_bl_good);
				mholder.tv_bl_good = (TextView) convertView
						.findViewById(R.id.tv_bl_good);
				mholder.tv_baoliaoandbaoguang_content = (TextView) convertView
						.findViewById(R.id.tv_baoliaoandbaoguang_content);
				mholder.tv_baoliaoandbaoguang_createtime = (TextView) convertView
						.findViewById(R.id.tv_baoliaoandbaoguang_createtime);
				mholder.iv_baoliaoandbaoguang_headimg = (ImageView) convertView
						.findViewById(R.id.iv_baoliaoandbaoguang_headimg);
				mholder.iv_baoliaoandbaoguang_top = (ImageView) convertView
						.findViewById(R.id.iv_baoliaoandbaoguang_top);
				mholder.rl_baoliaoandbaoguang = (RelativeLayout) convertView
						.findViewById(R.id.rl_baoliaoandbaoguang);
				mholder.tv_baoliaoandbaoguang_deldetail = (Button) convertView
						.findViewById(R.id.tv_baoliaoandbaoguang_deldetail);
				mholder.gv_img = (MyGridView2) convertView
						.findViewById(R.id.gv_img);
				mholder.imgAdapter = new MyGridViewAdapter(mContext);
				mholder.iv_baoliaoandbaoguang_dj = (ImageView) convertView
						.findViewById(R.id.iv_baoliaoandbaoguang_dj);
				convertView.setTag(mholder);
				break;
			}
		} else {
			mholder = (BaoliaoandbaoguangHolder) convertView.getTag();
		}
		switch (type) {
		case 1:
			imageLoader.displayImage(imageUrl_head,
					mholder.iv_baoliaoandbaoguang_headimg, options_head, null);
			// TODO
			imageLoader.displayImage(isdjpath,
					mholder.iv_baoliaoandbaoguang_dj, options, null);
			imageLoader.displayImage(istoppath,
					mholder.iv_baoliaoandbaoguang_top, options, null);
			urls = imagepath.split(",");
			mholder.tv_baoliaoandbaoguang_nickname.setText(nickname);
			mholder.tv_baoliaoandbaoguang_createtime.setText(date);
			mholder.tv_bl_good.setText(likecount);
			mholder.tv_baoliaoandbaoguang_content.setText(content);
			mholder.ll_bl_share.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (imagepath != null) {
						showShare(transmiturl, content, imagepath.split(",")[0], stcollectid);
					}
				}
			});
			mholder.ll_bl_good.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {

					if (uid != null) {
						new Thread() {
							@Override
							public void run() {
								addGood(stcollectid, position);
							}
						}.start();
					}
				}
			});
			if (imagepath != null) {
				urls = imagepath.split(",");
				final Intent intent = new Intent(mContext,
						ViewPagerActivity.class);
				intent.putExtra("imgurl", urls);
				mholder.imgAdapter.setUrls(imagepath);
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
			mholder.iv_baoliaoandbaoguang_laba.setVisibility(View.VISIBLE);
			mholder.iv_baoliaoandbaoguang_laba
					.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							if (mPlayer != null && mPlayer.isPlaying()) {
								stopPlaying();
							} else {
								new Thread() {
									@Override
									public void run() {
										startPlaying(videopath);
										super.run();
									}
								}.start();
							}
						}
					});
			break;
		case 2:
			imageLoader.displayImage(imageUrl_head,
					mholder.iv_baoliaoandbaoguang_headimg, options_head, null);
			// TODO
			imageLoader.displayImage(isdjpath,
					mholder.iv_baoliaoandbaoguang_dj, options, null);
			imageLoader.displayImage(istoppath,
					mholder.iv_baoliaoandbaoguang_top, options, null);
			mholder.tv_baoliaoandbaoguang_nickname.setText(nickname);
			mholder.tv_baoliaoandbaoguang_createtime.setText(date);
			mholder.tv_bl_good.setText(likecount);
			mholder.tv_baoliaoandbaoguang_content.setText(content);
			mholder.tv_baoliaoandbaoguang_deldetail.setVisibility(View.VISIBLE);
			mholder.tv_baoliaoandbaoguang_deldetail
					.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							deldialog(stcollectid, position);
						}
					});
			mholder.ll_bl_share.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (imagepath != null) {
						showShare(transmiturl, content, imagepath.split(",")[0], stcollectid);
					}
				}
			});
			mholder.ll_bl_good.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {

					if (uid != null) {
						new Thread() {
							@Override
							public void run() {
								addGood(stcollectid, position);
							}
						}.start();
					}
				}
			});
			if (imagepath != null) {
				urls = imagepath.split(",");
				final Intent intent = new Intent(mContext,
						ViewPagerActivity.class);
				intent.putExtra("imgurl", urls);
				mholder.imgAdapter.setUrls(imagepath);
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
			mholder.iv_baoliaoandbaoguang_laba.setVisibility(View.VISIBLE);
			mholder.iv_baoliaoandbaoguang_laba
					.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							if (mPlayer != null && mPlayer.isPlaying()) {
								stopPlaying();
							} else {
								new Thread() {
									@Override
									public void run() {
										startPlaying(videopath);
										super.run();
									}
								}.start();
							}
						}
					});
			break;
		case 3:
			imageLoader.displayImage(imageUrl_head,
					mholder.iv_baoliaoandbaoguang_headimg, options_head, null);
			// TODO
			imageLoader.displayImage(isdjpath,
					mholder.iv_baoliaoandbaoguang_dj, options, null);
			imageLoader.displayImage(istoppath,
					mholder.iv_baoliaoandbaoguang_top, options, null);
			mholder.tv_baoliaoandbaoguang_nickname.setText(nickname);
			mholder.tv_baoliaoandbaoguang_createtime.setText(date);
			mholder.tv_bl_good.setText(likecount);
			mholder.tv_baoliaoandbaoguang_content.setText(content);
			mholder.ll_bl_share.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					showShare(transmiturl, content, null, stcollectid);
				}
			});
			mholder.ll_bl_good.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {

					if (uid != null) {
						new Thread() {
							@Override
							public void run() {
								addGood(stcollectid, position);
							}
						}.start();
					}
				}
			});
			mholder.iv_baoliaoandbaoguang_laba.setVisibility(View.VISIBLE);
			mholder.iv_baoliaoandbaoguang_laba
					.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							if (mPlayer != null && mPlayer.isPlaying()) {
								stopPlaying();
							} else {
								new Thread() {
									@Override
									public void run() {
										startPlaying(videopath);
										super.run();
									}
								}.start();
							}
						}
					});
			break;
		case 4:
			imageLoader.displayImage(imageUrl_head,
					mholder.iv_baoliaoandbaoguang_headimg, options_head, null);
			// TODO
			imageLoader.displayImage(isdjpath,
					mholder.iv_baoliaoandbaoguang_dj, options, null);
			imageLoader.displayImage(istoppath,
					mholder.iv_baoliaoandbaoguang_top, options, null);
			mholder.tv_baoliaoandbaoguang_nickname.setText(nickname);
			mholder.tv_baoliaoandbaoguang_createtime.setText(date);
			mholder.tv_bl_good.setText(likecount);
			mholder.tv_baoliaoandbaoguang_content.setText(content);
			mholder.tv_baoliaoandbaoguang_deldetail.setVisibility(View.VISIBLE);
			mholder.tv_baoliaoandbaoguang_deldetail
					.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							deldialog(stcollectid, position);
						}
					});
			mholder.ll_bl_share.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					showShare(transmiturl, content, null, stcollectid);
				}
			});
			mholder.ll_bl_good.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {

					if (uid != null) {
						new Thread() {
							@Override
							public void run() {
								addGood(stcollectid, position);
							}
						}.start();
					}
				}
			});
			mholder.iv_baoliaoandbaoguang_laba.setVisibility(View.VISIBLE);
			mholder.iv_baoliaoandbaoguang_laba
					.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							if (mPlayer != null && mPlayer.isPlaying()) {
								stopPlaying();
							} else {
								new Thread() {
									@Override
									public void run() {
										startPlaying(videopath);
										super.run();
									}
								}.start();
							}
						}
					});
			break;
		case 5:
			imageLoader.displayImage(imageUrl_head,
					mholder.iv_baoliaoandbaoguang_headimg, options_head, null);
			// TODO
			imageLoader.displayImage(isdjpath,
					mholder.iv_baoliaoandbaoguang_dj, options, null);
			imageLoader.displayImage(istoppath,
					mholder.iv_baoliaoandbaoguang_top, options, null);
			mholder.tv_baoliaoandbaoguang_nickname.setText(nickname);
			mholder.tv_baoliaoandbaoguang_createtime.setText(date);
			mholder.tv_bl_good.setText(likecount);
			mholder.tv_baoliaoandbaoguang_content.setText(content);
			mholder.ll_bl_share.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (imagepath != null) {
						showShare(transmiturl, content, imagepath.split(",")[0], stcollectid);
					}
				}
			});
			mholder.ll_bl_good.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {

					if (uid != null) {
						new Thread() {
							@Override
							public void run() {
								addGood(stcollectid, position);
							}
						}.start();
					}
				}
			});
			if (imagepath != null) {
				urls = imagepath.split(",");
				final Intent intent = new Intent(mContext,
						ViewPagerActivity.class);
				intent.putExtra("imgurl", urls);
				mholder.imgAdapter.setUrls(imagepath);
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
			break;
		case 6:
			imageLoader.displayImage(imageUrl_head,
					mholder.iv_baoliaoandbaoguang_headimg, options_head, null);
			// TODO
			imageLoader.displayImage(isdjpath,
					mholder.iv_baoliaoandbaoguang_dj, options, null);
			imageLoader.displayImage(istoppath,
					mholder.iv_baoliaoandbaoguang_top, options, null);
			mholder.tv_baoliaoandbaoguang_nickname.setText(nickname);
			mholder.tv_baoliaoandbaoguang_createtime.setText(date);
			mholder.tv_bl_good.setText(likecount);
			mholder.tv_baoliaoandbaoguang_content.setText(content);
			mholder.tv_baoliaoandbaoguang_deldetail.setVisibility(View.VISIBLE);
			mholder.tv_baoliaoandbaoguang_deldetail
					.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							deldialog(stcollectid, position);
						}
					});
			mholder.ll_bl_share.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (imagepath != null) {
						showShare(transmiturl, content, imagepath.split(",")[0], stcollectid);
					}
				}
			});
			mholder.ll_bl_good.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {

					if (uid != null) {
						new Thread() {
							@Override
							public void run() {
								addGood(stcollectid, position);
							}
						}.start();
					}
				}
			});
			if (imagepath != null) {
				urls = imagepath.split(",");
				final Intent intent = new Intent(mContext,
						ViewPagerActivity.class);
				intent.putExtra("imgurl", urls);
				mholder.imgAdapter.setUrls(imagepath);
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
			break;
		case 7:
			imageLoader.displayImage(imageUrl_head,
					mholder.iv_baoliaoandbaoguang_headimg, options_head, null);
			// TODO
			imageLoader.displayImage(isdjpath,
					mholder.iv_baoliaoandbaoguang_dj, options, null);
			imageLoader.displayImage(istoppath,
					mholder.iv_baoliaoandbaoguang_top, options, null);
			mholder.tv_baoliaoandbaoguang_nickname.setText(nickname);
			mholder.tv_baoliaoandbaoguang_createtime.setText(date);
			mholder.tv_bl_good.setText(likecount);
			mholder.tv_baoliaoandbaoguang_content.setText(content);
			mholder.ll_bl_share.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					showShare(transmiturl, content, null, stcollectid);
				}
			});
			mholder.ll_bl_good.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {

					if (uid != null) {
						new Thread() {
							@Override
							public void run() {
								addGood(stcollectid, position);
							}
						}.start();
					}
				}
			});
			break;
		case 8:
			imageLoader.displayImage(imageUrl_head,
					mholder.iv_baoliaoandbaoguang_headimg, options_head, null);
			// TODO
			imageLoader.displayImage(isdjpath,
					mholder.iv_baoliaoandbaoguang_dj, options, null);
			imageLoader.displayImage(istoppath,
					mholder.iv_baoliaoandbaoguang_top, options, null);
			mholder.tv_baoliaoandbaoguang_nickname.setText(nickname);
			mholder.tv_baoliaoandbaoguang_createtime.setText(date);
			mholder.tv_bl_good.setText(likecount);
			mholder.tv_baoliaoandbaoguang_content.setText(content);
			mholder.tv_baoliaoandbaoguang_deldetail.setVisibility(View.VISIBLE);
			mholder.tv_baoliaoandbaoguang_deldetail
					.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							deldialog(stcollectid, position);
						}
					});
			mholder.ll_bl_share.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					showShare(transmiturl, content, null, stcollectid);
				}
			});
			mholder.ll_bl_good.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {

					if (uid != null) {
						new Thread() {
							@Override
							public void run() {
								addGood(stcollectid, position);
							}
						}.start();
					}
				}
			});
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
			case Constant.ADDGOOD_SUCCEED:
				int position_good = (Integer) msg.obj;
				String num = arrays.get(position_good).getLikecount();
				int_num = Integer.parseInt(num) + 1;
				arrays.get(position_good).setLikecount(int_num + "");
				notifyDataSetChanged();
				break;
			}
		}
	};

	private void deldialog(final String stcollectid, final int position) {
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
										.GetStringFromUrl(delbl_url
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
		ImageView iv_baoliaoandbaoguang_laba;
		ImageView iv_baoliaoandbaoguang_dj;
		TextView tv_baoliaoandbaoguang_createtime;
		TextView tv_bl_good;
		Button tv_baoliaoandbaoguang_deldetail;
		RelativeLayout rl_baoliaoandbaoguang;
		LinearLayout ll_bl_share;
		LinearLayout ll_bl_good;
		MyGridView2 gv_img;
		MyGridViewAdapter imgAdapter;
	}

	private void startPlaying(String path) {
		try {
			// String tpath =
			// "http://218.104.177.210:8090/ztmedia/uphdreplay/2CE1B7A2-8933-4578-BE32-C51AB6240BF5.mp4";
			// 简便方法，每次点播放先reset一次，否则在player不同状态调不同方法容易出异常
			mPlayer.reset();
			mPlayer.setDataSource(path);
			mPlayer.prepare();
			mPlayer.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void stopPlaying() {
		mPlayer.stop();
	}

	private void showShare(String shareurl, String content, String image, final String stcollectid) {
		ShareSDK.initSDK(mContext);
		OnekeyShare oks = new OnekeyShare();
		if (content.length() > 70) {
			content = content.substring(0, 70) + "...";
		}
		oks.setTitle(content);
		oks.setText(content + " " + shareurl);
		oks.setUrl(shareurl);
		oks.setTitleUrl(shareurl);
		if (image != null && !image.equals("")) {
			oks.setImageUrl(image);
		} else {
			oks.setImageUrl(mContext.getString(R.string.qrcode_url));
		}
		oks.setCallback(new PlatformActionListener() {

			@Override
			public void onError(Platform platform, int arg1, Throwable arg2) {
			}

			@Override
			public void onComplete(Platform platform, int arg1,
					HashMap<String, Object> arg2) {
				
				final String plat = platform.toString();
				
				new Thread(){
					public void run() {
						ShareCountUtil.ShareCount(api_url, uid, stcollectid, "3", 
								plat, "113.129151,23.024514");
					}
				}.start();
			}

			@Override
			public void onCancel(Platform platform, int arg1) {
			}
		});
		// 启动分享GUI
		oks.show(mContext);
	}

	// 点赞
	private void addGood(String stcollectid, int position) {
		JSONObject jsonObject;
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("action", "InsertStcollectlikeByUid"));
		params.add(new BasicNameValuePair("connected_uid", uid));
		params.add(new BasicNameValuePair("stcollectid", stcollectid));
		String result = HttpUtil.PostStringFromUrl(api_url, params);
		if (result != null) {
			try {
				jsonObject = new JSONObject(result);
				String code = jsonObject.getString("code");
				if ("200".equals(code)) {
					handler.obtainMessage(Constant.ADDGOOD_SUCCEED, position)
							.sendToTarget();
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}
}

package cn.fszt.trafficapp.adapter;

import java.lang.ref.WeakReference;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
import android.os.Handler;
import android.text.SpannableString;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import cn.fszt.trafficapp.R;
import cn.fszt.trafficapp.domain.RadioSecretDetailData;
import cn.fszt.trafficapp.ui.activity.RadioSecretDetailActivity;
import cn.fszt.trafficapp.util.HttpUtil;
import cn.fszt.trafficapp.widget.MyGridView2;
import cn.fszt.trafficapp.widget.expression.ExpressionUtil;
import cn.fszt.trafficapp.widget.msg.AppMsg;
import cn.fszt.trafficapp.widget.photoview.DisplayImageOptionsUtil;
import cn.fszt.trafficapp.widget.photoview.ViewPagerActivity;

/**
 * 私信聊天列表
 */
public class RadioSecretDetailListAdapter extends BaseAdapter {

	private Context mContext;
	private List<RadioSecretDetailData> arrays;
	private LayoutInflater inflater;
	private String uid, api_url, delhdsecretletter_url;
	private ImageLoader imageLoader;
	private DisplayImageOptions options_head;
	private int densityDpi;
	AppMsg.Style style = AppMsg.STYLE_INFO;
	AppMsg appMsg;

	private WeakReference<Activity> weak_act;

	public RadioSecretDetailListAdapter(Context mContext, List<RadioSecretDetailData> arrays, int densityDpi,
			String uid, String api_url, String delhdsecretletter_url) {
		this.mContext = mContext;
		this.arrays = arrays;
		this.inflater = LayoutInflater.from(mContext);
		this.uid = uid;
		this.api_url = api_url;
		this.delhdsecretletter_url = delhdsecretletter_url;
		this.densityDpi = densityDpi;
		this.weak_act = new WeakReference<Activity>((Activity) mContext);
		imageLoader = ImageLoader.getInstance();
		options_head = DisplayImageOptionsUtil.getOptions(R.drawable.default_head, R.drawable.default_head,
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
		String connected_uid = arrays.get(position).getSconnected_uid();
		// 自己
		if (connected_uid.equals(uid)) {
			return 1;
		}
		// 非自己
		else {
			return 2;
		}
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		String date = arrays.get(position).getCreatedate();
		String headimg = arrays.get(position).getHeadimg();
		String imagepath = arrays.get(position).getImagepath();
		String imagepath_small = arrays.get(position).getImagepath_small();
		String[] urls = null;
		final String hdsecretletterid = arrays.get(position).getHdsecretletterid();
		int type = getItemViewType(position);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			Date ddate = sdf.parse(date);
			SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd HH:mm");
			date = dateFormat.format(ddate);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		SecretDetailHolder secretHolder = null;
		if (convertView == null) {

			convertView = inflater.inflate(R.layout.item_baoguang_list, null);
			secretHolder = new SecretDetailHolder();
			secretHolder.iv_bgpl_headimg = (ImageView) convertView.findViewById(R.id.iv_baoliaoandbaoguang_headimg);
			secretHolder.tv_bgdetail_createtime = (TextView) convertView
					.findViewById(R.id.tv_baoliaoandbaoguang_createtime);
			secretHolder.tv_bgpl_content = (TextView) convertView.findViewById(R.id.tv_baoliaoandbaoguang_content);
			secretHolder.tv_bgpl_nickname = (TextView) convertView.findViewById(R.id.tv_baoliaoandbaoguang_nickname);
			secretHolder.btn_bgdetail_deldetail = (Button) convertView
					.findViewById(R.id.tv_baoliaoandbaoguang_deldetail);
			secretHolder.iv_bg_good = (ImageView) convertView.findViewById(R.id.iv_bg_good);
			secretHolder.iv_bg_good.setVisibility(View.GONE);
			secretHolder.iv_bg_comment = (ImageView) convertView.findViewById(R.id.iv_bg_comment);
			secretHolder.iv_bg_comment.setVisibility(View.GONE);
			secretHolder.imgAdapter = new MyGridViewAdapter(mContext);
			secretHolder.gv_img = (MyGridView2) convertView.findViewById(R.id.gv_img);

			convertView.setTag(secretHolder);

		} else {
			secretHolder = (SecretDetailHolder) convertView.getTag();
		}
		String zhengze = mContext.getString(R.string.expression_matches); // 正则表达式，用来判断消息内是否有表情
		String content = arrays.get(position).getTextcontent();
		SpannableString spannableString = ExpressionUtil.getExpressionString(mContext, content, zhengze, densityDpi);
		switch (type) {
		case 1: // 自己的评论
			imageLoader.displayImage(headimg, secretHolder.iv_bgpl_headimg, options_head, null);
			if (!imagepath_small.isEmpty()) {
				urls = imagepath.split(",");
				final Intent intent = new Intent(mContext, ViewPagerActivity.class);
				intent.putExtra("imgurl", urls);
				secretHolder.imgAdapter.setUrls(imagepath_small);

				secretHolder.gv_img.setAdapter(secretHolder.imgAdapter);
				secretHolder.gv_img.setOnItemClickListener(new OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
						intent.putExtra("ID", position);
						mContext.startActivity(intent);
					}
				});
			}
			secretHolder.tv_bgpl_nickname.setText(arrays.get(position).getNickname());
			secretHolder.tv_bgdetail_createtime.setText(date);
			secretHolder.btn_bgdetail_deldetail.setVisibility(View.VISIBLE);
			secretHolder.btn_bgdetail_deldetail.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					dialog(hdsecretletterid, position);
				}
			});
			secretHolder.tv_bgpl_content.setText(spannableString);
			break;
		case 2:
			imageLoader.displayImage(headimg, secretHolder.iv_bgpl_headimg, options_head, null);
			if (!imagepath_small.isEmpty()) {
				urls = imagepath.split(",");
				final Intent intent = new Intent(mContext, ViewPagerActivity.class);
				intent.putExtra("imgurl", urls);
				secretHolder.imgAdapter.setUrls(imagepath_small);

				secretHolder.gv_img.setAdapter(secretHolder.imgAdapter);
				secretHolder.gv_img.setOnItemClickListener(new OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
						intent.putExtra("ID", position);
						mContext.startActivity(intent);
					}
				});
			}
			secretHolder.tv_bgpl_nickname.setText(arrays.get(position).getNickname());
			secretHolder.tv_bgdetail_createtime.setText(date);
			secretHolder.tv_bgpl_content.setText(spannableString);
			break;

		}
		return convertView;
	}

	Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 1:
				int position = (Integer) msg.obj;
				// arrays.remove(position);
				// notifyDataSetChanged();
				showMsg(mContext.getResources().getString(R.string.del_success), "info", "bottom");
				final RadioSecretDetailActivity activity = (RadioSecretDetailActivity) weak_act.get();
				activity.initData();
				break;
			case 0:
				showMsg(mContext.getResources().getString(R.string.del_fail), "info", "bottom");
				break;
			case 2:
				showMsg(mContext.getResources().getString(R.string.report_success), "info", "bottom");
				break;
			case 3:
				showMsg(mContext.getResources().getString(R.string.report_fail), "info", "bottom");
				break;
			}
		}
	};

	/**
	 * 删除私信
	 */
	private void dialog(final String hdsecretletterid, final int position) {
		AlertDialog.Builder builder = new Builder(mContext);
		builder.setMessage("确认删除吗?");
		builder.setTitle("提示");
		builder.setPositiveButton(mContext.getResources().getString(R.string.dialog_confirm_yes),
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();

						new Thread() {
							public void run() {
								JSONObject jsonObject;
								List<NameValuePair> params = new ArrayList<NameValuePair>();
								params.add(new BasicNameValuePair("action", delhdsecretletter_url));
								params.add(new BasicNameValuePair("hdsecretletterid", hdsecretletterid));
								String resultString = HttpUtil.PostStringFromUrl(api_url, params);
								try {
									if (resultString != null) {
										jsonObject = new JSONObject(resultString);
										String code = jsonObject.getString("code");
										if (code.equals("200")) {
											handler.obtainMessage(1, position).sendToTarget();
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
		builder.setNegativeButton(mContext.getResources().getString(R.string.dialog_no),
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

	private class SecretDetailHolder {
		TextView tv_bgpl_nickname;
		TextView tv_bgpl_content;
		TextView tv_bgdetail_createtime;
		ImageView iv_bgpl_headimg;
		Button btn_bgdetail_deldetail;
		ImageView iv_bg_comment;
		ImageView iv_bg_good;
		MyGridViewAdapter imgAdapter;
		MyGridView2 gv_img;
	}
}

package cn.fszt.trafficapp.adapter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cn.fszt.trafficapp.R;
import cn.fszt.trafficapp.domain.ChatData;
import cn.fszt.trafficapp.ui.activity.RadioCommentEditActivity;
import cn.fszt.trafficapp.util.HttpUtil;
import cn.fszt.trafficapp.widget.expression.ExpressionUtil;
import cn.fszt.trafficapp.widget.msg.AppMsg;
import cn.fszt.trafficapp.widget.photoview.DisplayImageOptionsUtil;
import cn.fszt.trafficapp.widget.photoview.MenuItem;
import cn.fszt.trafficapp.widget.photoview.PopupMenu;
import cn.fszt.trafficapp.widget.photoview.ViewPagerActivity;
import cn.fszt.trafficapp.widget.photoview.PopupMenu.OnItemSelectedListener;

/**
 * 节目互动每个节目评论列表界面Adapter
 * 
 * @author AeiouKong
 *
 */
public class RadioCommentListAdapter2 extends BaseAdapter {

	private Context mContext;
	private List<ChatData> arrays;
	private LayoutInflater inflater;
	private String delmpl_url, reportychat_url, id, uid;
	private MediaPlayer mPlayer = null;
	private int densityDpi;
	private ImageLoader imageLoader;
	private DisplayImageOptions options, options_head;
	AppMsg.Style style = AppMsg.STYLE_INFO;
	AppMsg appMsg;
	private static final int REPORT = 1;
	private static final int SUBMITPINGLUN = 2;

	public RadioCommentListAdapter2(Context mContext, List<ChatData> arrays,
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

		options = DisplayImageOptionsUtil.getOptions(R.drawable.ic_empty,
				R.drawable.ic_error, R.drawable.default_image);

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

		String[] urls;
		String istop = arrays.get(position).getIstop();
		String imagepath = arrays.get(position).getImagepath();
		String isdj = arrays.get(position).getIsdj();

		// 自己
		if ("1".equals(istop)) {
			// 有图片是dj
			if (!"".equals(imagepath) && "1".equals(isdj)) {
				urls = imagepath.split(",");
				if (urls.length == 1) {
					return 11;
				} else if (urls.length == 2) {
					return 12;
				} else if (urls.length == 3) {
					return 13;
				} else if (urls.length == 4) {
					return 14;
				}
			}
			// 有图片不是dj
			else if (!"".equals(imagepath) && "0".equals(isdj)) {
				urls = imagepath.split(",");
				if (urls.length == 1) {
					return 15;
				} else if (urls.length == 2) {
					return 16;
				} else if (urls.length == 3) {
					return 17;
				} else if (urls.length == 4) {
					return 18;
				}
			}
			// 没有图片是dj
			else if ("".equals(imagepath) && "1".equals(isdj)) {
				return 19;
			}
			// 没有图片不是dj
			else {
				return 1;
			}
		}
		// 非自己
		else {
			// 有图片是dj
			if (!"".equals(imagepath) && "1".equals(isdj)) {
				urls = imagepath.split(",");
				if (urls.length == 1) {
					return 21;
				} else if (urls.length == 2) {
					return 22;
				} else if (urls.length == 3) {
					return 23;
				} else if (urls.length == 4) {
					return 24;
				}
			}
			// 有图片不是dj
			else if (!"".equals(imagepath) && "0".equals(isdj)) {
				urls = imagepath.split(",");
				if (urls.length == 1) {
					return 25;
				} else if (urls.length == 2) {
					return 26;
				} else if (urls.length == 3) {
					return 27;
				} else if (urls.length == 4) {
					return 28;
				}
			}
			// 没有图片是dj
			else if ("".equals(imagepath) && "1".equals(isdj)) {
				return 29;
			}
			// 没有图片不是dj
			else {
				return 2;
			}
		}

		return 2;
	}

	@Override
	public int getViewTypeCount() {
		return 40;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		YchatHolder yholder = null;
		MchatHolder mholder = null;
		int type = getItemViewType(position);
		final String hdcommentid = arrays.get(position).getHdcommentid();
		String[] urls = null;
		List<ImageView> list_imageview = new ArrayList<ImageView>();
		String imagepath = arrays.get(position).getImagepath();
		final String voicepath = arrays.get(position).getVoicepath();
		String headimg = arrays.get(position).getHeadimg();
		String date = arrays.get(position).getCreatedate();
		final String nickname = arrays.get(position).getNickname();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			Date ddate = sdf.parse(date);
			SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd HH:mm");
			date = dateFormat.format(ddate);
		} catch (ParseException e) {
			e.printStackTrace();
		}

		if (convertView == null) {
			switch (type) {
			case 1:
				convertView = inflater.inflate(R.layout.item_ychat_list, null);
				yholder = new YchatHolder();
				yholder.iv_ychat = (ImageView) convertView
						.findViewById(R.id.iv_ychat);
				yholder.btn_ychat_reply = (Button) convertView
						.findViewById(R.id.btn_ychat_reply);
				yholder.tv_ychat_deldetail = (Button) convertView
						.findViewById(R.id.tv_ychat_deldetail);
				yholder.iv_ychat_reply = (ImageView) convertView
						.findViewById(R.id.iv_ychat_reply);
				yholder.tv_ychat_nickname = (TextView) convertView
						.findViewById(R.id.tv_ychat_nickname);
				yholder.tv_ychat_createtime = (TextView) convertView
						.findViewById(R.id.tv_ychat_createtime);
				yholder.tv_ychat_content = (TextView) convertView
						.findViewById(R.id.tv_ychat_content);
				yholder.tv_ychat_report = (TextView) convertView
						.findViewById(R.id.tv_ychat_report);
				yholder.iv_ychat1 = (ImageView) convertView
						.findViewById(R.id.iv_ychat_img1);
				yholder.iv_ychat2 = (ImageView) convertView
						.findViewById(R.id.iv_ychat_img2);
				yholder.iv_ychat3 = (ImageView) convertView
						.findViewById(R.id.iv_ychat_img3);
				yholder.iv_ychat4 = (ImageView) convertView
						.findViewById(R.id.iv_ychat_img4);
				yholder.rl_ychat = (RelativeLayout) convertView
						.findViewById(R.id.rl_ychat);
				yholder.iv_ychat_dj = (ImageView) convertView
						.findViewById(R.id.iv_ychat_dj);
				convertView.setTag(yholder);
				break;
			case 11:
				convertView = inflater.inflate(R.layout.item_ychat_list, null);
				yholder = new YchatHolder();
				yholder.iv_ychat = (ImageView) convertView
						.findViewById(R.id.iv_ychat);
				yholder.btn_ychat_reply = (Button) convertView
						.findViewById(R.id.btn_ychat_reply);
				yholder.tv_ychat_deldetail = (Button) convertView
						.findViewById(R.id.tv_ychat_deldetail);
				yholder.iv_ychat_reply = (ImageView) convertView
						.findViewById(R.id.iv_ychat_reply);
				yholder.tv_ychat_nickname = (TextView) convertView
						.findViewById(R.id.tv_ychat_nickname);
				yholder.tv_ychat_createtime = (TextView) convertView
						.findViewById(R.id.tv_ychat_createtime);
				yholder.tv_ychat_content = (TextView) convertView
						.findViewById(R.id.tv_ychat_content);
				yholder.tv_ychat_report = (TextView) convertView
						.findViewById(R.id.tv_ychat_report);
				yholder.iv_ychat1 = (ImageView) convertView
						.findViewById(R.id.iv_ychat_img1);
				yholder.iv_ychat2 = (ImageView) convertView
						.findViewById(R.id.iv_ychat_img2);
				yholder.iv_ychat3 = (ImageView) convertView
						.findViewById(R.id.iv_ychat_img3);
				yholder.iv_ychat4 = (ImageView) convertView
						.findViewById(R.id.iv_ychat_img4);
				yholder.rl_ychat = (RelativeLayout) convertView
						.findViewById(R.id.rl_ychat);
				yholder.iv_ychat_dj = (ImageView) convertView
						.findViewById(R.id.iv_ychat_dj);
				convertView.setTag(yholder);
				break;
			case 12:
				convertView = inflater.inflate(R.layout.item_ychat_list, null);
				yholder = new YchatHolder();
				yholder.iv_ychat = (ImageView) convertView
						.findViewById(R.id.iv_ychat);
				yholder.btn_ychat_reply = (Button) convertView
						.findViewById(R.id.btn_ychat_reply);
				yholder.tv_ychat_deldetail = (Button) convertView
						.findViewById(R.id.tv_ychat_deldetail);
				yholder.iv_ychat_reply = (ImageView) convertView
						.findViewById(R.id.iv_ychat_reply);
				yholder.tv_ychat_nickname = (TextView) convertView
						.findViewById(R.id.tv_ychat_nickname);
				yholder.tv_ychat_createtime = (TextView) convertView
						.findViewById(R.id.tv_ychat_createtime);
				yholder.tv_ychat_content = (TextView) convertView
						.findViewById(R.id.tv_ychat_content);
				yholder.tv_ychat_report = (TextView) convertView
						.findViewById(R.id.tv_ychat_report);
				yholder.iv_ychat1 = (ImageView) convertView
						.findViewById(R.id.iv_ychat_img1);
				yholder.iv_ychat2 = (ImageView) convertView
						.findViewById(R.id.iv_ychat_img2);
				yholder.iv_ychat3 = (ImageView) convertView
						.findViewById(R.id.iv_ychat_img3);
				yholder.iv_ychat4 = (ImageView) convertView
						.findViewById(R.id.iv_ychat_img4);
				yholder.rl_ychat = (RelativeLayout) convertView
						.findViewById(R.id.rl_ychat);
				yholder.iv_ychat_dj = (ImageView) convertView
						.findViewById(R.id.iv_ychat_dj);
				convertView.setTag(yholder);
				break;
			case 13:
				convertView = inflater.inflate(R.layout.item_ychat_list, null);
				yholder = new YchatHolder();
				yholder.iv_ychat = (ImageView) convertView
						.findViewById(R.id.iv_ychat);
				yholder.btn_ychat_reply = (Button) convertView
						.findViewById(R.id.btn_ychat_reply);
				yholder.tv_ychat_deldetail = (Button) convertView
						.findViewById(R.id.tv_ychat_deldetail);
				yholder.iv_ychat_reply = (ImageView) convertView
						.findViewById(R.id.iv_ychat_reply);
				yholder.tv_ychat_nickname = (TextView) convertView
						.findViewById(R.id.tv_ychat_nickname);
				yholder.tv_ychat_createtime = (TextView) convertView
						.findViewById(R.id.tv_ychat_createtime);
				yholder.tv_ychat_content = (TextView) convertView
						.findViewById(R.id.tv_ychat_content);
				yholder.tv_ychat_report = (TextView) convertView
						.findViewById(R.id.tv_ychat_report);
				yholder.iv_ychat1 = (ImageView) convertView
						.findViewById(R.id.iv_ychat_img1);
				yholder.iv_ychat2 = (ImageView) convertView
						.findViewById(R.id.iv_ychat_img2);
				yholder.iv_ychat3 = (ImageView) convertView
						.findViewById(R.id.iv_ychat_img3);
				yholder.iv_ychat4 = (ImageView) convertView
						.findViewById(R.id.iv_ychat_img4);
				yholder.rl_ychat = (RelativeLayout) convertView
						.findViewById(R.id.rl_ychat);
				yholder.iv_ychat_dj = (ImageView) convertView
						.findViewById(R.id.iv_ychat_dj);
				convertView.setTag(yholder);
				break;
			case 14:
				convertView = inflater.inflate(R.layout.item_ychat_list, null);
				yholder = new YchatHolder();
				yholder.iv_ychat = (ImageView) convertView
						.findViewById(R.id.iv_ychat);
				yholder.btn_ychat_reply = (Button) convertView
						.findViewById(R.id.btn_ychat_reply);
				yholder.tv_ychat_deldetail = (Button) convertView
						.findViewById(R.id.tv_ychat_deldetail);
				yholder.iv_ychat_reply = (ImageView) convertView
						.findViewById(R.id.iv_ychat_reply);
				yholder.tv_ychat_nickname = (TextView) convertView
						.findViewById(R.id.tv_ychat_nickname);
				yholder.tv_ychat_createtime = (TextView) convertView
						.findViewById(R.id.tv_ychat_createtime);
				yholder.tv_ychat_content = (TextView) convertView
						.findViewById(R.id.tv_ychat_content);
				yholder.tv_ychat_report = (TextView) convertView
						.findViewById(R.id.tv_ychat_report);
				yholder.iv_ychat1 = (ImageView) convertView
						.findViewById(R.id.iv_ychat_img1);
				yholder.iv_ychat2 = (ImageView) convertView
						.findViewById(R.id.iv_ychat_img2);
				yholder.iv_ychat3 = (ImageView) convertView
						.findViewById(R.id.iv_ychat_img3);
				yholder.iv_ychat4 = (ImageView) convertView
						.findViewById(R.id.iv_ychat_img4);
				yholder.rl_ychat = (RelativeLayout) convertView
						.findViewById(R.id.rl_ychat);
				yholder.iv_ychat_dj = (ImageView) convertView
						.findViewById(R.id.iv_ychat_dj);
				convertView.setTag(yholder);
				break;
			case 15:
				convertView = inflater.inflate(R.layout.item_ychat_list, null);
				yholder = new YchatHolder();
				yholder.iv_ychat = (ImageView) convertView
						.findViewById(R.id.iv_ychat);
				yholder.btn_ychat_reply = (Button) convertView
						.findViewById(R.id.btn_ychat_reply);
				yholder.tv_ychat_deldetail = (Button) convertView
						.findViewById(R.id.tv_ychat_deldetail);
				yholder.iv_ychat_reply = (ImageView) convertView
						.findViewById(R.id.iv_ychat_reply);
				yholder.tv_ychat_nickname = (TextView) convertView
						.findViewById(R.id.tv_ychat_nickname);
				yholder.tv_ychat_createtime = (TextView) convertView
						.findViewById(R.id.tv_ychat_createtime);
				yholder.tv_ychat_content = (TextView) convertView
						.findViewById(R.id.tv_ychat_content);
				yholder.tv_ychat_report = (TextView) convertView
						.findViewById(R.id.tv_ychat_report);
				yholder.iv_ychat1 = (ImageView) convertView
						.findViewById(R.id.iv_ychat_img1);
				yholder.iv_ychat2 = (ImageView) convertView
						.findViewById(R.id.iv_ychat_img2);
				yholder.iv_ychat3 = (ImageView) convertView
						.findViewById(R.id.iv_ychat_img3);
				yholder.iv_ychat4 = (ImageView) convertView
						.findViewById(R.id.iv_ychat_img4);
				yholder.iv_ychat_laba = (ImageView) convertView
						.findViewById(R.id.iv_ychat_laba);
				yholder.rl_ychat = (RelativeLayout) convertView
						.findViewById(R.id.rl_ychat);
				yholder.iv_ychat_dj = (ImageView) convertView
						.findViewById(R.id.iv_ychat_dj);
				convertView.setTag(yholder);
				break;
			case 16:
				convertView = inflater.inflate(R.layout.item_ychat_list, null);
				yholder = new YchatHolder();
				yholder.iv_ychat = (ImageView) convertView
						.findViewById(R.id.iv_ychat);
				yholder.btn_ychat_reply = (Button) convertView
						.findViewById(R.id.btn_ychat_reply);
				yholder.tv_ychat_deldetail = (Button) convertView
						.findViewById(R.id.tv_ychat_deldetail);
				yholder.iv_ychat_reply = (ImageView) convertView
						.findViewById(R.id.iv_ychat_reply);
				yholder.tv_ychat_nickname = (TextView) convertView
						.findViewById(R.id.tv_ychat_nickname);
				yholder.tv_ychat_createtime = (TextView) convertView
						.findViewById(R.id.tv_ychat_createtime);
				yholder.tv_ychat_content = (TextView) convertView
						.findViewById(R.id.tv_ychat_content);
				yholder.tv_ychat_report = (TextView) convertView
						.findViewById(R.id.tv_ychat_report);
				yholder.iv_ychat1 = (ImageView) convertView
						.findViewById(R.id.iv_ychat_img1);
				yholder.iv_ychat2 = (ImageView) convertView
						.findViewById(R.id.iv_ychat_img2);
				yholder.iv_ychat3 = (ImageView) convertView
						.findViewById(R.id.iv_ychat_img3);
				yholder.iv_ychat4 = (ImageView) convertView
						.findViewById(R.id.iv_ychat_img4);
				yholder.iv_ychat_laba = (ImageView) convertView
						.findViewById(R.id.iv_ychat_laba);
				yholder.rl_ychat = (RelativeLayout) convertView
						.findViewById(R.id.rl_ychat);
				yholder.iv_ychat_dj = (ImageView) convertView
						.findViewById(R.id.iv_ychat_dj);
				convertView.setTag(yholder);
				break;
			case 17:
				convertView = inflater.inflate(R.layout.item_ychat_list, null);
				yholder = new YchatHolder();
				yholder.iv_ychat = (ImageView) convertView
						.findViewById(R.id.iv_ychat);
				yholder.btn_ychat_reply = (Button) convertView
						.findViewById(R.id.btn_ychat_reply);
				yholder.tv_ychat_deldetail = (Button) convertView
						.findViewById(R.id.tv_ychat_deldetail);
				yholder.iv_ychat_reply = (ImageView) convertView
						.findViewById(R.id.iv_ychat_reply);
				yholder.tv_ychat_nickname = (TextView) convertView
						.findViewById(R.id.tv_ychat_nickname);
				yholder.tv_ychat_createtime = (TextView) convertView
						.findViewById(R.id.tv_ychat_createtime);
				yholder.tv_ychat_content = (TextView) convertView
						.findViewById(R.id.tv_ychat_content);
				yholder.tv_ychat_report = (TextView) convertView
						.findViewById(R.id.tv_ychat_report);
				yholder.iv_ychat1 = (ImageView) convertView
						.findViewById(R.id.iv_ychat_img1);
				yholder.iv_ychat2 = (ImageView) convertView
						.findViewById(R.id.iv_ychat_img2);
				yholder.iv_ychat3 = (ImageView) convertView
						.findViewById(R.id.iv_ychat_img3);
				yholder.iv_ychat4 = (ImageView) convertView
						.findViewById(R.id.iv_ychat_img4);
				yholder.iv_ychat_laba = (ImageView) convertView
						.findViewById(R.id.iv_ychat_laba);
				yholder.rl_ychat = (RelativeLayout) convertView
						.findViewById(R.id.rl_ychat);
				yholder.iv_ychat_dj = (ImageView) convertView
						.findViewById(R.id.iv_ychat_dj);
				convertView.setTag(yholder);
				break;
			case 18:
				convertView = inflater.inflate(R.layout.item_ychat_list, null);
				yholder = new YchatHolder();
				yholder.iv_ychat = (ImageView) convertView
						.findViewById(R.id.iv_ychat);
				yholder.btn_ychat_reply = (Button) convertView
						.findViewById(R.id.btn_ychat_reply);
				yholder.tv_ychat_deldetail = (Button) convertView
						.findViewById(R.id.tv_ychat_deldetail);
				yholder.iv_ychat_reply = (ImageView) convertView
						.findViewById(R.id.iv_ychat_reply);
				yholder.tv_ychat_nickname = (TextView) convertView
						.findViewById(R.id.tv_ychat_nickname);
				yholder.tv_ychat_createtime = (TextView) convertView
						.findViewById(R.id.tv_ychat_createtime);
				yholder.tv_ychat_content = (TextView) convertView
						.findViewById(R.id.tv_ychat_content);
				yholder.tv_ychat_report = (TextView) convertView
						.findViewById(R.id.tv_ychat_report);
				yholder.iv_ychat1 = (ImageView) convertView
						.findViewById(R.id.iv_ychat_img1);
				yholder.iv_ychat2 = (ImageView) convertView
						.findViewById(R.id.iv_ychat_img2);
				yholder.iv_ychat3 = (ImageView) convertView
						.findViewById(R.id.iv_ychat_img3);
				yholder.iv_ychat4 = (ImageView) convertView
						.findViewById(R.id.iv_ychat_img4);
				yholder.iv_ychat_laba = (ImageView) convertView
						.findViewById(R.id.iv_ychat_laba);
				yholder.rl_ychat = (RelativeLayout) convertView
						.findViewById(R.id.rl_ychat);
				yholder.iv_ychat_dj = (ImageView) convertView
						.findViewById(R.id.iv_ychat_dj);
				convertView.setTag(yholder);
				break;
			case 19:
				convertView = inflater.inflate(R.layout.item_ychat_list, null);
				yholder = new YchatHolder();
				yholder.iv_ychat = (ImageView) convertView
						.findViewById(R.id.iv_ychat);
				yholder.btn_ychat_reply = (Button) convertView
						.findViewById(R.id.btn_ychat_reply);
				yholder.tv_ychat_deldetail = (Button) convertView
						.findViewById(R.id.tv_ychat_deldetail);
				yholder.iv_ychat_reply = (ImageView) convertView
						.findViewById(R.id.iv_ychat_reply);
				yholder.tv_ychat_nickname = (TextView) convertView
						.findViewById(R.id.tv_ychat_nickname);
				yholder.tv_ychat_createtime = (TextView) convertView
						.findViewById(R.id.tv_ychat_createtime);
				yholder.tv_ychat_content = (TextView) convertView
						.findViewById(R.id.tv_ychat_content);
				yholder.tv_ychat_report = (TextView) convertView
						.findViewById(R.id.tv_ychat_report);
				yholder.iv_ychat1 = (ImageView) convertView
						.findViewById(R.id.iv_ychat_img1);
				yholder.iv_ychat2 = (ImageView) convertView
						.findViewById(R.id.iv_ychat_img2);
				yholder.iv_ychat3 = (ImageView) convertView
						.findViewById(R.id.iv_ychat_img3);
				yholder.iv_ychat4 = (ImageView) convertView
						.findViewById(R.id.iv_ychat_img4);
				yholder.iv_ychat_laba = (ImageView) convertView
						.findViewById(R.id.iv_ychat_laba);
				yholder.rl_ychat = (RelativeLayout) convertView
						.findViewById(R.id.rl_ychat);
				yholder.iv_ychat_dj = (ImageView) convertView
						.findViewById(R.id.iv_ychat_dj);
				convertView.setTag(yholder);
				break;
			case 2:
				convertView = inflater.inflate(R.layout.item_ychat_list, null);
				yholder = new YchatHolder();
				yholder.iv_ychat = (ImageView) convertView
						.findViewById(R.id.iv_ychat);
				yholder.btn_ychat_reply = (Button) convertView
						.findViewById(R.id.btn_ychat_reply);
				yholder.iv_ychat_reply = (ImageView) convertView
						.findViewById(R.id.iv_ychat_reply);
				yholder.tv_ychat_nickname = (TextView) convertView
						.findViewById(R.id.tv_ychat_nickname);
				yholder.tv_ychat_createtime = (TextView) convertView
						.findViewById(R.id.tv_ychat_createtime);
				yholder.tv_ychat_content = (TextView) convertView
						.findViewById(R.id.tv_ychat_content);
				yholder.tv_ychat_report = (TextView) convertView
						.findViewById(R.id.tv_ychat_report);
				yholder.iv_ychat1 = (ImageView) convertView
						.findViewById(R.id.iv_ychat_img1);
				yholder.iv_ychat2 = (ImageView) convertView
						.findViewById(R.id.iv_ychat_img2);
				yholder.iv_ychat3 = (ImageView) convertView
						.findViewById(R.id.iv_ychat_img3);
				yholder.iv_ychat4 = (ImageView) convertView
						.findViewById(R.id.iv_ychat_img4);
				yholder.rl_ychat = (RelativeLayout) convertView
						.findViewById(R.id.rl_ychat);
				yholder.iv_ychat_dj = (ImageView) convertView
						.findViewById(R.id.iv_ychat_dj);
				convertView.setTag(yholder);
				break;
			case 21:
				convertView = inflater.inflate(R.layout.item_ychat_list, null);
				yholder = new YchatHolder();
				yholder.iv_ychat = (ImageView) convertView
						.findViewById(R.id.iv_ychat);
				yholder.btn_ychat_reply = (Button) convertView
						.findViewById(R.id.btn_ychat_reply);
				yholder.iv_ychat_reply = (ImageView) convertView
						.findViewById(R.id.iv_ychat_reply);
				yholder.tv_ychat_nickname = (TextView) convertView
						.findViewById(R.id.tv_ychat_nickname);
				yholder.tv_ychat_createtime = (TextView) convertView
						.findViewById(R.id.tv_ychat_createtime);
				yholder.tv_ychat_content = (TextView) convertView
						.findViewById(R.id.tv_ychat_content);
				yholder.tv_ychat_report = (TextView) convertView
						.findViewById(R.id.tv_ychat_report);
				yholder.iv_ychat1 = (ImageView) convertView
						.findViewById(R.id.iv_ychat_img1);
				yholder.iv_ychat2 = (ImageView) convertView
						.findViewById(R.id.iv_ychat_img2);
				yholder.iv_ychat3 = (ImageView) convertView
						.findViewById(R.id.iv_ychat_img3);
				yholder.iv_ychat4 = (ImageView) convertView
						.findViewById(R.id.iv_ychat_img4);
				yholder.rl_ychat = (RelativeLayout) convertView
						.findViewById(R.id.rl_ychat);
				yholder.iv_ychat_dj = (ImageView) convertView
						.findViewById(R.id.iv_ychat_dj);
				convertView.setTag(yholder);
				break;
			case 22:
				convertView = inflater.inflate(R.layout.item_ychat_list, null);
				yholder = new YchatHolder();
				yholder.iv_ychat = (ImageView) convertView
						.findViewById(R.id.iv_ychat);
				yholder.btn_ychat_reply = (Button) convertView
						.findViewById(R.id.btn_ychat_reply);
				yholder.iv_ychat_reply = (ImageView) convertView
						.findViewById(R.id.iv_ychat_reply);
				yholder.tv_ychat_nickname = (TextView) convertView
						.findViewById(R.id.tv_ychat_nickname);
				yholder.tv_ychat_createtime = (TextView) convertView
						.findViewById(R.id.tv_ychat_createtime);
				yholder.tv_ychat_content = (TextView) convertView
						.findViewById(R.id.tv_ychat_content);
				yholder.tv_ychat_report = (TextView) convertView
						.findViewById(R.id.tv_ychat_report);
				yholder.iv_ychat1 = (ImageView) convertView
						.findViewById(R.id.iv_ychat_img1);
				yholder.iv_ychat2 = (ImageView) convertView
						.findViewById(R.id.iv_ychat_img2);
				yholder.iv_ychat3 = (ImageView) convertView
						.findViewById(R.id.iv_ychat_img3);
				yholder.iv_ychat4 = (ImageView) convertView
						.findViewById(R.id.iv_ychat_img4);
				yholder.rl_ychat = (RelativeLayout) convertView
						.findViewById(R.id.rl_ychat);
				yholder.iv_ychat_dj = (ImageView) convertView
						.findViewById(R.id.iv_ychat_dj);
				convertView.setTag(yholder);
				break;
			case 23:
				convertView = inflater.inflate(R.layout.item_ychat_list, null);
				yholder = new YchatHolder();
				yholder.iv_ychat = (ImageView) convertView
						.findViewById(R.id.iv_ychat);
				yholder.btn_ychat_reply = (Button) convertView
						.findViewById(R.id.btn_ychat_reply);
				yholder.iv_ychat_reply = (ImageView) convertView
						.findViewById(R.id.iv_ychat_reply);
				yholder.tv_ychat_nickname = (TextView) convertView
						.findViewById(R.id.tv_ychat_nickname);
				yholder.tv_ychat_createtime = (TextView) convertView
						.findViewById(R.id.tv_ychat_createtime);
				yholder.tv_ychat_content = (TextView) convertView
						.findViewById(R.id.tv_ychat_content);
				yholder.tv_ychat_report = (TextView) convertView
						.findViewById(R.id.tv_ychat_report);
				yholder.iv_ychat1 = (ImageView) convertView
						.findViewById(R.id.iv_ychat_img1);
				yholder.iv_ychat2 = (ImageView) convertView
						.findViewById(R.id.iv_ychat_img2);
				yholder.iv_ychat3 = (ImageView) convertView
						.findViewById(R.id.iv_ychat_img3);
				yholder.iv_ychat4 = (ImageView) convertView
						.findViewById(R.id.iv_ychat_img4);
				yholder.rl_ychat = (RelativeLayout) convertView
						.findViewById(R.id.rl_ychat);
				yholder.iv_ychat_dj = (ImageView) convertView
						.findViewById(R.id.iv_ychat_dj);
				convertView.setTag(yholder);
				break;
			case 24:
				convertView = inflater.inflate(R.layout.item_ychat_list, null);
				yholder = new YchatHolder();
				yholder.iv_ychat = (ImageView) convertView
						.findViewById(R.id.iv_ychat);
				yholder.btn_ychat_reply = (Button) convertView
						.findViewById(R.id.btn_ychat_reply);
				yholder.iv_ychat_reply = (ImageView) convertView
						.findViewById(R.id.iv_ychat_reply);
				yholder.tv_ychat_nickname = (TextView) convertView
						.findViewById(R.id.tv_ychat_nickname);
				yholder.tv_ychat_createtime = (TextView) convertView
						.findViewById(R.id.tv_ychat_createtime);
				yholder.tv_ychat_content = (TextView) convertView
						.findViewById(R.id.tv_ychat_content);
				yholder.tv_ychat_report = (TextView) convertView
						.findViewById(R.id.tv_ychat_report);
				yholder.iv_ychat1 = (ImageView) convertView
						.findViewById(R.id.iv_ychat_img1);
				yholder.iv_ychat2 = (ImageView) convertView
						.findViewById(R.id.iv_ychat_img2);
				yholder.iv_ychat3 = (ImageView) convertView
						.findViewById(R.id.iv_ychat_img3);
				yholder.iv_ychat4 = (ImageView) convertView
						.findViewById(R.id.iv_ychat_img4);
				yholder.rl_ychat = (RelativeLayout) convertView
						.findViewById(R.id.rl_ychat);
				yholder.iv_ychat_dj = (ImageView) convertView
						.findViewById(R.id.iv_ychat_dj);
				convertView.setTag(yholder);
				break;
			case 25:
				convertView = inflater.inflate(R.layout.item_ychat_list, null);
				yholder = new YchatHolder();
				yholder.iv_ychat = (ImageView) convertView
						.findViewById(R.id.iv_ychat);
				yholder.btn_ychat_reply = (Button) convertView
						.findViewById(R.id.btn_ychat_reply);
				yholder.iv_ychat_reply = (ImageView) convertView
						.findViewById(R.id.iv_ychat_reply);
				yholder.tv_ychat_nickname = (TextView) convertView
						.findViewById(R.id.tv_ychat_nickname);
				yholder.tv_ychat_createtime = (TextView) convertView
						.findViewById(R.id.tv_ychat_createtime);
				yholder.tv_ychat_content = (TextView) convertView
						.findViewById(R.id.tv_ychat_content);
				yholder.tv_ychat_report = (TextView) convertView
						.findViewById(R.id.tv_ychat_report);
				yholder.iv_ychat1 = (ImageView) convertView
						.findViewById(R.id.iv_ychat_img1);
				yholder.iv_ychat2 = (ImageView) convertView
						.findViewById(R.id.iv_ychat_img2);
				yholder.iv_ychat3 = (ImageView) convertView
						.findViewById(R.id.iv_ychat_img3);
				yholder.iv_ychat4 = (ImageView) convertView
						.findViewById(R.id.iv_ychat_img4);
				yholder.iv_ychat_laba = (ImageView) convertView
						.findViewById(R.id.iv_ychat_laba);
				yholder.rl_ychat = (RelativeLayout) convertView
						.findViewById(R.id.rl_ychat);
				yholder.iv_ychat_dj = (ImageView) convertView
						.findViewById(R.id.iv_ychat_dj);
				convertView.setTag(yholder);
				break;
			case 26:
				convertView = inflater.inflate(R.layout.item_ychat_list, null);
				yholder = new YchatHolder();
				yholder.iv_ychat = (ImageView) convertView
						.findViewById(R.id.iv_ychat);
				yholder.btn_ychat_reply = (Button) convertView
						.findViewById(R.id.btn_ychat_reply);
				yholder.iv_ychat_reply = (ImageView) convertView
						.findViewById(R.id.iv_ychat_reply);
				yholder.tv_ychat_nickname = (TextView) convertView
						.findViewById(R.id.tv_ychat_nickname);
				yholder.tv_ychat_createtime = (TextView) convertView
						.findViewById(R.id.tv_ychat_createtime);
				yholder.tv_ychat_content = (TextView) convertView
						.findViewById(R.id.tv_ychat_content);
				yholder.tv_ychat_report = (TextView) convertView
						.findViewById(R.id.tv_ychat_report);
				yholder.iv_ychat1 = (ImageView) convertView
						.findViewById(R.id.iv_ychat_img1);
				yholder.iv_ychat2 = (ImageView) convertView
						.findViewById(R.id.iv_ychat_img2);
				yholder.iv_ychat3 = (ImageView) convertView
						.findViewById(R.id.iv_ychat_img3);
				yholder.iv_ychat4 = (ImageView) convertView
						.findViewById(R.id.iv_ychat_img4);
				yholder.iv_ychat_laba = (ImageView) convertView
						.findViewById(R.id.iv_ychat_laba);
				yholder.rl_ychat = (RelativeLayout) convertView
						.findViewById(R.id.rl_ychat);
				yholder.iv_ychat_dj = (ImageView) convertView
						.findViewById(R.id.iv_ychat_dj);
				convertView.setTag(yholder);
				break;
			case 27:
				convertView = inflater.inflate(R.layout.item_ychat_list, null);
				yholder = new YchatHolder();
				yholder.iv_ychat = (ImageView) convertView
						.findViewById(R.id.iv_ychat);
				yholder.btn_ychat_reply = (Button) convertView
						.findViewById(R.id.btn_ychat_reply);
				yholder.iv_ychat_reply = (ImageView) convertView
						.findViewById(R.id.iv_ychat_reply);
				yholder.tv_ychat_nickname = (TextView) convertView
						.findViewById(R.id.tv_ychat_nickname);
				yholder.tv_ychat_createtime = (TextView) convertView
						.findViewById(R.id.tv_ychat_createtime);
				yholder.tv_ychat_content = (TextView) convertView
						.findViewById(R.id.tv_ychat_content);
				yholder.tv_ychat_report = (TextView) convertView
						.findViewById(R.id.tv_ychat_report);
				yholder.iv_ychat1 = (ImageView) convertView
						.findViewById(R.id.iv_ychat_img1);
				yholder.iv_ychat2 = (ImageView) convertView
						.findViewById(R.id.iv_ychat_img2);
				yholder.iv_ychat3 = (ImageView) convertView
						.findViewById(R.id.iv_ychat_img3);
				yholder.iv_ychat4 = (ImageView) convertView
						.findViewById(R.id.iv_ychat_img4);
				yholder.iv_ychat_laba = (ImageView) convertView
						.findViewById(R.id.iv_ychat_laba);
				yholder.rl_ychat = (RelativeLayout) convertView
						.findViewById(R.id.rl_ychat);
				yholder.iv_ychat_dj = (ImageView) convertView
						.findViewById(R.id.iv_ychat_dj);
				convertView.setTag(yholder);
				break;
			case 28:
				convertView = inflater.inflate(R.layout.item_ychat_list, null);
				yholder = new YchatHolder();
				yholder.iv_ychat = (ImageView) convertView
						.findViewById(R.id.iv_ychat);
				yholder.btn_ychat_reply = (Button) convertView
						.findViewById(R.id.btn_ychat_reply);
				yholder.iv_ychat_reply = (ImageView) convertView
						.findViewById(R.id.iv_ychat_reply);
				yholder.tv_ychat_nickname = (TextView) convertView
						.findViewById(R.id.tv_ychat_nickname);
				yholder.tv_ychat_createtime = (TextView) convertView
						.findViewById(R.id.tv_ychat_createtime);
				yholder.tv_ychat_content = (TextView) convertView
						.findViewById(R.id.tv_ychat_content);
				yholder.tv_ychat_report = (TextView) convertView
						.findViewById(R.id.tv_ychat_report);
				yholder.iv_ychat1 = (ImageView) convertView
						.findViewById(R.id.iv_ychat_img1);
				yholder.iv_ychat2 = (ImageView) convertView
						.findViewById(R.id.iv_ychat_img2);
				yholder.iv_ychat3 = (ImageView) convertView
						.findViewById(R.id.iv_ychat_img3);
				yholder.iv_ychat4 = (ImageView) convertView
						.findViewById(R.id.iv_ychat_img4);
				yholder.iv_ychat_laba = (ImageView) convertView
						.findViewById(R.id.iv_ychat_laba);
				yholder.rl_ychat = (RelativeLayout) convertView
						.findViewById(R.id.rl_ychat);
				yholder.iv_ychat_dj = (ImageView) convertView
						.findViewById(R.id.iv_ychat_dj);
				convertView.setTag(yholder);
				break;
			case 29:
				convertView = inflater.inflate(R.layout.item_ychat_list, null);
				yholder = new YchatHolder();
				yholder.iv_ychat = (ImageView) convertView
						.findViewById(R.id.iv_ychat);
				yholder.btn_ychat_reply = (Button) convertView
						.findViewById(R.id.btn_ychat_reply);
				yholder.iv_ychat_reply = (ImageView) convertView
						.findViewById(R.id.iv_ychat_reply);
				yholder.tv_ychat_nickname = (TextView) convertView
						.findViewById(R.id.tv_ychat_nickname);
				yholder.tv_ychat_createtime = (TextView) convertView
						.findViewById(R.id.tv_ychat_createtime);
				yholder.tv_ychat_content = (TextView) convertView
						.findViewById(R.id.tv_ychat_content);
				yholder.tv_ychat_report = (TextView) convertView
						.findViewById(R.id.tv_ychat_report);
				yholder.iv_ychat1 = (ImageView) convertView
						.findViewById(R.id.iv_ychat_img1);
				yholder.iv_ychat2 = (ImageView) convertView
						.findViewById(R.id.iv_ychat_img2);
				yholder.iv_ychat3 = (ImageView) convertView
						.findViewById(R.id.iv_ychat_img3);
				yholder.iv_ychat4 = (ImageView) convertView
						.findViewById(R.id.iv_ychat_img4);
				yholder.iv_ychat_laba = (ImageView) convertView
						.findViewById(R.id.iv_ychat_laba);
				yholder.rl_ychat = (RelativeLayout) convertView
						.findViewById(R.id.rl_ychat);
				yholder.iv_ychat_dj = (ImageView) convertView
						.findViewById(R.id.iv_ychat_dj);
				convertView.setTag(yholder);
				break;
			}
		} else {
			switch (type) {
			case 1:
				yholder = (YchatHolder) convertView.getTag();
				break;
			case 11:
				yholder = (YchatHolder) convertView.getTag();
				break;
			case 12:
				yholder = (YchatHolder) convertView.getTag();
				break;
			case 13:
				yholder = (YchatHolder) convertView.getTag();
				break;
			case 14:
				yholder = (YchatHolder) convertView.getTag();
				break;
			case 15:
				yholder = (YchatHolder) convertView.getTag();
				break;
			case 16:
				yholder = (YchatHolder) convertView.getTag();
				break;
			case 17:
				yholder = (YchatHolder) convertView.getTag();
				break;
			case 18:
				yholder = (YchatHolder) convertView.getTag();
				break;
			case 19:
				yholder = (YchatHolder) convertView.getTag();
				break;
			case 2:
				yholder = (YchatHolder) convertView.getTag();
				break;
			case 21:
				yholder = (YchatHolder) convertView.getTag();
				break;
			case 22:
				yholder = (YchatHolder) convertView.getTag();
				break;
			case 23:
				yholder = (YchatHolder) convertView.getTag();
				break;
			case 24:
				yholder = (YchatHolder) convertView.getTag();
				break;
			case 25:
				yholder = (YchatHolder) convertView.getTag();
				break;
			case 26:
				yholder = (YchatHolder) convertView.getTag();
				break;
			case 27:
				yholder = (YchatHolder) convertView.getTag();
				break;
			case 28:
				yholder = (YchatHolder) convertView.getTag();
				break;
			case 29:
				yholder = (YchatHolder) convertView.getTag();
				break;
			}
		}

		String zhengze = mContext.getString(R.string.expression_matches); // 正则表达式，用来判断消息内是否有表情
		String content = arrays.get(position).getContent();
		if (content != null && !content.equals("")) {
			content = content.replace("[", "");
			content = content.replace("]", "");
		}
		switch (type) {
		// case 1:
		// imageLoader.displayImage(headimg, mholder.iv_mchat, options_head,
		// null);
		// mholder.tv_mchat_nickname.setText(nickname);
		// mholder.tv_mchat_createtime.setText(date);
		// try {
		// SpannableString spannableString =
		// ExpressionUtil.getExpressionString(mContext, content,
		// zhengze,densityDpi);
		// mholder.tv_mchat_content.setText(spannableString);
		// } catch (NumberFormatException e) {
		// e.printStackTrace();
		// } catch (SecurityException e) {
		// e.printStackTrace();
		// } catch (IllegalArgumentException e) {
		// e.printStackTrace();
		// }
		// mholder.tv_mchat_del.setOnClickListener(new OnClickListener() {
		// @Override
		// public void onClick(View v) {
		// delDialog(hdcommentid,position);
		// }
		// });
		// break;
		// //评论中有图片
		// case 11:
		// imageLoader.displayImage(headimg, mholder.iv_mchat, options_head,
		// null);
		// //评论的图片
		// list_imageview.add(mholder.iv_mchat1);
		// if(imagepath!=null){
		// urls = imagepath.split(",");
		// for(int i=0;i<urls.length;i++){
		// list_imageview.get(i).setVisibility(View.VISIBLE);
		// imageLoader.displayImage(urls[i], list_imageview.get(i), options,
		// null);
		// final Intent intent = new Intent(mContext,ViewPagerActivity.class);
		// intent.putExtra("ID", i);
		// intent.putExtra("imgurl", urls);
		// list_imageview.get(i).setOnClickListener(new OnClickListener() {
		// @Override
		// public void onClick(View v) {
		// mContext.startActivity(intent);
		// ((Activity) mContext).overridePendingTransition(R.anim.zoom_enter,0);
		// }
		// });
		// }
		// }
		// mholder.tv_mchat_nickname.setText(nickname);
		// mholder.tv_mchat_createtime.setText(date);
		// try {
		// SpannableString spannableString =
		// ExpressionUtil.getExpressionString(mContext, content,
		// zhengze,densityDpi);
		// mholder.tv_mchat_content.setText(spannableString);
		// } catch (NumberFormatException e) {
		// e.printStackTrace();
		// } catch (SecurityException e) {
		// e.printStackTrace();
		// } catch (IllegalArgumentException e) {
		// e.printStackTrace();
		// }
		// mholder.tv_mchat_del.setOnClickListener(new OnClickListener() {
		// @Override
		// public void onClick(View v) {
		// delDialog(hdcommentid,position);
		// }
		// });
		// break;
		// case 12:
		// //头像
		// imageLoader.displayImage(headimg, mholder.iv_mchat, options_head,
		// null);
		// //评论的图片
		// list_imageview.add(mholder.iv_mchat1);
		// list_imageview.add(mholder.iv_mchat2);
		//
		// if(imagepath!=null){
		// urls = imagepath.split(",");
		// for(int i=0;i<urls.length;i++){
		// list_imageview.get(i).setVisibility(View.VISIBLE);
		// imageLoader.displayImage(urls[i], list_imageview.get(i), options,
		// null);
		// final Intent intent = new Intent(mContext,ViewPagerActivity.class);
		// intent.putExtra("ID", i);
		// intent.putExtra("imgurl", urls);
		// list_imageview.get(i).setOnClickListener(new OnClickListener() {
		// @Override
		// public void onClick(View v) {
		// mContext.startActivity(intent);
		// ((Activity) mContext).overridePendingTransition(R.anim.zoom_enter,0);
		// }
		// });
		// }
		// }
		// mholder.tv_mchat_nickname.setText(nickname);
		// mholder.tv_mchat_createtime.setText(date);
		// try {
		// SpannableString spannableString =
		// ExpressionUtil.getExpressionString(mContext, content,
		// zhengze,densityDpi);
		// mholder.tv_mchat_content.setText(spannableString);
		// } catch (NumberFormatException e) {
		// e.printStackTrace();
		// } catch (SecurityException e) {
		// e.printStackTrace();
		// } catch (IllegalArgumentException e) {
		// e.printStackTrace();
		// }
		// mholder.tv_mchat_del.setOnClickListener(new OnClickListener() {
		// @Override
		// public void onClick(View v) {
		// delDialog(hdcommentid,position);
		// }
		// });
		// break;
		// case 13:
		// //头像
		// imageLoader.displayImage(headimg, mholder.iv_mchat, options_head,
		// null);
		//
		// //评论的图片
		// list_imageview.add(mholder.iv_mchat1);
		// list_imageview.add(mholder.iv_mchat2);
		// list_imageview.add(mholder.iv_mchat3);
		//
		// if(imagepath!=null){
		// urls = imagepath.split(",");
		// for(int i=0;i<urls.length;i++){
		// list_imageview.get(i).setVisibility(View.VISIBLE);
		// imageLoader.displayImage(urls[i], list_imageview.get(i), options,
		// null);
		// final Intent intent = new Intent(mContext,ViewPagerActivity.class);
		// intent.putExtra("ID", i);
		// intent.putExtra("imgurl", urls);
		// list_imageview.get(i).setOnClickListener(new OnClickListener() {
		// @Override
		// public void onClick(View v) {
		// mContext.startActivity(intent);
		// ((Activity) mContext).overridePendingTransition(R.anim.zoom_enter,0);
		// }
		// });
		// }
		// }
		// mholder.tv_mchat_nickname.setText(nickname);
		// mholder.tv_mchat_createtime.setText(date);
		// try {
		// SpannableString spannableString =
		// ExpressionUtil.getExpressionString(mContext, content,
		// zhengze,densityDpi);
		// mholder.tv_mchat_content.setText(spannableString);
		// } catch (NumberFormatException e) {
		// e.printStackTrace();
		// } catch (SecurityException e) {
		// e.printStackTrace();
		// } catch (IllegalArgumentException e) {
		// e.printStackTrace();
		// }
		// mholder.tv_mchat_del.setOnClickListener(new OnClickListener() {
		// @Override
		// public void onClick(View v) {
		// delDialog(hdcommentid,position);
		// }
		// });
		//
		// break;
		// case 14:
		// //头像
		// imageLoader.displayImage(headimg, mholder.iv_mchat, options_head,
		// null);
		// //评论的图片
		// list_imageview.add(mholder.iv_mchat1);
		// list_imageview.add(mholder.iv_mchat2);
		// list_imageview.add(mholder.iv_mchat3);
		// list_imageview.add(mholder.iv_mchat4);
		//
		// if(imagepath!=null){
		// urls = imagepath.split(",");
		// for(int i=0;i<urls.length;i++){
		// list_imageview.get(i).setVisibility(View.VISIBLE);
		// imageLoader.displayImage(urls[i], list_imageview.get(i), options,
		// null);
		// final Intent intent = new Intent(mContext,ViewPagerActivity.class);
		// intent.putExtra("ID", i);
		// intent.putExtra("imgurl", urls);
		// list_imageview.get(i).setOnClickListener(new OnClickListener() {
		// @Override
		// public void onClick(View v) {
		// mContext.startActivity(intent);
		// ((Activity) mContext).overridePendingTransition(R.anim.zoom_enter,0);
		// }
		// });
		// }
		// }
		// mholder.tv_mchat_nickname.setText(nickname);
		// mholder.tv_mchat_createtime.setText(date);
		// try {
		// SpannableString spannableString =
		// ExpressionUtil.getExpressionString(mContext, content,
		// zhengze,densityDpi);
		// mholder.tv_mchat_content.setText(spannableString);
		// } catch (NumberFormatException e) {
		// e.printStackTrace();
		// } catch (SecurityException e) {
		// e.printStackTrace();
		// } catch (IllegalArgumentException e) {
		// e.printStackTrace();
		// }
		// mholder.tv_mchat_del.setOnClickListener(new OnClickListener() {
		// @Override
		// public void onClick(View v) {
		// delDialog(hdcommentid,position);
		// }
		// });
		// break;
		//
		//
		// case 15:
		// imageLoader.displayImage(headimg, mholder.iv_mchat, options_head,
		// null);
		// //评论的图片
		// list_imageview.add(mholder.iv_mchat1);
		// if(imagepath!=null){
		// urls = imagepath.split(",");
		// for(int i=0;i<urls.length;i++){
		// list_imageview.get(i).setVisibility(View.VISIBLE);
		// imageLoader.displayImage(urls[i], list_imageview.get(i), options,
		// null);
		// final Intent intent = new Intent(mContext,ViewPagerActivity.class);
		// intent.putExtra("ID", i);
		// intent.putExtra("imgurl", urls);
		// list_imageview.get(i).setOnClickListener(new OnClickListener() {
		// @Override
		// public void onClick(View v) {
		// mContext.startActivity(intent);
		// ((Activity) mContext).overridePendingTransition(R.anim.zoom_enter,0);
		// }
		// });
		// }
		// }
		// mholder.iv_mchat_laba.setVisibility(View.VISIBLE);
		// mPlayer.setOnCompletionListener(new OnCompletionListener() {
		// @Override
		// public void onCompletion(MediaPlayer mp) {
		// }
		// });
		// mholder.rl_mchat.setOnClickListener(new OnClickListener() {
		// @Override
		// public void onClick(View v) {
		// if(mPlayer!=null&&mPlayer.isPlaying()){
		// stopPlaying();
		// }else{
		// new Thread(){
		// @Override
		// public void run() {
		// startPlaying(voicepath);
		// super.run();
		// }
		// }.start();
		// }
		// }
		// });
		// mholder.tv_mchat_nickname.setText(nickname);
		// mholder.tv_mchat_createtime.setText(date);
		// try {
		// SpannableString spannableString =
		// ExpressionUtil.getExpressionString(mContext, content,
		// zhengze,densityDpi);
		// mholder.tv_mchat_content.setText(spannableString);
		// } catch (NumberFormatException e) {
		// e.printStackTrace();
		// } catch (SecurityException e) {
		// e.printStackTrace();
		// } catch (IllegalArgumentException e) {
		// e.printStackTrace();
		// }
		// mholder.tv_mchat_del.setOnClickListener(new OnClickListener() {
		// @Override
		// public void onClick(View v) {
		// delDialog(hdcommentid,position);
		// }
		// });
		// break;
		// case 16:
		// //头像
		// imageLoader.displayImage(headimg, mholder.iv_mchat, options_head,
		// null);
		// //评论的图片
		// list_imageview.add(mholder.iv_mchat1);
		// list_imageview.add(mholder.iv_mchat2);
		//
		// if(imagepath!=null){
		// urls = imagepath.split(",");
		// for(int i=0;i<urls.length;i++){
		// list_imageview.get(i).setVisibility(View.VISIBLE);
		// imageLoader.displayImage(urls[i], list_imageview.get(i), options,
		// null);
		// final Intent intent = new Intent(mContext,ViewPagerActivity.class);
		// intent.putExtra("ID", i);
		// intent.putExtra("imgurl", urls);
		// list_imageview.get(i).setOnClickListener(new OnClickListener() {
		// @Override
		// public void onClick(View v) {
		// mContext.startActivity(intent);
		// ((Activity) mContext).overridePendingTransition(R.anim.zoom_enter,0);
		// }
		// });
		// }
		// }
		// mholder.iv_mchat_laba.setVisibility(View.VISIBLE);
		// mPlayer.setOnCompletionListener(new OnCompletionListener() {
		// @Override
		// public void onCompletion(MediaPlayer mp) {
		// }
		// });
		// mholder.rl_mchat.setOnClickListener(new OnClickListener() {
		// @Override
		// public void onClick(View v) {
		// if(mPlayer!=null&&mPlayer.isPlaying()){
		// stopPlaying();
		// }else{
		// new Thread(){
		// @Override
		// public void run() {
		// startPlaying(voicepath);
		// super.run();
		// }
		// }.start();
		// }
		// }
		// });
		// mholder.tv_mchat_nickname.setText(nickname);
		// mholder.tv_mchat_createtime.setText(date);
		// try {
		// SpannableString spannableString =
		// ExpressionUtil.getExpressionString(mContext, content,
		// zhengze,densityDpi);
		// mholder.tv_mchat_content.setText(spannableString);
		// } catch (NumberFormatException e) {
		// e.printStackTrace();
		// } catch (SecurityException e) {
		// e.printStackTrace();
		// } catch (IllegalArgumentException e) {
		// e.printStackTrace();
		// }
		// mholder.tv_mchat_del.setOnClickListener(new OnClickListener() {
		// @Override
		// public void onClick(View v) {
		// delDialog(hdcommentid,position);
		// }
		// });
		// break;
		// case 17:
		// //头像
		// imageLoader.displayImage(headimg, mholder.iv_mchat, options_head,
		// null);
		// //评论的图片
		// list_imageview.add(mholder.iv_mchat1);
		// list_imageview.add(mholder.iv_mchat2);
		// list_imageview.add(mholder.iv_mchat3);
		//
		// if(imagepath!=null){
		// urls = imagepath.split(",");
		// for(int i=0;i<urls.length;i++){
		// list_imageview.get(i).setVisibility(View.VISIBLE);
		// imageLoader.displayImage(urls[i], list_imageview.get(i), options,
		// null);
		// final Intent intent = new Intent(mContext,ViewPagerActivity.class);
		// intent.putExtra("ID", i);
		// intent.putExtra("imgurl", urls);
		// list_imageview.get(i).setOnClickListener(new OnClickListener() {
		// @Override
		// public void onClick(View v) {
		// mContext.startActivity(intent);
		// ((Activity) mContext).overridePendingTransition(R.anim.zoom_enter,0);
		// }
		// });
		// }
		// }
		// mholder.iv_mchat_laba.setVisibility(View.VISIBLE);
		// mPlayer.setOnCompletionListener(new OnCompletionListener() {
		// @Override
		// public void onCompletion(MediaPlayer mp) {
		// }
		// });
		// mholder.rl_mchat.setOnClickListener(new OnClickListener() {
		// @Override
		// public void onClick(View v) {
		// if(mPlayer!=null&&mPlayer.isPlaying()){
		// stopPlaying();
		// }else{
		// new Thread(){
		// @Override
		// public void run() {
		// startPlaying(voicepath);
		// super.run();
		// }
		// }.start();
		// }
		// }
		// });
		// mholder.tv_mchat_nickname.setText(nickname);
		// mholder.tv_mchat_createtime.setText(date);
		// try {
		// SpannableString spannableString =
		// ExpressionUtil.getExpressionString(mContext, content,
		// zhengze,densityDpi);
		// mholder.tv_mchat_content.setText(spannableString);
		// } catch (NumberFormatException e) {
		// e.printStackTrace();
		// } catch (SecurityException e) {
		// e.printStackTrace();
		// } catch (IllegalArgumentException e) {
		// e.printStackTrace();
		// }
		// mholder.tv_mchat_del.setOnClickListener(new OnClickListener() {
		// @Override
		// public void onClick(View v) {
		// delDialog(hdcommentid,position);
		// }
		// });
		//
		// break;
		// case 18:
		// //头像
		// imageLoader.displayImage(headimg, mholder.iv_mchat, options_head,
		// null);
		// //评论的图片
		// list_imageview.add(mholder.iv_mchat1);
		// list_imageview.add(mholder.iv_mchat2);
		// list_imageview.add(mholder.iv_mchat3);
		// list_imageview.add(mholder.iv_mchat4);
		//
		// if(imagepath!=null){
		// urls = imagepath.split(",");
		// for(int i=0;i<urls.length;i++){
		// list_imageview.get(i).setVisibility(View.VISIBLE);
		// imageLoader.displayImage(urls[i], list_imageview.get(i), options,
		// null);
		// final Intent intent = new Intent(mContext,ViewPagerActivity.class);
		// intent.putExtra("ID", i);
		// intent.putExtra("imgurl", urls);
		// list_imageview.get(i).setOnClickListener(new OnClickListener() {
		// @Override
		// public void onClick(View v) {
		// mContext.startActivity(intent);
		// ((Activity) mContext).overridePendingTransition(R.anim.zoom_enter,0);
		// }
		// });
		// }
		// }
		// mholder.iv_mchat_laba.setVisibility(View.VISIBLE);
		// mPlayer.setOnCompletionListener(new OnCompletionListener() {
		// @Override
		// public void onCompletion(MediaPlayer mp) {
		// }
		// });
		// mholder.rl_mchat.setOnClickListener(new OnClickListener() {
		// @Override
		// public void onClick(View v) {
		// if(mPlayer!=null&&mPlayer.isPlaying()){
		// stopPlaying();
		// }else{
		// new Thread(){
		// @Override
		// public void run() {
		// startPlaying(voicepath);
		// super.run();
		// }
		// }.start();
		// }
		// }
		// });
		// mholder.tv_mchat_nickname.setText(nickname);
		// mholder.tv_mchat_createtime.setText(date);
		// try {
		// SpannableString spannableString =
		// ExpressionUtil.getExpressionString(mContext, content,
		// zhengze,densityDpi);
		// mholder.tv_mchat_content.setText(spannableString);
		// } catch (NumberFormatException e) {
		// e.printStackTrace();
		// } catch (SecurityException e) {
		// e.printStackTrace();
		// } catch (IllegalArgumentException e) {
		// e.printStackTrace();
		// }
		// mholder.tv_mchat_del.setOnClickListener(new OnClickListener() {
		// @Override
		// public void onClick(View v) {
		// delDialog(hdcommentid,position);
		// }
		// });
		// break;
		// case 19:
		// //头像
		// imageLoader.displayImage(headimg, mholder.iv_mchat, options_head,
		// null);
		//
		// mholder.iv_mchat_laba.setVisibility(View.VISIBLE);
		// mPlayer.setOnCompletionListener(new OnCompletionListener() {
		// @Override
		// public void onCompletion(MediaPlayer mp) {
		// }
		// });
		// mholder.rl_mchat.setOnClickListener(new OnClickListener() {
		// @Override
		// public void onClick(View v) {
		// if(mPlayer!=null&&mPlayer.isPlaying()){
		// stopPlaying();
		// }else{
		// new Thread(){
		// @Override
		// public void run() {
		// startPlaying(voicepath);
		// super.run();
		// }
		// }.start();
		// }
		// }
		// });
		// mholder.tv_mchat_nickname.setText(nickname);
		// mholder.tv_mchat_createtime.setText(date);
		// try {
		// SpannableString spannableString =
		// ExpressionUtil.getExpressionString(mContext, content,
		// zhengze,densityDpi);
		// mholder.tv_mchat_content.setText(spannableString);
		// } catch (NumberFormatException e) {
		// e.printStackTrace();
		// } catch (SecurityException e) {
		// e.printStackTrace();
		// } catch (IllegalArgumentException e) {
		// e.printStackTrace();
		// }
		// mholder.tv_mchat_del.setOnClickListener(new OnClickListener() {
		// @Override
		// public void onClick(View v) {
		// delDialog(hdcommentid,position);
		// }
		// });
		// break;
		case 1:
			imageLoader.displayImage(headimg, yholder.iv_ychat, options_head,
					null);
			yholder.tv_ychat_nickname.setText(nickname);
			yholder.tv_ychat_createtime.setText(date);
			try {
				SpannableString spannableString = ExpressionUtil
						.getExpressionString(mContext, content, zhengze,
								densityDpi);
				yholder.tv_ychat_content.setText(spannableString);
			} catch (NumberFormatException e) {
				e.printStackTrace();
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			}
			yholder.btn_ychat_reply.setVisibility(View.GONE);
			yholder.tv_ychat_deldetail.setVisibility(View.VISIBLE);
			yholder.tv_ychat_deldetail
					.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							delDialog(hdcommentid, position);
						}
					});
			break;
		case 11:
			// 头像
			imageLoader.displayImage(headimg, yholder.iv_ychat, options_head,
					null);
			// 评论的图片
			list_imageview.add(yholder.iv_ychat1);

			if (imagepath != null) {
				urls = imagepath.split(",");
				for (int i = 0; i < urls.length; i++) {
					list_imageview.get(i).setVisibility(View.VISIBLE);
					imageLoader.displayImage(urls[i], list_imageview.get(i),
							options, null);
					final Intent intent = new Intent(mContext,
							ViewPagerActivity.class);
					intent.putExtra("ID", i);
					intent.putExtra("imgurl", urls);
					list_imageview.get(i).setOnClickListener(
							new OnClickListener() {
								@Override
								public void onClick(View v) {
									mContext.startActivity(intent);
									((Activity) mContext)
											.overridePendingTransition(
													R.anim.zoom_enter, 0);
								}
							});
				}
			}
			yholder.tv_ychat_nickname.setText(nickname);
			yholder.tv_ychat_createtime.setText(date);
			yholder.iv_ychat_dj.setVisibility(View.VISIBLE);
			try {
				SpannableString spannableString = ExpressionUtil
						.getExpressionString(mContext, content, zhengze,
								densityDpi);
				yholder.tv_ychat_content.setText(spannableString);
			} catch (NumberFormatException e) {
				e.printStackTrace();
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			}
			yholder.btn_ychat_reply.setVisibility(View.GONE);
			yholder.tv_ychat_deldetail.setVisibility(View.VISIBLE);
			yholder.tv_ychat_deldetail
					.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							delDialog(hdcommentid, position);
						}
					});
			break;
		case 12:
			// 头像
			imageLoader.displayImage(headimg, yholder.iv_ychat, options_head,
					null);
			// 评论的图片
			list_imageview.add(yholder.iv_ychat1);
			list_imageview.add(yholder.iv_ychat2);

			if (imagepath != null) {
				urls = imagepath.split(",");
				for (int i = 0; i < urls.length; i++) {
					list_imageview.get(i).setVisibility(View.VISIBLE);
					imageLoader.displayImage(urls[i], list_imageview.get(i),
							options, null);
					final Intent intent = new Intent(mContext,
							ViewPagerActivity.class);
					intent.putExtra("ID", i);
					intent.putExtra("imgurl", urls);
					list_imageview.get(i).setOnClickListener(
							new OnClickListener() {
								@Override
								public void onClick(View v) {
									mContext.startActivity(intent);
									((Activity) mContext)
											.overridePendingTransition(
													R.anim.zoom_enter, 0);
								}
							});
				}
			}
			yholder.tv_ychat_nickname.setText(nickname);
			yholder.tv_ychat_createtime.setText(date);
			yholder.iv_ychat_dj.setVisibility(View.VISIBLE);
			try {
				SpannableString spannableString = ExpressionUtil
						.getExpressionString(mContext, content, zhengze,
								densityDpi);
				yholder.tv_ychat_content.setText(spannableString);
			} catch (NumberFormatException e) {
				e.printStackTrace();
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			}
			yholder.btn_ychat_reply.setVisibility(View.GONE);
			yholder.tv_ychat_deldetail.setVisibility(View.VISIBLE);
			yholder.tv_ychat_deldetail
					.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							delDialog(hdcommentid, position);
						}
					});
			break;

		case 13:
			// 头像
			imageLoader.displayImage(headimg, yholder.iv_ychat, options_head,
					null);
			// 评论的图片
			list_imageview.add(yholder.iv_ychat1);
			list_imageview.add(yholder.iv_ychat2);
			list_imageview.add(yholder.iv_ychat3);

			if (imagepath != null) {
				urls = imagepath.split(",");
				for (int i = 0; i < urls.length; i++) {
					list_imageview.get(i).setVisibility(View.VISIBLE);
					imageLoader.displayImage(urls[i], list_imageview.get(i),
							options, null);
					final Intent intent = new Intent(mContext,
							ViewPagerActivity.class);
					intent.putExtra("ID", i);
					intent.putExtra("imgurl", urls);
					list_imageview.get(i).setOnClickListener(
							new OnClickListener() {
								@Override
								public void onClick(View v) {
									mContext.startActivity(intent);
									((Activity) mContext)
											.overridePendingTransition(
													R.anim.zoom_enter, 0);
								}
							});
				}
			}
			yholder.tv_ychat_nickname.setText(nickname);
			yholder.tv_ychat_createtime.setText(date);
			yholder.iv_ychat_dj.setVisibility(View.VISIBLE);
			try {
				SpannableString spannableString = ExpressionUtil
						.getExpressionString(mContext, content, zhengze,
								densityDpi);
				yholder.tv_ychat_content.setText(spannableString);
			} catch (NumberFormatException e) {
				e.printStackTrace();
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			}
			yholder.btn_ychat_reply.setVisibility(View.GONE);
			yholder.tv_ychat_deldetail.setVisibility(View.VISIBLE);
			yholder.tv_ychat_deldetail
					.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							delDialog(hdcommentid, position);
						}
					});
			break;
		case 14:
			// 头像
			imageLoader.displayImage(headimg, yholder.iv_ychat, options_head,
					null);
			// 评论的图片
			list_imageview.add(yholder.iv_ychat1);
			list_imageview.add(yholder.iv_ychat2);
			list_imageview.add(yholder.iv_ychat3);
			list_imageview.add(yholder.iv_ychat4);

			if (imagepath != null) {
				urls = imagepath.split(",");
				for (int i = 0; i < urls.length; i++) {
					list_imageview.get(i).setVisibility(View.VISIBLE);
					imageLoader.displayImage(urls[i], list_imageview.get(i),
							options, null);
					final Intent intent = new Intent(mContext,
							ViewPagerActivity.class);
					intent.putExtra("ID", i);
					intent.putExtra("imgurl", urls);
					list_imageview.get(i).setOnClickListener(
							new OnClickListener() {
								@Override
								public void onClick(View v) {
									mContext.startActivity(intent);
									((Activity) mContext)
											.overridePendingTransition(
													R.anim.zoom_enter, 0);
								}
							});
				}
			}
			yholder.tv_ychat_nickname.setText(nickname);
			yholder.tv_ychat_createtime.setText(date);
			yholder.iv_ychat_dj.setVisibility(View.VISIBLE);
			try {
				SpannableString spannableString = ExpressionUtil
						.getExpressionString(mContext, content, zhengze,
								densityDpi);
				yholder.tv_ychat_content.setText(spannableString);
			} catch (NumberFormatException e) {
				e.printStackTrace();
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			}
			yholder.btn_ychat_reply.setVisibility(View.GONE);
			yholder.tv_ychat_deldetail.setVisibility(View.VISIBLE);
			yholder.tv_ychat_deldetail
					.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							delDialog(hdcommentid, position);
						}
					});
			break;
		case 15:
			// 头像
			imageLoader.displayImage(headimg, yholder.iv_ychat, options_head,
					null);
			// 评论的图片
			list_imageview.add(yholder.iv_ychat1);

			if (imagepath != null) {
				urls = imagepath.split(",");
				for (int i = 0; i < urls.length; i++) {
					list_imageview.get(i).setVisibility(View.VISIBLE);
					imageLoader.displayImage(urls[i], list_imageview.get(i),
							options, null);
					final Intent intent = new Intent(mContext,
							ViewPagerActivity.class);
					intent.putExtra("ID", i);
					intent.putExtra("imgurl", urls);
					list_imageview.get(i).setOnClickListener(
							new OnClickListener() {
								@Override
								public void onClick(View v) {
									mContext.startActivity(intent);
									((Activity) mContext)
											.overridePendingTransition(
													R.anim.zoom_enter, 0);
								}
							});
				}
			}
			// yholder.iv_ychat_laba.setVisibility(View.VISIBLE);
			// mPlayer.setOnCompletionListener(new OnCompletionListener() {
			// @Override
			// public void onCompletion(MediaPlayer mp) {
			// }
			// });
			// yholder.rl_ychat.setOnClickListener(new OnClickListener() {
			// @Override
			// public void onClick(View v) {
			// if(mPlayer!=null&&mPlayer.isPlaying()){
			// stopPlaying();
			// }else{
			// new Thread(){
			// @Override
			// public void run() {
			// startPlaying(voicepath);
			// super.run();
			// }
			// }.start();
			// }
			// }
			// });
			yholder.tv_ychat_nickname.setText(nickname);
			yholder.tv_ychat_createtime.setText(date);
			try {
				SpannableString spannableString = ExpressionUtil
						.getExpressionString(mContext, content, zhengze,
								densityDpi);
				yholder.tv_ychat_content.setText(spannableString);
			} catch (NumberFormatException e) {
				e.printStackTrace();
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			}
			yholder.btn_ychat_reply.setVisibility(View.GONE);
			yholder.tv_ychat_deldetail.setVisibility(View.VISIBLE);
			yholder.tv_ychat_deldetail
					.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							delDialog(hdcommentid, position);
						}
					});
			break;
		case 16:
			// 头像
			imageLoader.displayImage(headimg, yholder.iv_ychat, options_head,
					null);
			// 评论的图片
			list_imageview.add(yholder.iv_ychat1);
			list_imageview.add(yholder.iv_ychat2);

			if (imagepath != null) {
				urls = imagepath.split(",");
				for (int i = 0; i < urls.length; i++) {
					list_imageview.get(i).setVisibility(View.VISIBLE);
					imageLoader.displayImage(urls[i], list_imageview.get(i),
							options, null);
					final Intent intent = new Intent(mContext,
							ViewPagerActivity.class);
					intent.putExtra("ID", i);
					intent.putExtra("imgurl", urls);
					list_imageview.get(i).setOnClickListener(
							new OnClickListener() {
								@Override
								public void onClick(View v) {
									mContext.startActivity(intent);
									((Activity) mContext)
											.overridePendingTransition(
													R.anim.zoom_enter, 0);
								}
							});
				}
			}
			// yholder.iv_ychat_laba.setVisibility(View.VISIBLE);
			// mPlayer.setOnCompletionListener(new OnCompletionListener() {
			// @Override
			// public void onCompletion(MediaPlayer mp) {
			// }
			// });
			// yholder.rl_ychat.setOnClickListener(new OnClickListener() {
			// @Override
			// public void onClick(View v) {
			// if(mPlayer!=null&&mPlayer.isPlaying()){
			// stopPlaying();
			// }else{
			// new Thread(){
			// @Override
			// public void run() {
			// startPlaying(voicepath);
			// super.run();
			// }
			// }.start();
			// }
			// }
			// });
			yholder.tv_ychat_nickname.setText(nickname);
			yholder.tv_ychat_createtime.setText(date);
			try {
				SpannableString spannableString = ExpressionUtil
						.getExpressionString(mContext, content, zhengze,
								densityDpi);
				yholder.tv_ychat_content.setText(spannableString);
			} catch (NumberFormatException e) {
				e.printStackTrace();
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			}
			yholder.btn_ychat_reply.setVisibility(View.GONE);
			yholder.tv_ychat_deldetail.setVisibility(View.VISIBLE);
			yholder.tv_ychat_deldetail
					.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							delDialog(hdcommentid, position);
						}
					});
			break;
		case 17:
			// 头像
			imageLoader.displayImage(headimg, yholder.iv_ychat, options_head,
					null);
			// 评论的图片
			list_imageview.add(yholder.iv_ychat1);
			list_imageview.add(yholder.iv_ychat2);
			list_imageview.add(yholder.iv_ychat3);

			if (imagepath != null) {
				urls = imagepath.split(",");
				for (int i = 0; i < urls.length; i++) {
					list_imageview.get(i).setVisibility(View.VISIBLE);
					imageLoader.displayImage(urls[i], list_imageview.get(i),
							options, null);
					final Intent intent = new Intent(mContext,
							ViewPagerActivity.class);
					intent.putExtra("ID", i);
					intent.putExtra("imgurl", urls);
					list_imageview.get(i).setOnClickListener(
							new OnClickListener() {
								@Override
								public void onClick(View v) {
									mContext.startActivity(intent);
									((Activity) mContext)
											.overridePendingTransition(
													R.anim.zoom_enter, 0);
								}
							});
				}
			}
			// yholder.iv_ychat_laba.setVisibility(View.VISIBLE);
			// mPlayer.setOnCompletionListener(new OnCompletionListener() {
			// @Override
			// public void onCompletion(MediaPlayer mp) {
			// }
			// });
			// yholder.rl_ychat.setOnClickListener(new OnClickListener() {
			// @Override
			// public void onClick(View v) {
			// if(mPlayer!=null&&mPlayer.isPlaying()){
			// stopPlaying();
			// }else{
			// new Thread(){
			// @Override
			// public void run() {
			// startPlaying(voicepath);
			// super.run();
			// }
			// }.start();
			// }
			// }
			// });
			yholder.tv_ychat_nickname.setText(nickname);
			yholder.tv_ychat_createtime.setText(date);
			try {
				SpannableString spannableString = ExpressionUtil
						.getExpressionString(mContext, content, zhengze,
								densityDpi);
				yholder.tv_ychat_content.setText(spannableString);
			} catch (NumberFormatException e) {
				e.printStackTrace();
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			}
			yholder.btn_ychat_reply.setVisibility(View.GONE);
			yholder.tv_ychat_deldetail.setVisibility(View.VISIBLE);
			yholder.tv_ychat_deldetail
					.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							delDialog(hdcommentid, position);
						}
					});
			break;
		case 18:
			// 头像
			imageLoader.displayImage(headimg, yholder.iv_ychat, options_head,
					null);
			// 评论的图片
			list_imageview.add(yholder.iv_ychat1);
			list_imageview.add(yholder.iv_ychat2);
			list_imageview.add(yholder.iv_ychat3);
			list_imageview.add(yholder.iv_ychat4);

			if (imagepath != null) {
				urls = imagepath.split(",");
				for (int i = 0; i < urls.length; i++) {
					list_imageview.get(i).setVisibility(View.VISIBLE);
					imageLoader.displayImage(urls[i], list_imageview.get(i),
							options, null);
					final Intent intent = new Intent(mContext,
							ViewPagerActivity.class);
					intent.putExtra("ID", i);
					intent.putExtra("imgurl", urls);
					list_imageview.get(i).setOnClickListener(
							new OnClickListener() {
								@Override
								public void onClick(View v) {
									mContext.startActivity(intent);
									((Activity) mContext)
											.overridePendingTransition(
													R.anim.zoom_enter, 0);
								}
							});
				}
			}
			// yholder.iv_ychat_laba.setVisibility(View.VISIBLE);
			// mPlayer.setOnCompletionListener(new OnCompletionListener() {
			// @Override
			// public void onCompletion(MediaPlayer mp) {
			// }
			// });
			// yholder.rl_ychat.setOnClickListener(new OnClickListener() {
			// @Override
			// public void onClick(View v) {
			// if(mPlayer!=null&&mPlayer.isPlaying()){
			// stopPlaying();
			// }else{
			// new Thread(){
			// @Override
			// public void run() {
			// startPlaying(voicepath);
			// super.run();
			// }
			// }.start();
			// }
			// }
			// });
			yholder.tv_ychat_nickname.setText(nickname);
			yholder.tv_ychat_createtime.setText(date);
			try {
				SpannableString spannableString = ExpressionUtil
						.getExpressionString(mContext, content, zhengze,
								densityDpi);
				yholder.tv_ychat_content.setText(spannableString);
			} catch (NumberFormatException e) {
				e.printStackTrace();
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			}
			yholder.btn_ychat_reply.setVisibility(View.GONE);
			yholder.tv_ychat_deldetail.setVisibility(View.VISIBLE);
			yholder.tv_ychat_deldetail
					.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							delDialog(hdcommentid, position);
						}
					});
			break;
		case 19:
			// 头像
			imageLoader.displayImage(headimg, yholder.iv_ychat, options_head,
					null);
			// yholder.iv_ychat_laba.setVisibility(View.VISIBLE);
			// mPlayer.setOnCompletionListener(new OnCompletionListener() {
			// @Override
			// public void onCompletion(MediaPlayer mp) {
			// }
			// });
			// yholder.rl_ychat.setOnClickListener(new OnClickListener() {
			// @Override
			// public void onClick(View v) {
			// if(mPlayer!=null&&mPlayer.isPlaying()){
			// stopPlaying();
			// }else{
			// new Thread(){
			// @Override
			// public void run() {
			// startPlaying(voicepath);
			// super.run();
			// }
			// }.start();
			// }
			// }
			// });

			yholder.tv_ychat_nickname.setText(nickname);
			yholder.tv_ychat_createtime.setText(date);
			yholder.iv_ychat_dj.setVisibility(View.VISIBLE);
			try {
				SpannableString spannableString = ExpressionUtil
						.getExpressionString(mContext, content, zhengze,
								densityDpi);
				yholder.tv_ychat_content.setText(spannableString);
			} catch (NumberFormatException e) {
				e.printStackTrace();
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			}
			yholder.btn_ychat_reply.setVisibility(View.GONE);
			yholder.tv_ychat_deldetail.setVisibility(View.VISIBLE);
			yholder.tv_ychat_deldetail
					.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							delDialog(hdcommentid, position);
						}
					});
			break;
		case 2:
			imageLoader.displayImage(headimg, yholder.iv_ychat, options_head,
					null);
			yholder.tv_ychat_nickname.setText(nickname);
			yholder.tv_ychat_createtime.setText(date);
			try {
				SpannableString spannableString = ExpressionUtil
						.getExpressionString(mContext, content, zhengze,
								densityDpi);
				yholder.tv_ychat_content.setText(spannableString);
			} catch (NumberFormatException e) {
				e.printStackTrace();
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			}

			if (uid != null) {
				yholder.rl_ychat
						.setOnLongClickListener(new OnLongClickListener() {
							@Override
							public boolean onLongClick(View v) {
								PopupMenu menu = new PopupMenu(mContext);
								menu.setOnItemSelectedListener(new OnItemSelectedListener() {
									@Override
									public void onItemSelected(MenuItem item) {
										switch (item.getItemId()) {
										case REPORT:
											reportdialog(hdcommentid);
											break;
										}
									}
								});
								menu.add(REPORT, R.string.report);
								menu.show(null);
								return false;
							}
						});
			}

			yholder.btn_ychat_reply.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					reply(nickname);
				}
			});
			break;
		case 21:
			// 头像
			imageLoader.displayImage(headimg, yholder.iv_ychat, options_head,
					null);
			// 评论的图片
			list_imageview.add(yholder.iv_ychat1);

			if (imagepath != null) {
				urls = imagepath.split(",");
				for (int i = 0; i < urls.length; i++) {
					list_imageview.get(i).setVisibility(View.VISIBLE);
					imageLoader.displayImage(urls[i], list_imageview.get(i),
							options, null);
					final Intent intent = new Intent(mContext,
							ViewPagerActivity.class);
					intent.putExtra("ID", i);
					intent.putExtra("imgurl", urls);
					list_imageview.get(i).setOnClickListener(
							new OnClickListener() {
								@Override
								public void onClick(View v) {
									mContext.startActivity(intent);
									((Activity) mContext)
											.overridePendingTransition(
													R.anim.zoom_enter, 0);
								}
							});
				}
			}
			yholder.tv_ychat_nickname.setText(nickname);
			yholder.tv_ychat_createtime.setText(date);
			yholder.iv_ychat_dj.setVisibility(View.VISIBLE);
			try {
				SpannableString spannableString = ExpressionUtil
						.getExpressionString(mContext, content, zhengze,
								densityDpi);
				yholder.tv_ychat_content.setText(spannableString);
			} catch (NumberFormatException e) {
				e.printStackTrace();
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			}
			if (uid != null) {
				yholder.rl_ychat
						.setOnLongClickListener(new OnLongClickListener() {
							@Override
							public boolean onLongClick(View v) {
								PopupMenu menu = new PopupMenu(mContext);
								menu.setOnItemSelectedListener(new OnItemSelectedListener() {
									@Override
									public void onItemSelected(MenuItem item) {
										switch (item.getItemId()) {
										case REPORT:
											reportdialog(hdcommentid);
											break;
										}
									}
								});
								menu.add(REPORT, R.string.report);
								menu.show(null);
								return false;
							}
						});
			}
			yholder.btn_ychat_reply.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					reply(nickname);
				}
			});
			break;
		case 22:
			// 头像
			imageLoader.displayImage(headimg, yholder.iv_ychat, options_head,
					null);
			// 评论的图片
			list_imageview.add(yholder.iv_ychat1);
			list_imageview.add(yholder.iv_ychat2);

			if (imagepath != null) {
				urls = imagepath.split(",");
				for (int i = 0; i < urls.length; i++) {
					list_imageview.get(i).setVisibility(View.VISIBLE);
					imageLoader.displayImage(urls[i], list_imageview.get(i),
							options, null);
					final Intent intent = new Intent(mContext,
							ViewPagerActivity.class);
					intent.putExtra("ID", i);
					intent.putExtra("imgurl", urls);
					list_imageview.get(i).setOnClickListener(
							new OnClickListener() {
								@Override
								public void onClick(View v) {
									mContext.startActivity(intent);
									((Activity) mContext)
											.overridePendingTransition(
													R.anim.zoom_enter, 0);
								}
							});
				}
			}
			yholder.tv_ychat_nickname.setText(nickname);
			yholder.tv_ychat_createtime.setText(date);
			yholder.iv_ychat_dj.setVisibility(View.VISIBLE);
			try {
				SpannableString spannableString = ExpressionUtil
						.getExpressionString(mContext, content, zhengze,
								densityDpi);
				yholder.tv_ychat_content.setText(spannableString);
			} catch (NumberFormatException e) {
				e.printStackTrace();
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			}
			if (uid != null) {
				yholder.rl_ychat
						.setOnLongClickListener(new OnLongClickListener() {
							@Override
							public boolean onLongClick(View v) {
								PopupMenu menu = new PopupMenu(mContext);
								menu.setOnItemSelectedListener(new OnItemSelectedListener() {
									@Override
									public void onItemSelected(MenuItem item) {
										switch (item.getItemId()) {
										case REPORT:
											reportdialog(hdcommentid);
											break;
										}
									}
								});
								menu.add(REPORT, R.string.report);
								menu.show(null);
								return false;
							}
						});
			}
			yholder.btn_ychat_reply.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					reply(nickname);
				}
			});
			break;

		case 23:
			// 头像
			imageLoader.displayImage(headimg, yholder.iv_ychat, options_head,
					null);
			// 评论的图片
			list_imageview.add(yholder.iv_ychat1);
			list_imageview.add(yholder.iv_ychat2);
			list_imageview.add(yholder.iv_ychat3);

			if (imagepath != null) {
				urls = imagepath.split(",");
				for (int i = 0; i < urls.length; i++) {
					list_imageview.get(i).setVisibility(View.VISIBLE);
					imageLoader.displayImage(urls[i], list_imageview.get(i),
							options, null);
					final Intent intent = new Intent(mContext,
							ViewPagerActivity.class);
					intent.putExtra("ID", i);
					intent.putExtra("imgurl", urls);
					list_imageview.get(i).setOnClickListener(
							new OnClickListener() {
								@Override
								public void onClick(View v) {
									mContext.startActivity(intent);
									((Activity) mContext)
											.overridePendingTransition(
													R.anim.zoom_enter, 0);
								}
							});
				}
			}
			yholder.tv_ychat_nickname.setText(nickname);
			yholder.tv_ychat_createtime.setText(date);
			yholder.iv_ychat_dj.setVisibility(View.VISIBLE);
			try {
				SpannableString spannableString = ExpressionUtil
						.getExpressionString(mContext, content, zhengze,
								densityDpi);
				yholder.tv_ychat_content.setText(spannableString);
			} catch (NumberFormatException e) {
				e.printStackTrace();
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			}
			if (uid != null) {
				yholder.rl_ychat
						.setOnLongClickListener(new OnLongClickListener() {
							@Override
							public boolean onLongClick(View v) {
								PopupMenu menu = new PopupMenu(mContext);
								menu.setOnItemSelectedListener(new OnItemSelectedListener() {
									@Override
									public void onItemSelected(MenuItem item) {
										switch (item.getItemId()) {
										case REPORT:
											reportdialog(hdcommentid);
											break;
										}
									}
								});
								menu.add(REPORT, R.string.report);
								menu.show(null);
								return false;
							}
						});
			}
			yholder.btn_ychat_reply.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					reply(nickname);
				}
			});
			break;
		case 24:
			// 头像
			imageLoader.displayImage(headimg, yholder.iv_ychat, options_head,
					null);
			// 评论的图片
			list_imageview.add(yholder.iv_ychat1);
			list_imageview.add(yholder.iv_ychat2);
			list_imageview.add(yholder.iv_ychat3);
			list_imageview.add(yholder.iv_ychat4);

			if (imagepath != null) {
				urls = imagepath.split(",");
				for (int i = 0; i < urls.length; i++) {
					list_imageview.get(i).setVisibility(View.VISIBLE);
					imageLoader.displayImage(urls[i], list_imageview.get(i),
							options, null);
					final Intent intent = new Intent(mContext,
							ViewPagerActivity.class);
					intent.putExtra("ID", i);
					intent.putExtra("imgurl", urls);
					list_imageview.get(i).setOnClickListener(
							new OnClickListener() {
								@Override
								public void onClick(View v) {
									mContext.startActivity(intent);
									((Activity) mContext)
											.overridePendingTransition(
													R.anim.zoom_enter, 0);
								}
							});
				}
			}
			yholder.tv_ychat_nickname.setText(nickname);
			yholder.tv_ychat_createtime.setText(date);
			yholder.iv_ychat_dj.setVisibility(View.VISIBLE);
			try {
				SpannableString spannableString = ExpressionUtil
						.getExpressionString(mContext, content, zhengze,
								densityDpi);
				yholder.tv_ychat_content.setText(spannableString);
			} catch (NumberFormatException e) {
				e.printStackTrace();
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			}
			if (uid != null) {
				yholder.rl_ychat
						.setOnLongClickListener(new OnLongClickListener() {
							@Override
							public boolean onLongClick(View v) {
								PopupMenu menu = new PopupMenu(mContext);
								menu.setOnItemSelectedListener(new OnItemSelectedListener() {
									@Override
									public void onItemSelected(MenuItem item) {
										switch (item.getItemId()) {
										case REPORT:
											reportdialog(hdcommentid);
											break;
										}
									}
								});
								menu.add(REPORT, R.string.report);
								menu.show(null);
								return false;
							}
						});
			}
			yholder.btn_ychat_reply.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					reply(nickname);
				}
			});
			break;
		case 25:
			// 头像
			imageLoader.displayImage(headimg, yholder.iv_ychat, options_head,
					null);
			// 评论的图片
			list_imageview.add(yholder.iv_ychat1);

			if (imagepath != null) {
				urls = imagepath.split(",");
				for (int i = 0; i < urls.length; i++) {
					list_imageview.get(i).setVisibility(View.VISIBLE);
					imageLoader.displayImage(urls[i], list_imageview.get(i),
							options, null);
					final Intent intent = new Intent(mContext,
							ViewPagerActivity.class);
					intent.putExtra("ID", i);
					intent.putExtra("imgurl", urls);
					list_imageview.get(i).setOnClickListener(
							new OnClickListener() {
								@Override
								public void onClick(View v) {
									mContext.startActivity(intent);
									((Activity) mContext)
											.overridePendingTransition(
													R.anim.zoom_enter, 0);
								}
							});
				}
			}
			// yholder.iv_ychat_laba.setVisibility(View.VISIBLE);
			// mPlayer.setOnCompletionListener(new OnCompletionListener() {
			// @Override
			// public void onCompletion(MediaPlayer mp) {
			// }
			// });
			// yholder.rl_ychat.setOnClickListener(new OnClickListener() {
			// @Override
			// public void onClick(View v) {
			// if(mPlayer!=null&&mPlayer.isPlaying()){
			// stopPlaying();
			// }else{
			// new Thread(){
			// @Override
			// public void run() {
			// startPlaying(voicepath);
			// super.run();
			// }
			// }.start();
			// }
			// }
			// });
			yholder.tv_ychat_nickname.setText(nickname);
			yholder.tv_ychat_createtime.setText(date);
			try {
				SpannableString spannableString = ExpressionUtil
						.getExpressionString(mContext, content, zhengze,
								densityDpi);
				yholder.tv_ychat_content.setText(spannableString);
			} catch (NumberFormatException e) {
				e.printStackTrace();
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			}
			if (uid != null) {
				yholder.rl_ychat
						.setOnLongClickListener(new OnLongClickListener() {
							@Override
							public boolean onLongClick(View v) {
								PopupMenu menu = new PopupMenu(mContext);
								menu.setOnItemSelectedListener(new OnItemSelectedListener() {
									@Override
									public void onItemSelected(MenuItem item) {
										switch (item.getItemId()) {
										case REPORT:
											reportdialog(hdcommentid);
											break;
										}
									}
								});
								menu.add(REPORT, R.string.report);
								menu.show(null);
								return false;
							}
						});
			}
			yholder.btn_ychat_reply.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					reply(nickname);
				}
			});
			break;
		case 26:
			// 头像
			imageLoader.displayImage(headimg, yholder.iv_ychat, options_head,
					null);
			// 评论的图片
			list_imageview.add(yholder.iv_ychat1);
			list_imageview.add(yholder.iv_ychat2);

			if (imagepath != null) {
				urls = imagepath.split(",");
				for (int i = 0; i < urls.length; i++) {
					list_imageview.get(i).setVisibility(View.VISIBLE);
					imageLoader.displayImage(urls[i], list_imageview.get(i),
							options, null);
					final Intent intent = new Intent(mContext,
							ViewPagerActivity.class);
					intent.putExtra("ID", i);
					intent.putExtra("imgurl", urls);
					list_imageview.get(i).setOnClickListener(
							new OnClickListener() {
								@Override
								public void onClick(View v) {
									mContext.startActivity(intent);
									((Activity) mContext)
											.overridePendingTransition(
													R.anim.zoom_enter, 0);
								}
							});
				}
			}
			// yholder.iv_ychat_laba.setVisibility(View.VISIBLE);
			// mPlayer.setOnCompletionListener(new OnCompletionListener() {
			// @Override
			// public void onCompletion(MediaPlayer mp) {
			// }
			// });
			// yholder.rl_ychat.setOnClickListener(new OnClickListener() {
			// @Override
			// public void onClick(View v) {
			// if(mPlayer!=null&&mPlayer.isPlaying()){
			// stopPlaying();
			// }else{
			// new Thread(){
			// @Override
			// public void run() {
			// startPlaying(voicepath);
			// super.run();
			// }
			// }.start();
			// }
			// }
			// });
			yholder.tv_ychat_nickname.setText(nickname);
			yholder.tv_ychat_createtime.setText(date);
			try {
				SpannableString spannableString = ExpressionUtil
						.getExpressionString(mContext, content, zhengze,
								densityDpi);
				yholder.tv_ychat_content.setText(spannableString);
			} catch (NumberFormatException e) {
				e.printStackTrace();
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			}
			if (uid != null) {
				yholder.rl_ychat
						.setOnLongClickListener(new OnLongClickListener() {
							@Override
							public boolean onLongClick(View v) {
								PopupMenu menu = new PopupMenu(mContext);
								menu.setOnItemSelectedListener(new OnItemSelectedListener() {
									@Override
									public void onItemSelected(MenuItem item) {
										switch (item.getItemId()) {
										case REPORT:
											reportdialog(hdcommentid);
											break;
										}
									}
								});
								menu.add(REPORT, R.string.report);
								menu.show(null);
								return false;
							}
						});
			}
			yholder.btn_ychat_reply.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					reply(nickname);
				}
			});
			break;
		case 27:
			// 头像
			imageLoader.displayImage(headimg, yholder.iv_ychat, options_head,
					null);
			// 评论的图片
			list_imageview.add(yholder.iv_ychat1);
			list_imageview.add(yholder.iv_ychat2);
			list_imageview.add(yholder.iv_ychat3);

			if (imagepath != null) {
				urls = imagepath.split(",");
				for (int i = 0; i < urls.length; i++) {
					list_imageview.get(i).setVisibility(View.VISIBLE);
					imageLoader.displayImage(urls[i], list_imageview.get(i),
							options, null);
					final Intent intent = new Intent(mContext,
							ViewPagerActivity.class);
					intent.putExtra("ID", i);
					intent.putExtra("imgurl", urls);
					list_imageview.get(i).setOnClickListener(
							new OnClickListener() {
								@Override
								public void onClick(View v) {
									mContext.startActivity(intent);
									((Activity) mContext)
											.overridePendingTransition(
													R.anim.zoom_enter, 0);
								}
							});
				}
			}
			// yholder.iv_ychat_laba.setVisibility(View.VISIBLE);
			// mPlayer.setOnCompletionListener(new OnCompletionListener() {
			// @Override
			// public void onCompletion(MediaPlayer mp) {
			// }
			// });
			// yholder.rl_ychat.setOnClickListener(new OnClickListener() {
			// @Override
			// public void onClick(View v) {
			// if(mPlayer!=null&&mPlayer.isPlaying()){
			// stopPlaying();
			// }else{
			// new Thread(){
			// @Override
			// public void run() {
			// startPlaying(voicepath);
			// super.run();
			// }
			// }.start();
			// }
			// }
			// });
			yholder.tv_ychat_nickname.setText(nickname);
			yholder.tv_ychat_createtime.setText(date);
			try {
				SpannableString spannableString = ExpressionUtil
						.getExpressionString(mContext, content, zhengze,
								densityDpi);
				yholder.tv_ychat_content.setText(spannableString);
			} catch (NumberFormatException e) {
				e.printStackTrace();
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			}
			if (uid != null) {
				yholder.rl_ychat
						.setOnLongClickListener(new OnLongClickListener() {
							@Override
							public boolean onLongClick(View v) {
								PopupMenu menu = new PopupMenu(mContext);
								menu.setOnItemSelectedListener(new OnItemSelectedListener() {
									@Override
									public void onItemSelected(MenuItem item) {
										switch (item.getItemId()) {
										case REPORT:
											reportdialog(hdcommentid);
											break;
										}
									}
								});
								menu.add(REPORT, R.string.report);
								menu.show(null);
								return false;
							}
						});
			}
			yholder.btn_ychat_reply.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					reply(nickname);
				}
			});
			break;
		case 28:
			// 头像
			imageLoader.displayImage(headimg, yholder.iv_ychat, options_head,
					null);
			// 评论的图片
			list_imageview.add(yholder.iv_ychat1);
			list_imageview.add(yholder.iv_ychat2);
			list_imageview.add(yholder.iv_ychat3);
			list_imageview.add(yholder.iv_ychat4);

			if (imagepath != null) {
				urls = imagepath.split(",");
				for (int i = 0; i < urls.length; i++) {
					list_imageview.get(i).setVisibility(View.VISIBLE);
					imageLoader.displayImage(urls[i], list_imageview.get(i),
							options, null);
					final Intent intent = new Intent(mContext,
							ViewPagerActivity.class);
					intent.putExtra("ID", i);
					intent.putExtra("imgurl", urls);
					list_imageview.get(i).setOnClickListener(
							new OnClickListener() {
								@Override
								public void onClick(View v) {
									mContext.startActivity(intent);
									((Activity) mContext)
											.overridePendingTransition(
													R.anim.zoom_enter, 0);
								}
							});
				}
			}
			// yholder.iv_ychat_laba.setVisibility(View.VISIBLE);
			// mPlayer.setOnCompletionListener(new OnCompletionListener() {
			// @Override
			// public void onCompletion(MediaPlayer mp) {
			// }
			// });
			// yholder.rl_ychat.setOnClickListener(new OnClickListener() {
			// @Override
			// public void onClick(View v) {
			// if(mPlayer!=null&&mPlayer.isPlaying()){
			// stopPlaying();
			// }else{
			// new Thread(){
			// @Override
			// public void run() {
			// startPlaying(voicepath);
			// super.run();
			// }
			// }.start();
			// }
			// }
			// });
			yholder.tv_ychat_nickname.setText(nickname);
			yholder.tv_ychat_createtime.setText(date);
			try {
				SpannableString spannableString = ExpressionUtil
						.getExpressionString(mContext, content, zhengze,
								densityDpi);
				yholder.tv_ychat_content.setText(spannableString);
			} catch (NumberFormatException e) {
				e.printStackTrace();
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			}
			if (uid != null) {
				yholder.rl_ychat
						.setOnLongClickListener(new OnLongClickListener() {
							@Override
							public boolean onLongClick(View v) {
								PopupMenu menu = new PopupMenu(mContext);
								menu.setOnItemSelectedListener(new OnItemSelectedListener() {
									@Override
									public void onItemSelected(MenuItem item) {
										switch (item.getItemId()) {
										case REPORT:
											reportdialog(hdcommentid);
											break;
										}
									}
								});
								menu.add(REPORT, R.string.report);
								menu.show(null);
								return false;
							}
						});
			}
			yholder.btn_ychat_reply.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					reply(nickname);
				}
			});
			break;
		case 29:
			// 头像
			imageLoader.displayImage(headimg, yholder.iv_ychat, options_head,
					null);
			// yholder.iv_ychat_laba.setVisibility(View.VISIBLE);
			// mPlayer.setOnCompletionListener(new OnCompletionListener() {
			// @Override
			// public void onCompletion(MediaPlayer mp) {
			// }
			// });
			// yholder.rl_ychat.setOnClickListener(new OnClickListener() {
			// @Override
			// public void onClick(View v) {
			// if(mPlayer!=null&&mPlayer.isPlaying()){
			// stopPlaying();
			// }else{
			// new Thread(){
			// @Override
			// public void run() {
			// startPlaying(voicepath);
			// super.run();
			// }
			// }.start();
			// }
			// }
			// });

			yholder.tv_ychat_nickname.setText(nickname);
			yholder.tv_ychat_createtime.setText(date);
			yholder.iv_ychat_dj.setVisibility(View.VISIBLE);
			try {
				SpannableString spannableString = ExpressionUtil
						.getExpressionString(mContext, content, zhengze,
								densityDpi);
				yholder.tv_ychat_content.setText(spannableString);
			} catch (NumberFormatException e) {
				e.printStackTrace();
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			}
			if (uid != null) {
				yholder.rl_ychat
						.setOnLongClickListener(new OnLongClickListener() {
							@Override
							public boolean onLongClick(View v) {
								PopupMenu menu = new PopupMenu(mContext);
								menu.setOnItemSelectedListener(new OnItemSelectedListener() {
									@Override
									public void onItemSelected(MenuItem item) {
										switch (item.getItemId()) {
										case REPORT:
											reportdialog(hdcommentid);
											break;
										}
									}
								});
								menu.add(REPORT, R.string.report);
								menu.show(null);
								return false;
							}
						});
			}
			yholder.btn_ychat_reply.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					reply(nickname);
				}
			});
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

	private void reportdialog(final String hdcommentid) {
		AlertDialog.Builder builder = new Builder(mContext);
		builder.setMessage(mContext.getResources().getString(
				R.string.report_tips));
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
										.GetStringFromUrl(reportychat_url
												+ hdcommentid);
								try {
									if (resultString != null) {
										jsonObject = new JSONObject(
												resultString);
										String code = jsonObject
												.getString("code");
										if (code.equals("200")) {
											handler.sendEmptyMessage(2);
										} else {
											handler.sendEmptyMessage(3);
										}
									} else {
										handler.sendEmptyMessage(3);
									}
								} catch (JSONException e) {
									handler.sendEmptyMessage(3);
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

	class YchatHolder {
		TextView tv_ychat_nickname;
		TextView tv_ychat_createtime;
		TextView tv_ychat_content;
		TextView tv_ychat_report;
		ImageView iv_ychat;
		ImageView iv_ychat1;
		ImageView iv_ychat2;
		ImageView iv_ychat3;
		ImageView iv_ychat4;
		ImageView iv_ychat_laba;
		ImageView iv_ychat_reply;
		Button btn_ychat_reply;
		Button tv_ychat_deldetail;
		RelativeLayout rl_ychat;
		ImageView iv_ychat_dj;
	}

	class MchatHolder {
		TextView tv_mchat_nickname;
		TextView tv_mchat_createtime;
		TextView tv_mchat_content;
		TextView tv_mchat_del;
		ImageView iv_mchat;
		ImageView iv_mchat1;
		ImageView iv_mchat2;
		ImageView iv_mchat3;
		ImageView iv_mchat4;
		ImageView iv_mchat_laba;
		ImageView iv_mchat_reply;
		RelativeLayout rl_mchat;
	}

	private void startPlaying(String path) {
		try {
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
}

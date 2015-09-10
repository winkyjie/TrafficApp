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
import cn.fszt.trafficapp.domain.PinglunData;
import cn.fszt.trafficapp.ui.activity.ActCommentEditActivity;
import cn.fszt.trafficapp.util.HttpUtil;
import cn.fszt.trafficapp.widget.expression.ExpressionUtil;
import cn.fszt.trafficapp.widget.msg.AppMsg;
import cn.fszt.trafficapp.widget.photoview.DisplayImageOptionsUtil;
import cn.fszt.trafficapp.widget.photoview.MenuItem;
import cn.fszt.trafficapp.widget.photoview.PopupMenu;
import cn.fszt.trafficapp.widget.photoview.ViewPagerActivity;
import cn.fszt.trafficapp.widget.photoview.PopupMenu.OnItemSelectedListener;

//fun 任务 适配器
public class ActDetailAdapter extends BaseAdapter {
	private static final int PINGLUN = 99;
	private Context mContext;
	private List<PinglunData> arrays;
	private LayoutInflater inflater;
	private String delmpl_url,reportypl_url,id,uid;
	private ImageLoader imageLoader;
	private DisplayImageOptions options,options_head;
	private int densityDpi;
	AppMsg.Style style = AppMsg.STYLE_INFO;
	AppMsg appMsg;
	private static final int REPORT = 1;
	
	public ActDetailAdapter(Context mContext, List<PinglunData> arrays,LayoutInflater inflater,String delmpl_url,String reportypl_url,String uid,int densityDpi,String id){
		this.mContext = mContext;
		this.arrays = arrays;
		this.inflater = inflater;
		this.delmpl_url = delmpl_url;
		this.reportypl_url = reportypl_url;
		this.uid = uid;
		this.id = id;
		this.densityDpi = densityDpi;
		imageLoader = ImageLoader.getInstance();
		
		options = DisplayImageOptionsUtil.getOptions(
				R.drawable.ic_empty, R.drawable.ic_error,R.drawable.default_image);
		
		options_head = DisplayImageOptionsUtil.getOptions(
				R.drawable.default_head, R.drawable.default_head,R.drawable.default_image,
				new RoundedBitmapDisplayer(90));
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
		
			//是自己
			if("1".equals(istop)){
				//有图片是dj
				if(!"".equals(imagepath)&&"1".equals(isdj)){
					urls = imagepath.split(",");
					if(urls.length==1){
						return 11;
					}else if(urls.length==2){
						return 12;
					}else if(urls.length==3){
						return 13;
					}else if(urls.length==4){
						return 14;
					}
				}
				//有图片不是dj
				else if(!"".equals(imagepath)&&"0".equals(isdj)){
					urls = imagepath.split(",");
					if(urls.length==1){
						return 15;
					}else if(urls.length==2){
						return 16;
					}else if(urls.length==3){
						return 17;
					}else if(urls.length==4){
						return 18;
					}
				}
				//没有图片是dj
				else if("".equals(imagepath)&&"1".equals(isdj)){
					return 19;
				}
				//没有图片不是dj
				else{
					return 1;
				}
				
			}
			//非自己
			else{
				//有图片是dj
				if(!"".equals(imagepath)&&"1".equals(isdj)){
					urls = imagepath.split(",");
					if(urls.length==1){
						return 21;
					}else if(urls.length==2){
						return 22;
					}else if(urls.length==3){
						return 23;
					}else if(urls.length==4){
						return 24;
					}
				}
				//有图片不是dj
				else if(!"".equals(imagepath)&&"0".equals(isdj)){
					urls = imagepath.split(",");
					if(urls.length==1){
						return 25;
					}else if(urls.length==2){
						return 26;
					}else if(urls.length==3){
						return 27;
					}else if(urls.length==4){
						return 28;
					}
				}
				//没有图片是dj
				else if("".equals(imagepath)&&"1".equals(isdj)){
					return 29;
				}
				//没图片不是dj
				else{
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
		int type = getItemViewType(position); 
		final String hdinfocommentid = arrays.get(position).getHdinfocommentid();
		final String nickname = arrays.get(position).getNickname();
		String date = arrays.get(position).getCreatedate();
		String imageUrl = arrays.get(position).getHeadimg();  
		String imagePath = arrays.get(position).getImagepath();  
    	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			Date ddate = sdf.parse(date);
			SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd HH:mm");
			date = dateFormat.format(ddate);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		MplHolder mholder = null;
		YplHolder yholder = null;
		if (convertView == null) {
			switch(type){
			case 1:
				convertView  = inflater.inflate(R.layout.item_ypl_list, null);
				yholder = new YplHolder();
				yholder.iv_ypl = (ImageView) convertView.findViewById(R.id.iv_ypl);
				yholder.iv_ypl1 = (ImageView) convertView.findViewById(R.id.iv_ypl_img1);
				yholder.iv_ypl2 = (ImageView) convertView.findViewById(R.id.iv_ypl_img2);
				yholder.iv_ypl3 = (ImageView) convertView.findViewById(R.id.iv_ypl_img3);
				yholder.iv_ypl4 = (ImageView) convertView.findViewById(R.id.iv_ypl_img4);
				yholder.tv_ypl_nickname = (TextView) convertView.findViewById(R.id.tv_ypl_nickname);
				yholder.tv_ypl_createtime = (TextView) convertView.findViewById(R.id.tv_ypl_createtime);
				yholder.tv_ypl_content = (TextView) convertView.findViewById(R.id.tv_ypl_content);
				yholder.tv_ypl_report = (TextView) convertView.findViewById(R.id.tv_ypl_report);
				yholder.rl_ychat = (RelativeLayout) convertView.findViewById(R.id.rl_ychat);
				yholder.btn_ypl_reply = (Button) convertView.findViewById(R.id.btn_ypl_reply);
				yholder.tv_ypl_deldetail = (Button) convertView.findViewById(R.id.tv_ypl_deldetail);
				yholder.iv_ypl_dj = (ImageView) convertView.findViewById(R.id.iv_ypl_dj);
				convertView.setTag(yholder);
			break;
			case 11:
				convertView  = inflater.inflate(R.layout.item_ypl_list, null);
				yholder = new YplHolder();
				yholder.iv_ypl = (ImageView) convertView.findViewById(R.id.iv_ypl);
				yholder.iv_ypl1 = (ImageView) convertView.findViewById(R.id.iv_ypl_img1);
				yholder.iv_ypl2 = (ImageView) convertView.findViewById(R.id.iv_ypl_img2);
				yholder.iv_ypl3 = (ImageView) convertView.findViewById(R.id.iv_ypl_img3);
				yholder.iv_ypl4 = (ImageView) convertView.findViewById(R.id.iv_ypl_img4);
				yholder.tv_ypl_nickname = (TextView) convertView.findViewById(R.id.tv_ypl_nickname);
				yholder.tv_ypl_createtime = (TextView) convertView.findViewById(R.id.tv_ypl_createtime);
				yholder.tv_ypl_content = (TextView) convertView.findViewById(R.id.tv_ypl_content);
				yholder.tv_ypl_report = (TextView) convertView.findViewById(R.id.tv_ypl_report);
				yholder.rl_ychat = (RelativeLayout) convertView.findViewById(R.id.rl_ychat);
				yholder.btn_ypl_reply = (Button) convertView.findViewById(R.id.btn_ypl_reply);
				yholder.tv_ypl_deldetail = (Button) convertView.findViewById(R.id.tv_ypl_deldetail);
				yholder.iv_ypl_dj = (ImageView) convertView.findViewById(R.id.iv_ypl_dj);
				convertView.setTag(yholder);
			break;
			case 12:
				convertView  = inflater.inflate(R.layout.item_ypl_list, null);
				yholder = new YplHolder();
				yholder.iv_ypl = (ImageView) convertView.findViewById(R.id.iv_ypl);
				yholder.iv_ypl1 = (ImageView) convertView.findViewById(R.id.iv_ypl_img1);
				yholder.iv_ypl2 = (ImageView) convertView.findViewById(R.id.iv_ypl_img2);
				yholder.iv_ypl3 = (ImageView) convertView.findViewById(R.id.iv_ypl_img3);
				yholder.iv_ypl4 = (ImageView) convertView.findViewById(R.id.iv_ypl_img4);
				yholder.tv_ypl_nickname = (TextView) convertView.findViewById(R.id.tv_ypl_nickname);
				yholder.tv_ypl_createtime = (TextView) convertView.findViewById(R.id.tv_ypl_createtime);
				yholder.tv_ypl_content = (TextView) convertView.findViewById(R.id.tv_ypl_content);
				yholder.tv_ypl_report = (TextView) convertView.findViewById(R.id.tv_ypl_report);
				yholder.rl_ychat = (RelativeLayout) convertView.findViewById(R.id.rl_ychat);
				yholder.btn_ypl_reply = (Button) convertView.findViewById(R.id.btn_ypl_reply);
				yholder.tv_ypl_deldetail = (Button) convertView.findViewById(R.id.tv_ypl_deldetail);
				yholder.iv_ypl_dj = (ImageView) convertView.findViewById(R.id.iv_ypl_dj);
				convertView.setTag(yholder);
			break;
			case 13:
				convertView  = inflater.inflate(R.layout.item_ypl_list, null);
				yholder = new YplHolder();
				yholder.iv_ypl = (ImageView) convertView.findViewById(R.id.iv_ypl);
				yholder.iv_ypl1 = (ImageView) convertView.findViewById(R.id.iv_ypl_img1);
				yholder.iv_ypl2 = (ImageView) convertView.findViewById(R.id.iv_ypl_img2);
				yholder.iv_ypl3 = (ImageView) convertView.findViewById(R.id.iv_ypl_img3);
				yholder.iv_ypl4 = (ImageView) convertView.findViewById(R.id.iv_ypl_img4);
				yholder.tv_ypl_nickname = (TextView) convertView.findViewById(R.id.tv_ypl_nickname);
				yholder.tv_ypl_createtime = (TextView) convertView.findViewById(R.id.tv_ypl_createtime);
				yholder.tv_ypl_content = (TextView) convertView.findViewById(R.id.tv_ypl_content);
				yholder.tv_ypl_report = (TextView) convertView.findViewById(R.id.tv_ypl_report);
				yholder.rl_ychat = (RelativeLayout) convertView.findViewById(R.id.rl_ychat);
				yholder.btn_ypl_reply = (Button) convertView.findViewById(R.id.btn_ypl_reply);
				yholder.tv_ypl_deldetail = (Button) convertView.findViewById(R.id.tv_ypl_deldetail);
				yholder.iv_ypl_dj = (ImageView) convertView.findViewById(R.id.iv_ypl_dj);
				convertView.setTag(yholder);
			break;
			case 14:
				convertView  = inflater.inflate(R.layout.item_ypl_list, null);
				yholder = new YplHolder();
				yholder.iv_ypl = (ImageView) convertView.findViewById(R.id.iv_ypl);
				yholder.iv_ypl1 = (ImageView) convertView.findViewById(R.id.iv_ypl_img1);
				yholder.iv_ypl2 = (ImageView) convertView.findViewById(R.id.iv_ypl_img2);
				yholder.iv_ypl3 = (ImageView) convertView.findViewById(R.id.iv_ypl_img3);
				yholder.iv_ypl4 = (ImageView) convertView.findViewById(R.id.iv_ypl_img4);
				yholder.tv_ypl_nickname = (TextView) convertView.findViewById(R.id.tv_ypl_nickname);
				yholder.tv_ypl_createtime = (TextView) convertView.findViewById(R.id.tv_ypl_createtime);
				yholder.tv_ypl_content = (TextView) convertView.findViewById(R.id.tv_ypl_content);
				yholder.tv_ypl_report = (TextView) convertView.findViewById(R.id.tv_ypl_report);
				yholder.rl_ychat = (RelativeLayout) convertView.findViewById(R.id.rl_ychat);
				yholder.btn_ypl_reply = (Button) convertView.findViewById(R.id.btn_ypl_reply);
				yholder.tv_ypl_deldetail = (Button) convertView.findViewById(R.id.tv_ypl_deldetail);
				yholder.iv_ypl_dj = (ImageView) convertView.findViewById(R.id.iv_ypl_dj);
				convertView.setTag(yholder);
			break;
			case 15:
				convertView  = inflater.inflate(R.layout.item_ypl_list, null);
				yholder = new YplHolder();
				yholder.iv_ypl = (ImageView) convertView.findViewById(R.id.iv_ypl);
				yholder.iv_ypl1 = (ImageView) convertView.findViewById(R.id.iv_ypl_img1);
				yholder.iv_ypl2 = (ImageView) convertView.findViewById(R.id.iv_ypl_img2);
				yholder.iv_ypl3 = (ImageView) convertView.findViewById(R.id.iv_ypl_img3);
				yholder.iv_ypl4 = (ImageView) convertView.findViewById(R.id.iv_ypl_img4);
				yholder.tv_ypl_nickname = (TextView) convertView.findViewById(R.id.tv_ypl_nickname);
				yholder.tv_ypl_createtime = (TextView) convertView.findViewById(R.id.tv_ypl_createtime);
				yholder.tv_ypl_content = (TextView) convertView.findViewById(R.id.tv_ypl_content);
				yholder.tv_ypl_report = (TextView) convertView.findViewById(R.id.tv_ypl_report);
				yholder.rl_ychat = (RelativeLayout) convertView.findViewById(R.id.rl_ychat);
				yholder.btn_ypl_reply = (Button) convertView.findViewById(R.id.btn_ypl_reply);
				yholder.tv_ypl_deldetail = (Button) convertView.findViewById(R.id.tv_ypl_deldetail);
				yholder.iv_ypl_dj = (ImageView) convertView.findViewById(R.id.iv_ypl_dj);
				convertView.setTag(yholder);
			break;
			case 16:
				convertView  = inflater.inflate(R.layout.item_ypl_list, null);
				yholder = new YplHolder();
				yholder.iv_ypl = (ImageView) convertView.findViewById(R.id.iv_ypl);
				yholder.iv_ypl1 = (ImageView) convertView.findViewById(R.id.iv_ypl_img1);
				yholder.iv_ypl2 = (ImageView) convertView.findViewById(R.id.iv_ypl_img2);
				yholder.iv_ypl3 = (ImageView) convertView.findViewById(R.id.iv_ypl_img3);
				yholder.iv_ypl4 = (ImageView) convertView.findViewById(R.id.iv_ypl_img4);
				yholder.tv_ypl_nickname = (TextView) convertView.findViewById(R.id.tv_ypl_nickname);
				yholder.tv_ypl_createtime = (TextView) convertView.findViewById(R.id.tv_ypl_createtime);
				yholder.tv_ypl_content = (TextView) convertView.findViewById(R.id.tv_ypl_content);
				yholder.tv_ypl_report = (TextView) convertView.findViewById(R.id.tv_ypl_report);
				yholder.rl_ychat = (RelativeLayout) convertView.findViewById(R.id.rl_ychat);
				yholder.btn_ypl_reply = (Button) convertView.findViewById(R.id.btn_ypl_reply);
				yholder.tv_ypl_deldetail = (Button) convertView.findViewById(R.id.tv_ypl_deldetail);
				yholder.iv_ypl_dj = (ImageView) convertView.findViewById(R.id.iv_ypl_dj);
				convertView.setTag(yholder);
			break;
			case 17:
				convertView  = inflater.inflate(R.layout.item_ypl_list, null);
				yholder = new YplHolder();
				yholder.iv_ypl = (ImageView) convertView.findViewById(R.id.iv_ypl);
				yholder.iv_ypl1 = (ImageView) convertView.findViewById(R.id.iv_ypl_img1);
				yholder.iv_ypl2 = (ImageView) convertView.findViewById(R.id.iv_ypl_img2);
				yholder.iv_ypl3 = (ImageView) convertView.findViewById(R.id.iv_ypl_img3);
				yholder.iv_ypl4 = (ImageView) convertView.findViewById(R.id.iv_ypl_img4);
				yholder.tv_ypl_nickname = (TextView) convertView.findViewById(R.id.tv_ypl_nickname);
				yholder.tv_ypl_createtime = (TextView) convertView.findViewById(R.id.tv_ypl_createtime);
				yholder.tv_ypl_content = (TextView) convertView.findViewById(R.id.tv_ypl_content);
				yholder.tv_ypl_report = (TextView) convertView.findViewById(R.id.tv_ypl_report);
				yholder.rl_ychat = (RelativeLayout) convertView.findViewById(R.id.rl_ychat);
				yholder.btn_ypl_reply = (Button) convertView.findViewById(R.id.btn_ypl_reply);
				yholder.tv_ypl_deldetail = (Button) convertView.findViewById(R.id.tv_ypl_deldetail);
				yholder.iv_ypl_dj = (ImageView) convertView.findViewById(R.id.iv_ypl_dj);
				convertView.setTag(yholder);
			break;
			case 18:
				convertView  = inflater.inflate(R.layout.item_ypl_list, null);
				yholder = new YplHolder();
				yholder.iv_ypl = (ImageView) convertView.findViewById(R.id.iv_ypl);
				yholder.iv_ypl1 = (ImageView) convertView.findViewById(R.id.iv_ypl_img1);
				yholder.iv_ypl2 = (ImageView) convertView.findViewById(R.id.iv_ypl_img2);
				yholder.iv_ypl3 = (ImageView) convertView.findViewById(R.id.iv_ypl_img3);
				yholder.iv_ypl4 = (ImageView) convertView.findViewById(R.id.iv_ypl_img4);
				yholder.tv_ypl_nickname = (TextView) convertView.findViewById(R.id.tv_ypl_nickname);
				yholder.tv_ypl_createtime = (TextView) convertView.findViewById(R.id.tv_ypl_createtime);
				yholder.tv_ypl_content = (TextView) convertView.findViewById(R.id.tv_ypl_content);
				yholder.tv_ypl_report = (TextView) convertView.findViewById(R.id.tv_ypl_report);
				yholder.rl_ychat = (RelativeLayout) convertView.findViewById(R.id.rl_ychat);
				yholder.btn_ypl_reply = (Button) convertView.findViewById(R.id.btn_ypl_reply);
				yholder.tv_ypl_deldetail = (Button) convertView.findViewById(R.id.tv_ypl_deldetail);
				yholder.iv_ypl_dj = (ImageView) convertView.findViewById(R.id.iv_ypl_dj);
				convertView.setTag(yholder);
			break;
			case 19:
				convertView  = inflater.inflate(R.layout.item_ypl_list, null);
				yholder = new YplHolder();
				yholder.iv_ypl = (ImageView) convertView.findViewById(R.id.iv_ypl);
				yholder.iv_ypl1 = (ImageView) convertView.findViewById(R.id.iv_ypl_img1);
				yholder.iv_ypl2 = (ImageView) convertView.findViewById(R.id.iv_ypl_img2);
				yholder.iv_ypl3 = (ImageView) convertView.findViewById(R.id.iv_ypl_img3);
				yholder.iv_ypl4 = (ImageView) convertView.findViewById(R.id.iv_ypl_img4);
				yholder.tv_ypl_nickname = (TextView) convertView.findViewById(R.id.tv_ypl_nickname);
				yholder.tv_ypl_createtime = (TextView) convertView.findViewById(R.id.tv_ypl_createtime);
				yholder.tv_ypl_content = (TextView) convertView.findViewById(R.id.tv_ypl_content);
				yholder.tv_ypl_report = (TextView) convertView.findViewById(R.id.tv_ypl_report);
				yholder.rl_ychat = (RelativeLayout) convertView.findViewById(R.id.rl_ychat);
				yholder.btn_ypl_reply = (Button) convertView.findViewById(R.id.btn_ypl_reply);
				yholder.tv_ypl_deldetail = (Button) convertView.findViewById(R.id.tv_ypl_deldetail);
				yholder.iv_ypl_dj = (ImageView) convertView.findViewById(R.id.iv_ypl_dj);
				convertView.setTag(yholder);
			break;
			case 2:
				convertView  = inflater.inflate(R.layout.item_ypl_list, null);
				yholder = new YplHolder();
				yholder.iv_ypl = (ImageView) convertView.findViewById(R.id.iv_ypl);
				yholder.iv_ypl1 = (ImageView) convertView.findViewById(R.id.iv_ypl_img1);
				yholder.iv_ypl2 = (ImageView) convertView.findViewById(R.id.iv_ypl_img2);
				yholder.iv_ypl3 = (ImageView) convertView.findViewById(R.id.iv_ypl_img3);
				yholder.iv_ypl4 = (ImageView) convertView.findViewById(R.id.iv_ypl_img4);
				yholder.tv_ypl_nickname = (TextView) convertView.findViewById(R.id.tv_ypl_nickname);
				yholder.tv_ypl_createtime = (TextView) convertView.findViewById(R.id.tv_ypl_createtime);
				yholder.tv_ypl_content = (TextView) convertView.findViewById(R.id.tv_ypl_content);
				yholder.tv_ypl_report = (TextView) convertView.findViewById(R.id.tv_ypl_report);
				yholder.rl_ychat = (RelativeLayout) convertView.findViewById(R.id.rl_ychat);
				yholder.btn_ypl_reply = (Button) convertView.findViewById(R.id.btn_ypl_reply);
				yholder.iv_ypl_dj = (ImageView) convertView.findViewById(R.id.iv_ypl_dj);
				convertView.setTag(yholder);
			break;
			case 21:
				convertView  = inflater.inflate(R.layout.item_ypl_list, null);
				yholder = new YplHolder();
				yholder.iv_ypl = (ImageView) convertView.findViewById(R.id.iv_ypl);
				yholder.iv_ypl1 = (ImageView) convertView.findViewById(R.id.iv_ypl_img1);
				yholder.iv_ypl2 = (ImageView) convertView.findViewById(R.id.iv_ypl_img2);
				yholder.iv_ypl3 = (ImageView) convertView.findViewById(R.id.iv_ypl_img3);
				yholder.iv_ypl4 = (ImageView) convertView.findViewById(R.id.iv_ypl_img4);
				yholder.tv_ypl_nickname = (TextView) convertView.findViewById(R.id.tv_ypl_nickname);
				yholder.tv_ypl_createtime = (TextView) convertView.findViewById(R.id.tv_ypl_createtime);
				yholder.tv_ypl_content = (TextView) convertView.findViewById(R.id.tv_ypl_content);
				yholder.tv_ypl_report = (TextView) convertView.findViewById(R.id.tv_ypl_report);
				yholder.rl_ychat = (RelativeLayout) convertView.findViewById(R.id.rl_ychat);
				yholder.btn_ypl_reply = (Button) convertView.findViewById(R.id.btn_ypl_reply);
				yholder.iv_ypl_dj = (ImageView) convertView.findViewById(R.id.iv_ypl_dj);
				convertView.setTag(yholder);
			break;
			case 22:
				convertView  = inflater.inflate(R.layout.item_ypl_list, null);
				yholder = new YplHolder();
				yholder.iv_ypl = (ImageView) convertView.findViewById(R.id.iv_ypl);
				yholder.iv_ypl1 = (ImageView) convertView.findViewById(R.id.iv_ypl_img1);
				yholder.iv_ypl2 = (ImageView) convertView.findViewById(R.id.iv_ypl_img2);
				yholder.iv_ypl3 = (ImageView) convertView.findViewById(R.id.iv_ypl_img3);
				yholder.iv_ypl4 = (ImageView) convertView.findViewById(R.id.iv_ypl_img4);
				yholder.tv_ypl_nickname = (TextView) convertView.findViewById(R.id.tv_ypl_nickname);
				yholder.tv_ypl_createtime = (TextView) convertView.findViewById(R.id.tv_ypl_createtime);
				yholder.tv_ypl_content = (TextView) convertView.findViewById(R.id.tv_ypl_content);
				yholder.tv_ypl_report = (TextView) convertView.findViewById(R.id.tv_ypl_report);
				yholder.rl_ychat = (RelativeLayout) convertView.findViewById(R.id.rl_ychat);
				yholder.btn_ypl_reply = (Button) convertView.findViewById(R.id.btn_ypl_reply);
				yholder.iv_ypl_dj = (ImageView) convertView.findViewById(R.id.iv_ypl_dj);
				convertView.setTag(yholder);
			break;
			case 23:
				convertView  = inflater.inflate(R.layout.item_ypl_list, null);
				yholder = new YplHolder();
				yholder.iv_ypl = (ImageView) convertView.findViewById(R.id.iv_ypl);
				yholder.iv_ypl1 = (ImageView) convertView.findViewById(R.id.iv_ypl_img1);
				yholder.iv_ypl2 = (ImageView) convertView.findViewById(R.id.iv_ypl_img2);
				yholder.iv_ypl3 = (ImageView) convertView.findViewById(R.id.iv_ypl_img3);
				yholder.iv_ypl4 = (ImageView) convertView.findViewById(R.id.iv_ypl_img4);
				yholder.tv_ypl_nickname = (TextView) convertView.findViewById(R.id.tv_ypl_nickname);
				yholder.tv_ypl_createtime = (TextView) convertView.findViewById(R.id.tv_ypl_createtime);
				yholder.tv_ypl_content = (TextView) convertView.findViewById(R.id.tv_ypl_content);
				yholder.tv_ypl_report = (TextView) convertView.findViewById(R.id.tv_ypl_report);
				yholder.rl_ychat = (RelativeLayout) convertView.findViewById(R.id.rl_ychat);
				yholder.btn_ypl_reply = (Button) convertView.findViewById(R.id.btn_ypl_reply);
				yholder.iv_ypl_dj = (ImageView) convertView.findViewById(R.id.iv_ypl_dj);
				convertView.setTag(yholder);
			break;
			case 24:
				convertView  = inflater.inflate(R.layout.item_ypl_list, null);
				yholder = new YplHolder();
				yholder.iv_ypl = (ImageView) convertView.findViewById(R.id.iv_ypl);
				yholder.iv_ypl1 = (ImageView) convertView.findViewById(R.id.iv_ypl_img1);
				yholder.iv_ypl2 = (ImageView) convertView.findViewById(R.id.iv_ypl_img2);
				yholder.iv_ypl3 = (ImageView) convertView.findViewById(R.id.iv_ypl_img3);
				yholder.iv_ypl4 = (ImageView) convertView.findViewById(R.id.iv_ypl_img4);
				yholder.tv_ypl_nickname = (TextView) convertView.findViewById(R.id.tv_ypl_nickname);
				yholder.tv_ypl_createtime = (TextView) convertView.findViewById(R.id.tv_ypl_createtime);
				yholder.tv_ypl_content = (TextView) convertView.findViewById(R.id.tv_ypl_content);
				yholder.tv_ypl_report = (TextView) convertView.findViewById(R.id.tv_ypl_report);
				yholder.rl_ychat = (RelativeLayout) convertView.findViewById(R.id.rl_ychat);
				yholder.btn_ypl_reply = (Button) convertView.findViewById(R.id.btn_ypl_reply);
				yholder.iv_ypl_dj = (ImageView) convertView.findViewById(R.id.iv_ypl_dj);
				convertView.setTag(yholder);
			break;
			case 25:
				convertView  = inflater.inflate(R.layout.item_ypl_list, null);
				yholder = new YplHolder();
				yholder.iv_ypl = (ImageView) convertView.findViewById(R.id.iv_ypl);
				yholder.iv_ypl1 = (ImageView) convertView.findViewById(R.id.iv_ypl_img1);
				yholder.iv_ypl2 = (ImageView) convertView.findViewById(R.id.iv_ypl_img2);
				yholder.iv_ypl3 = (ImageView) convertView.findViewById(R.id.iv_ypl_img3);
				yholder.iv_ypl4 = (ImageView) convertView.findViewById(R.id.iv_ypl_img4);
				yholder.tv_ypl_nickname = (TextView) convertView.findViewById(R.id.tv_ypl_nickname);
				yholder.tv_ypl_createtime = (TextView) convertView.findViewById(R.id.tv_ypl_createtime);
				yholder.tv_ypl_content = (TextView) convertView.findViewById(R.id.tv_ypl_content);
				yholder.tv_ypl_report = (TextView) convertView.findViewById(R.id.tv_ypl_report);
				yholder.rl_ychat = (RelativeLayout) convertView.findViewById(R.id.rl_ychat);
				yholder.btn_ypl_reply = (Button) convertView.findViewById(R.id.btn_ypl_reply);
				yholder.iv_ypl_dj = (ImageView) convertView.findViewById(R.id.iv_ypl_dj);
				convertView.setTag(yholder);
			break;
			case 26:
				convertView  = inflater.inflate(R.layout.item_ypl_list, null);
				yholder = new YplHolder();
				yholder.iv_ypl = (ImageView) convertView.findViewById(R.id.iv_ypl);
				yholder.iv_ypl1 = (ImageView) convertView.findViewById(R.id.iv_ypl_img1);
				yholder.iv_ypl2 = (ImageView) convertView.findViewById(R.id.iv_ypl_img2);
				yholder.iv_ypl3 = (ImageView) convertView.findViewById(R.id.iv_ypl_img3);
				yholder.iv_ypl4 = (ImageView) convertView.findViewById(R.id.iv_ypl_img4);
				yholder.tv_ypl_nickname = (TextView) convertView.findViewById(R.id.tv_ypl_nickname);
				yholder.tv_ypl_createtime = (TextView) convertView.findViewById(R.id.tv_ypl_createtime);
				yholder.tv_ypl_content = (TextView) convertView.findViewById(R.id.tv_ypl_content);
				yholder.tv_ypl_report = (TextView) convertView.findViewById(R.id.tv_ypl_report);
				yholder.rl_ychat = (RelativeLayout) convertView.findViewById(R.id.rl_ychat);
				yholder.btn_ypl_reply = (Button) convertView.findViewById(R.id.btn_ypl_reply);
				yholder.iv_ypl_dj = (ImageView) convertView.findViewById(R.id.iv_ypl_dj);
				convertView.setTag(yholder);
			break;
			case 27:
				convertView  = inflater.inflate(R.layout.item_ypl_list, null);
				yholder = new YplHolder();
				yholder.iv_ypl = (ImageView) convertView.findViewById(R.id.iv_ypl);
				yholder.iv_ypl1 = (ImageView) convertView.findViewById(R.id.iv_ypl_img1);
				yholder.iv_ypl2 = (ImageView) convertView.findViewById(R.id.iv_ypl_img2);
				yholder.iv_ypl3 = (ImageView) convertView.findViewById(R.id.iv_ypl_img3);
				yholder.iv_ypl4 = (ImageView) convertView.findViewById(R.id.iv_ypl_img4);
				yholder.tv_ypl_nickname = (TextView) convertView.findViewById(R.id.tv_ypl_nickname);
				yholder.tv_ypl_createtime = (TextView) convertView.findViewById(R.id.tv_ypl_createtime);
				yholder.tv_ypl_content = (TextView) convertView.findViewById(R.id.tv_ypl_content);
				yholder.tv_ypl_report = (TextView) convertView.findViewById(R.id.tv_ypl_report);
				yholder.rl_ychat = (RelativeLayout) convertView.findViewById(R.id.rl_ychat);
				yholder.btn_ypl_reply = (Button) convertView.findViewById(R.id.btn_ypl_reply);
				yholder.iv_ypl_dj = (ImageView) convertView.findViewById(R.id.iv_ypl_dj);
				convertView.setTag(yholder);
			break;
			case 28:
				convertView  = inflater.inflate(R.layout.item_ypl_list, null);
				yholder = new YplHolder();
				yholder.iv_ypl = (ImageView) convertView.findViewById(R.id.iv_ypl);
				yholder.iv_ypl1 = (ImageView) convertView.findViewById(R.id.iv_ypl_img1);
				yholder.iv_ypl2 = (ImageView) convertView.findViewById(R.id.iv_ypl_img2);
				yholder.iv_ypl3 = (ImageView) convertView.findViewById(R.id.iv_ypl_img3);
				yholder.iv_ypl4 = (ImageView) convertView.findViewById(R.id.iv_ypl_img4);
				yholder.tv_ypl_nickname = (TextView) convertView.findViewById(R.id.tv_ypl_nickname);
				yholder.tv_ypl_createtime = (TextView) convertView.findViewById(R.id.tv_ypl_createtime);
				yholder.tv_ypl_content = (TextView) convertView.findViewById(R.id.tv_ypl_content);
				yholder.tv_ypl_report = (TextView) convertView.findViewById(R.id.tv_ypl_report);
				yholder.rl_ychat = (RelativeLayout) convertView.findViewById(R.id.rl_ychat);
				yholder.btn_ypl_reply = (Button) convertView.findViewById(R.id.btn_ypl_reply);
				yholder.iv_ypl_dj = (ImageView) convertView.findViewById(R.id.iv_ypl_dj);
				convertView.setTag(yholder);
			break;
			case 29:
				convertView  = inflater.inflate(R.layout.item_ypl_list, null);
				yholder = new YplHolder();
				yholder.iv_ypl = (ImageView) convertView.findViewById(R.id.iv_ypl);
				yholder.iv_ypl1 = (ImageView) convertView.findViewById(R.id.iv_ypl_img1);
				yholder.iv_ypl2 = (ImageView) convertView.findViewById(R.id.iv_ypl_img2);
				yholder.iv_ypl3 = (ImageView) convertView.findViewById(R.id.iv_ypl_img3);
				yholder.iv_ypl4 = (ImageView) convertView.findViewById(R.id.iv_ypl_img4);
				yholder.tv_ypl_nickname = (TextView) convertView.findViewById(R.id.tv_ypl_nickname);
				yholder.tv_ypl_createtime = (TextView) convertView.findViewById(R.id.tv_ypl_createtime);
				yholder.tv_ypl_content = (TextView) convertView.findViewById(R.id.tv_ypl_content);
				yholder.tv_ypl_report = (TextView) convertView.findViewById(R.id.tv_ypl_report);
				yholder.rl_ychat = (RelativeLayout) convertView.findViewById(R.id.rl_ychat);
				yholder.btn_ypl_reply = (Button) convertView.findViewById(R.id.btn_ypl_reply);
				yholder.iv_ypl_dj = (ImageView) convertView.findViewById(R.id.iv_ypl_dj);
				convertView.setTag(yholder);
			break;
			}
		}else {
			switch(type){
			case 1:
				yholder = (YplHolder) convertView.getTag();
			break;
			case 11:
				yholder = (YplHolder) convertView.getTag();
			break;
			case 12:
				yholder = (YplHolder) convertView.getTag();
			break;
			case 13:
				yholder = (YplHolder) convertView.getTag();
			break;
			case 14:
				yholder = (YplHolder) convertView.getTag();
			break;
			case 15:
				yholder = (YplHolder) convertView.getTag();
			break;
			case 16:
				yholder = (YplHolder) convertView.getTag();
			break;
			case 17:
				yholder = (YplHolder) convertView.getTag();
			break;
			case 18:
				yholder = (YplHolder) convertView.getTag();
			break;
			case 19:
				yholder = (YplHolder) convertView.getTag();
			break;
			case 2:
				yholder = (YplHolder) convertView.getTag();
			break;
			case 21:
				yholder = (YplHolder) convertView.getTag();
			break;
			case 22:
				yholder = (YplHolder) convertView.getTag();
			break;
			case 23:
				yholder = (YplHolder) convertView.getTag();
			break;
			case 24:
				yholder = (YplHolder) convertView.getTag();
			break;
			case 25:
				yholder = (YplHolder) convertView.getTag();
			break;
			case 26:
				yholder = (YplHolder) convertView.getTag();
			break;
			case 27:
				yholder = (YplHolder) convertView.getTag();
			break;
			case 28:
				yholder = (YplHolder) convertView.getTag();
			break;
			case 29:
				yholder = (YplHolder) convertView.getTag();
			break;
			}
		}
		String zhengze = mContext.getString(R.string.expression_matches);	//正则表达式，用来判断消息内是否有表情
		String content = arrays.get(position).getContent();
		if(content!=null&&!content.equals("")){
			content = content.replace("[", "");
			content = content.replace("]", "");
		}
		switch(type){
		
		case 1:  
        	imageLoader.displayImage(imageUrl, yholder.iv_ypl, options_head, null);
            yholder.tv_ypl_nickname.setText(nickname);
        	yholder.tv_ypl_createtime.setText(date);
        	try {
				SpannableString spannableString = ExpressionUtil.getExpressionString(mContext, content, zhengze,densityDpi);
				yholder.tv_ypl_content.setText(spannableString);
			} catch (NumberFormatException e) {
				e.printStackTrace();
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			}
        	yholder.btn_ypl_reply.setVisibility(View.GONE);
        	yholder.tv_ypl_deldetail.setVisibility(View.VISIBLE);
        	yholder.tv_ypl_deldetail.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					dialog(hdinfocommentid, position);
				}
			});
        break;  
        
        case 11:
        	//头像
        	imageLoader.displayImage(imageUrl, yholder.iv_ypl, options_head, null);
        	
        	//评论的图片
            List<ImageView> list_ypl1 = new ArrayList<ImageView>();
            list_ypl1.add(yholder.iv_ypl1);
            
            String[] urls21 = null;
	        if(imagePath!=null){
	        	urls21 = imagePath.split(",");
	        	for(int i=0;i<urls21.length;i++){
	        		list_ypl1.get(i).setVisibility(View.VISIBLE);
	        		imageLoader.displayImage(urls21[i], list_ypl1.get(i), options, null);
	        		final Intent intent = new Intent(mContext,ViewPagerActivity.class);
	        		intent.putExtra("ID", i);
					intent.putExtra("imgurl", urls21);
	        		list_ypl1.get(i).setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							mContext.startActivity(intent);
							((Activity) mContext).overridePendingTransition(R.anim.zoom_enter,0);
						}
					});
	        	}
	        }
            	yholder.tv_ypl_nickname.setText(nickname);
            	yholder.tv_ypl_createtime.setText(date);
            	yholder.iv_ypl_dj.setVisibility(View.VISIBLE);
            	try {
    				SpannableString spannableString = ExpressionUtil.getExpressionString(mContext, content, zhengze,densityDpi);
    				yholder.tv_ypl_content.setText(spannableString);
    			} catch (NumberFormatException e) {
    				e.printStackTrace();
    			} catch (SecurityException e) {
    				e.printStackTrace();
    			} catch (IllegalArgumentException e) {
    				e.printStackTrace();
    			}
            	yholder.btn_ypl_reply.setVisibility(View.GONE);
            	yholder.tv_ypl_deldetail.setVisibility(View.VISIBLE);
            	yholder.tv_ypl_deldetail.setOnClickListener(new OnClickListener() {
    				@Override
    				public void onClick(View v) {
    					dialog(hdinfocommentid, position);
    				}
    			});
                break; 
            
        case 12:  
        	//头像
        	imageLoader.displayImage(imageUrl, yholder.iv_ypl, options_head, null);
        	
        	//评论的图片
            List<ImageView> list_ypl2 = new ArrayList<ImageView>();
            list_ypl2.add(yholder.iv_ypl1);
            list_ypl2.add(yholder.iv_ypl2);
            
            String[] urls22 = null;
        	
	        if(imagePath!=null){
	        	urls22 = imagePath.split(",");
	        	for(int i=0;i<urls22.length;i++){
	        		list_ypl2.get(i).setVisibility(View.VISIBLE);
	        		imageLoader.displayImage(urls22[i], list_ypl2.get(i), options, null);
	        		final Intent intent = new Intent(mContext,ViewPagerActivity.class);
					intent.putExtra("ID", i);
					intent.putExtra("imgurl", urls22);
	        		list_ypl2.get(i).setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							mContext.startActivity(intent);
							((Activity) mContext).overridePendingTransition(R.anim.zoom_enter,0);
						}
					});
	        	}
	        }
            	yholder.tv_ypl_nickname.setText(nickname);
            	yholder.tv_ypl_createtime.setText(date);
            	yholder.iv_ypl_dj.setVisibility(View.VISIBLE);
            	try {
    				SpannableString spannableString = ExpressionUtil.getExpressionString(mContext, content, zhengze,densityDpi);
    				yholder.tv_ypl_content.setText(spannableString);
    			} catch (NumberFormatException e) {
    				e.printStackTrace();
    			} catch (SecurityException e) {
    				e.printStackTrace();
    			} catch (IllegalArgumentException e) {
    				e.printStackTrace();
    			}
            	yholder.btn_ypl_reply.setVisibility(View.GONE);
            	yholder.tv_ypl_deldetail.setVisibility(View.VISIBLE);
            	yholder.tv_ypl_deldetail.setOnClickListener(new OnClickListener() {
    				@Override
    				public void onClick(View v) {
    					dialog(hdinfocommentid, position);
    				}
    			});
                break; 
                
        case 13:  
        	//头像
        	imageLoader.displayImage(imageUrl, yholder.iv_ypl, options_head, null);
        	
        	//评论的图片
            List<ImageView> list_ypl3 = new ArrayList<ImageView>();
            list_ypl3.add(yholder.iv_ypl1);
            list_ypl3.add(yholder.iv_ypl2);
            list_ypl3.add(yholder.iv_ypl3);
            
            String[] urls23 = null;
	        if(imagePath!=null){
	        	urls23 = imagePath.split(",");
	        	for(int i=0;i<urls23.length;i++){
	        		list_ypl3.get(i).setVisibility(View.VISIBLE);
	        		imageLoader.displayImage(urls23[i], list_ypl3.get(i), options, null);
	        		final Intent intent = new Intent(mContext,ViewPagerActivity.class);
	        		intent.putExtra("ID", i);
					intent.putExtra("imgurl", urls23);
	        		list_ypl3.get(i).setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							mContext.startActivity(intent);
							((Activity) mContext).overridePendingTransition(R.anim.zoom_enter,0);
						}
					});
	        	}
	        }
            	yholder.tv_ypl_nickname.setText(nickname);
            	yholder.tv_ypl_createtime.setText(date);
            	yholder.iv_ypl_dj.setVisibility(View.VISIBLE);
            	try {
    				SpannableString spannableString = ExpressionUtil.getExpressionString(mContext, content, zhengze,densityDpi);
    				yholder.tv_ypl_content.setText(spannableString);
    			} catch (NumberFormatException e) {
    				e.printStackTrace();
    			} catch (SecurityException e) {
    				e.printStackTrace();
    			} catch (IllegalArgumentException e) {
    				e.printStackTrace();
    			}
            	yholder.btn_ypl_reply.setVisibility(View.GONE);
            	yholder.tv_ypl_deldetail.setVisibility(View.VISIBLE);
            	yholder.tv_ypl_deldetail.setOnClickListener(new OnClickListener() {
    				@Override
    				public void onClick(View v) {
    					dialog(hdinfocommentid, position);
    				}
    			});
                break; 
                
        case 14:  
        	//头像
        	imageLoader.displayImage(imageUrl, yholder.iv_ypl, options_head, null);
        	//评论的图片
            List<ImageView> list_ypl4 = new ArrayList<ImageView>();
            list_ypl4.add(yholder.iv_ypl1);
            list_ypl4.add(yholder.iv_ypl2);
            list_ypl4.add(yholder.iv_ypl3);
            list_ypl4.add(yholder.iv_ypl4);
            
            String[] urls24 = null;
	        if(imagePath!=null){
	        	urls24 = imagePath.split(",");
	        	for(int i=0;i<urls24.length;i++){
	        		list_ypl4.get(i).setVisibility(View.VISIBLE);
	        		imageLoader.displayImage(urls24[i], list_ypl4.get(i), options, null);
	        		final Intent intent = new Intent(mContext,ViewPagerActivity.class);
	        		intent.putExtra("ID", i);
					intent.putExtra("imgurl", urls24);
	        		list_ypl4.get(i).setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							mContext.startActivity(intent);
							((Activity) mContext).overridePendingTransition(R.anim.zoom_enter,0);
						}
					});
	        	}
	        }
            	yholder.tv_ypl_nickname.setText(nickname);
            	yholder.tv_ypl_createtime.setText(date);
            	yholder.iv_ypl_dj.setVisibility(View.VISIBLE);
            	try {
    				SpannableString spannableString = ExpressionUtil.getExpressionString(mContext, content, zhengze,densityDpi);
    				yholder.tv_ypl_content.setText(spannableString);
    			} catch (NumberFormatException e) {
    				e.printStackTrace();
    			} catch (SecurityException e) {
    				e.printStackTrace();
    			} catch (IllegalArgumentException e) {
    				e.printStackTrace();
    			}
            	yholder.btn_ypl_reply.setVisibility(View.GONE);
            	yholder.tv_ypl_deldetail.setVisibility(View.VISIBLE);
            	yholder.tv_ypl_deldetail.setOnClickListener(new OnClickListener() {
    				@Override
    				public void onClick(View v) {
    					dialog(hdinfocommentid, position);
    				}
    			});
            	break; 
        
        case 15:
        	//头像
        	imageLoader.displayImage(imageUrl, yholder.iv_ypl, options_head, null);
        	
        	//评论的图片
            List<ImageView> list_ypl15 = new ArrayList<ImageView>();
            list_ypl15.add(yholder.iv_ypl1);
            
            String[] urls15 = null;
	        if(imagePath!=null){
	        	urls15 = imagePath.split(",");
	        	for(int i=0;i<urls15.length;i++){
	        		list_ypl15.get(i).setVisibility(View.VISIBLE);
	        		imageLoader.displayImage(urls15[i], list_ypl15.get(i), options, null);
	        		final Intent intent = new Intent(mContext,ViewPagerActivity.class);
	        		intent.putExtra("ID", i);
					intent.putExtra("imgurl", urls15);
					list_ypl15.get(i).setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							mContext.startActivity(intent);
							((Activity) mContext).overridePendingTransition(R.anim.zoom_enter,0);
						}
					});
	        	}
	        }
            	yholder.tv_ypl_nickname.setText(nickname);
            	yholder.tv_ypl_createtime.setText(date);
            	try {
    				SpannableString spannableString = ExpressionUtil.getExpressionString(mContext, content, zhengze,densityDpi);
    				yholder.tv_ypl_content.setText(spannableString);
    			} catch (NumberFormatException e) {
    				e.printStackTrace();
    			} catch (SecurityException e) {
    				e.printStackTrace();
    			} catch (IllegalArgumentException e) {
    				e.printStackTrace();
    			}
            	yholder.btn_ypl_reply.setVisibility(View.GONE);
            	yholder.tv_ypl_deldetail.setVisibility(View.VISIBLE);
            	yholder.tv_ypl_deldetail.setOnClickListener(new OnClickListener() {
    				@Override
    				public void onClick(View v) {
    					dialog(hdinfocommentid, position);
    				}
    			});
                break; 
            
        case 16:  
        	//头像
        	imageLoader.displayImage(imageUrl, yholder.iv_ypl, options_head, null);
        	
        	//评论的图片
            List<ImageView> list_ypl16 = new ArrayList<ImageView>();
            list_ypl16.add(yholder.iv_ypl1);
            list_ypl16.add(yholder.iv_ypl2);
            
            String[] urls16 = null;
        	
	        if(imagePath!=null){
	        	urls16 = imagePath.split(",");
	        	for(int i=0;i<urls16.length;i++){
	        		list_ypl16.get(i).setVisibility(View.VISIBLE);
	        		imageLoader.displayImage(urls16[i], list_ypl16.get(i), options, null);
	        		final Intent intent = new Intent(mContext,ViewPagerActivity.class);
					intent.putExtra("ID", i);
					intent.putExtra("imgurl", urls16);
					list_ypl16.get(i).setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							mContext.startActivity(intent);
							((Activity) mContext).overridePendingTransition(R.anim.zoom_enter,0);
						}
					});
	        	}
	        }
            	yholder.tv_ypl_nickname.setText(nickname);
            	yholder.tv_ypl_createtime.setText(date);
            	try {
    				SpannableString spannableString = ExpressionUtil.getExpressionString(mContext, content, zhengze,densityDpi);
    				yholder.tv_ypl_content.setText(spannableString);
    			} catch (NumberFormatException e) {
    				e.printStackTrace();
    			} catch (SecurityException e) {
    				e.printStackTrace();
    			} catch (IllegalArgumentException e) {
    				e.printStackTrace();
    			}
            	yholder.btn_ypl_reply.setVisibility(View.GONE);
            	yholder.tv_ypl_deldetail.setVisibility(View.VISIBLE);
            	yholder.tv_ypl_deldetail.setOnClickListener(new OnClickListener() {
    				@Override
    				public void onClick(View v) {
    					dialog(hdinfocommentid, position);
    				}
    			});
                break; 
                
        case 17:  
        	//头像
        	imageLoader.displayImage(imageUrl, yholder.iv_ypl, options_head, null);
        	
        	//评论的图片
            List<ImageView> list_ypl17 = new ArrayList<ImageView>();
            list_ypl17.add(yholder.iv_ypl1);
            list_ypl17.add(yholder.iv_ypl2);
            list_ypl17.add(yholder.iv_ypl3);
            
            String[] urls17 = null;
	        if(imagePath!=null){
	        	urls17 = imagePath.split(",");
	        	for(int i=0;i<urls17.length;i++){
	        		list_ypl17.get(i).setVisibility(View.VISIBLE);
	        		imageLoader.displayImage(urls17[i], list_ypl17.get(i), options, null);
	        		final Intent intent = new Intent(mContext,ViewPagerActivity.class);
	        		intent.putExtra("ID", i);
					intent.putExtra("imgurl", urls17);
					list_ypl17.get(i).setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							mContext.startActivity(intent);
							((Activity) mContext).overridePendingTransition(R.anim.zoom_enter,0);
						}
					});
	        	}
	        }
            	yholder.tv_ypl_nickname.setText(nickname);
            	yholder.tv_ypl_createtime.setText(date);
            	try {
    				SpannableString spannableString = ExpressionUtil.getExpressionString(mContext, content, zhengze,densityDpi);
    				yholder.tv_ypl_content.setText(spannableString);
    			} catch (NumberFormatException e) {
    				e.printStackTrace();
    			} catch (SecurityException e) {
    				e.printStackTrace();
    			} catch (IllegalArgumentException e) {
    				e.printStackTrace();
    			}
            	yholder.btn_ypl_reply.setVisibility(View.GONE);
            	yholder.tv_ypl_deldetail.setVisibility(View.VISIBLE);
            	yholder.tv_ypl_deldetail.setOnClickListener(new OnClickListener() {
    				@Override
    				public void onClick(View v) {
    					dialog(hdinfocommentid, position);
    				}
    			});
                break; 
                
        case 18:  
        	//头像
        	imageLoader.displayImage(imageUrl, yholder.iv_ypl, options_head, null);
        	//评论的图片
            List<ImageView> list_ypl18 = new ArrayList<ImageView>();
            list_ypl18.add(yholder.iv_ypl1);
            list_ypl18.add(yholder.iv_ypl2);
            list_ypl18.add(yholder.iv_ypl3);
            list_ypl18.add(yholder.iv_ypl4);
            
            String[] urls18 = null;
	        if(imagePath!=null){
	        	urls18 = imagePath.split(",");
	        	for(int i=0;i<urls18.length;i++){
	        		list_ypl18.get(i).setVisibility(View.VISIBLE);
	        		imageLoader.displayImage(urls18[i], list_ypl18.get(i), options, null);
	        		final Intent intent = new Intent(mContext,ViewPagerActivity.class);
	        		intent.putExtra("ID", i);
					intent.putExtra("imgurl", urls18);
					list_ypl18.get(i).setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							mContext.startActivity(intent);
							((Activity) mContext).overridePendingTransition(R.anim.zoom_enter,0);
						}
					});
	        	}
	        }
            	yholder.tv_ypl_nickname.setText(nickname);
            	yholder.tv_ypl_createtime.setText(date);
            	try {
    				SpannableString spannableString = ExpressionUtil.getExpressionString(mContext, content, zhengze,densityDpi);
    				yholder.tv_ypl_content.setText(spannableString);
    			} catch (NumberFormatException e) {
    				e.printStackTrace();
    			} catch (SecurityException e) {
    				e.printStackTrace();
    			} catch (IllegalArgumentException e) {
    				e.printStackTrace();
    			}
            	yholder.btn_ypl_reply.setVisibility(View.GONE);
            	yholder.tv_ypl_deldetail.setVisibility(View.VISIBLE);
            	yholder.tv_ypl_deldetail.setOnClickListener(new OnClickListener() {
    				@Override
    				public void onClick(View v) {
    					dialog(hdinfocommentid, position);
    				}
    			});
            	break; 
        case 19:  
        	imageLoader.displayImage(imageUrl, yholder.iv_ypl, options_head, null);
            yholder.tv_ypl_nickname.setText(nickname);
        	yholder.tv_ypl_createtime.setText(date);
        	yholder.iv_ypl_dj.setVisibility(View.VISIBLE);
        	try {
				SpannableString spannableString = ExpressionUtil.getExpressionString(mContext, content, zhengze,densityDpi);
				yholder.tv_ypl_content.setText(spannableString);
			} catch (NumberFormatException e) {
				e.printStackTrace();
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			}
        	yholder.btn_ypl_reply.setVisibility(View.GONE);
        	yholder.tv_ypl_deldetail.setVisibility(View.VISIBLE);
        	yholder.tv_ypl_deldetail.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					dialog(hdinfocommentid, position);
				}
			});
        break;  	
            case 2:  
            	imageLoader.displayImage(imageUrl, yholder.iv_ypl, options_head, null);
                yholder.tv_ypl_nickname.setText(nickname);
            	yholder.tv_ypl_createtime.setText(date);
            	try {
    				SpannableString spannableString = ExpressionUtil.getExpressionString(mContext, content, zhengze,densityDpi);
    				yholder.tv_ypl_content.setText(spannableString);
    			} catch (NumberFormatException e) {
    				e.printStackTrace();
    			} catch (SecurityException e) {
    				e.printStackTrace();
    			} catch (IllegalArgumentException e) {
    				e.printStackTrace();
    			}
            	if(uid!=null){
            		yholder.rl_ychat.setOnLongClickListener(new OnLongClickListener() {
	    				@Override
	    				public boolean onLongClick(View v) {
	    					PopupMenu menu = new PopupMenu(mContext);
	    					menu.setOnItemSelectedListener(new OnItemSelectedListener() {
	    						@Override
	    						public void onItemSelected(MenuItem item) {
	    							switch(item.getItemId()){
	    								case REPORT:
	    									reportdialog(hdinfocommentid);
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
            	yholder.btn_ypl_reply.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						reply(nickname);
					}
				});
            break;  
            
            case 21:
            	//头像
            	imageLoader.displayImage(imageUrl, yholder.iv_ypl, options_head, null);
            	
            	//评论的图片
                List<ImageView> ylist_ypl1 = new ArrayList<ImageView>();
                ylist_ypl1.add(yholder.iv_ypl1);
                
                String[] yurls21 = null;
    	        if(imagePath!=null){
    	        	yurls21 = imagePath.split(",");
    	        	for(int i=0;i<yurls21.length;i++){
    	        		ylist_ypl1.get(i).setVisibility(View.VISIBLE);
    	        		imageLoader.displayImage(yurls21[i], ylist_ypl1.get(i), options, null);
    	        		final Intent intent = new Intent(mContext,ViewPagerActivity.class);
    	        		intent.putExtra("ID", i);
						intent.putExtra("imgurl", yurls21);
    	        		ylist_ypl1.get(i).setOnClickListener(new OnClickListener() {
							@Override
							public void onClick(View v) {
								mContext.startActivity(intent);
								((Activity) mContext).overridePendingTransition(R.anim.zoom_enter,0);
							}
						});
    	        	}
    	        }
                	yholder.tv_ypl_nickname.setText(nickname);
                	yholder.tv_ypl_createtime.setText(date);
                	yholder.iv_ypl_dj.setVisibility(View.VISIBLE);
                	try {
        				SpannableString spannableString = ExpressionUtil.getExpressionString(mContext, content, zhengze,densityDpi);
        				yholder.tv_ypl_content.setText(spannableString);
        			} catch (NumberFormatException e) {
        				e.printStackTrace();
        			} catch (SecurityException e) {
        				e.printStackTrace();
        			} catch (IllegalArgumentException e) {
        				e.printStackTrace();
        			}
                	if(uid!=null){
                		yholder.rl_ychat.setOnLongClickListener(new OnLongClickListener() {
    	    				@Override
    	    				public boolean onLongClick(View v) {
    	    					PopupMenu menu = new PopupMenu(mContext);
    	    					menu.setOnItemSelectedListener(new OnItemSelectedListener() {
    	    						@Override
    	    						public void onItemSelected(MenuItem item) {
    	    							switch(item.getItemId()){
    	    								case REPORT:
    	    									reportdialog(hdinfocommentid);
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
                	yholder.btn_ypl_reply.setOnClickListener(new OnClickListener() {
    					@Override
    					public void onClick(View v) {
    						reply(nickname);
    					}
    				});
                    break; 
                
            case 22:  
            	//头像
            	imageLoader.displayImage(imageUrl, yholder.iv_ypl, options_head, null);
            	
            	//评论的图片
                List<ImageView> ylist_ypl2 = new ArrayList<ImageView>();
                ylist_ypl2.add(yholder.iv_ypl1);
                ylist_ypl2.add(yholder.iv_ypl2);
                
                String[] yurls22 = null;
            	
    	        if(imagePath!=null){
    	        	yurls22 = imagePath.split(",");
    	        	for(int i=0;i<yurls22.length;i++){
    	        		ylist_ypl2.get(i).setVisibility(View.VISIBLE);
    	        		imageLoader.displayImage(yurls22[i], ylist_ypl2.get(i), options, null);
    	        		final Intent intent = new Intent(mContext,ViewPagerActivity.class);
						intent.putExtra("ID", i);
						intent.putExtra("imgurl", yurls22);
    	        		ylist_ypl2.get(i).setOnClickListener(new OnClickListener() {
							@Override
							public void onClick(View v) {
								mContext.startActivity(intent);
								((Activity) mContext).overridePendingTransition(R.anim.zoom_enter,0);
							}
						});
    	        	}
    	        }
                	yholder.tv_ypl_nickname.setText(nickname);
                	yholder.tv_ypl_createtime.setText(date);
                	yholder.iv_ypl_dj.setVisibility(View.VISIBLE);
                	try {
        				SpannableString spannableString = ExpressionUtil.getExpressionString(mContext, content, zhengze,densityDpi);
        				yholder.tv_ypl_content.setText(spannableString);
        			} catch (NumberFormatException e) {
        				e.printStackTrace();
        			} catch (SecurityException e) {
        				e.printStackTrace();
        			} catch (IllegalArgumentException e) {
        				e.printStackTrace();
        			}
                	if(uid!=null){
                		yholder.rl_ychat.setOnLongClickListener(new OnLongClickListener() {
    	    				@Override
    	    				public boolean onLongClick(View v) {
    	    					PopupMenu menu = new PopupMenu(mContext);
    	    					menu.setOnItemSelectedListener(new OnItemSelectedListener() {
    	    						@Override
    	    						public void onItemSelected(MenuItem item) {
    	    							switch(item.getItemId()){
    	    								case REPORT:
    	    									reportdialog(hdinfocommentid);
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
                	yholder.btn_ypl_reply.setOnClickListener(new OnClickListener() {
    					@Override
    					public void onClick(View v) {
    						reply(nickname);
    					}
    				});
                    break; 
                    
            case 23:  
            	//头像
            	imageLoader.displayImage(imageUrl, yholder.iv_ypl, options_head, null);
            	
            	//评论的图片
                List<ImageView> ylist_ypl3 = new ArrayList<ImageView>();
                ylist_ypl3.add(yholder.iv_ypl1);
                ylist_ypl3.add(yholder.iv_ypl2);
                ylist_ypl3.add(yholder.iv_ypl3);
                
                String[] yurls23 = null;
    	        if(imagePath!=null){
    	        	yurls23 = imagePath.split(",");
    	        	for(int i=0;i<yurls23.length;i++){
    	        		ylist_ypl3.get(i).setVisibility(View.VISIBLE);
    	        		imageLoader.displayImage(yurls23[i], ylist_ypl3.get(i), options, null);
    	        		final Intent intent = new Intent(mContext,ViewPagerActivity.class);
    	        		intent.putExtra("ID", i);
						intent.putExtra("imgurl", yurls23);
    	        		ylist_ypl3.get(i).setOnClickListener(new OnClickListener() {
							@Override
							public void onClick(View v) {
								mContext.startActivity(intent);
								((Activity) mContext).overridePendingTransition(R.anim.zoom_enter,0);
							}
						});
    	        	}
    	        }
                	yholder.tv_ypl_nickname.setText(nickname);
                	yholder.tv_ypl_createtime.setText(date);
                	yholder.iv_ypl_dj.setVisibility(View.VISIBLE);
                	try {
        				SpannableString spannableString = ExpressionUtil.getExpressionString(mContext, content, zhengze,densityDpi);
        				yholder.tv_ypl_content.setText(spannableString);
        			} catch (NumberFormatException e) {
        				e.printStackTrace();
        			} catch (SecurityException e) {
        				e.printStackTrace();
        			} catch (IllegalArgumentException e) {
        				e.printStackTrace();
        			}
                	if(uid!=null){
                		yholder.rl_ychat.setOnLongClickListener(new OnLongClickListener() {
    	    				@Override
    	    				public boolean onLongClick(View v) {
    	    					PopupMenu menu = new PopupMenu(mContext);
    	    					menu.setOnItemSelectedListener(new OnItemSelectedListener() {
    	    						@Override
    	    						public void onItemSelected(MenuItem item) {
    	    							switch(item.getItemId()){
    	    								case REPORT:
    	    									reportdialog(hdinfocommentid);
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
                	yholder.btn_ypl_reply.setOnClickListener(new OnClickListener() {
    					@Override
    					public void onClick(View v) {
    						reply(nickname);
    					}
    				});
                    break; 
                    
            case 24:  
            	//头像
            	imageLoader.displayImage(imageUrl, yholder.iv_ypl, options_head, null);
            	//评论的图片
                List<ImageView> ylist_ypl4 = new ArrayList<ImageView>();
                ylist_ypl4.add(yholder.iv_ypl1);
                ylist_ypl4.add(yholder.iv_ypl2);
                ylist_ypl4.add(yholder.iv_ypl3);
                ylist_ypl4.add(yholder.iv_ypl4);
                
                String[] yurls24 = null;
    	        if(imagePath!=null){
    	        	yurls24 = imagePath.split(",");
    	        	for(int i=0;i<yurls24.length;i++){
    	        		ylist_ypl4.get(i).setVisibility(View.VISIBLE);
    	        		imageLoader.displayImage(yurls24[i], ylist_ypl4.get(i), options, null);
    	        		final Intent intent = new Intent(mContext,ViewPagerActivity.class);
    	        		intent.putExtra("ID", i);
						intent.putExtra("imgurl", yurls24);
    	        		ylist_ypl4.get(i).setOnClickListener(new OnClickListener() {
							@Override
							public void onClick(View v) {
								mContext.startActivity(intent);
								((Activity) mContext).overridePendingTransition(R.anim.zoom_enter,0);
							}
						});
    	        	}
    	        }
                	yholder.tv_ypl_nickname.setText(nickname);
                	yholder.tv_ypl_createtime.setText(date);
                	yholder.iv_ypl_dj.setVisibility(View.VISIBLE);
                	try {
        				SpannableString spannableString = ExpressionUtil.getExpressionString(mContext, content, zhengze,densityDpi);
        				yholder.tv_ypl_content.setText(spannableString);
        			} catch (NumberFormatException e) {
        				e.printStackTrace();
        			} catch (SecurityException e) {
        				e.printStackTrace();
        			} catch (IllegalArgumentException e) {
        				e.printStackTrace();
        			}
                	if(uid!=null){
                		yholder.rl_ychat.setOnLongClickListener(new OnLongClickListener() {
    	    				@Override
    	    				public boolean onLongClick(View v) {
    	    					PopupMenu menu = new PopupMenu(mContext);
    	    					menu.setOnItemSelectedListener(new OnItemSelectedListener() {
    	    						@Override
    	    						public void onItemSelected(MenuItem item) {
    	    							switch(item.getItemId()){
    	    								case REPORT:
    	    									reportdialog(hdinfocommentid);
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
                	yholder.btn_ypl_reply.setOnClickListener(new OnClickListener() {
    					@Override
    					public void onClick(View v) {
    						reply(nickname);
    					}
    				});
                	break; 
                	case 25:
                    	//头像
                    	imageLoader.displayImage(imageUrl, yholder.iv_ypl, options_head, null);
                    	
                    	//评论的图片
                        List<ImageView> ylist_ypl25 = new ArrayList<ImageView>();
                        ylist_ypl25.add(yholder.iv_ypl1);
                        
                        String[] yurls25 = null;
            	        if(imagePath!=null){
            	        	yurls25 = imagePath.split(",");
            	        	for(int i=0;i<yurls25.length;i++){
            	        		ylist_ypl25.get(i).setVisibility(View.VISIBLE);
            	        		imageLoader.displayImage(yurls25[i], ylist_ypl25.get(i), options, null);
            	        		final Intent intent = new Intent(mContext,ViewPagerActivity.class);
            	        		intent.putExtra("ID", i);
        						intent.putExtra("imgurl", yurls25);
        						ylist_ypl25.get(i).setOnClickListener(new OnClickListener() {
        							@Override
        							public void onClick(View v) {
        								mContext.startActivity(intent);
        								((Activity) mContext).overridePendingTransition(R.anim.zoom_enter,0);
        							}
        						});
            	        	}
            	        }
                        	yholder.tv_ypl_nickname.setText(nickname);
                        	yholder.tv_ypl_createtime.setText(date);
                        	try {
                				SpannableString spannableString = ExpressionUtil.getExpressionString(mContext, content, zhengze,densityDpi);
                				yholder.tv_ypl_content.setText(spannableString);
                			} catch (NumberFormatException e) {
                				e.printStackTrace();
                			} catch (SecurityException e) {
                				e.printStackTrace();
                			} catch (IllegalArgumentException e) {
                				e.printStackTrace();
                			}
                        	if(uid!=null){
                        		yholder.rl_ychat.setOnLongClickListener(new OnLongClickListener() {
            	    				@Override
            	    				public boolean onLongClick(View v) {
            	    					PopupMenu menu = new PopupMenu(mContext);
            	    					menu.setOnItemSelectedListener(new OnItemSelectedListener() {
            	    						@Override
            	    						public void onItemSelected(MenuItem item) {
            	    							switch(item.getItemId()){
            	    								case REPORT:
            	    									reportdialog(hdinfocommentid);
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
                        	yholder.btn_ypl_reply.setOnClickListener(new OnClickListener() {
            					@Override
            					public void onClick(View v) {
            						reply(nickname);
            					}
            				});
                            break; 
                        
                    case 26:  
                    	//头像
                    	imageLoader.displayImage(imageUrl, yholder.iv_ypl, options_head, null);
                    	
                    	//评论的图片
                        List<ImageView> ylist_ypl26 = new ArrayList<ImageView>();
                        ylist_ypl26.add(yholder.iv_ypl1);
                        ylist_ypl26.add(yholder.iv_ypl2);
                        
                        String[] yurls26 = null;
                    	
            	        if(imagePath!=null){
            	        	yurls26 = imagePath.split(",");
            	        	for(int i=0;i<yurls26.length;i++){
            	        		ylist_ypl26.get(i).setVisibility(View.VISIBLE);
            	        		imageLoader.displayImage(yurls26[i], ylist_ypl26.get(i), options, null);
            	        		final Intent intent = new Intent(mContext,ViewPagerActivity.class);
        						intent.putExtra("ID", i);
        						intent.putExtra("imgurl", yurls26);
        						ylist_ypl26.get(i).setOnClickListener(new OnClickListener() {
        							@Override
        							public void onClick(View v) {
        								mContext.startActivity(intent);
        								((Activity) mContext).overridePendingTransition(R.anim.zoom_enter,0);
        							}
        						});
            	        	}
            	        }
                        	yholder.tv_ypl_nickname.setText(nickname);
                        	yholder.tv_ypl_createtime.setText(date);
                        	try {
                				SpannableString spannableString = ExpressionUtil.getExpressionString(mContext, content, zhengze,densityDpi);
                				yholder.tv_ypl_content.setText(spannableString);
                			} catch (NumberFormatException e) {
                				e.printStackTrace();
                			} catch (SecurityException e) {
                				e.printStackTrace();
                			} catch (IllegalArgumentException e) {
                				e.printStackTrace();
                			}
                        	if(uid!=null){
                        		yholder.rl_ychat.setOnLongClickListener(new OnLongClickListener() {
            	    				@Override
            	    				public boolean onLongClick(View v) {
            	    					PopupMenu menu = new PopupMenu(mContext);
            	    					menu.setOnItemSelectedListener(new OnItemSelectedListener() {
            	    						@Override
            	    						public void onItemSelected(MenuItem item) {
            	    							switch(item.getItemId()){
            	    								case REPORT:
            	    									reportdialog(hdinfocommentid);
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
                        	yholder.btn_ypl_reply.setOnClickListener(new OnClickListener() {
            					@Override
            					public void onClick(View v) {
            						reply(nickname);
            					}
            				});
                            break; 
                            
                    case 27:  
                    	//头像
                    	imageLoader.displayImage(imageUrl, yholder.iv_ypl, options_head, null);
                    	
                    	//评论的图片
                        List<ImageView> ylist_ypl27 = new ArrayList<ImageView>();
                        ylist_ypl27.add(yholder.iv_ypl1);
                        ylist_ypl27.add(yholder.iv_ypl2);
                        ylist_ypl27.add(yholder.iv_ypl3);
                        
                        String[] yurls27 = null;
            	        if(imagePath!=null){
            	        	yurls27 = imagePath.split(",");
            	        	for(int i=0;i<yurls27.length;i++){
            	        		ylist_ypl27.get(i).setVisibility(View.VISIBLE);
            	        		imageLoader.displayImage(yurls27[i], ylist_ypl27.get(i), options, null);
            	        		final Intent intent = new Intent(mContext,ViewPagerActivity.class);
            	        		intent.putExtra("ID", i);
        						intent.putExtra("imgurl", yurls27);
        						ylist_ypl27.get(i).setOnClickListener(new OnClickListener() {
        							@Override
        							public void onClick(View v) {
        								mContext.startActivity(intent);
        								((Activity) mContext).overridePendingTransition(R.anim.zoom_enter,0);
        							}
        						});
            	        	}
            	        }
                        	yholder.tv_ypl_nickname.setText(nickname);
                        	yholder.tv_ypl_createtime.setText(date);
                        	try {
                				SpannableString spannableString = ExpressionUtil.getExpressionString(mContext, content, zhengze,densityDpi);
                				yholder.tv_ypl_content.setText(spannableString);
                			} catch (NumberFormatException e) {
                				e.printStackTrace();
                			} catch (SecurityException e) {
                				e.printStackTrace();
                			} catch (IllegalArgumentException e) {
                				e.printStackTrace();
                			}
                        	if(uid!=null){
                        		yholder.rl_ychat.setOnLongClickListener(new OnLongClickListener() {
            	    				@Override
            	    				public boolean onLongClick(View v) {
            	    					PopupMenu menu = new PopupMenu(mContext);
            	    					menu.setOnItemSelectedListener(new OnItemSelectedListener() {
            	    						@Override
            	    						public void onItemSelected(MenuItem item) {
            	    							switch(item.getItemId()){
            	    								case REPORT:
            	    									reportdialog(hdinfocommentid);
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
                        	yholder.btn_ypl_reply.setOnClickListener(new OnClickListener() {
            					@Override
            					public void onClick(View v) {
            						reply(nickname);
            					}
            				});
                            break; 
                            
                    case 28:  
                    	//头像
                    	imageLoader.displayImage(imageUrl, yholder.iv_ypl, options_head, null);
                    	//评论的图片
                        List<ImageView> ylist_ypl28 = new ArrayList<ImageView>();
                        ylist_ypl28.add(yholder.iv_ypl1);
                        ylist_ypl28.add(yholder.iv_ypl2);
                        ylist_ypl28.add(yholder.iv_ypl3);
                        ylist_ypl28.add(yholder.iv_ypl4);
                        
                        String[] yurls28 = null;
            	        if(imagePath!=null){
            	        	yurls28 = imagePath.split(",");
            	        	for(int i=0;i<yurls28.length;i++){
            	        		ylist_ypl28.get(i).setVisibility(View.VISIBLE);
            	        		imageLoader.displayImage(yurls28[i], ylist_ypl28.get(i), options, null);
            	        		final Intent intent = new Intent(mContext,ViewPagerActivity.class);
            	        		intent.putExtra("ID", i);
        						intent.putExtra("imgurl", yurls28);
        						ylist_ypl28.get(i).setOnClickListener(new OnClickListener() {
        							@Override
        							public void onClick(View v) {
        								mContext.startActivity(intent);
        								((Activity) mContext).overridePendingTransition(R.anim.zoom_enter,0);
        							}
        						});
            	        	}
            	        }
                        	yholder.tv_ypl_nickname.setText(nickname);
                        	yholder.tv_ypl_createtime.setText(date);
                        	try {
                				SpannableString spannableString = ExpressionUtil.getExpressionString(mContext, content, zhengze,densityDpi);
                				yholder.tv_ypl_content.setText(spannableString);
                			} catch (NumberFormatException e) {
                				e.printStackTrace();
                			} catch (SecurityException e) {
                				e.printStackTrace();
                			} catch (IllegalArgumentException e) {
                				e.printStackTrace();
                			}
                        	if(uid!=null){
                        		yholder.rl_ychat.setOnLongClickListener(new OnLongClickListener() {
            	    				@Override
            	    				public boolean onLongClick(View v) {
            	    					PopupMenu menu = new PopupMenu(mContext);
            	    					menu.setOnItemSelectedListener(new OnItemSelectedListener() {
            	    						@Override
            	    						public void onItemSelected(MenuItem item) {
            	    							switch(item.getItemId()){
            	    								case REPORT:
            	    									reportdialog(hdinfocommentid);
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
                        	yholder.btn_ypl_reply.setOnClickListener(new OnClickListener() {
            					@Override
            					public void onClick(View v) {
            						reply(nickname);
            					}
            				});
                        	break; 
                    case 29:  
                    	imageLoader.displayImage(imageUrl, yholder.iv_ypl, options_head, null);
                        yholder.tv_ypl_nickname.setText(nickname);
                    	yholder.tv_ypl_createtime.setText(date);
                    	yholder.iv_ypl_dj.setVisibility(View.VISIBLE);
                    	try {
            				SpannableString spannableString = ExpressionUtil.getExpressionString(mContext, content, zhengze,densityDpi);
            				yholder.tv_ypl_content.setText(spannableString);
            			} catch (NumberFormatException e) {
            				e.printStackTrace();
            			} catch (SecurityException e) {
            				e.printStackTrace();
            			} catch (IllegalArgumentException e) {
            				e.printStackTrace();
            			}
                    	if(uid!=null){
                    		yholder.rl_ychat.setOnLongClickListener(new OnLongClickListener() {
        	    				@Override
        	    				public boolean onLongClick(View v) {
        	    					PopupMenu menu = new PopupMenu(mContext);
        	    					menu.setOnItemSelectedListener(new OnItemSelectedListener() {
        	    						@Override
        	    						public void onItemSelected(MenuItem item) {
        	    							switch(item.getItemId()){
        	    								case REPORT:
        	    									reportdialog(hdinfocommentid);
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
                    	yholder.btn_ypl_reply.setOnClickListener(new OnClickListener() {
        					@Override
        					public void onClick(View v) {
        						reply(nickname);
        					}
        				});
                    break; 
        } 
		return convertView;
	}
	
	Handler handler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch(msg.what){
			case 1:
				int position = (Integer) msg.obj;
				arrays.remove(position);
				notifyDataSetChanged();
				showMsg(mContext.getResources().getString(R.string.del_success),"info","bottom");
				break;
			case 0:
				showMsg(mContext.getResources().getString(R.string.del_fail),"info","bottom");
				break;
			case 2:
				showMsg(mContext.getResources().getString(R.string.report_success),"info","bottom");
				break;
			case 3:
				showMsg(mContext.getResources().getString(R.string.report_fail),"info","bottom");
				break;
			}
		}
	};
	
		//删除自己评论
		private void dialog(final String hdinfocommentid,final int position){
			AlertDialog.Builder builder = new Builder(mContext);
			builder.setMessage("确认删除吗?");
			builder.setTitle("提示");
			builder.setPositiveButton(mContext.getResources().getString(R.string.dialog_confirm_yes), new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
					
					new Thread(){
						public void run() {
							JSONObject jsonObject;
							String resultString = HttpUtil.GetStringFromUrl(delmpl_url+hdinfocommentid);
							try {
								if(resultString!=null){
									jsonObject = new JSONObject(resultString);
									String code = jsonObject.getString("code");
									if(code.equals("200")){
										handler.obtainMessage(1, position).sendToTarget();
									}else{
										handler.sendEmptyMessage(0);
									}
								}else{
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
			builder.setNegativeButton(mContext.getResources().getString(R.string.dialog_no), new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			});
			builder.create().show();
		}
		
		private void reportdialog(final String hdinfocommentid){
			AlertDialog.Builder builder = new Builder(mContext);
			builder.setMessage(mContext.getResources().getString(R.string.report_tips));
			builder.setTitle("提示");
			builder.setPositiveButton(mContext.getResources().getString(R.string.dialog_confirm_yes), new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
					new Thread(){
						public void run() {
							JSONObject jsonObject;
							String resultString = HttpUtil.GetStringFromUrl(reportypl_url+hdinfocommentid);
							try {
								if(resultString!=null){
									jsonObject = new JSONObject(resultString);
									String code = jsonObject.getString("code");
									if(code.equals("200")){
										handler.sendEmptyMessage(2);
									}else{
										handler.sendEmptyMessage(3);
									}
								}else{
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
			builder.setNegativeButton(mContext.getResources().getString(R.string.dialog_no), new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			});
			builder.create().show();
		}
		
		
		private void showMsg(String msg,String styletype,String gravity){
			
			if(styletype.equals("alert")){
				style = AppMsg.STYLE_ALERT;
			}else if(styletype.equals("info")){
				style = AppMsg.STYLE_INFO;
			}
			
			appMsg = AppMsg.makeText((Activity) mContext, msg, style);
			
			if(gravity.equals("bottom")){
				appMsg.setLayoutGravity(Gravity.BOTTOM);
			}else if(gravity.equals("top")){
			}
            appMsg.show();
		}
		
		/**
		 * 跳转发评论界面
		 * @param nickname
		 */
		private void reply(String nickname){
			Intent intent = new Intent(mContext,ActCommentEditActivity.class);
			intent.putExtra("nickname", nickname);
			intent.putExtra("id", id);
			intent.putExtra("pingluntype", "huodong");
			((Activity) mContext).startActivityForResult(intent,PINGLUN);
		}
		
		private class MplHolder {
			TextView tv_mpl_nickname;
			TextView tv_mpl_createtime;
			TextView tv_mpl_content;
			TextView tv_mpl_del;
			ImageView iv_mpl;
			ImageView iv_mpl1;
			ImageView iv_mpl2;
			ImageView iv_mpl3;
			ImageView iv_mpl4;
		}
		private class YplHolder {
			TextView tv_ypl_nickname;
			TextView tv_ypl_createtime;
			TextView tv_ypl_content;
			TextView tv_ypl_report;
			RelativeLayout rl_ychat;
			Button btn_ypl_reply;
			Button tv_ypl_deldetail;
			ImageView iv_ypl;
			ImageView iv_ypl1;
			ImageView iv_ypl2;
			ImageView iv_ypl3;
			ImageView iv_ypl4;
			ImageView iv_ypl_dj;
		}
}



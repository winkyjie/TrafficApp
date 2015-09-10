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
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cn.fszt.trafficapp.R;
import cn.fszt.trafficapp.domain.PinglunNewsData;
import cn.fszt.trafficapp.util.HttpUtil;
import cn.fszt.trafficapp.widget.expression.ExpressionUtil;
import cn.fszt.trafficapp.widget.msg.AppMsg;
import cn.fszt.trafficapp.widget.photoview.DisplayImageOptionsUtil;
import cn.fszt.trafficapp.widget.photoview.MenuItem;
import cn.fszt.trafficapp.widget.photoview.PopupMenu;
import cn.fszt.trafficapp.widget.photoview.ViewPagerActivity;
import cn.fszt.trafficapp.widget.photoview.PopupMenu.OnItemSelectedListener;

public class NewsDetailAdapter extends BaseAdapter {
	private Context mContext;
	private List<PinglunNewsData> arrays;
	private LayoutInflater inflater;
	private String delmpl_url,reportypl_url;
	private int densityDpi,uid;
	private ImageLoader imageLoader;
	private DisplayImageOptions options,options_head;
	AppMsg.Style style = AppMsg.STYLE_INFO;
	AppMsg appMsg;
	private static final int REPORT = 1;
	
	public NewsDetailAdapter(Context mContext, List<PinglunNewsData> arrays,LayoutInflater inflater,String delmpl_url,int densityDpi,int uid,String reportypl_url){
		this.mContext = mContext;
		this.arrays = arrays;
		this.inflater = inflater;
		this.delmpl_url = delmpl_url; 
		this.uid = uid;
		this.reportypl_url = reportypl_url;
		
		this.densityDpi = densityDpi;
		imageLoader = ImageLoader.getInstance();
		
		options = DisplayImageOptionsUtil.getOptions(
				R.drawable.ic_empty, R.drawable.ic_error,R.drawable.default_image);
		
		options_head = DisplayImageOptionsUtil.getOptions(
				R.drawable.default_head, R.drawable.default_head,R.drawable.default_image,
				new RoundedBitmapDisplayer(15));
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
		
			if("1".equals(istop)){
				if(!"".equals(imagepath)){
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
				}else{
					return 1;
				}
				
			}else{
				if(!"".equals(imagepath)){
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
				}else{
					return 2;
				}
			}
			return 2;
			
	}

	@Override
	public int getViewTypeCount() {
		return 30;
	}
	

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		int type = getItemViewType(position); 
		final String hdnewcommentid = arrays.get(position).getHdnewcommentid();
		String date = arrays.get(position).getCreatedate();
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
				convertView  = inflater.inflate(R.layout.item_mpl_list, null);
				mholder = new MplHolder();
				mholder.iv_mpl = (ImageView) convertView.findViewById(R.id.iv_mpl);
				mholder.iv_mpl1 = (ImageView) convertView.findViewById(R.id.iv_mpl_img1);
				mholder.iv_mpl2 = (ImageView) convertView.findViewById(R.id.iv_mpl_img2);
				mholder.iv_mpl3 = (ImageView) convertView.findViewById(R.id.iv_mpl_img3);
				mholder.iv_mpl4 = (ImageView) convertView.findViewById(R.id.iv_mpl_img4);
				mholder.tv_mpl_nickname = (TextView) convertView.findViewById(R.id.tv_mpl_nickname);
				mholder.tv_mpl_createtime = (TextView) convertView.findViewById(R.id.tv_mpl_createtime);
				mholder.tv_mpl_content = (TextView) convertView.findViewById(R.id.tv_mpl_content);
				mholder.tv_mpl_del = (TextView) convertView.findViewById(R.id.tv_mpl_del);
				convertView.setTag(mholder);
			break;
			case 11:
				convertView  = inflater.inflate(R.layout.item_mpl_list, null);
				mholder = new MplHolder();
				mholder.iv_mpl = (ImageView) convertView.findViewById(R.id.iv_mpl);
				mholder.iv_mpl1 = (ImageView) convertView.findViewById(R.id.iv_mpl_img1);
				mholder.iv_mpl2 = (ImageView) convertView.findViewById(R.id.iv_mpl_img2);
				mholder.iv_mpl3 = (ImageView) convertView.findViewById(R.id.iv_mpl_img3);
				mholder.iv_mpl4 = (ImageView) convertView.findViewById(R.id.iv_mpl_img4);
				mholder.tv_mpl_nickname = (TextView) convertView.findViewById(R.id.tv_mpl_nickname);
				mholder.tv_mpl_createtime = (TextView) convertView.findViewById(R.id.tv_mpl_createtime);
				mholder.tv_mpl_content = (TextView) convertView.findViewById(R.id.tv_mpl_content);
				mholder.tv_mpl_del = (TextView) convertView.findViewById(R.id.tv_mpl_del);
				convertView.setTag(mholder);
			break;
			case 12:
				convertView  = inflater.inflate(R.layout.item_mpl_list, null);
				mholder = new MplHolder();
				mholder.iv_mpl = (ImageView) convertView.findViewById(R.id.iv_mpl);
				mholder.iv_mpl1 = (ImageView) convertView.findViewById(R.id.iv_mpl_img1);
				mholder.iv_mpl2 = (ImageView) convertView.findViewById(R.id.iv_mpl_img2);
				mholder.iv_mpl3 = (ImageView) convertView.findViewById(R.id.iv_mpl_img3);
				mholder.iv_mpl4 = (ImageView) convertView.findViewById(R.id.iv_mpl_img4);
				mholder.tv_mpl_nickname = (TextView) convertView.findViewById(R.id.tv_mpl_nickname);
				mholder.tv_mpl_createtime = (TextView) convertView.findViewById(R.id.tv_mpl_createtime);
				mholder.tv_mpl_content = (TextView) convertView.findViewById(R.id.tv_mpl_content);
				mholder.tv_mpl_del = (TextView) convertView.findViewById(R.id.tv_mpl_del);
				convertView.setTag(mholder);
			break;
			case 13:
				convertView  = inflater.inflate(R.layout.item_mpl_list, null);
				mholder = new MplHolder();
				mholder.iv_mpl = (ImageView) convertView.findViewById(R.id.iv_mpl);
				mholder.iv_mpl1 = (ImageView) convertView.findViewById(R.id.iv_mpl_img1);
				mholder.iv_mpl2 = (ImageView) convertView.findViewById(R.id.iv_mpl_img2);
				mholder.iv_mpl3 = (ImageView) convertView.findViewById(R.id.iv_mpl_img3);
				mholder.iv_mpl4 = (ImageView) convertView.findViewById(R.id.iv_mpl_img4);
				mholder.tv_mpl_nickname = (TextView) convertView.findViewById(R.id.tv_mpl_nickname);
				mholder.tv_mpl_createtime = (TextView) convertView.findViewById(R.id.tv_mpl_createtime);
				mholder.tv_mpl_content = (TextView) convertView.findViewById(R.id.tv_mpl_content);
				mholder.tv_mpl_del = (TextView) convertView.findViewById(R.id.tv_mpl_del);
				convertView.setTag(mholder);
			break;
			case 14:
				convertView  = inflater.inflate(R.layout.item_mpl_list, null);
				mholder = new MplHolder();
				mholder.iv_mpl = (ImageView) convertView.findViewById(R.id.iv_mpl);
				mholder.iv_mpl1 = (ImageView) convertView.findViewById(R.id.iv_mpl_img1);
				mholder.iv_mpl2 = (ImageView) convertView.findViewById(R.id.iv_mpl_img2);
				mholder.iv_mpl3 = (ImageView) convertView.findViewById(R.id.iv_mpl_img3);
				mholder.iv_mpl4 = (ImageView) convertView.findViewById(R.id.iv_mpl_img4);
				mholder.tv_mpl_nickname = (TextView) convertView.findViewById(R.id.tv_mpl_nickname);
				mholder.tv_mpl_createtime = (TextView) convertView.findViewById(R.id.tv_mpl_createtime);
				mholder.tv_mpl_content = (TextView) convertView.findViewById(R.id.tv_mpl_content);
				mholder.tv_mpl_del = (TextView) convertView.findViewById(R.id.tv_mpl_del);
				convertView.setTag(mholder);
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
				convertView.setTag(yholder);
			break;
			}
		}else {
			switch(type){
			case 1:
				mholder = (MplHolder) convertView.getTag();
			break;
			case 11:
				mholder = (MplHolder) convertView.getTag();
			break;
			case 12:
				mholder = (MplHolder) convertView.getTag();
			break;
			case 13:
				mholder = (MplHolder) convertView.getTag();
			break;
			case 14:
				mholder = (MplHolder) convertView.getTag();
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
			}
		}
		
		String zhengze = "em_[0-9]{2}|em_[0-9]{1}";	//正则表达式，用来判断消息内是否有表情
		String content = arrays.get(position).getContent();
		if(content!=null&&!content.equals("")){
			content = content.replace("[", "");
			content = content.replace("]", "");
		}
		
		switch(type){
		
        case 1:  
        	String imageUrl_mpl = arrays.get(position).getHeadimg();  
        	imageLoader.displayImage(imageUrl_mpl, mholder.iv_mpl, options_head, null);
        	mholder.tv_mpl_nickname.setText(arrays.get(position).getNickname());
        	mholder.tv_mpl_createtime.setText(date);
        	
        	try {
				SpannableString spannableString = ExpressionUtil.getExpressionString(mContext, content, zhengze,densityDpi);
				mholder.tv_mpl_content.setText(spannableString);
			} catch (NumberFormatException e) {
				e.printStackTrace();
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			}
        	
        	mholder.tv_mpl_del.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					dialog(hdnewcommentid,position);
				}
			});
        break; 
            
        case 11:
        	//头像
        	String imageUrl_mpl1_head = arrays.get(position).getHeadimg();  
        	imageLoader.displayImage(imageUrl_mpl1_head, mholder.iv_mpl, options_head, null);
        	//评论的图片
            List<ImageView> list_mpl1 = new ArrayList<ImageView>();
            list_mpl1.add(mholder.iv_mpl1);
            String[] urls1 = null;
        	String imageUrl_mpl1 = arrays.get(position).getImagepath();  
	        if(imageUrl_mpl1!=null){
	        	urls1 = imageUrl_mpl1.split(",");
	        	for(int i=0;i<urls1.length;i++){
	        		list_mpl1.get(i).setVisibility(View.VISIBLE);
	        		imageLoader.displayImage(urls1[i], list_mpl1.get(i), options, null);
	        		final Intent intent = new Intent(mContext,ViewPagerActivity.class);
					intent.putExtra("ID", i);
					intent.putExtra("imgurl", urls1);
	        		list_mpl1.get(i).setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							mContext.startActivity(intent);
							((Activity) mContext).overridePendingTransition(R.anim.zoom_enter,0);
						}
					});
	        	}
	        }
            	mholder.tv_mpl_nickname.setText(arrays.get(position).getNickname());
            	mholder.tv_mpl_createtime.setText(date);
            	try {
    				SpannableString spannableString = ExpressionUtil.getExpressionString(mContext, content, zhengze,densityDpi);
    				mholder.tv_mpl_content.setText(spannableString);
    			} catch (NumberFormatException e) {
    				e.printStackTrace();
    			} catch (SecurityException e) {
    				e.printStackTrace();
    			} catch (IllegalArgumentException e) {
    				e.printStackTrace();
    			}
            	mholder.tv_mpl_del.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						dialog(hdnewcommentid,position);
					}
				});
        break; 
            
        case 12:  
        	//头像
        	String imageUrl_mpl2_head = arrays.get(position).getHeadimg();  
        	imageLoader.displayImage(imageUrl_mpl2_head, mholder.iv_mpl, options_head, null);
        	
        	//评论的图片
            List<ImageView> list_mpl2 = new ArrayList<ImageView>();
            list_mpl2.add(mholder.iv_mpl1);
            list_mpl2.add(mholder.iv_mpl2);
            
            String[] urls2 = null;
        	String imageUrl_mpl2 = arrays.get(position).getImagepath();  
	        if(imageUrl_mpl2!=null){
	        	urls2 = imageUrl_mpl2.split(",");
	        	for(int i=0;i<urls2.length;i++){
	        		list_mpl2.get(i).setVisibility(View.VISIBLE);
	        		imageLoader.displayImage(urls2[i], list_mpl2.get(i), options, null);
	        		final Intent intent = new Intent(mContext,ViewPagerActivity.class);
					intent.putExtra("ID", i);
					intent.putExtra("imgurl", urls2);
	        		list_mpl2.get(i).setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							mContext.startActivity(intent);
							((Activity) mContext).overridePendingTransition(R.anim.zoom_enter,0);
						}
					});
	        	}
	        }
            	mholder.tv_mpl_nickname.setText(arrays.get(position).getNickname());
            	mholder.tv_mpl_createtime.setText(date);
            	try {
    				SpannableString spannableString = ExpressionUtil.getExpressionString(mContext, content, zhengze,densityDpi);
    				mholder.tv_mpl_content.setText(spannableString);
    			} catch (NumberFormatException e) {
    				e.printStackTrace();
    			} catch (SecurityException e) {
    				e.printStackTrace();
    			} catch (IllegalArgumentException e) {
    				e.printStackTrace();
    			}
            	mholder.tv_mpl_del.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						dialog(hdnewcommentid,position);
					}
				});
        break; 
                
        case 13:  
        	//头像
        	String imageUrl_mpl3_head = arrays.get(position).getHeadimg();  
        	imageLoader.displayImage(imageUrl_mpl3_head, mholder.iv_mpl, options_head, null);
        	
        	//评论的图片
            List<ImageView> list_mpl3 = new ArrayList<ImageView>();
            list_mpl3.add(mholder.iv_mpl1);
            list_mpl3.add(mholder.iv_mpl2);
            list_mpl3.add(mholder.iv_mpl3);
            
            String[] urls3 = null;
        	String imageUrl_mpl3 = arrays.get(position).getImagepath();  
	        if(imageUrl_mpl3!=null){
	        	urls3 = imageUrl_mpl3.split(",");
	        	for(int i=0;i<urls3.length;i++){
	        		list_mpl3.get(i).setVisibility(View.VISIBLE);
	        		imageLoader.displayImage(urls3[i], list_mpl3.get(i), options, null);
	        		final Intent intent = new Intent(mContext,ViewPagerActivity.class);
					intent.putExtra("ID", i);
					intent.putExtra("imgurl", urls3);
	        		list_mpl3.get(i).setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							mContext.startActivity(intent);
							((Activity) mContext).overridePendingTransition(R.anim.zoom_enter,0);
						}
					});
	        	}
	        }
            	mholder.tv_mpl_nickname.setText(arrays.get(position).getNickname());
            	mholder.tv_mpl_createtime.setText(date);
            	try {
    				SpannableString spannableString = ExpressionUtil.getExpressionString(mContext, content, zhengze,densityDpi);
    				mholder.tv_mpl_content.setText(spannableString);
    			} catch (NumberFormatException e) {
    				e.printStackTrace();
    			} catch (SecurityException e) {
    				e.printStackTrace();
    			} catch (IllegalArgumentException e) {
    				e.printStackTrace();
    			}
            	mholder.tv_mpl_del.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						dialog(hdnewcommentid,position);
					}
				});
                	
        break; 
                
        case 14:  
        	//头像
        	String imageUrl_mpl4_head = arrays.get(position).getHeadimg();  
        	imageLoader.displayImage(imageUrl_mpl4_head, mholder.iv_mpl, options_head, null);
        	//评论的图片
            List<ImageView> list_mpl4 = new ArrayList<ImageView>();
            list_mpl4.add(mholder.iv_mpl1);
            list_mpl4.add(mholder.iv_mpl2);
            list_mpl4.add(mholder.iv_mpl3);
            list_mpl4.add(mholder.iv_mpl4);
            
            String[] urls4 = null;
        	String imageUrl_mpl4 = arrays.get(position).getImagepath();  
	        if(imageUrl_mpl4!=null){
	        	urls4 = imageUrl_mpl4.split(",");
	        	for(int i=0;i<urls4.length;i++){
	        		list_mpl4.get(i).setVisibility(View.VISIBLE);
	        		imageLoader.displayImage(urls4[i], list_mpl4.get(i), options, null);
	        		final Intent intent = new Intent(mContext,ViewPagerActivity.class);
					intent.putExtra("ID", i);
					intent.putExtra("imgurl", urls4);
	        		list_mpl4.get(i).setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							mContext.startActivity(intent);
							((Activity) mContext).overridePendingTransition(R.anim.zoom_enter,0);
						}
					});
	        	}
	        }
            	mholder.tv_mpl_nickname.setText(arrays.get(position).getNickname());
            	mholder.tv_mpl_createtime.setText(date);
            	try {
    				SpannableString spannableString = ExpressionUtil.getExpressionString(mContext, content, zhengze,densityDpi);
    				mholder.tv_mpl_content.setText(spannableString);
    			} catch (NumberFormatException e) {
    				e.printStackTrace();
    			} catch (SecurityException e) {
    				e.printStackTrace();
    			} catch (IllegalArgumentException e) {
    				e.printStackTrace();
    			}
            	mholder.tv_mpl_del.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						dialog(hdnewcommentid,position);
					}
				});
            break; 
            
            case 2:  
            	String imageUrl_ypl = arrays.get(position).getHeadimg();  
            	imageLoader.displayImage(imageUrl_ypl, yholder.iv_ypl, options_head, null);
                yholder.tv_ypl_nickname.setText(arrays.get(position).getNickname());
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
            	if(uid!=0){
            		yholder.rl_ychat.setOnLongClickListener(new OnLongClickListener() {
	    				@Override
	    				public boolean onLongClick(View v) {
	    					PopupMenu menu = new PopupMenu(mContext);
	    					menu.setOnItemSelectedListener(new OnItemSelectedListener() {
	    						@Override
	    						public void onItemSelected(MenuItem item) {
	    							switch(item.getItemId()){
	    								case REPORT:
	    									reportdialog(hdnewcommentid);
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
//            	if(uid==0){
//            		yholder.tv_ypl_report.setVisibility(View.GONE);
//            	}
//            	yholder.tv_ypl_report.setOnClickListener(new OnClickListener() {
//    				@Override
//    				public void onClick(View v) {
//    					reportdialog(hdnewcommentid);
//    				}
//    			});
            break;  
            
            case 21:
            	//头像
            	String imageUrl_ypl1_head = arrays.get(position).getHeadimg();  
            	imageLoader.displayImage(imageUrl_ypl1_head, yholder.iv_ypl, options_head, null);
            	
            	//评论的图片
                List<ImageView> list_ypl1 = new ArrayList<ImageView>();
                list_ypl1.add(yholder.iv_ypl1);
                
                String[] urls21 = null;
            	String imageUrl_ypl1 = arrays.get(position).getImagepath();  
    	        if(imageUrl_ypl1!=null){
    	        	urls21 = imageUrl_ypl1.split(",");
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
                	yholder.tv_ypl_nickname.setText(arrays.get(position).getNickname());
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
                	if(uid!=0){
                		yholder.rl_ychat.setOnLongClickListener(new OnLongClickListener() {
    	    				@Override
    	    				public boolean onLongClick(View v) {
    	    					PopupMenu menu = new PopupMenu(mContext);
    	    					menu.setOnItemSelectedListener(new OnItemSelectedListener() {
    	    						@Override
    	    						public void onItemSelected(MenuItem item) {
    	    							switch(item.getItemId()){
    	    								case REPORT:
    	    									reportdialog(hdnewcommentid);
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
//                	if(uid==0){
//                		yholder.tv_ypl_report.setVisibility(View.GONE);
//                	}
//                	yholder.tv_ypl_report.setOnClickListener(new OnClickListener() {
//        				@Override
//        				public void onClick(View v) {
//        					reportdialog(hdnewcommentid);
//        				}
//        			});
                    break; 
                
            case 22:  
            	//头像
            	String imageUrl_ypl2_head = arrays.get(position).getHeadimg();  
            	imageLoader.displayImage(imageUrl_ypl2_head, yholder.iv_ypl, options_head, null);
            	
            	//评论的图片
                List<ImageView> list_ypl2 = new ArrayList<ImageView>();
                list_ypl2.add(yholder.iv_ypl1);
                list_ypl2.add(yholder.iv_ypl2);
                
                String[] urls22 = null;
            	String imageUrl_ypl2 = arrays.get(position).getImagepath();  
    	        if(imageUrl_ypl2!=null){
    	        	urls22 = imageUrl_ypl2.split(",");
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
                	yholder.tv_ypl_nickname.setText(arrays.get(position).getNickname());
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
                	if(uid!=0){
                		yholder.rl_ychat.setOnLongClickListener(new OnLongClickListener() {
    	    				@Override
    	    				public boolean onLongClick(View v) {
    	    					PopupMenu menu = new PopupMenu(mContext);
    	    					menu.setOnItemSelectedListener(new OnItemSelectedListener() {
    	    						@Override
    	    						public void onItemSelected(MenuItem item) {
    	    							switch(item.getItemId()){
    	    								case REPORT:
    	    									reportdialog(hdnewcommentid);
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
//                	if(uid==0){
//                		yholder.tv_ypl_report.setVisibility(View.GONE);
//                	}
//                	yholder.tv_ypl_report.setOnClickListener(new OnClickListener() {
//        				@Override
//        				public void onClick(View v) {
//        					reportdialog(hdnewcommentid);
//        				}
//        			});
//                    break; 
                    
            case 23:  
            	//头像
            	String imageUrl_ypl3_head = arrays.get(position).getHeadimg();  
            	imageLoader.displayImage(imageUrl_ypl3_head, yholder.iv_ypl, options_head, null);
            	
            	//评论的图片
                List<ImageView> list_ypl3 = new ArrayList<ImageView>();
                list_ypl3.add(yholder.iv_ypl1);
                list_ypl3.add(yholder.iv_ypl2);
                list_ypl3.add(yholder.iv_ypl3);
                
                String[] urls23 = null;
            	String imageUrl_ypl3 = arrays.get(position).getImagepath();  
    	        if(imageUrl_ypl3!=null){
    	        	urls23 = imageUrl_ypl3.split(",");
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
                	yholder.tv_ypl_nickname.setText(arrays.get(position).getNickname());
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
                	if(uid!=0){
                		yholder.rl_ychat.setOnLongClickListener(new OnLongClickListener() {
    	    				@Override
    	    				public boolean onLongClick(View v) {
    	    					PopupMenu menu = new PopupMenu(mContext);
    	    					menu.setOnItemSelectedListener(new OnItemSelectedListener() {
    	    						@Override
    	    						public void onItemSelected(MenuItem item) {
    	    							switch(item.getItemId()){
    	    								case REPORT:
    	    									reportdialog(hdnewcommentid);
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
//                	if(uid==0){
//                		yholder.tv_ypl_report.setVisibility(View.GONE);
//                	}
//                	yholder.tv_ypl_report.setOnClickListener(new OnClickListener() {
//        				@Override
//        				public void onClick(View v) {
//        					reportdialog(hdnewcommentid);
//        				}
//        			});
                    break; 
                    
            case 24:  
            	//头像
            	String imageUrl_ypl4_head = arrays.get(position).getHeadimg();  
            	imageLoader.displayImage(imageUrl_ypl4_head, yholder.iv_ypl, options_head, null);
            	//评论的图片
                List<ImageView> list_ypl4 = new ArrayList<ImageView>();
                list_ypl4.add(yholder.iv_ypl1);
                list_ypl4.add(yholder.iv_ypl2);
                list_ypl4.add(yholder.iv_ypl3);
                list_ypl4.add(yholder.iv_ypl4);
                
                String[] urls24 = null;
            	String imageUrl_ypl4 = arrays.get(position).getImagepath();  
    	        if(imageUrl_ypl4!=null){
    	        	urls24 = imageUrl_ypl4.split(",");
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
                	yholder.tv_ypl_nickname.setText(arrays.get(position).getNickname());
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
                	if(uid!=0){
                		yholder.rl_ychat.setOnLongClickListener(new OnLongClickListener() {
    	    				@Override
    	    				public boolean onLongClick(View v) {
    	    					PopupMenu menu = new PopupMenu(mContext);
    	    					menu.setOnItemSelectedListener(new OnItemSelectedListener() {
    	    						@Override
    	    						public void onItemSelected(MenuItem item) {
    	    							switch(item.getItemId()){
    	    								case REPORT:
    	    									reportdialog(hdnewcommentid);
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
//                	if(uid==0){
//                		yholder.tv_ypl_report.setVisibility(View.GONE);
//                	}
//                	yholder.tv_ypl_report.setOnClickListener(new OnClickListener() {
//        				@Override
//        				public void onClick(View v) {
//        					reportdialog(hdnewcommentid);
//        				}
//        			});
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
		
		private void reportdialog(final String hdnewcommentid){
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
							String resultString = HttpUtil.GetStringFromUrl(reportypl_url+hdnewcommentid);
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
			ImageView iv_ypl;
			ImageView iv_ypl1;
			ImageView iv_ypl2;
			ImageView iv_ypl3;
			ImageView iv_ypl4;
		}
}



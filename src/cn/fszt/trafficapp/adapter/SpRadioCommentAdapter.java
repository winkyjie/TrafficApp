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

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Handler;
import android.text.SpannableString;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cn.fszt.trafficapp.R;
import cn.fszt.trafficapp.db.DBManager;
import cn.fszt.trafficapp.domain.RadioHudongData;
import cn.fszt.trafficapp.util.HttpUtil;
import cn.fszt.trafficapp.widget.expression.ExpressionUtil;
import cn.fszt.trafficapp.widget.msg.AppMsg;
import cn.fszt.trafficapp.widget.photoview.DisplayImageOptionsUtil;
import cn.fszt.trafficapp.widget.photoview.ViewPagerActivity;

/**
 * 我的空间-电台节目评论互动适配器
 * @author AeiouKong
 *
 */
public class SpRadioCommentAdapter extends BaseAdapter {
	private Context mContext;
	private List<RadioHudongData> arrays;
	private LayoutInflater inflater;
	private String delcarlifeandhuodong_url,s_hdcommentid,uid;
	private ImageLoader imageLoader;
	private DisplayImageOptions options;
	private DBManager mgr;
	private int densityDpi;
	AppMsg.Style style = AppMsg.STYLE_INFO;
	AppMsg appMsg;
	private MediaPlayer mPlayer = null;
	
	public SpRadioCommentAdapter(Context mContext, List<RadioHudongData> arrays,LayoutInflater inflater,String delcarlifeandhuodong_url,String uid,int densityDpi){
		this.mContext = mContext;
		this.arrays = arrays;
		this.inflater = inflater;
		this.delcarlifeandhuodong_url = delcarlifeandhuodong_url;
		this.uid = uid;
		this.densityDpi = densityDpi;
		mgr = new DBManager(mContext);
		mPlayer = new MediaPlayer();
		
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
	public int getItemViewType(int position) {
		String[] urls;
		String imagepath = arrays.get(position).getImagepath();
		String voicepath = arrays.get(position).getVoicepath();

		//有图片没有录音
		if(!"".equals(imagepath)&&"".equals(voicepath)){
			urls = imagepath.split(",");
			if(urls.length==1&&"".equals(voicepath)){
				return 11;
			}else if(urls.length==2&&"".equals(voicepath)){
				return 12;
			}else if(urls.length==3&&"".equals(voicepath)){
				return 13;
			}else if(urls.length==4&&"".equals(voicepath)){
				return 14;
			}
		}
		//有图片有录音
		else if(!"".equals(imagepath)&&!"".equals(voicepath)){
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
		//没有图片有录音
		else if("".equals(imagepath)&&!"".equals(voicepath)){
			return 19;
		}
		//没有图片没有录音
		else{
			return 1;
		}
		return 1;
	}

	@Override
	public int getViewTypeCount() {
		return 30;
	}
	

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		RadioHudongHolder mholder = null;
		int type = getItemViewType(position); 
		final String hdcommentid = arrays.get(position).getHdcommentid();
		String[] urls = null;
		List<ImageView> list_imageview = new ArrayList<ImageView>();
		String imagepath = arrays.get(position).getImagepath();  
		final String voicepath = arrays.get(position).getVoicepath();
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
			switch(type){
			case 1:
				convertView  = inflater.inflate(R.layout.item_radiohudong_list, null);
				mholder = new RadioHudongHolder();
				mholder.tv_radiohudong_createtime = (TextView) convertView.findViewById(R.id.tv_radiohudong_createtime);
				mholder.tv_radiohudong_content = (TextView) convertView.findViewById(R.id.tv_radiohudong_content);
				mholder.rl_radiohudong = (RelativeLayout) convertView.findViewById(R.id.rl_radiohudong);
				convertView.setTag(mholder);
			break;
			case 11:
				convertView  = inflater.inflate(R.layout.item_radiohudong_list, null);
				mholder = new RadioHudongHolder();
				mholder.tv_radiohudong_createtime = (TextView) convertView.findViewById(R.id.tv_radiohudong_createtime);
				mholder.tv_radiohudong_content = (TextView) convertView.findViewById(R.id.tv_radiohudong_content);
				mholder.iv_radiohudong1 = (ImageView) convertView.findViewById(R.id.iv_radiohudong_img1);
				mholder.iv_radiohudong2 = (ImageView) convertView.findViewById(R.id.iv_radiohudong_img2);
				mholder.iv_radiohudong3 = (ImageView) convertView.findViewById(R.id.iv_radiohudong_img3);
				mholder.iv_radiohudong4 = (ImageView) convertView.findViewById(R.id.iv_radiohudong_img4);
				mholder.rl_radiohudong = (RelativeLayout) convertView.findViewById(R.id.rl_radiohudong);
				convertView.setTag(mholder);
			break;
			case 12:
				convertView  = inflater.inflate(R.layout.item_radiohudong_list, null);
				mholder = new RadioHudongHolder();
				mholder.tv_radiohudong_createtime = (TextView) convertView.findViewById(R.id.tv_radiohudong_createtime);
				mholder.tv_radiohudong_content = (TextView) convertView.findViewById(R.id.tv_radiohudong_content);
				mholder.iv_radiohudong1 = (ImageView) convertView.findViewById(R.id.iv_radiohudong_img1);
				mholder.iv_radiohudong2 = (ImageView) convertView.findViewById(R.id.iv_radiohudong_img2);
				mholder.iv_radiohudong3 = (ImageView) convertView.findViewById(R.id.iv_radiohudong_img3);
				mholder.iv_radiohudong4 = (ImageView) convertView.findViewById(R.id.iv_radiohudong_img4);
				mholder.rl_radiohudong = (RelativeLayout) convertView.findViewById(R.id.rl_radiohudong);
				convertView.setTag(mholder);
			break;
			case 13:
				convertView  = inflater.inflate(R.layout.item_radiohudong_list, null);
				mholder = new RadioHudongHolder();
				mholder.tv_radiohudong_createtime = (TextView) convertView.findViewById(R.id.tv_radiohudong_createtime);
				mholder.tv_radiohudong_content = (TextView) convertView.findViewById(R.id.tv_radiohudong_content);
				mholder.iv_radiohudong1 = (ImageView) convertView.findViewById(R.id.iv_radiohudong_img1);
				mholder.iv_radiohudong2 = (ImageView) convertView.findViewById(R.id.iv_radiohudong_img2);
				mholder.iv_radiohudong3 = (ImageView) convertView.findViewById(R.id.iv_radiohudong_img3);
				mholder.iv_radiohudong4 = (ImageView) convertView.findViewById(R.id.iv_radiohudong_img4);
				mholder.rl_radiohudong = (RelativeLayout) convertView.findViewById(R.id.rl_radiohudong);
				convertView.setTag(mholder);
			break;
			case 14:
				convertView  = inflater.inflate(R.layout.item_radiohudong_list, null);
				mholder = new RadioHudongHolder();
				mholder.tv_radiohudong_createtime = (TextView) convertView.findViewById(R.id.tv_radiohudong_createtime);
				mholder.tv_radiohudong_content = (TextView) convertView.findViewById(R.id.tv_radiohudong_content);
				mholder.iv_radiohudong1 = (ImageView) convertView.findViewById(R.id.iv_radiohudong_img1);
				mholder.iv_radiohudong2 = (ImageView) convertView.findViewById(R.id.iv_radiohudong_img2);
				mholder.iv_radiohudong3 = (ImageView) convertView.findViewById(R.id.iv_radiohudong_img3);
				mholder.iv_radiohudong4 = (ImageView) convertView.findViewById(R.id.iv_radiohudong_img4);
				mholder.rl_radiohudong = (RelativeLayout) convertView.findViewById(R.id.rl_radiohudong);
				convertView.setTag(mholder);
			break;
			case 15:
				convertView  = inflater.inflate(R.layout.item_radiohudong_list, null);
				mholder = new RadioHudongHolder();
				mholder.tv_radiohudong_createtime = (TextView) convertView.findViewById(R.id.tv_radiohudong_createtime);
				mholder.tv_radiohudong_content = (TextView) convertView.findViewById(R.id.tv_radiohudong_content);
				mholder.iv_radiohudong1 = (ImageView) convertView.findViewById(R.id.iv_radiohudong_img1);
				mholder.iv_radiohudong2 = (ImageView) convertView.findViewById(R.id.iv_radiohudong_img2);
				mholder.iv_radiohudong3 = (ImageView) convertView.findViewById(R.id.iv_radiohudong_img3);
				mholder.iv_radiohudong4 = (ImageView) convertView.findViewById(R.id.iv_radiohudong_img4);
				mholder.iv_radiohudong_laba = (ImageView) convertView.findViewById(R.id.iv_radiohudong_laba);
				mholder.rl_radiohudong = (RelativeLayout) convertView.findViewById(R.id.rl_radiohudong);
				convertView.setTag(mholder);
			break;
			case 16:
				convertView  = inflater.inflate(R.layout.item_radiohudong_list, null);
				mholder = new RadioHudongHolder();
				mholder.tv_radiohudong_createtime = (TextView) convertView.findViewById(R.id.tv_radiohudong_createtime);
				mholder.tv_radiohudong_content = (TextView) convertView.findViewById(R.id.tv_radiohudong_content);
				mholder.iv_radiohudong1 = (ImageView) convertView.findViewById(R.id.iv_radiohudong_img1);
				mholder.iv_radiohudong2 = (ImageView) convertView.findViewById(R.id.iv_radiohudong_img2);
				mholder.iv_radiohudong3 = (ImageView) convertView.findViewById(R.id.iv_radiohudong_img3);
				mholder.iv_radiohudong4 = (ImageView) convertView.findViewById(R.id.iv_radiohudong_img4);
				mholder.iv_radiohudong_laba = (ImageView) convertView.findViewById(R.id.iv_radiohudong_laba);
				mholder.rl_radiohudong = (RelativeLayout) convertView.findViewById(R.id.rl_radiohudong);
				convertView.setTag(mholder);
			break;
			case 17:
				convertView  = inflater.inflate(R.layout.item_radiohudong_list, null);
				mholder = new RadioHudongHolder();
				mholder.tv_radiohudong_createtime = (TextView) convertView.findViewById(R.id.tv_radiohudong_createtime);
				mholder.tv_radiohudong_content = (TextView) convertView.findViewById(R.id.tv_radiohudong_content);
				mholder.iv_radiohudong1 = (ImageView) convertView.findViewById(R.id.iv_radiohudong_img1);
				mholder.iv_radiohudong2 = (ImageView) convertView.findViewById(R.id.iv_radiohudong_img2);
				mholder.iv_radiohudong3 = (ImageView) convertView.findViewById(R.id.iv_radiohudong_img3);
				mholder.iv_radiohudong4 = (ImageView) convertView.findViewById(R.id.iv_radiohudong_img4);
				mholder.iv_radiohudong_laba = (ImageView) convertView.findViewById(R.id.iv_radiohudong_laba);
				mholder.rl_radiohudong = (RelativeLayout) convertView.findViewById(R.id.rl_radiohudong);
				convertView.setTag(mholder);
			break;
			case 18:
				convertView  = inflater.inflate(R.layout.item_radiohudong_list, null);
				mholder = new RadioHudongHolder();
				mholder.tv_radiohudong_createtime = (TextView) convertView.findViewById(R.id.tv_radiohudong_createtime);
				mholder.tv_radiohudong_content = (TextView) convertView.findViewById(R.id.tv_radiohudong_content);
				mholder.iv_radiohudong1 = (ImageView) convertView.findViewById(R.id.iv_radiohudong_img1);
				mholder.iv_radiohudong2 = (ImageView) convertView.findViewById(R.id.iv_radiohudong_img2);
				mholder.iv_radiohudong3 = (ImageView) convertView.findViewById(R.id.iv_radiohudong_img3);
				mholder.iv_radiohudong4 = (ImageView) convertView.findViewById(R.id.iv_radiohudong_img4);
				mholder.iv_radiohudong_laba = (ImageView) convertView.findViewById(R.id.iv_radiohudong_laba);
				mholder.rl_radiohudong = (RelativeLayout) convertView.findViewById(R.id.rl_radiohudong);
				convertView.setTag(mholder);
			break;
			case 19:
				convertView  = inflater.inflate(R.layout.item_radiohudong_list, null);
				mholder = new RadioHudongHolder();
				mholder.tv_radiohudong_createtime = (TextView) convertView.findViewById(R.id.tv_radiohudong_createtime);
				mholder.tv_radiohudong_content = (TextView) convertView.findViewById(R.id.tv_radiohudong_content);
				mholder.iv_radiohudong1 = (ImageView) convertView.findViewById(R.id.iv_radiohudong_img1);
				mholder.iv_radiohudong2 = (ImageView) convertView.findViewById(R.id.iv_radiohudong_img2);
				mholder.iv_radiohudong3 = (ImageView) convertView.findViewById(R.id.iv_radiohudong_img3);
				mholder.iv_radiohudong4 = (ImageView) convertView.findViewById(R.id.iv_radiohudong_img4);
				mholder.iv_radiohudong_laba = (ImageView) convertView.findViewById(R.id.iv_radiohudong_laba);
				mholder.rl_radiohudong = (RelativeLayout) convertView.findViewById(R.id.rl_radiohudong);
				convertView.setTag(mholder);
			break;
			}
		}else {
			switch(type){
			case 1:
				mholder = (RadioHudongHolder) convertView.getTag();
			break;
			case 11:
				mholder = (RadioHudongHolder) convertView.getTag();
			break;
			case 12:
				mholder = (RadioHudongHolder) convertView.getTag();
			break;
			case 13:
				mholder = (RadioHudongHolder) convertView.getTag();
			break;
			case 14:
				mholder = (RadioHudongHolder) convertView.getTag();
			break;
			case 15:
				mholder = (RadioHudongHolder) convertView.getTag();
			break;
			case 16:
				mholder = (RadioHudongHolder) convertView.getTag();
			break;
			case 17:
				mholder = (RadioHudongHolder) convertView.getTag();
			break;
			case 18:
				mholder = (RadioHudongHolder) convertView.getTag();
			break;
			case 19:
				mholder = (RadioHudongHolder) convertView.getTag();
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
        	mholder.tv_radiohudong_createtime.setText(date);
        	try {
				SpannableString spannableString = ExpressionUtil.getExpressionString(mContext, content, zhengze,densityDpi);
				mholder.tv_radiohudong_content.setText(spannableString);
			} catch (NumberFormatException e) {
				e.printStackTrace();
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			}
        	mholder.rl_radiohudong.setOnLongClickListener(new OnLongClickListener() {
				
				@Override
				public boolean onLongClick(View v) {
					// TODO Auto-generated method stub
					dialog(hdcommentid,position);
					return true;
				}
			});
        break;  
        case 11:  
          //评论的图片
            list_imageview.add(mholder.iv_radiohudong1);
	        if(imagepath!=null){
	        	urls = imagepath.split(",");
	        	for(int i=0;i<urls.length;i++){
	        		list_imageview.get(i).setVisibility(View.VISIBLE);
	        		imageLoader.displayImage(urls[i], list_imageview.get(i), options, null);
	        		final Intent intent = new Intent(mContext,ViewPagerActivity.class);
					intent.putExtra("ID", i);
					intent.putExtra("imgurl", urls);
	        		list_imageview.get(i).setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							mContext.startActivity(intent);
						}
					});
	        	}
	        }
        	mholder.tv_radiohudong_createtime.setText(date);
        	try {
				SpannableString spannableString = ExpressionUtil.getExpressionString(mContext, content, zhengze,densityDpi);
				mholder.tv_radiohudong_content.setText(spannableString);
			} catch (NumberFormatException e) {
				e.printStackTrace();
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			}
        	mholder.rl_radiohudong.setOnLongClickListener(new OnLongClickListener() {
				
				@Override
				public boolean onLongClick(View v) {
					// TODO Auto-generated method stub
					dialog(hdcommentid,position);
					return true;
				}
			});
        break;  
        case 12:  
        	//评论的图片
            list_imageview.add(mholder.iv_radiohudong1);
            list_imageview.add(mholder.iv_radiohudong2);
            
	        if(imagepath!=null){
	        	urls = imagepath.split(",");
	        	for(int i=0;i<urls.length;i++){
	        		list_imageview.get(i).setVisibility(View.VISIBLE);
	        		imageLoader.displayImage(urls[i], list_imageview.get(i), options, null);
	        		final Intent intent = new Intent(mContext,ViewPagerActivity.class);
					intent.putExtra("ID", i);
					intent.putExtra("imgurl", urls);
	        		list_imageview.get(i).setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							mContext.startActivity(intent);
						}
					});
	        	}
	        }
            	mholder.tv_radiohudong_createtime.setText(date);
            	try {
    				SpannableString spannableString = ExpressionUtil.getExpressionString(mContext, content, zhengze,densityDpi);
    				mholder.tv_radiohudong_content.setText(spannableString);
    			} catch (NumberFormatException e) {
    				e.printStackTrace();
    			} catch (SecurityException e) {
    				e.printStackTrace();
    			} catch (IllegalArgumentException e) {
    				e.printStackTrace();
    			}
            	mholder.rl_radiohudong.setOnLongClickListener(new OnLongClickListener() {
    				
    				@Override
    				public boolean onLongClick(View v) {
    					// TODO Auto-generated method stub
    					dialog(hdcommentid,position);
    					return true;
    				}
    			});
        break; 
        case 13:  
        	//评论的图片
            list_imageview.add(mholder.iv_radiohudong1);
            list_imageview.add(mholder.iv_radiohudong2);
            list_imageview.add(mholder.iv_radiohudong3);
            
	        if(imagepath!=null){
	        	urls = imagepath.split(",");
	        	for(int i=0;i<urls.length;i++){
	        		list_imageview.get(i).setVisibility(View.VISIBLE);
	        		imageLoader.displayImage(urls[i], list_imageview.get(i), options, null);
	        		final Intent intent = new Intent(mContext,ViewPagerActivity.class);
					intent.putExtra("ID", i);
					intent.putExtra("imgurl", urls);
	        		list_imageview.get(i).setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							mContext.startActivity(intent);
						}
					});
	        	}
	        }
            	mholder.tv_radiohudong_createtime.setText(date);
            	try {
    				SpannableString spannableString = ExpressionUtil.getExpressionString(mContext, content, zhengze,densityDpi);
    				mholder.tv_radiohudong_content.setText(spannableString);
    			} catch (NumberFormatException e) {
    				e.printStackTrace();
    			} catch (SecurityException e) {
    				e.printStackTrace();
    			} catch (IllegalArgumentException e) {
    				e.printStackTrace();
    			}
            	mholder.rl_radiohudong.setOnLongClickListener(new OnLongClickListener() {
    				
    				@Override
    				public boolean onLongClick(View v) {
    					// TODO Auto-generated method stub
    					dialog(hdcommentid,position);
    					return true;
    				}
    			});                	
        break; 
        case 14:  
        	//评论的图片
            list_imageview.add(mholder.iv_radiohudong1);
            list_imageview.add(mholder.iv_radiohudong2);
            list_imageview.add(mholder.iv_radiohudong3);
            list_imageview.add(mholder.iv_radiohudong4);
            
	        if(imagepath!=null){
	        	urls = imagepath.split(",");
	        	for(int i=0;i<urls.length;i++){
	        		list_imageview.get(i).setVisibility(View.VISIBLE);
	        		imageLoader.displayImage(urls[i], list_imageview.get(i), options, null);
	        		final Intent intent = new Intent(mContext,ViewPagerActivity.class);
					intent.putExtra("ID", i);
					intent.putExtra("imgurl", urls);
	        		list_imageview.get(i).setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							mContext.startActivity(intent);
						}
					});
	        	}
	        }
            	mholder.tv_radiohudong_createtime.setText(date);
            	try {
    				SpannableString spannableString = ExpressionUtil.getExpressionString(mContext, content, zhengze,densityDpi);
    				mholder.tv_radiohudong_content.setText(spannableString);
    			} catch (NumberFormatException e) {
    				e.printStackTrace();
    			} catch (SecurityException e) {
    				e.printStackTrace();
    			} catch (IllegalArgumentException e) {
    				e.printStackTrace();
    			}
            	mholder.rl_radiohudong.setOnLongClickListener(new OnLongClickListener() {
    				
    				@Override
    				public boolean onLongClick(View v) {
    					// TODO Auto-generated method stub
    					dialog(hdcommentid,position);
    					return true;
    				}
    			});
        break; 
        
        
        case 15:  
            //评论的图片
            list_imageview.add(mholder.iv_radiohudong1);
	        if(imagepath!=null){
	        	urls = imagepath.split(",");
	        	for(int i=0;i<urls.length;i++){
	        		list_imageview.get(i).setVisibility(View.VISIBLE);
	        		imageLoader.displayImage(urls[i], list_imageview.get(i), options, null);
	        		final Intent intent = new Intent(mContext,ViewPagerActivity.class);
					intent.putExtra("ID", i);
					intent.putExtra("imgurl", urls);
	        		list_imageview.get(i).setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							mContext.startActivity(intent);
						}
					});
	        	}
	        }
	        mholder.iv_radiohudong_laba.setVisibility(View.VISIBLE);
	        mPlayer.setOnCompletionListener(new OnCompletionListener() {
				@Override
				public void onCompletion(MediaPlayer mp) {
				}
			});
	        mholder.rl_radiohudong.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if(mPlayer!=null&&mPlayer.isPlaying()){
						stopPlaying();
					}else{
						new Thread(){
							@Override
							public void run() {
								startPlaying(voicepath);
								super.run();
							}
						}.start();
					}
				}
			});
	        mholder.rl_radiohudong.setOnLongClickListener(new OnLongClickListener() {
				
				@Override
				public boolean onLongClick(View v) {
					// TODO Auto-generated method stub
					dialog(hdcommentid,position);
					return true;
				}
			});
        	mholder.tv_radiohudong_createtime.setText(date);
        	try {
				SpannableString spannableString = ExpressionUtil.getExpressionString(mContext, content, zhengze,densityDpi);
				mholder.tv_radiohudong_content.setText(spannableString);
			} catch (NumberFormatException e) {
				e.printStackTrace();
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			}
        break;  
        case 16:  
        	//评论的图片
            list_imageview.add(mholder.iv_radiohudong1);
            list_imageview.add(mholder.iv_radiohudong2);
            
	        if(imagepath!=null){
	        	urls = imagepath.split(",");
	        	for(int i=0;i<urls.length;i++){
	        		list_imageview.get(i).setVisibility(View.VISIBLE);
	        		imageLoader.displayImage(urls[i], list_imageview.get(i), options, null);
	        		final Intent intent = new Intent(mContext,ViewPagerActivity.class);
					intent.putExtra("ID", i);
					intent.putExtra("imgurl", urls);
	        		list_imageview.get(i).setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							mContext.startActivity(intent);
						}
					});
	        	}
	        }
    	        mholder.iv_radiohudong_laba.setVisibility(View.VISIBLE);
    	        mPlayer.setOnCompletionListener(new OnCompletionListener() {
					@Override
					public void onCompletion(MediaPlayer mp) {
					}
				});
    	        mholder.rl_radiohudong.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						if(mPlayer!=null&&mPlayer.isPlaying()){
							stopPlaying();
						}else{
							new Thread(){
								@Override
								public void run() {
									startPlaying(voicepath);
									super.run();
								}
							}.start();
						}
					}
				});
    	        mholder.rl_radiohudong.setOnLongClickListener(new OnLongClickListener() {
					
					@Override
					public boolean onLongClick(View v) {
						// TODO Auto-generated method stub
						dialog(hdcommentid,position);
						return true;
					}
				});
            	mholder.tv_radiohudong_createtime.setText(date);
            	try {
    				SpannableString spannableString = ExpressionUtil.getExpressionString(mContext, content, zhengze,densityDpi);
    				mholder.tv_radiohudong_content.setText(spannableString);
    			} catch (NumberFormatException e) {
    				e.printStackTrace();
    			} catch (SecurityException e) {
    				e.printStackTrace();
    			} catch (IllegalArgumentException e) {
    				e.printStackTrace();
    			}
        break; 
        case 17:  
        	//评论的图片
            list_imageview.add(mholder.iv_radiohudong1);
            list_imageview.add(mholder.iv_radiohudong2);
            list_imageview.add(mholder.iv_radiohudong3);
            
	        if(imagepath!=null){
	        	urls = imagepath.split(",");
	        	for(int i=0;i<urls.length;i++){
	        		list_imageview.get(i).setVisibility(View.VISIBLE);
	        		imageLoader.displayImage(urls[i], list_imageview.get(i), options, null);
	        		final Intent intent = new Intent(mContext,ViewPagerActivity.class);
					intent.putExtra("ID", i);
					intent.putExtra("imgurl", urls);
	        		list_imageview.get(i).setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							mContext.startActivity(intent);
						}
					});
	        	}
	        }
    	        mholder.iv_radiohudong_laba.setVisibility(View.VISIBLE);
    	        mPlayer.setOnCompletionListener(new OnCompletionListener() {
					@Override
					public void onCompletion(MediaPlayer mp) {
					}
				});
    	        mholder.rl_radiohudong.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						if(mPlayer!=null&&mPlayer.isPlaying()){
							stopPlaying();
						}else{
							new Thread(){
								@Override
								public void run() {
									startPlaying(voicepath);
									super.run();
								}
							}.start();
						}
					}
				});
    	        mholder.rl_radiohudong.setOnLongClickListener(new OnLongClickListener() {
					
					@Override
					public boolean onLongClick(View v) {
						// TODO Auto-generated method stub
						dialog(hdcommentid,position);
						return true;
					}
				});
            	mholder.tv_radiohudong_createtime.setText(date);
            	try {
    				SpannableString spannableString = ExpressionUtil.getExpressionString(mContext, content, zhengze,densityDpi);
    				mholder.tv_radiohudong_content.setText(spannableString);
    			} catch (NumberFormatException e) {
    				e.printStackTrace();
    			} catch (SecurityException e) {
    				e.printStackTrace();
    			} catch (IllegalArgumentException e) {
    				e.printStackTrace();
    			}
        break; 
        case 18:  
        	//评论的图片
            list_imageview.add(mholder.iv_radiohudong1);
            list_imageview.add(mholder.iv_radiohudong2);
            list_imageview.add(mholder.iv_radiohudong3);
            list_imageview.add(mholder.iv_radiohudong4);
            
	        if(imagepath!=null){
	        	urls = imagepath.split(",");
	        	for(int i=0;i<urls.length;i++){
	        		list_imageview.get(i).setVisibility(View.VISIBLE);
	        		imageLoader.displayImage(urls[i], list_imageview.get(i), options, null);
	        		final Intent intent = new Intent(mContext,ViewPagerActivity.class);
					intent.putExtra("ID", i);
					intent.putExtra("imgurl", urls);
	        		list_imageview.get(i).setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							mContext.startActivity(intent);
						}
					});
	        	}
	        }
    	        mholder.iv_radiohudong_laba.setVisibility(View.VISIBLE);
    	        mPlayer.setOnCompletionListener(new OnCompletionListener() {
					@Override
					public void onCompletion(MediaPlayer mp) {
					}
				});
    	        mholder.rl_radiohudong.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						if(mPlayer!=null&&mPlayer.isPlaying()){
							stopPlaying();
						}else{
							new Thread(){
								@Override
								public void run() {
									startPlaying(voicepath);
									super.run();
								}
							}.start();
						}
					}
				});
    	        
            	mholder.tv_radiohudong_createtime.setText(date);
            	try {
    				SpannableString spannableString = ExpressionUtil.getExpressionString(mContext, content, zhengze,densityDpi);
    				mholder.tv_radiohudong_content.setText(spannableString);
    			} catch (NumberFormatException e) {
    				e.printStackTrace();
    			} catch (SecurityException e) {
    				e.printStackTrace();
    			} catch (IllegalArgumentException e) {
    				e.printStackTrace();
    			}
            	mholder.rl_radiohudong.setOnLongClickListener(new OnLongClickListener() {
					
					@Override
					public boolean onLongClick(View v) {
						// TODO Auto-generated method stub
						dialog(hdcommentid,position);
						return true;
					}
				});
        break; 
        case 19:  
            
    	        mholder.iv_radiohudong_laba.setVisibility(View.VISIBLE);
    	        mPlayer.setOnCompletionListener(new OnCompletionListener() {
					@Override
					public void onCompletion(MediaPlayer mp) {
					}
				});
    	        mholder.rl_radiohudong.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						if(mPlayer!=null&&mPlayer.isPlaying()){
							stopPlaying();
						}else{
							new Thread(){
								@Override
								public void run() {
									startPlaying(voicepath);
									super.run();
								}
							}.start();
						}
					}
				});
    	        
            	mholder.tv_radiohudong_createtime.setText(date);
            	try {
    				SpannableString spannableString = ExpressionUtil.getExpressionString(mContext, content, zhengze,densityDpi);
    				mholder.tv_radiohudong_content.setText(spannableString);
    			} catch (NumberFormatException e) {
    				e.printStackTrace();
    			} catch (SecurityException e) {
    				e.printStackTrace();
    			} catch (IllegalArgumentException e) {
    				e.printStackTrace();
    			}
            	mholder.rl_radiohudong.setOnLongClickListener(new OnLongClickListener() {
					
					@Override
					public boolean onLongClick(View v) {
						// TODO Auto-generated method stub
						dialog(hdcommentid,position);
						return true;
					}
				});
        break;
            
        } 
		return convertView;
	}
	
	Handler handler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch(msg.what){
			case 0:
				showMsg(mContext.getResources().getString(R.string.del_fail),"info","bottom");
				break;
			case 1:
				int position = (Integer) msg.obj;
				arrays.remove(position);
				notifyDataSetChanged();
				mgr.deleteS_radiohudongByhdcommentid(s_hdcommentid);
				showMsg(mContext.getResources().getString(R.string.del_success),"info","bottom");
				break;
			case 2:
				String message = (String) msg.obj;
				if(message!=null){
					showMsg(message,"info","bottom");
				}
				break;
			}
		}
	};
	
		
		private void dialog(final String hdcommentid,final int position){
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
							String resultString = HttpUtil.GetStringFromUrl(delcarlifeandhuodong_url+hdcommentid);
							try {
								if(resultString!=null){
									jsonObject = new JSONObject(resultString);
									String code = jsonObject.getString("code");
									if(code.equals("200")){
										s_hdcommentid = hdcommentid;
										handler.obtainMessage(1, position).sendToTarget();
									}else if(code.equals("500")){
										String message = jsonObject.getString("message");
										handler.obtainMessage(2, message).sendToTarget();
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
		
		private class RadioHudongHolder {
			TextView tv_radiohudong_createtime;
			TextView tv_radiohudong_content;
			ImageView iv_radiohudong1;
			ImageView iv_radiohudong2;
			ImageView iv_radiohudong3;
			ImageView iv_radiohudong4;
			ImageView iv_radiohudong_laba;
			RelativeLayout rl_radiohudong;
		}
		
		private void startPlaying(String path) { 
	        try { 
	        	//简便方法，每次点播放先reset一次，否则在player不同状态调不同方法容易出异常
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



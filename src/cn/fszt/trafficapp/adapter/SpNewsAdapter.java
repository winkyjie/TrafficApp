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
import cn.fszt.trafficapp.db.DBManager;
import cn.fszt.trafficapp.domain.NewsSpaceData;
import cn.fszt.trafficapp.util.HttpUtil;
import cn.fszt.trafficapp.widget.expression.ExpressionUtil;
import cn.fszt.trafficapp.widget.msg.AppMsg;
import cn.fszt.trafficapp.widget.photoview.DisplayImageOptionsUtil;
import cn.fszt.trafficapp.widget.photoview.ViewPagerActivity;

/**
 * 我的空间-GO精选适配器
 * @author AeiouKong
 *
 */
public class SpNewsAdapter extends BaseAdapter {
	private Context mContext;
	private List<NewsSpaceData> arrays;
	private LayoutInflater inflater;
	private String delcarlifeandhuodong_url,s_hdnewcommentid;
	private ImageLoader imageLoader;
	private DisplayImageOptions options;
	private DBManager mgr;
	private int densityDpi;
	AppMsg.Style style = AppMsg.STYLE_INFO;
	AppMsg appMsg;
	
	public SpNewsAdapter(Context mContext, List<NewsSpaceData> arrays,LayoutInflater inflater,String delcarlifeandhuodong_url,int densityDpi){
		this.mContext = mContext;
		this.arrays = arrays;
		this.inflater = inflater;
		this.delcarlifeandhuodong_url = delcarlifeandhuodong_url;
		this.densityDpi = densityDpi;
		mgr = new DBManager(mContext);
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
				return 1;
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
		CarlifeandhuodongHolder mholder = null;
		if (convertView == null) {
			switch(type){
			case 1:
				convertView  = inflater.inflate(R.layout.item_carlifeandhuodong_list, null);
				mholder = new CarlifeandhuodongHolder();
				mholder.iv_carlifeandhuodong1 = (ImageView) convertView.findViewById(R.id.iv_carlifeandhuodong_img1);
				mholder.iv_carlifeandhuodong2 = (ImageView) convertView.findViewById(R.id.iv_carlifeandhuodong_img2);
				mholder.iv_carlifeandhuodong3 = (ImageView) convertView.findViewById(R.id.iv_carlifeandhuodong_img3);
				mholder.iv_carlifeandhuodong4 = (ImageView) convertView.findViewById(R.id.iv_carlifeandhuodong_img4);
				mholder.tv_carlifeandhuodong_createtime = (TextView) convertView.findViewById(R.id.tv_carlifeandhuodong_createtime);
				mholder.tv_carlifeandhuodong_content = (TextView) convertView.findViewById(R.id.tv_carlifeandhuodong_content);
				mholder.rl_carlifeandhuodong = (RelativeLayout) convertView.findViewById(R.id.rl_carlifeandhuodong);
				convertView.setTag(mholder);
			break;
			case 11:
				convertView  = inflater.inflate(R.layout.item_carlifeandhuodong_list, null);
				mholder = new CarlifeandhuodongHolder();
				mholder.iv_carlifeandhuodong1 = (ImageView) convertView.findViewById(R.id.iv_carlifeandhuodong_img1);
				mholder.iv_carlifeandhuodong2 = (ImageView) convertView.findViewById(R.id.iv_carlifeandhuodong_img2);
				mholder.iv_carlifeandhuodong3 = (ImageView) convertView.findViewById(R.id.iv_carlifeandhuodong_img3);
				mholder.iv_carlifeandhuodong4 = (ImageView) convertView.findViewById(R.id.iv_carlifeandhuodong_img4);
				mholder.tv_carlifeandhuodong_createtime = (TextView) convertView.findViewById(R.id.tv_carlifeandhuodong_createtime);
				mholder.tv_carlifeandhuodong_content = (TextView) convertView.findViewById(R.id.tv_carlifeandhuodong_content);
				mholder.rl_carlifeandhuodong = (RelativeLayout) convertView.findViewById(R.id.rl_carlifeandhuodong);
				convertView.setTag(mholder);
			break;
			case 12:
				convertView  = inflater.inflate(R.layout.item_carlifeandhuodong_list, null);
				mholder = new CarlifeandhuodongHolder();
				mholder.iv_carlifeandhuodong1 = (ImageView) convertView.findViewById(R.id.iv_carlifeandhuodong_img1);
				mholder.iv_carlifeandhuodong2 = (ImageView) convertView.findViewById(R.id.iv_carlifeandhuodong_img2);
				mholder.iv_carlifeandhuodong3 = (ImageView) convertView.findViewById(R.id.iv_carlifeandhuodong_img3);
				mholder.iv_carlifeandhuodong4 = (ImageView) convertView.findViewById(R.id.iv_carlifeandhuodong_img4);
				mholder.tv_carlifeandhuodong_createtime = (TextView) convertView.findViewById(R.id.tv_carlifeandhuodong_createtime);
				mholder.tv_carlifeandhuodong_content = (TextView) convertView.findViewById(R.id.tv_carlifeandhuodong_content);
				mholder.rl_carlifeandhuodong = (RelativeLayout) convertView.findViewById(R.id.rl_carlifeandhuodong);
				convertView.setTag(mholder);
			break;
			case 13:
				convertView  = inflater.inflate(R.layout.item_carlifeandhuodong_list, null);
				mholder = new CarlifeandhuodongHolder();
				mholder.iv_carlifeandhuodong1 = (ImageView) convertView.findViewById(R.id.iv_carlifeandhuodong_img1);
				mholder.iv_carlifeandhuodong2 = (ImageView) convertView.findViewById(R.id.iv_carlifeandhuodong_img2);
				mholder.iv_carlifeandhuodong3 = (ImageView) convertView.findViewById(R.id.iv_carlifeandhuodong_img3);
				mholder.iv_carlifeandhuodong4 = (ImageView) convertView.findViewById(R.id.iv_carlifeandhuodong_img4);
				mholder.tv_carlifeandhuodong_createtime = (TextView) convertView.findViewById(R.id.tv_carlifeandhuodong_createtime);
				mholder.tv_carlifeandhuodong_content = (TextView) convertView.findViewById(R.id.tv_carlifeandhuodong_content);
				mholder.rl_carlifeandhuodong = (RelativeLayout) convertView.findViewById(R.id.rl_carlifeandhuodong);
				convertView.setTag(mholder);
			break;
			case 14:
				convertView  = inflater.inflate(R.layout.item_carlifeandhuodong_list, null);
				mholder = new CarlifeandhuodongHolder();
				mholder.iv_carlifeandhuodong1 = (ImageView) convertView.findViewById(R.id.iv_carlifeandhuodong_img1);
				mholder.iv_carlifeandhuodong2 = (ImageView) convertView.findViewById(R.id.iv_carlifeandhuodong_img2);
				mholder.iv_carlifeandhuodong3 = (ImageView) convertView.findViewById(R.id.iv_carlifeandhuodong_img3);
				mholder.iv_carlifeandhuodong4 = (ImageView) convertView.findViewById(R.id.iv_carlifeandhuodong_img4);
				mholder.tv_carlifeandhuodong_createtime = (TextView) convertView.findViewById(R.id.tv_carlifeandhuodong_createtime);
				mholder.tv_carlifeandhuodong_content = (TextView) convertView.findViewById(R.id.tv_carlifeandhuodong_content);
				mholder.rl_carlifeandhuodong = (RelativeLayout) convertView.findViewById(R.id.rl_carlifeandhuodong);
				convertView.setTag(mholder);
			break;
			}
		}else {
			switch(type){
			case 1:
				mholder = (CarlifeandhuodongHolder) convertView.getTag();
			break;
			case 11:
				mholder = (CarlifeandhuodongHolder) convertView.getTag();
			break;
			case 12:
				mholder = (CarlifeandhuodongHolder) convertView.getTag();
			break;
			case 13:
				mholder = (CarlifeandhuodongHolder) convertView.getTag();
			break;
			case 14:
				mholder = (CarlifeandhuodongHolder) convertView.getTag();
			break;
			}
		}
		String zhengze = "em_[0-9]{2}|em_[0-9]{1}";											//正则表达式，用来判断消息内是否有表情
		String content = arrays.get(position).getContent();
		if(content!=null&&!content.equals("")){
			content = content.replace("[", "");
			content = content.replace("]", "");
		}
		switch(type){
		
        case 1:  
        	mholder.tv_carlifeandhuodong_createtime.setText(date);
        	try {
				SpannableString spannableString = ExpressionUtil.getExpressionString(mContext, content, zhengze, densityDpi);
				mholder.tv_carlifeandhuodong_content.setText(spannableString);
			} catch (NumberFormatException e) {
				e.printStackTrace();
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			}
        	mholder.rl_carlifeandhuodong.setOnLongClickListener(new OnLongClickListener() {
				
				@Override
				public boolean onLongClick(View v) {
					// TODO Auto-generated method stub
					dialog(hdnewcommentid,position);
					return true;
				}
			});
        break; 
            
        case 11:
        	
        	//评论的图片
            List<ImageView> list_carlifeandhuodong1 = new ArrayList<ImageView>();
            list_carlifeandhuodong1.add(mholder.iv_carlifeandhuodong1);
            String[] urls1 = null;
        	String imageUrl_carlifeandhuodong1 = arrays.get(position).getImagepath();  
	        if(imageUrl_carlifeandhuodong1!=null){
	        	urls1 = imageUrl_carlifeandhuodong1.split(",");
	        	for(int i=0;i<urls1.length;i++){
	        		list_carlifeandhuodong1.get(i).setVisibility(View.VISIBLE);
	        		imageLoader.displayImage(urls1[i], list_carlifeandhuodong1.get(i), options, null);
	        		final Intent intent = new Intent(mContext,ViewPagerActivity.class);
					intent.putExtra("ID", i);
					intent.putExtra("imgurl", urls1);
	        		list_carlifeandhuodong1.get(i).setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							mContext.startActivity(intent);
						}
					});
	        	}
	        }
            	mholder.tv_carlifeandhuodong_createtime.setText(date);
            	try {
    				SpannableString spannableString = ExpressionUtil.getExpressionString(mContext, content, zhengze, densityDpi);
    				mholder.tv_carlifeandhuodong_content.setText(spannableString);
    			} catch (NumberFormatException e) {
    				e.printStackTrace();
    			} catch (SecurityException e) {
    				e.printStackTrace();
    			} catch (IllegalArgumentException e) {
    				e.printStackTrace();
    			}
            	mholder.rl_carlifeandhuodong.setOnLongClickListener(new OnLongClickListener() {
    				
    				@Override
    				public boolean onLongClick(View v) {
    					// TODO Auto-generated method stub
    					dialog(hdnewcommentid,position);
    					return true;
    				}
    			});
        break; 
            
        case 12:  
        	
        	//评论的图片
            List<ImageView> list_carlifeandhuodong2 = new ArrayList<ImageView>();
            list_carlifeandhuodong2.add(mholder.iv_carlifeandhuodong1);
            list_carlifeandhuodong2.add(mholder.iv_carlifeandhuodong2);
            
            String[] urls2 = null;
        	String imageUrl_carlifeandhuodong2 = arrays.get(position).getImagepath();  
	        if(imageUrl_carlifeandhuodong2!=null){
	        	urls2 = imageUrl_carlifeandhuodong2.split(",");
	        	for(int i=0;i<urls2.length;i++){
	        		list_carlifeandhuodong2.get(i).setVisibility(View.VISIBLE);
	        		imageLoader.displayImage(urls2[i], list_carlifeandhuodong2.get(i), options, null);
	        		final Intent intent = new Intent(mContext,ViewPagerActivity.class);
					intent.putExtra("ID", i);
					intent.putExtra("imgurl", urls2);
	        		list_carlifeandhuodong2.get(i).setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							mContext.startActivity(intent);
						}
					});
	        	}
	        }
            	mholder.tv_carlifeandhuodong_createtime.setText(date);
            	try {
    				SpannableString spannableString = ExpressionUtil.getExpressionString(mContext, content, zhengze, densityDpi);
    				mholder.tv_carlifeandhuodong_content.setText(spannableString);
    			} catch (NumberFormatException e) {
    				e.printStackTrace();
    			} catch (SecurityException e) {
    				e.printStackTrace();
    			} catch (IllegalArgumentException e) {
    				e.printStackTrace();
    			}
            	mholder.rl_carlifeandhuodong.setOnLongClickListener(new OnLongClickListener() {
    				
    				@Override
    				public boolean onLongClick(View v) {
    					// TODO Auto-generated method stub
    					dialog(hdnewcommentid,position);
    					return true;
    				}
    			});
        break; 
                
        case 13:  
        	
        	//评论的图片
            List<ImageView> list_carlifeandhuodong3 = new ArrayList<ImageView>();
            list_carlifeandhuodong3.add(mholder.iv_carlifeandhuodong1);
            list_carlifeandhuodong3.add(mholder.iv_carlifeandhuodong2);
            list_carlifeandhuodong3.add(mholder.iv_carlifeandhuodong3);
            
            String[] urls3 = null;
        	String imageUrl_carlifeandhuodong3 = arrays.get(position).getImagepath();  
	        if(imageUrl_carlifeandhuodong3!=null){
	        	urls3 = imageUrl_carlifeandhuodong3.split(",");
	        	for(int i=0;i<urls3.length;i++){
	        		list_carlifeandhuodong3.get(i).setVisibility(View.VISIBLE);
	        		imageLoader.displayImage(urls3[i], list_carlifeandhuodong3.get(i), options, null);
	        		final Intent intent = new Intent(mContext,ViewPagerActivity.class);
					intent.putExtra("ID", i);
					intent.putExtra("imgurl", urls3);
	        		list_carlifeandhuodong3.get(i).setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							mContext.startActivity(intent);
						}
					});
	        	}
	        }
            	mholder.tv_carlifeandhuodong_createtime.setText(date);
            	try {
    				SpannableString spannableString = ExpressionUtil.getExpressionString(mContext, content, zhengze, densityDpi);
    				mholder.tv_carlifeandhuodong_content.setText(spannableString);
    			} catch (NumberFormatException e) {
    				e.printStackTrace();
    			} catch (SecurityException e) {
    				e.printStackTrace();
    			} catch (IllegalArgumentException e) {
    				e.printStackTrace();
    			}
            	mholder.rl_carlifeandhuodong.setOnLongClickListener(new OnLongClickListener() {
    				
    				@Override
    				public boolean onLongClick(View v) {
    					// TODO Auto-generated method stub
    					dialog(hdnewcommentid,position);
    					return true;
    				}
    			});
        break; 
                
        case 14:  
        	//评论的图片
            List<ImageView> list_carlifeandhuodong4 = new ArrayList<ImageView>();
            list_carlifeandhuodong4.add(mholder.iv_carlifeandhuodong1);
            list_carlifeandhuodong4.add(mholder.iv_carlifeandhuodong2);
            list_carlifeandhuodong4.add(mholder.iv_carlifeandhuodong3);
            list_carlifeandhuodong4.add(mholder.iv_carlifeandhuodong4);
            
            String[] urls4 = null;
        	String imageUrl_carlifeandhuodong4 = arrays.get(position).getImagepath();  
	        if(imageUrl_carlifeandhuodong4!=null){
	        	urls4 = imageUrl_carlifeandhuodong4.split(",");
	        	for(int i=0;i<urls4.length;i++){
	        		list_carlifeandhuodong4.get(i).setVisibility(View.VISIBLE);
	        		imageLoader.displayImage(urls4[i], list_carlifeandhuodong4.get(i), options, null);
	        		final Intent intent = new Intent(mContext,ViewPagerActivity.class);
					intent.putExtra("ID", i);
					intent.putExtra("imgurl", urls4);
	        		list_carlifeandhuodong4.get(i).setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							mContext.startActivity(intent);
						}
					});
	        	}
	        }
            	mholder.tv_carlifeandhuodong_createtime.setText(date);
            	try {
    				SpannableString spannableString = ExpressionUtil.getExpressionString(mContext, content, zhengze, densityDpi);
    				mholder.tv_carlifeandhuodong_content.setText(spannableString);
    			} catch (NumberFormatException e) {
    				e.printStackTrace();
    			} catch (SecurityException e) {
    				e.printStackTrace();
    			} catch (IllegalArgumentException e) {
    				e.printStackTrace();
    			}
            	mholder.rl_carlifeandhuodong.setOnLongClickListener(new OnLongClickListener() {
    				
    				@Override
    				public boolean onLongClick(View v) {
    					// TODO Auto-generated method stub
    					dialog(hdnewcommentid,position);
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
				mgr.deleteS_carlifeByhdinfocommentid(s_hdnewcommentid);
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
	
		
		private void dialog(final String hdnewcommentid,final int position){
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
							String resultString = HttpUtil.GetStringFromUrl(delcarlifeandhuodong_url+hdnewcommentid);
							try {
								if(resultString!=null){
									jsonObject = new JSONObject(resultString);
									String code = jsonObject.getString("code");
									if(code.equals("200")){
										s_hdnewcommentid = hdnewcommentid;
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
		
		private class CarlifeandhuodongHolder {
			TextView tv_carlifeandhuodong_createtime;
			TextView tv_carlifeandhuodong_content;
			ImageView iv_carlifeandhuodong1;
			ImageView iv_carlifeandhuodong2;
			ImageView iv_carlifeandhuodong3;
			ImageView iv_carlifeandhuodong4;
			RelativeLayout rl_carlifeandhuodong;
		}
}



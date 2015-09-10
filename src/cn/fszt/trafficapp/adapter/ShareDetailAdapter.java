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
import cn.fszt.trafficapp.R;
import cn.fszt.trafficapp.domain.ShareDetailReplyData;
import cn.fszt.trafficapp.util.HttpUtil;
import cn.fszt.trafficapp.widget.expression.ExpressionUtil;
import cn.fszt.trafficapp.widget.msg.AppMsg;
import cn.fszt.trafficapp.widget.photoview.DisplayImageOptionsUtil;

//我要分享评论详情 适配器
public class ShareDetailAdapter extends BaseAdapter {
	private Context mContext;
	private List<ShareDetailReplyData> arrays;
	private LayoutInflater inflater;
	private String delbgpl_url,reportypl_url,uid;
	private ImageLoader imageLoader;
	private DisplayImageOptions options,options_head;
	private int densityDpi;
	AppMsg.Style style = AppMsg.STYLE_INFO;
	AppMsg appMsg;
	private static final int REPORT = 1;
	
	public ShareDetailAdapter(Context mContext, List<ShareDetailReplyData> arrays,LayoutInflater inflater,int densityDpi,String delbgpl_url,String uid){
		this.mContext = mContext;
		this.arrays = arrays;
		this.inflater = inflater;
		this.delbgpl_url = delbgpl_url;
//		this.reportypl_url = reportypl_url;
		this.uid = uid;
		this.densityDpi = densityDpi;
		imageLoader = ImageLoader.getInstance();
		
		options = DisplayImageOptionsUtil.getOptions(
				R.drawable.ic_transparent, R.drawable.ic_transparent,R.drawable.ic_transparent);
		
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
		String connected_uid = arrays.get(position).getConnected_uid();
		
		//是自己
		if(connected_uid.equals(uid)){
			return 1;
		}
		//非自己
		else{
			return 2;
		}
	}

	@Override
	public int getViewTypeCount() {
		return 4;
	}
	

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		String date = arrays.get(position).getCreatedate();
		String imageUrl = arrays.get(position).getHeadimg();  
		String isdjpath = arrays.get(position).getIsdjpath();
		final String stcollectreplyid = arrays.get(position).getStcollectreplyid();
		int type = getItemViewType(position); 
    	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			Date ddate = sdf.parse(date);
			SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd HH:mm");
			date = dateFormat.format(ddate);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		BgplHolder bgplholder = null;
		if (convertView == null) {
			switch(type){
			//非自己的评论
			case 1:
				convertView  = inflater.inflate(R.layout.item_baoguangpl_list, null);
				bgplholder = new BgplHolder();
				bgplholder.iv_bgpl_headimg = (ImageView) convertView.findViewById(R.id.iv_bgpl_headimg);
				bgplholder.tv_bgdetail_createtime = (TextView) convertView.findViewById(R.id.tv_bgdetail_createtime);
				bgplholder.tv_bgpl_content = (TextView) convertView.findViewById(R.id.tv_bgpl_content);
				bgplholder.tv_bgpl_nickname = (TextView) convertView.findViewById(R.id.tv_bgpl_nickname);
				bgplholder.btn_bgdetail_deldetail = (Button) convertView.findViewById(R.id.btn_bgdetail_deldetail);
				bgplholder.iv_bgpl_dj = (ImageView) convertView.findViewById(R.id.iv_bgpl_dj);
				convertView.setTag(bgplholder);
				break;
			//是自己的评论
			case 2:
				convertView  = inflater.inflate(R.layout.item_baoguangpl_list, null);
				bgplholder = new BgplHolder();
				bgplholder.iv_bgpl_headimg = (ImageView) convertView.findViewById(R.id.iv_bgpl_headimg);
				bgplholder.tv_bgdetail_createtime = (TextView) convertView.findViewById(R.id.tv_bgdetail_createtime);
				bgplholder.tv_bgpl_content = (TextView) convertView.findViewById(R.id.tv_bgpl_content);
				bgplholder.tv_bgpl_nickname = (TextView) convertView.findViewById(R.id.tv_bgpl_nickname);
				bgplholder.btn_bgdetail_deldetail = (Button) convertView.findViewById(R.id.btn_bgdetail_deldetail);
				bgplholder.iv_bgpl_dj = (ImageView) convertView.findViewById(R.id.iv_bgpl_dj);
				convertView.setTag(bgplholder);
				break;
				
			}
		}else {
			bgplholder = (BgplHolder) convertView.getTag();
		}
		String zhengze = mContext.getString(R.string.expression_matches);	//正则表达式，用来判断消息内是否有表情
		String content = arrays.get(position).getContent();
		SpannableString spannableString = ExpressionUtil.getExpressionString(mContext, content, zhengze,densityDpi);
		switch(type){
		case 1:
        	imageLoader.displayImage(imageUrl, bgplholder.iv_bgpl_headimg, options_head, null);
        	imageLoader.displayImage(isdjpath, bgplholder.iv_bgpl_dj, options, null);
        	bgplholder.tv_bgpl_nickname.setText(arrays.get(position).getNickname());
        	bgplholder.tv_bgdetail_createtime.setText(date);
        	bgplholder.btn_bgdetail_deldetail.setVisibility(View.VISIBLE);
        	bgplholder.btn_bgdetail_deldetail.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					dialog(stcollectreplyid, position);
				}
			});
        	bgplholder.tv_bgpl_content.setText(spannableString);
			break;
		case 2:
        	imageLoader.displayImage(imageUrl, bgplholder.iv_bgpl_headimg, options_head, null);
        	imageLoader.displayImage(isdjpath, bgplholder.iv_bgpl_dj, options, null);
        	bgplholder.tv_bgpl_nickname.setText(arrays.get(position).getNickname());
        	bgplholder.tv_bgdetail_createtime.setText(date);
        	bgplholder.tv_bgpl_content.setText(spannableString);
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
	
		
		private void dialog(final String stcollectreplyid,final int position){
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
							String resultString = HttpUtil.GetStringFromUrl(delbgpl_url+"&stcollectreplyid="+stcollectreplyid);
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
		
		private class BgplHolder {
			TextView tv_bgpl_nickname;
			TextView tv_bgpl_content;
			TextView tv_bgdetail_createtime;
			ImageView iv_bgpl_headimg;
			Button btn_bgdetail_deldetail;
			ImageView iv_bgpl_dj;
		}
}



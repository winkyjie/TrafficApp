package cn.fszt.trafficapp.adapter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnLongClickListener;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cn.fszt.trafficapp.R;
import cn.fszt.trafficapp.db.DBManager;
import cn.fszt.trafficapp.domain.BaomingData;
import cn.fszt.trafficapp.util.HttpUtil;
import cn.fszt.trafficapp.widget.msg.AppMsg;

/**
 * 我的空间-FUN互动活动报名的适配器
 * @author AeiouKong
 *
 */
public class SpBaomingAdapter extends BaseAdapter {
	private Context mContext;
	private List<BaomingData> arrays;
	private LayoutInflater inflater;
	private String delbaoming_url,s_hdinfocommentid,uid;
	private DBManager mgr;
	AppMsg.Style style = AppMsg.STYLE_INFO;
	AppMsg appMsg;
	
	public SpBaomingAdapter(Context mContext, List<BaomingData> arrays,LayoutInflater inflater,String delbaoming_url,String uid){
		this.mContext = mContext;
		this.arrays = arrays;
		this.inflater = inflater;
		this.delbaoming_url = delbaoming_url;
		this.uid = uid;
		mgr = new DBManager(mContext);
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
	public View getView(final int position, View convertView, ViewGroup parent) {
		String content = arrays.get(position).getRemark();
		String date = arrays.get(position).getCreatedate();
		final String hdactivityresultid = arrays.get(position).getHdactivityresultid();
    	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			Date ddate = sdf.parse(date);
			SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd HH:mm");
			date = dateFormat.format(ddate);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		BaomingHolder mholder = null;
		if (convertView == null) {
				convertView  = inflater.inflate(R.layout.item_baoming_list, null);
				mholder = new BaomingHolder();
				mholder.tv_baoming_content = (TextView) convertView.findViewById(R.id.tv_baoming_content);
				mholder.tv_baoming_createtime = (TextView) convertView.findViewById(R.id.tv_baoming_createtime);
				mholder.rl_baoming = (RelativeLayout) convertView.findViewById(R.id.rl_baoming);
				convertView.setTag(mholder);
		}else {
				mholder = (BaomingHolder) convertView.getTag();
		}
        	mholder.tv_baoming_createtime.setText(date);
        	mholder.tv_baoming_content.setText(content);
        	mholder.rl_baoming.setOnLongClickListener(new OnLongClickListener() {
				
				@Override
				public boolean onLongClick(View v) {
					// TODO Auto-generated method stub
					dialog(hdactivityresultid,position);
					return true;
				}
			});
		return convertView;
	}
	
	Handler handler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch(msg.what){
			case 0:
				showMsg(mContext.getResources().getString(R.string.cancel_fail),"info","bottom");
				break;
			case 1:
				int position = (Integer) msg.obj;
				arrays.remove(position);
				notifyDataSetChanged();
				mgr.deleteS_baomingByhdactivityresultid(s_hdinfocommentid);
				showMsg(mContext.getResources().getString(R.string.cancel_success),"info","bottom");
				break;
			case 2:
				String message = (String)msg.obj;
				if(message!=null){
					showMsg(message,"info","bottom");
				}
				break;
			}
		}
	};
	
		
		private void dialog(final String hdinfocommentid,final int position){
			AlertDialog.Builder builder = new Builder(mContext);
			builder.setMessage("确认取消报名吗?");
			builder.setTitle("提示");
			builder.setPositiveButton(mContext.getResources().getString(R.string.dialog_confirm_yes), new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
					
					new Thread(){
						public void run() {
							JSONObject jsonObject;
							String resultString = HttpUtil.GetStringFromUrl(delbaoming_url+hdinfocommentid);
							try {
								if(resultString!=null){
									jsonObject = new JSONObject(resultString);
									String code = jsonObject.getString("code");
									if(code.equals("200")){
										s_hdinfocommentid = hdinfocommentid;
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
		
		private class BaomingHolder {
			TextView tv_baoming_content;
			TextView tv_baoming_createtime;
			RelativeLayout rl_baoming;
		}
}



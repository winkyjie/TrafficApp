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
import cn.fszt.trafficapp.domain.TuangouData;
import cn.fszt.trafficapp.util.HttpUtil;
import cn.fszt.trafficapp.widget.msg.AppMsg;

/**
 * 我的空间-车生活团购报名的适配器
 * @author AeiouKong
 *
 */
public class SpTuangouAdapter extends BaseAdapter {
	private Context mContext;
	private List<TuangouData> arrays;
	private LayoutInflater inflater;
	private String deltuangou_url,s_hdgroupresultid,uid;
	private DBManager mgr;
	AppMsg.Style style = AppMsg.STYLE_INFO;
	AppMsg appMsg;
	
	public SpTuangouAdapter(Context mContext, List<TuangouData> arrays,LayoutInflater inflater,String deltuangou_url,String uid){
		this.mContext = mContext;
		this.arrays = arrays;
		this.inflater = inflater;
		this.deltuangou_url = deltuangou_url;
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
		final String hdgroupresultid = arrays.get(position).getHdgroupresultid();
    	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			Date ddate = sdf.parse(date);
			SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd HH:mm");
			date = dateFormat.format(ddate);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		TuangouHolder mholder = null;
		if (convertView == null) {
				convertView  = inflater.inflate(R.layout.item_tuangou_list, null);
				mholder = new TuangouHolder();
				mholder.tv_tuangou_content = (TextView) convertView.findViewById(R.id.tv_tuangou_content);
				mholder.tv_tuangou_createtime = (TextView) convertView.findViewById(R.id.tv_tuangou_createtime);
				mholder.rl_tuangou = (RelativeLayout) convertView.findViewById(R.id.rl_tuangou);
				convertView.setTag(mholder);
		}else {
				mholder = (TuangouHolder) convertView.getTag();
		}
        	mholder.tv_tuangou_createtime.setText(date);
        	mholder.tv_tuangou_content.setText(content);
        	mholder.rl_tuangou.setOnLongClickListener(new OnLongClickListener() {
				
				@Override
				public boolean onLongClick(View v) {
					// TODO Auto-generated method stub
					dialog(hdgroupresultid,position);
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
				mgr.deleteS_tuangouByhdgroupresultid(s_hdgroupresultid);
				showMsg(mContext.getResources().getString(R.string.cancel_success),"info","bottom");
				break;
			case 2:
				String message = (String)msg.obj;
				if(message!=null){
					showMsg(message,"info","bottom");
				}
			}
		}
	};
	
		
		private void dialog(final String hdgroupresultid,final int position){
			AlertDialog.Builder builder = new Builder(mContext);
			builder.setMessage("确认取消团购吗?");
			builder.setTitle("提示");
			builder.setPositiveButton(mContext.getResources().getString(R.string.dialog_confirm_yes), new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
					
					new Thread(){
						public void run() {
							JSONObject jsonObject;
							String resultString = HttpUtil.GetStringFromUrl(deltuangou_url+hdgroupresultid);
							try {
								if(resultString!=null){
									jsonObject = new JSONObject(resultString);
									String code = jsonObject.getString("code");
									if(code.equals("200")){
										s_hdgroupresultid = hdgroupresultid;
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
		
		private class TuangouHolder {
			TextView tv_tuangou_content;
			TextView tv_tuangou_createtime;
			RelativeLayout rl_tuangou;
		}
}



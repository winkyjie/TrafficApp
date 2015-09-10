package cn.fszt.trafficapp.adapter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import cn.fszt.trafficapp.R;
import cn.fszt.trafficapp.db.DBManager;
import cn.fszt.trafficapp.domain.AwardlistData;
import cn.fszt.trafficapp.widget.msg.AppMsg;

/**
 * 我的消息-中奖通知
 * @author AeiouKong
 *
 */
public class MsgAwardListAdapter extends BaseAdapter {
	private Context mContext;
	private List<AwardlistData> arrays;
	private LayoutInflater inflater;
	private String s_hdinfocommentid;
	private DBManager mgr;
	AppMsg.Style style = AppMsg.STYLE_INFO;
	AppMsg appMsg;
	
	public MsgAwardListAdapter(Context mContext, List<AwardlistData> arrays,LayoutInflater inflater){
		this.mContext = mContext;
		this.arrays = arrays;
		this.inflater = inflater;
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
		String content = arrays.get(position).getAwardcontent();
		String date = arrays.get(position).getCreatetime();
//		final String hdactivityresultid = arrays.get(position).getHdactivityresultid();
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
				convertView.setTag(mholder);
		}else {
				mholder = (BaomingHolder) convertView.getTag();
		}
        	mholder.tv_baoming_createtime.setText(date);
        	mholder.tv_baoming_content.setText(content);
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
		}
}



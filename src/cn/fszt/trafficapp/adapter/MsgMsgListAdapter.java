package cn.fszt.trafficapp.adapter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cn.fszt.trafficapp.R;
import cn.fszt.trafficapp.db.DBManager;
import cn.fszt.trafficapp.domain.MsglistData;
import cn.fszt.trafficapp.ui.activity.MsgMsgDetailActivity;
import cn.fszt.trafficapp.widget.msg.AppMsg;

/**
 * 我的消息-最新通知
 * @author AeiouKong
 *
 */
public class MsgMsgListAdapter extends BaseAdapter {
	private Context mContext;
	private List<MsglistData> arrays;
	private LayoutInflater inflater;
	private DBManager mgr;
	AppMsg.Style style = AppMsg.STYLE_INFO;
	AppMsg appMsg;
	
	public MsgMsgListAdapter(Context mContext, List<MsglistData> arrays,LayoutInflater inflater){
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
		String content = arrays.get(position).getMsgtitle();
		final String msgtype = arrays.get(position).getMsgtype();
		final String msgid = arrays.get(position).getMsgid();
		String date = arrays.get(position).getStarttime();
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
        	mholder.rl_baoming.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					//TODO
					Intent intent = new Intent(mContext,MsgMsgDetailActivity.class);
					intent.putExtra("msgid", msgid);
					intent.putExtra("msgtype", msgtype);
					mContext.startActivity(intent);
				}
			});
		return convertView;
	}
	
	Handler handler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch(msg.what){
			
			}
		}
	};
	
		private class BaomingHolder {
			TextView tv_baoming_content;
			TextView tv_baoming_createtime;
			RelativeLayout rl_baoming;
		}
}



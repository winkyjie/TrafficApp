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
import android.widget.BaseAdapter;
import android.widget.TextView;
import cn.fszt.trafficapp.R;
import cn.fszt.trafficapp.domain.VoteData;
import cn.fszt.trafficapp.util.HttpUtil;
import cn.fszt.trafficapp.widget.msg.AppMsg;

/**
 * 我的空间-投票的适配器
 * @author AeiouKong
 *
 */
public class SpVoteAdapter extends BaseAdapter {
	private Context mContext;
	private List<VoteData> arrays;
	private LayoutInflater inflater;
	private String delvote_url,uid;
	AppMsg.Style style = AppMsg.STYLE_INFO;
	AppMsg appMsg;
	
	public SpVoteAdapter(Context mContext, List<VoteData> arrays,LayoutInflater inflater,String delvote_url,String uid){
		this.mContext = mContext;
		this.arrays = arrays;
		this.inflater = inflater;
		this.delvote_url = delvote_url;
		this.uid = uid;
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
    	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			Date ddate = sdf.parse(date);
			SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd HH:mm");
			date = dateFormat.format(ddate);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		voteHolder mholder = null;
		if (convertView == null) {
				convertView  = inflater.inflate(R.layout.item_vote_list, null);
				mholder = new voteHolder();
				mholder.tv_vote_content = (TextView) convertView.findViewById(R.id.tv_vote_content);
				mholder.tv_vote_createtime = (TextView) convertView.findViewById(R.id.tv_vote_createtime);
				convertView.setTag(mholder);
		}else {
				mholder = (voteHolder) convertView.getTag();
		}
        	mholder.tv_vote_createtime.setText(date);
        	mholder.tv_vote_content.setText(content);
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
							String resultString = HttpUtil.GetStringFromUrl(delvote_url+hdinfocommentid);
							try {
								jsonObject = new JSONObject(resultString);
								String code = jsonObject.getString("code");
								if(code.equals("200")){
									handler.obtainMessage(1, position).sendToTarget();
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
		
		private class voteHolder {
			TextView tv_vote_content;
			TextView tv_vote_createtime;
		}
}



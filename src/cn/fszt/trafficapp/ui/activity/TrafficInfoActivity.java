package cn.fszt.trafficapp.ui.activity;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONException;
import org.json.JSONObject;

import com.umeng.analytics.MobclickAgent;

import cn.fszt.trafficapp.R;
import cn.fszt.trafficapp.util.FileUtil;
import cn.fszt.trafficapp.util.HttpUtil;
import cn.fszt.trafficapp.widget.msg.AppMsg;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.Gravity;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

public class TrafficInfoActivity extends Activity {

	private TextView tv_trafficinfo;
	private String trafficinfo_url;
	private ProgressBar pb_trafficinfo;
	
	AppMsg.Style style = AppMsg.STYLE_INFO;
	AppMsg appMsg;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
        setContentView(R.layout.activity_trafficinfo);
		initView();
		new trafficinfo().start();
	}

	private void initView() {
		trafficinfo_url = getResources().getString(R.string.server_url)+getResources().getString(R.string.trafficinfo_url);
		tv_trafficinfo = (TextView) findViewById(R.id.tv_trafficinfo);
		pb_trafficinfo = (ProgressBar) findViewById(R.id.pb_trafficinfo);
	}

	
	class trafficinfo extends Thread{
		@Override
		public void run() {
			JSONObject jsonObject;
			String trafficinfo = null;
			String result = HttpUtil.GetStringFromUrl(trafficinfo_url);
			if(result!=null){
				try {
					jsonObject = new JSONObject(result);
					trafficinfo = jsonObject.getString("content");
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}else{
				handler.sendEmptyMessage(0);
			}
			handler.obtainMessage(1, trafficinfo).sendToTarget();
		}
	}
	
	private Handler handler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			if(msg.what==1){
				String trafficinfo = (String) msg.obj;
				if(trafficinfo!=null){
					pb_trafficinfo.setVisibility(View.GONE);
					trafficinfo = FileUtil.ToDBC(trafficinfo);
					tv_trafficinfo.setText(highKeywords(trafficinfo));
				}else{
					pb_trafficinfo.setVisibility(View.GONE);
				}
			}else if(msg.what==0){
				pb_trafficinfo.setVisibility(View.GONE);
				showMsg(getResources().getString(R.string.response_fail), "info", "bottom");
			}
		}
	};
	
	/**
	 * 高亮关键字
	 * @param text
	 * @return
	 */
	private SpannableString highKeywords(String text){
		String[] keywords = new String[] { "禅城区","南海区","顺德区","高明区","三水区","一环","高速"};
	    SpannableString s = new SpannableString(text);
	    for (int i = 0; i < keywords.length; i++) {
	        Pattern p = Pattern.compile(keywords[i] + "\\W");
	        Matcher m = p.matcher(s);
	         while (m.find()) {
	        	int start = m.start();
	        	int end = m.end() - 1;
//	        	s.setSpan(new BackgroundColorSpan(getResources().getColor(R.color.orange_bg)), start, end,
//	        			Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
	        	s.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.orange_bg)), start, end,
                        Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
	          }
	    }
	     return s;
	}
	
	private void showMsg(String msg,String styletype,String gravity){
		
		if(styletype.equals("alert")){
			style = AppMsg.STYLE_ALERT;
		}else if(styletype.equals("info")){
			style = AppMsg.STYLE_INFO;
		}
		
		appMsg = AppMsg.makeText(TrafficInfoActivity.this, msg, style);
		
		if(gravity.equals("bottom")){
			appMsg.setLayoutGravity(Gravity.BOTTOM);
		}else if(gravity.equals("top")){
		}
        appMsg.show();
	}
	
	public void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
		}
	public void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}
}

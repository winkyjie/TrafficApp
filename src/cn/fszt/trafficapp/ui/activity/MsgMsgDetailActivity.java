package cn.fszt.trafficapp.ui.activity;

import org.json.JSONException;
import org.json.JSONObject;

import cn.fszt.trafficapp.R;
import cn.fszt.trafficapp.util.HttpUtil;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MsgMsgDetailActivity extends Activity implements OnClickListener{
	
	private static final int SUCCEED = 0;
	private static final int FAILED = 1;
	private TextView tv_msg_title,tv_msg_date,tv_msg_content;
	private String message_msgdetail_url,msgtitle,msgcontent,starttime,msgid,msgtype;
	private Intent intent;
	private LinearLayout ll_msg_back;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_msgdetail);
		
		initView();
		
		new Thread(){
			public void run() {
				String result = HttpUtil.GetStringFromUrl(message_msgdetail_url+"&msgid="+msgid+"&msgtype="+msgtype);
				if(result!=null){
					try {
						JSONObject jsonObject = new JSONObject(result);
						String code = jsonObject.getString("code");
						if("200".equals(code)){
							JSONObject msgdetail = jsonObject.getJSONObject("msgdetail");
							msgtitle = msgdetail.getString("msgtitle");
							msgcontent = msgdetail.getString("msgcontent");
							starttime = msgdetail.getString("starttime");
							handler.sendEmptyMessage(SUCCEED);
						}else{
							handler.sendEmptyMessage(FAILED);
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}else{
					handler.sendEmptyMessage(FAILED);
				}
			}
		}.start();
	}

	private void initView() {
		intent = getIntent();
		msgid = intent.getStringExtra("msgid");
		msgtype = intent.getStringExtra("msgtype");
		message_msgdetail_url = getString(R.string.server_url)+getString(R.string.message_msgdetail_url);
		tv_msg_title = (TextView) findViewById(R.id.tv_msg_title);
		tv_msg_date = (TextView) findViewById(R.id.tv_msg_date);
		tv_msg_content = (TextView) findViewById(R.id.tv_msg_content);
		ll_msg_back = (LinearLayout) findViewById(R.id.ll_msg_back);
		ll_msg_back.setOnClickListener(this);
	}
	
	Handler handler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch(msg.what){
			case SUCCEED:
				tv_msg_title.setText(msgtitle);
				tv_msg_date.setText(starttime);
				tv_msg_content.setText(msgcontent);
				break;
			case FAILED:
				
				break;
			}
		}
	};

	@Override
	public void onClick(View v) {
		if(v == ll_msg_back){
			finish();
		}
	}
}

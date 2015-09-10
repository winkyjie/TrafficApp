package cn.fszt.trafficapp.ui.activity;

import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import cn.fszt.trafficapp.R;
import cn.fszt.trafficapp.util.HttpUtil;
import cn.fszt.trafficapp.widget.msg.AppMsg;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.SparseBooleanArray;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class VoteMultipleChoiceActivity extends Activity implements OnClickListener{

	private static final int LOGININ = 99;
	private static final int REQ_LOGIN = 10;
	private List<String> mChoices,mChoicesid;
	private String hdvote_title,hdvote_maxcount,hdvoteid,votetype,hdvoteoptionid,hdsurveyvoteid;
	private ListView listView;
	private Button btn_vote_vote,btn_vote_voteresult;
	private SharedPreferences sp;
    private int uid;
    private String setvote_url,setsurvey_url;
    AppMsg.Style style = AppMsg.STYLE_INFO;
	AppMsg appMsg;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.fragment_page);
		
		initData();
		initView();
		
		getActionBar().setTitle(getResources().getString(R.string.vote));
		getActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.titlebar)));
		getActionBar().setDisplayHomeAsUpEnabled(true);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch(item.getItemId()){
			case android.R.id.home:
				finish();
				break;
		}
		return true;
	}
	
	private void initData(){
		setvote_url = getString(R.string.server_url)+getString(R.string.setvote_url);//单个投票
    	setsurvey_url = getString(R.string.server_url)+getString(R.string.setsurvey_url);//复合调查
		Intent intent = getIntent();
		mChoices = intent.getStringArrayListExtra("hdvoteoptions");
		mChoicesid = intent.getStringArrayListExtra("hdvoteoptionids");
	    hdvote_title = intent.getStringExtra("hdvote_title");
	    hdvoteid = intent.getStringExtra("hdvoteid");
	    hdsurveyvoteid = intent.getStringExtra("hdsurveyvoteid");
	    hdvote_maxcount = intent.getStringExtra("hdvote_maxcount");
	    votetype = intent.getStringExtra("votetype");
	    sp = getSharedPreferences("USER", Context.MODE_PRIVATE);
    	uid = sp.getInt("uid", 0);
	}
	
	private void initView() {
		((TextView) findViewById(android.R.id.title)).setText(hdvote_title);
        listView = (ListView) findViewById(android.R.id.list);
        listView.setAdapter(new ArrayAdapter<String>(this,
                R.layout.multiple_choice,
                android.R.id.text1,
                mChoices));
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        listView.setOnItemClickListener(itemclick);
        
        btn_vote_vote = (Button) findViewById(R.id.btn_vote_vote);
        btn_vote_vote.setOnClickListener(this);
        btn_vote_voteresult = (Button) findViewById(R.id.btn_vote_voteresult);
        btn_vote_voteresult.setOnClickListener(this);
	}
	
	OnItemClickListener itemclick = new OnItemClickListener(){

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			int maxcount = 2;
	    	if(hdvote_maxcount!=null && !"".equals(hdvote_maxcount)){
	    		maxcount = Integer.parseInt(hdvote_maxcount);
	    	}
	    	int count = listView.getCheckedItemCount();
	    	if(count>maxcount){
	        	Toast.makeText(VoteMultipleChoiceActivity.this, "最多只能选择"+maxcount+"项", Toast.LENGTH_SHORT).show();
	        	listView.setItemChecked(position, false);
	        }
	        SparseBooleanArray checkedPositions = listView.getCheckedItemPositions();
	        
	        hdvoteoptionid = "";
	        for (int i = 0; i < checkedPositions.size(); i++) {
	            if (checkedPositions.valueAt(i)) {
	            	hdvoteoptionid += mChoicesid.get(checkedPositions.keyAt(i))+",";
	            }
	        }
	        if(hdvoteoptionid.length()>0){
	        	hdvoteoptionid = hdvoteoptionid.substring(0, hdvoteoptionid.length()-1);
	        }
		}
	};
	
	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	if(requestCode == LOGININ && resultCode == REQ_LOGIN){
			 uid = data.getIntExtra("uid",0);
		 }
    	super.onActivityResult(requestCode, resultCode, data);
    }

	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.btn_vote_voteresult:
			Intent intent = new Intent(this,VoteResultActivity.class);
			intent.putExtra("hdvoteid", hdvoteid); //投票id
			intent.putExtra("hdsurveyvoteid", hdsurveyvoteid); //调查id
			intent.putExtra("hdvote_title", hdvote_title);
			startActivity(intent);
			break;
			
		case R.id.btn_vote_vote:
			//提交投票
        	if(uid==0){
				Intent intent_vote = new Intent(VoteMultipleChoiceActivity.this,StLoginActivity.class);
				startActivityForResult(intent_vote,LOGININ);
			}else{
				if(votetype.equals("vote")){
					summbitvote(true);
				}else if(votetype.equals("survey")){
					summbitvote(false);
				}
			}
			break;
		}
	}
	
	//提交投票
	private void summbitvote(final boolean isvote){
    	
    	new Thread(){
    		public void run() {
    			JSONObject jsonObject;
    			if(hdvoteoptionid!=null&&!hdvoteoptionid.equals("")){
    				String url;
    				if(isvote){
    					url = setvote_url+
    	    					"&connected_uid="+uid+"&hdvoteid="+hdvoteid+"&hdvoteoptionid="+hdvoteoptionid;
    				}else{
    					url = setsurvey_url+
            					"&connected_uid="+uid+"&hdsurveyvoteid="+hdsurveyvoteid+"&hdsurveyoptionid="+hdvoteoptionid;
    				}
	    			String vote_result = HttpUtil.GetStringFromUrl(url);
	    			if(vote_result!=null&&!vote_result.equals("")){
	    				try {
							jsonObject = new JSONObject(vote_result);
							String code = jsonObject.getString("code");
							String message = jsonObject.getString("message");
							if(code!=null && code.equals("200")){
								//发一个handler
								handler.obtainMessage(200, message).sendToTarget();
							}else if(code!=null && code.equals("501")){
								//表示不允许重复投票，投票失败
								handler.obtainMessage(501, message).sendToTarget();
							}else if(code!=null && code.equals("502")){
								//请在系统规定的时间内进行投票，投票失败
								handler.obtainMessage(502, message).sendToTarget();
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
	    			}
    			}else{
    				handler.sendEmptyMessage(500);
    			}
    		}
    	}.start();
    }
    
    Handler handler = new Handler(){
    	public void handleMessage(Message msg) {
    		switch(msg.what){
    			case 200:
    				showMsg((String)msg.obj, "info", "bottom");
    				break;
    			
    			case 501:
    				showMsg((String)msg.obj, "info", "bottom");
    				break;
    			
    			case 502:
    				showMsg((String)msg.obj, "info", "bottom");
    				break;
    				
    			case 500:
    				showMsg(getString(R.string.vote_nodata), "info", "bottom");
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
		
		appMsg = AppMsg.makeText(VoteMultipleChoiceActivity.this, msg, style);
		
		if(gravity.equals("bottom")){
			appMsg.setLayoutGravity(Gravity.BOTTOM);
		}else if(gravity.equals("top")){
		}
        appMsg.show();
	}
    
}

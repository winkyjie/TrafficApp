package cn.fszt.trafficapp.ui.activity;

import cn.fszt.trafficapp.R;
import cn.fszt.trafficapp.util.AppClickCount;
import cn.fszt.trafficapp.util.Constant;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

public class MainAddActivity extends BaseBackActivity implements OnClickListener{
	
	private View ll_report,ll_share;
	private TextView tv_cancel;
	private SharedPreferences sp;
	private String uid,mobile;
	private View outside;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_mainadd);
		
		initView();
	}

	private void initView() {
		sp = getSharedPreferences("USER", Context.MODE_PRIVATE);
		uid = sp.getString("uuid", null);
		mobile = sp.getString("mobile", null);
		ll_report = findViewById(R.id.ll_report);
		ll_share = findViewById(R.id.ll_share);
		tv_cancel = (TextView) findViewById(R.id.tv_cancel);
		outside = findViewById(R.id.outside);
		outside.setOnClickListener(this);
		ll_report.setOnClickListener(this);
		ll_share.setOnClickListener(this);
		tv_cancel.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.ll_report:
			if(uid!=null&&mobile!=null&&!"".equals(mobile)){
				Intent intent = new Intent(MainAddActivity.this,ReportEditActivity.class);
				intent.putExtra("ClickCount", "加号报路况");
				startActivity(intent);
			}else if(uid==null){
				Intent intent = new Intent(MainAddActivity.this, StLoginActivity.class);
				startActivity(intent);
			}else if(uid!=null&&"".equals(mobile)){
				Toast.makeText(this, "请先绑定手机号", Toast.LENGTH_LONG).show();
			}
			finish();

			break;
		case R.id.ll_share:
			if(uid!=null&&mobile!=null&&!"".equals(mobile)){
				Intent intent = new Intent(MainAddActivity.this,ShareEditActivity.class);
				intent.putExtra("ClickCount", "加号发车友分享");
				startActivityForResult(intent,Constant.PINGLUN_REQUESTCODE);
			}else if(uid==null){
				Intent intent = new Intent(MainAddActivity.this, StLoginActivity.class);
				startActivity(intent);
				finish();
			}else if(uid!=null&&"".equals(mobile)){
				Toast.makeText(this, "请先绑定手机号", Toast.LENGTH_LONG).show();
			}
			break;
		case R.id.tv_cancel:
			finish();
			break;
		case R.id.outside:
			finish();
			break;
		}
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		
		if (requestCode == Constant.PINGLUN_REQUESTCODE && resultCode == RESULT_OK) {
			setResult(RESULT_OK);
			finish();
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
}

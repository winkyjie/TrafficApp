package cn.fszt.trafficapp.ui.activity;

import cn.fszt.trafficapp.R;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class StRegisterProtocolActivity extends BaseBackActivity{
	
	private TextView tv_registerprotocol;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_registerprotocol);
		
		initView();
	}

	private void initView() {
		tv_registerprotocol = (TextView) findViewById(R.id.tv_registerprotocol);
		tv_registerprotocol.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}
}

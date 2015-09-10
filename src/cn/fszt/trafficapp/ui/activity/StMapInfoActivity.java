package cn.fszt.trafficapp.ui.activity;

import cn.fszt.trafficapp.R;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class StMapInfoActivity extends Activity implements OnClickListener{

	private EditText et_uptimer,et_linecolor;
	private Button btn_map_submit;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mapinfo);
		initView();
		
		
	}
	
	private void initView(){
		et_uptimer = (EditText) findViewById(R.id.et_uptimer);
		et_linecolor = (EditText) findViewById(R.id.et_linecolor);
		btn_map_submit = (Button) findViewById(R.id.btn_map_submit);
		btn_map_submit.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		if(v == btn_map_submit){
			SharedPreferences sp = getSharedPreferences("traffic", Context.MODE_PRIVATE);
			Editor ed = sp.edit(); 
			String str_timer = et_uptimer.getText().toString();
			String str_linecolor = et_linecolor.getText().toString();
			if(!"".equals(str_timer) && str_timer!=null){
				int timer = Integer.parseInt(str_timer);
				int linecolor = Integer.parseInt(str_linecolor);
				ed.putInt("updatetimer", timer);
				ed.putInt("linecolor", linecolor);
				ed.commit();
			}
		}
	}
	
}

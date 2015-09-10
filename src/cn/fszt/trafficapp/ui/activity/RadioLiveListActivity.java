package cn.fszt.trafficapp.ui.activity;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import cn.fszt.trafficapp.R;
import cn.fszt.trafficapp.adapter.RadioLiveListAdapter;
import cn.fszt.trafficapp.domain.RadioListData;
import cn.fszt.trafficapp.util.HttpUtil;
import cn.fszt.trafficapp.widget.PullDownView;
import cn.fszt.trafficapp.widget.ScrollOverListView;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ProgressBar;

/**
 * 听广播--节目时表
 * @author AeiouKong
 *
 */
public class RadioLiveListActivity extends Activity implements OnItemClickListener{

	private LayoutInflater inflater;
	private List<RadioListData> arrays;
	private PullDownView pullDownView;
	private ScrollOverListView listView;
	private ProgressBar pb_radiolist;
	private RadioLiveListAdapter adapter;
	private String radiolist_url,hdmenuid;//节目id
	private Intent intent;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_radiolist);
		
		initView();
		
		initArrays(new Handler(){
           	@Override
           	public void handleMessage(Message msg) {
           		
           		arrays = (List<RadioListData>) msg.obj;
           		if(arrays==null){  //网络更新失败时，还是显示数据库的历史数据
           			pb_radiolist.setVisibility(View.GONE);
           		}else{             //网络更新数据成功，更新数据库的数据
           			pb_radiolist.setVisibility(View.GONE);
           		}
           		adapter = new RadioLiveListAdapter(RadioLiveListActivity.this, arrays, inflater, hdmenuid);
           		listView.setAdapter(adapter);
           		int position = 0;
           		for(int i=0;i<arrays.size();i++){
           			if(arrays.get(i).getHdmenuid().equals(hdmenuid)){
           				position = i;
           			}
           		}
           		listView.setSelection(position);
           		adapter.notifyDataSetChanged();
           		pullDownView.notifyDidDataLoad(false);
           	}
           });
	}

	private void initView() {
		radiolist_url = getString(R.string.server_url) + getString(R.string.radiolist_url);
		inflater = getLayoutInflater();
		pullDownView = (PullDownView) findViewById(R.id.pdv_radiolist);
        pullDownView.enableLoadMore(false);
        pullDownView.enablePullDown(false);
        listView = pullDownView.getListView();
        listView.setOnItemClickListener(this);
        pb_radiolist = (ProgressBar) findViewById(R.id.pb_radiolist);
        intent = getIntent();
        hdmenuid = intent.getStringExtra("hdmenuid");
	}
	
	private List<RadioListData> getDataFromNetwork() throws Exception{
		List<RadioListData> lst = new ArrayList<RadioListData>();
		
		String resultString = HttpUtil.GetStringFromUrl(radiolist_url);
		if(resultString != null){
			Type listType = new TypeToken<List<RadioListData>>(){}.getType();
	        Gson gson = new Gson();
	        lst = gson.fromJson(resultString, listType);
	        return lst;
		}else{
			return null;
		}
	}
	
	/**
	 * 初始化数据
	 * @param handler
	 */
	private void initArrays(final Handler handler) {
		new Thread(new Runnable() {
			List<RadioListData> lst = new ArrayList<RadioListData>();
			@Override
			public void run() {
				
					try {  
						lst = getDataFromNetwork();
				} catch (Exception e) {
					e.printStackTrace();
				}
					handler.obtainMessage(0, lst).sendToTarget();
			}
		}).start();
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		finish();
	}

}

package cn.fszt.trafficapp.ui.activity;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.client.ClientProtocolException;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.umeng.analytics.MobclickAgent;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ProgressBar;
import android.widget.AdapterView.OnItemClickListener;
import cn.fszt.trafficapp.R;
import cn.fszt.trafficapp.adapter.TextAdapter;
import cn.fszt.trafficapp.db.DBManager;
import cn.fszt.trafficapp.domain.TextData;
import cn.fszt.trafficapp.util.HttpUtil;
import cn.fszt.trafficapp.widget.MyDialog;
import cn.fszt.trafficapp.widget.PullDownView;
import cn.fszt.trafficapp.widget.ScrollOverListView;
import cn.fszt.trafficapp.widget.PullDownView.OnPullDownListener;

public class TextActivity extends Activity implements OnClickListener,OnItemClickListener{

    private PullDownView pullDownView;
	private ScrollOverListView listView;
	private TextAdapter adapter;
	private List<TextData> arrays;
	private LayoutInflater inflater;
	private MyDialog roadDialog;
	private int startindex = 1;
	private int endindex = 10;
	private String requestURL;
	int densityDpi;  //屏幕dpi
	
	private DBManager mgr;
	private List<TextData> sql_arrays;

	private ProgressBar pb_text;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DisplayMetrics metric = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metric);
        densityDpi = metric.densityDpi;
        
        setContentView(R.layout.activity_text);
        
        initView();
        sql_arrays = mgr.queryText();
        if(sql_arrays.size()==0){  //当数据库没有数据时，从网络更新并插入数据库
        	initArrays(new Handler(){
            	@Override
            	public void handleMessage(Message msg) {
            		arrays = (List<TextData>) msg.obj;
            		adapter = new TextAdapter(TextActivity.this,arrays,inflater,densityDpi);
            		listView.setAdapter(adapter);
            		adapter.notifyDataSetChanged();
            		pullDownView.notifyDidDataLoad(false);
            		pb_text.setVisibility(View.GONE);
            		if(arrays!=null){
            			mgr.addText(arrays);
            		}else{
            		}
            	}
            });
        }else{  //查找数据库显示历史数据，然后再从网络更新并插入数据库
        	arrays = sql_arrays;
        	adapter = new TextAdapter(TextActivity.this,arrays,inflater,densityDpi);
    		listView.setAdapter(adapter);
    		adapter.notifyDataSetChanged();
    		pullDownView.notifyDidDataLoad(false);
    		pb_text.setVisibility(View.GONE);
    		initArrays(new Handler(){
            	@Override
            	public void handleMessage(Message msg) {
            		arrays = (List<TextData>) msg.obj;
            		if(arrays==null){  //网络更新失败时，还是显示数据库的历史数据
            			arrays = sql_arrays;
            		}else{             //网络更新数据成功，更新数据库的数据
            			mgr.deleteText();
						mgr.addText(arrays);
            		}
            		adapter = new TextAdapter(TextActivity.this,arrays,inflater,densityDpi);
            		listView.setAdapter(adapter);
            		adapter.notifyDataSetChanged();
            		pullDownView.notifyDidDataLoad(false);
            		pb_text.setVisibility(View.GONE);
            	}
            });
        }
        
        pullDownView.setOnPullDownListener(new OnPullDownListener() {
			//下拉刷新
			@Override
			public void onRefresh() {
				getRefresh(new Handler(){
		        	@Override
		        	public void handleMessage(Message msg) {
		        		arrays = (List<TextData>) msg.obj;
		        		if(arrays==null){
		        			arrays = sql_arrays;    //下拉更新失败，还是显示数据库历史数据
		        		}else{                      //下拉更新数据成功，更新数据库的数据
	            			mgr.deleteText();
							mgr.addText(arrays);
	            		}
		        		adapter = new TextAdapter(TextActivity.this,arrays,inflater,densityDpi);
		        		listView.setAdapter(adapter);
		        		adapter.notifyDataSetChanged();
		        		pullDownView.notifyDidRefresh(false);
		        	}
		        });
			}
			//上拉更多
			@Override
			public void onLoadMore() {
				getLoadMore(new Handler(){
					@Override
					public void handleMessage(Message msg) {
						if((List<TextData>)msg.obj!=null){
							for(TextData s :(List<TextData>)msg.obj){
								arrays.add(s);
							}
						}
						adapter.notifyDataSetChanged();
						pullDownView.notifyDidLoadMore((List<TextData>)msg.obj==null);
					}
				});
			}
		});
        
        getActionBar().setTitle(getResources().getString(R.string.item_text));
		getActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.titlebar)));
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setDisplayShowHomeEnabled(false);
    }
	
	private void initView(){
		mgr = new DBManager(this);
		requestURL = getResources().getString(R.string.server_url)+getResources().getString(R.string.text_url);
        inflater = getLayoutInflater();
        pullDownView = (PullDownView) findViewById(R.id.pulldownview);
        pullDownView.enableAutoFetchMore(true, 2);
        pullDownView.enableLoadMore(false);
        pullDownView.enablePullDown(true);
        listView = pullDownView.getListView();
        listView.setOnItemClickListener(this);
        listView.setCacheColorHint(Color.TRANSPARENT);
        listView.setDivider(getResources().getDrawable(R.color.list_view_divider));
        listView.setDividerHeight(1);
        pb_text = (ProgressBar) findViewById(R.id.pb_text);
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
	
	/**
	 * 初始化数据
	 * @param handler
	 */
	private void initArrays(final Handler handler) {
		new Thread(new Runnable() {
			List<TextData> lst = new ArrayList<TextData>();
			@Override
			public void run() {
				
					try {  
						lst = getDataFromNetwork(startindex,endindex);
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				handler.obtainMessage(0, lst).sendToTarget();
			}
		}).start();
	}
	
	/**
	 * 下拉刷新
	 * @param handler
	 */
	private void getRefresh(final Handler handler) {
		new Thread(new Runnable() {
			List<TextData> lst = new ArrayList<TextData>();
			@Override
			public void run() {
				
					try {  
						startindex = 1;
						endindex = 10;
						lst = getDataFromNetwork(startindex,endindex);
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				handler.obtainMessage(0, lst).sendToTarget();
			}
		}).start();
	}
	
	/**
	 * 加载更多
	 * @param handler
	 */
	private void getLoadMore(final Handler handler) {
		new Thread(new Runnable() {
			List<TextData> lst = new ArrayList<TextData>();
			@Override
			public void run() {
				
					startindex += 10;
					endindex += 10;
					try {
						lst = getDataFromNetwork(startindex,endindex);
					} catch (Exception e) {
						e.printStackTrace();
					}
					
				handler.obtainMessage(0, lst).sendToTarget();
			}
		}).start();
	}

	
	/**
	 * 从网络获取数据
	 * @return
	 * @throws IOException 
	 * @throws ClientProtocolException 
	 * @throws Exception
	 */
	private List<TextData> getDataFromNetwork(int start,int end) throws Exception{
		List<TextData> lst = new ArrayList<TextData>();
		
		String resultString = HttpUtil.GetStringFromUrl(requestURL+"&startindex="+start+"&endindex="+end);
            
            Type listType = new TypeToken<List<TextData>>(){}.getType();
            Gson gson = new Gson();
            lst = gson.fromJson(resultString, listType);
            
        return lst;
	}
	
	@Override
	public void onClick(View v) {
		if(v == MyDialog.getBtn(roadDialog, getResources().getString(R.string.back))){
			MyDialog.dismiss(roadDialog);
		}
	}

	/**
	 * 点击每条记录显示详细内容
	 */
	@Override
	public void onItemClick(AdapterView<?> arg0, View view, int position, long id) {
		String remark = arrays.get(position).getRemark();
		String createtime = arrays.get(position).getCreatetime();
		if("null".equals(remark)){
			remark = getResources().getString(R.string.notrafficdetail);
		}
		roadDialog = MyDialog.showDialog(this, arrays.get(position).getAddress(), createtime+"\r\n"+"\r\n"+remark,"返回");
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

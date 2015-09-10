package cn.fszt.trafficapp.ui.activity;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.client.ClientProtocolException;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.umeng.analytics.MobclickAgent;

import cn.fszt.trafficapp.R;
import cn.fszt.trafficapp.adapter.NewsListAdapter;
import cn.fszt.trafficapp.db.DBManager;
import cn.fszt.trafficapp.domain.NewsData;
import cn.fszt.trafficapp.util.HttpUtil;
import cn.fszt.trafficapp.widget.PullDownView;
import cn.fszt.trafficapp.widget.ScrollOverListView;
import cn.fszt.trafficapp.widget.PullDownView.OnPullDownListener;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

public class NewsListActivity extends Activity implements OnItemClickListener{
	
	private String requestURL,push;
	
	private int startindex = 1;
	private int endindex = 2;
	
	private LayoutInflater inflater;
	private List<NewsData> arrays;
	private PullDownView pullDownView;
	private ScrollOverListView listView;
	private NewsListAdapter adapter;
	
	private DBManager mgr;
	private List<NewsData> sql_arrays;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
        setContentView(R.layout.activity_news);	
        
        Intent intent = getIntent();
        push = intent.getStringExtra("push");
        
		initView();
		
		sql_arrays = mgr.queryNews();
	    if(sql_arrays.size()==0){  //当数据库没有数据时，从网络更新并插入数据库
	       	initArrays(new Handler(){
	           	@Override
	           	public void handleMessage(Message msg) {
	           		arrays = (List<NewsData>) msg.obj;
	           		adapter = new NewsListAdapter(arrays, inflater);
	           		listView.setAdapter(adapter);
	           		adapter.notifyDataSetChanged();
	           		pullDownView.notifyDidDataLoad(false);
	           		if(arrays!=null){
	           			mgr.addNews(arrays);
	           		}else{
	           		}
	           	}
	         });
	       }else{  //查找数据库显示历史数据，然后再从网络更新并插入数据库
		       	arrays = sql_arrays;
		       	adapter = new NewsListAdapter(arrays, inflater);
           		listView.setAdapter(adapter);
           		adapter.notifyDataSetChanged();
		   		pullDownView.notifyDidDataLoad(false);
	   		initArrays(new Handler(){
	           	@Override
	           	public void handleMessage(Message msg) {
	           		
	           		arrays = (List<NewsData>) msg.obj;
	           		if(arrays==null){  //网络更新失败时，还是显示数据库的历史数据
	           			arrays = sql_arrays;
	           		}else{             //网络更新数据成功，更新数据库的数据
	           			mgr.deleteNews();
						mgr.addNews(arrays);
	           		}
	           		adapter = new NewsListAdapter(arrays, inflater);
	           		listView.setAdapter(adapter);
	           		adapter.notifyDataSetChanged();
	           	}
	           });
	       }
		
		pullDownView.setOnPullDownListener(new OnPullDownListener() {
			//下拉刷新
			@Override
			public void onRefresh() {
			}
			//上拉更多
			@Override
			public void onLoadMore() {
				getRefresh(new Handler(){
		        	@Override
		        	public void handleMessage(Message msg) {
		        		if((List<NewsData>)msg.obj!=null){
							for(NewsData s :(List<NewsData>)msg.obj){
								arrays.add(s);
							}
						}
		           		adapter.notifyDataSetChanged();
		        		pullDownView.notifyDidLoadMore((List<NewsData>)msg.obj==null);
		        	}
		        });
			}
		});
		
		getActionBar().setTitle(getResources().getString(R.string.n_zixun));
		getActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.titlebar)));
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setDisplayShowHomeEnabled(false);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		if(item.getItemId() == android.R.id.home){
			
			Intent intent = new Intent(NewsListActivity.this,ActListActivity.class);
			if(push!=null&&"true".equals(push)){
				intent.putExtra("contenttype", "huodong");
				intent.putExtra("push", "true");
				startActivity(intent);
				finish();
			}else{
				finish();
			}
            return true;
        }
		return super.onOptionsItemSelected(item);
	}

	private void initView(){
		mgr = new DBManager(this);
		requestURL = getResources().getString(R.string.server_url)+getResources().getString(R.string.news_url);
		inflater = getLayoutInflater();
		pullDownView = (PullDownView) findViewById(R.id.pd_news);
        pullDownView.enableAutoFetchMore(true, 1);
        pullDownView.enableLoadMore(false);
        pullDownView.enablePullDown(false);
        listView = pullDownView.getListView();
        listView.setOnItemClickListener(this);
        listView.setCacheColorHint(Color.TRANSPARENT);
        listView.setSelector(new ColorDrawable(Color.TRANSPARENT));
        listView.setDivider(null);
        listView.setBackgroundColor(Color.WHITE);
	}


	/**
	 * 从网络获取数据
	 * @return
	 * @throws IOException 
	 * @throws ClientProtocolException 
	 * @throws Exception
	 */
	private List<NewsData> getDataFromNetwork(int start,int end) throws Exception{
		List<NewsData> lst = new ArrayList<NewsData>();
		
		String resultString = HttpUtil.GetStringFromUrl(requestURL+"&startindex="+start+"&endindex="+end);
		if(resultString != null){
			Type listType = new TypeToken<List<NewsData>>(){}.getType();
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
			List<NewsData> lst = new ArrayList<NewsData>();
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
	
	
	private void getRefresh(final Handler handler) {
		new Thread(new Runnable() {
			List<NewsData> lst = new ArrayList<NewsData>();
			@Override
			public void run() {
				
					try {  
						//有数据返回才加一
						startindex += 2;
						endindex += 2;
						lst = getDataFromNetwork(startindex,endindex);
				} catch (Exception e) {
					e.printStackTrace();
				}
				handler.obtainMessage(0, lst).sendToTarget();
			}
		}).start();
	}
	
	@Override
	public void onItemClick(AdapterView<?> arg0, View view, int position, long id) {
		
		Intent intent_newsitem = new Intent(this,NewsDetailActivity.class);
		intent_newsitem.putExtra("id", arrays.get(position).getHdnewid());
		startActivity(intent_newsitem);
	}
	
	public void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}
	public void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}
	
	@Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN){   
        	Intent intent = new Intent(NewsListActivity.this,ActListActivity.class);
			if(push!=null&&"true".equals(push)){
				intent.putExtra("contenttype", "huodong");
				intent.putExtra("push", "true");
				startActivity(intent);
				finish();
			}else{
				finish();
			}
            return true;   
        }
        return super.onKeyDown(keyCode, event);
    }
}

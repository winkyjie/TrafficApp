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
import cn.fszt.trafficapp.adapter.ActDetailAdapter;
import cn.fszt.trafficapp.domain.PinglunData;
import cn.fszt.trafficapp.util.HttpUtil;
import cn.fszt.trafficapp.widget.PullDownView;
import cn.fszt.trafficapp.widget.ScrollOverListView;
import cn.fszt.trafficapp.widget.PullDownView.OnPullDownListener;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

/**
 * 精选活动评论列表
 * @author AeiouKong
 *
 */
public class ActCommentListActivity extends Activity {
	private LayoutInflater inflater;
	private List<PinglunData> arrays;
	private PullDownView pullDownView;
	private ScrollOverListView listView;
	private ActDetailAdapter adapter;
	private String getpl_url,delmpl_url,reportypl_url,uid;
	private int densityDpi;  
	private String id; //文章id
	private int startindex = 1;
	private int endindex = 15;
	private static final int PINGLUN = 99;
	
	private ProgressBar pb_hditem;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		DisplayMetrics metric = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metric);
        densityDpi = metric.densityDpi;
		setContentView(R.layout.activity_huodongitempl);
        
        Intent intent = getIntent();
		id = intent.getStringExtra("id");
        initView();
        
        initArrays(new Handler(){
           	@Override
           	public void handleMessage(Message msg) {
           		arrays = (List<PinglunData>) msg.obj;
           		adapter = new ActDetailAdapter(ActCommentListActivity.this,arrays,inflater,delmpl_url,reportypl_url,uid,densityDpi,id);
                listView.setAdapter(adapter);
           		adapter.notifyDataSetChanged();
           		pullDownView.notifyDidDataLoad(false);
           		pb_hditem.setVisibility(View.GONE);
           	}
           });
        
        pullDownView.setOnPullDownListener(new OnPullDownListener() {
			//下拉刷新
			@Override
			public void onRefresh() {
				getRefresh(new Handler(){
		        	@Override
		        	public void handleMessage(Message msg) {
		        		arrays = (List<PinglunData>) msg.obj;
		        		adapter = new ActDetailAdapter(ActCommentListActivity.this,arrays,inflater,delmpl_url,reportypl_url,uid,densityDpi,id);
		                listView.setAdapter(adapter);
		        		adapter.notifyDataSetChanged();
		        		pullDownView.notifyDidRefresh(false);
		        	}
		        });
			}

			@Override
			public void onLoadMore() {
				getLoadMore(new Handler(){
					@Override
					public void handleMessage(Message msg) {
						//arrays.add((String) msg.obj);
						if((List<PinglunData>)msg.obj!=null){
							for(PinglunData s :(List<PinglunData>)msg.obj){
								arrays.add(s);
							}
						}
//						adapter = new HuodongAdapter(HuodongItemPLActivity.this,arrays,inflater,delmpl_url);
//				        listView.setAdapter(adapter);
						adapter.notifyDataSetChanged();
						pullDownView.notifyDidLoadMore((List<PinglunData>)msg.obj==null);
					}
				});
			}
			
		});
        getActionBar().setTitle(getResources().getString(R.string.allpingluns));
		getActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.titlebar)));
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setDisplayShowHomeEnabled(false);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		
		MenuInflater inflater = getMenuInflater();

	    inflater.inflate(R.menu.huodongitemplactivity, menu);

		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		switch(item.getItemId()){
		case android.R.id.home:
			finish();
			break;
			
		case R.id.item_hditempl_sentpinglun:
			Intent intent_sent = new Intent(this,ActCommentEditActivity.class);
			intent_sent.putExtra("id", id);
			intent_sent.putExtra("pingluntype", "huodong");
			startActivityForResult(intent_sent,PINGLUN);
			break;
		}
		return true;
	}

	private void initView() {
		SharedPreferences sp = getSharedPreferences("USER", Context.MODE_PRIVATE);
    	uid = sp.getString("uuid", null);
		getpl_url = getResources().getString(R.string.server_url)+getResources().getString(R.string.hd_getpl_url)+"&hdinfoid=";
		delmpl_url = getResources().getString(R.string.server_url)+getResources().getString(R.string.hd_delmpl_url)+"&hdinfocommentid=";
		reportypl_url = getResources().getString(R.string.server_url)+getResources().getString(R.string.reporthuodong_url)+"&hdinfocommentid=";
		inflater = getLayoutInflater();
		pullDownView = (PullDownView) findViewById(R.id.pd_hditem_pl);
        pullDownView.enableAutoFetchMore(true, 2);
        pullDownView.enableLoadMore(true);
        listView = pullDownView.getListView();
		pb_hditem = (ProgressBar) findViewById(R.id.pb_hditem);
	}
	
//	@Override
//    public void onRefreshStarted(View view) {
//		getRefresh(new Handler(){
//        	@Override
//        	public void handleMessage(Message msg) {
//        		arrays = (List<PinglunData>) msg.obj;
//        		adapter = new HuodongItemAdapter(HuodongItemPLActivity.this,arrays,inflater,delmpl_url,reportypl_url,uid,densityDpi,id);
//                listView.setAdapter(adapter);
//        		adapter.notifyDataSetChanged();
//        		pullDownView.notifyDidRefresh(false);
//        		mPullToRefreshAttacher.setRefreshComplete();
//        	}
//        });
//    }
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(resultCode == Activity.RESULT_OK && requestCode == PINGLUN){
			//评论成功刷新评论列表
			getRefresh(new Handler(){
	        	@Override
	        	public void handleMessage(Message msg) {
	        		arrays = (List<PinglunData>) msg.obj;
	        		adapter = new ActDetailAdapter(ActCommentListActivity.this,arrays,inflater,delmpl_url,reportypl_url,uid,densityDpi,id);
	                listView.setAdapter(adapter);
	        		adapter.notifyDataSetChanged();
	        		pullDownView.notifyDidRefresh(false);
	        	}
	        });
			setResult(Activity.RESULT_OK);  //无论添加评论或删除评论，成功后通知上级刷新列表
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	/**
	 * 初始化数据
	 * @param handler
	 */
	private void initArrays(final Handler handler) {
		new Thread(new Runnable() {
			List<PinglunData> lst = new ArrayList<PinglunData>();
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
			List<PinglunData> lst = new ArrayList<PinglunData>();
			@Override
			public void run() {
				
					try {  
						startindex = 1;
						endindex = 15;
						lst = getDataFromNetwork(startindex,endindex);
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				handler.obtainMessage(0, lst).sendToTarget();
			}
		}).start();
	}
	
	private void getLoadMore(final Handler handler) {
		new Thread(new Runnable() {
			List<PinglunData> lst = new ArrayList<PinglunData>();
			@Override
			public void run() {
					//每次上拉更新10条数据
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
	private List<PinglunData> getDataFromNetwork(int start,int end) throws Exception{
		List<PinglunData> lst = new ArrayList<PinglunData>();
		
		String resultString = HttpUtil.GetStringFromUrl(getpl_url+id+"&startindex="+start+"&endindex="+end+"&connected_uid="+uid);
		if(resultString != null){
			Type listType = new TypeToken<List<PinglunData>>(){}.getType();
	        Gson gson = new Gson();
	        lst = gson.fromJson(resultString, listType);
	        return lst;
		}else{
			return null;
		}
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

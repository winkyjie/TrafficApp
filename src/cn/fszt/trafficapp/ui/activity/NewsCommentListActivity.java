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
import cn.fszt.trafficapp.adapter.NewsDetailAdapter;
import cn.fszt.trafficapp.domain.PinglunNewsData;
import cn.fszt.trafficapp.util.HttpUtil;
import cn.fszt.trafficapp.widget.PullDownView;
import cn.fszt.trafficapp.widget.ScrollOverListView;
import cn.fszt.trafficapp.widget.PullDownView.OnPullDownListener;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

/**
 * GO精选 查看全部评论内容页面
 * @author AeiouKong
 *
 */
public class NewsCommentListActivity extends Activity {
	private LayoutInflater inflater;
	private List<PinglunNewsData> arrays;
	private PullDownView pullDownView;
	private ScrollOverListView listView;
	private NewsDetailAdapter adapter;
	private String getpl_url,delmpl_url,reportnews_url;
	private int uid,densityDpi;  //用户通行证id
	private String id; //文章id
	private int startindex = 1;
	private int endindex = 10;
	private static final int PINGLUN = 99;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		DisplayMetrics metric = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metric);
        densityDpi = metric.densityDpi;
		setContentView(R.layout.activity_newsitempl);
        Intent intent = getIntent();
		id = intent.getStringExtra("id");
        initView();
        
        initArrays(new Handler(){
           	@Override
           	public void handleMessage(Message msg) {
           		arrays = (List<PinglunNewsData>) msg.obj;
           		adapter = new NewsDetailAdapter(NewsCommentListActivity.this,arrays,inflater,delmpl_url,densityDpi,uid,reportnews_url);
                listView.setAdapter(adapter);
           		adapter.notifyDataSetChanged();
           		pullDownView.notifyDidDataLoad(false);
           	}
           });
        
        pullDownView.setOnPullDownListener(new OnPullDownListener() {
			//下拉刷新
			@Override
			public void onRefresh() {
				getRefresh(new Handler(){
		        	@Override
		        	public void handleMessage(Message msg) {
		        		arrays = (List<PinglunNewsData>) msg.obj;
		        		adapter = new NewsDetailAdapter(NewsCommentListActivity.this,arrays,inflater,delmpl_url,densityDpi,uid,reportnews_url);
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
						if((List<PinglunNewsData>)msg.obj!=null){
							for(PinglunNewsData s :(List<PinglunNewsData>)msg.obj){
								arrays.add(s);
							}
						}
//						adapter = new NewsAdapter(NewsItemPLActivity.this,arrays,inflater,delmpl_url);
//		                listView.setAdapter(adapter);
						adapter.notifyDataSetChanged();
						pullDownView.notifyDidLoadMore((List<PinglunNewsData>)msg.obj==null);
					}
				});
			}
		});
        
        getActionBar().setTitle(getResources().getString(R.string.allpinglun));
		getActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.titlebar)));
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setDisplayShowHomeEnabled(false);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		
		MenuInflater inflater = getMenuInflater();

	    inflater.inflate(R.menu.newsitemplactivity, menu);

		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch(item.getItemId()){
		case android.R.id.home:
			finish();
			break;
			
		case R.id.item_newsitempl_sentpinglun:
			Intent intent_sent = new Intent(this,ActCommentEditActivity.class);
			intent_sent.putExtra("id", id);
			intent_sent.putExtra("pingluntype", "news");
			startActivityForResult(intent_sent,PINGLUN);
			break;
		}
		return true;
	}

	private void initView() {
		SharedPreferences sp = getSharedPreferences("USER", Context.MODE_PRIVATE);
    	uid = sp.getInt("uid", 0);
		getpl_url = getResources().getString(R.string.server_url)+getResources().getString(R.string.news_getpl_url)+"&hdnewid=";
		delmpl_url = getResources().getString(R.string.server_url)+getResources().getString(R.string.news_delmpl_url)+"&hdnewcommentid=";
		reportnews_url = getResources().getString(R.string.server_url)+getResources().getString(R.string.reportnews_url)+"&hdnewcommentid=";
		inflater = getLayoutInflater();
		pullDownView = (PullDownView) findViewById(R.id.pd_newsitem_pl);
        pullDownView.enableAutoFetchMore(true, 2);
        pullDownView.enableLoadMore(false);
        listView = pullDownView.getListView();
        listView.setDivider(null);
        listView.setCacheColorHint(Color.TRANSPARENT);
        listView.setBackgroundColor(Color.WHITE);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(resultCode == Activity.RESULT_OK && requestCode == PINGLUN){
			//评论成功刷新评论列表
			getRefresh(new Handler(){
	        	@Override
	        	public void handleMessage(Message msg) {
	        		arrays = (List<PinglunNewsData>) msg.obj;
	        		adapter = new NewsDetailAdapter(NewsCommentListActivity.this,arrays,inflater,delmpl_url,densityDpi,uid,reportnews_url);
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
			List<PinglunNewsData> lst = new ArrayList<PinglunNewsData>();
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
			List<PinglunNewsData> lst = new ArrayList<PinglunNewsData>();
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
	
	private void getLoadMore(final Handler handler) {
		new Thread(new Runnable() {
			List<PinglunNewsData> lst = new ArrayList<PinglunNewsData>();
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
	private List<PinglunNewsData> getDataFromNetwork(int start,int end) throws Exception{
		List<PinglunNewsData> lst = new ArrayList<PinglunNewsData>();
		
		String resultString = HttpUtil.GetStringFromUrl(getpl_url+id+"&startindex="+start+"&endindex="+end+"&connected_uid="+uid);
		if(resultString != null){
			Type listType = new TypeToken<List<PinglunNewsData>>(){}.getType();
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

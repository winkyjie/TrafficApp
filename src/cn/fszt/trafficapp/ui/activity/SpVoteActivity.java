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
import cn.fszt.trafficapp.adapter.SpVoteAdapter;
import cn.fszt.trafficapp.db.DBManager;
import cn.fszt.trafficapp.domain.VoteData;
import cn.fszt.trafficapp.util.HttpUtil;
import cn.fszt.trafficapp.widget.PullDownView;
import cn.fszt.trafficapp.widget.ScrollOverListView;
import cn.fszt.trafficapp.widget.PullDownView.OnPullDownListener;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;

public class SpVoteActivity extends Activity{
	
	private LayoutInflater inflater;
	private List<VoteData> arrays;
	private PullDownView pullDownView;
	private ScrollOverListView listView;
	private SpVoteAdapter adapter;
	private String delmpl_url,space_vote_url,uid;
	private ProgressBar pb_carlife;
	
	private int startindex = 1;
	private int endindex = 10;
	
	private DBManager mgr;
	private List<VoteData> sql_arrays;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.space_carlife);
		
		initView();
        
        sql_arrays = mgr.queryS_voteByConnected_uid(uid+"");
		
		if(sql_arrays.size()==0){  //当数据库没有数据时，从网络更新并插入数据库
	       	initArrays(new Handler(){
	           	@Override
	           	public void handleMessage(Message msg) {
	           		arrays = (List<VoteData>) msg.obj;
	           		adapter = new SpVoteAdapter(SpVoteActivity.this,arrays,inflater,delmpl_url,uid);
	                listView.setAdapter(adapter);
	           		adapter.notifyDataSetChanged();
	           		pullDownView.notifyDidDataLoad(false);
	           		
	           		if(arrays!=null){
	           			mgr.addS_vote(arrays);
	           			pb_carlife.setVisibility(View.GONE);
	           		}else{
	           			pb_carlife.setVisibility(View.GONE);
	           		}
	           	}
	           });
	       }else{  //查找数据库显示历史数据，然后再从网络更新并插入数据库
		       	arrays = sql_arrays;
		       	adapter = new SpVoteAdapter(SpVoteActivity.this,arrays,inflater,delmpl_url,uid);
                listView.setAdapter(adapter);
		       	adapter.notifyDataSetChanged();
		   		pullDownView.notifyDidDataLoad(false);
	   		initArrays(new Handler(){
	           	@Override
	           	public void handleMessage(Message msg) {
	           		
	           		arrays = (List<VoteData>) msg.obj;
	           		if(arrays==null){  //网络更新失败时，还是显示数据库的历史数据
	           			arrays = sql_arrays;
	           			pb_carlife.setVisibility(View.GONE);
	           		}else{             //网络更新数据成功，更新数据库的数据
	           			mgr.deleteS_voteByconnected_uid(uid+"");
						mgr.addS_vote(arrays);
						pb_carlife.setVisibility(View.GONE);
	           		}
	           		adapter = new SpVoteAdapter(SpVoteActivity.this,arrays,inflater,delmpl_url,uid);
	    	        listView.setAdapter(adapter);
	           		adapter.notifyDataSetChanged();
	           		pullDownView.notifyDidDataLoad(false);
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
		        		arrays = (List<VoteData>) msg.obj;
		        		
		        		if(arrays==null){
		        			arrays = sql_arrays;    //下拉更新失败，还是显示数据库历史数据
		        		}else{                      //下拉更新数据成功，更新数据库的数据
		        			mgr.deleteS_voteByconnected_uid(uid+"");
							mgr.addS_vote(arrays);
	            		}
		        		
		        		adapter = new SpVoteAdapter(SpVoteActivity.this,arrays,inflater,delmpl_url,uid);
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
						if((List<VoteData>)msg.obj!=null){
							for(VoteData s :(List<VoteData>)msg.obj){
								arrays.add(s);
							}
						}
						adapter.notifyDataSetChanged();
						pullDownView.notifyDidLoadMore((List<VoteData>)msg.obj==null);
					}
				});
			}
			
		});
		
	}

	private void initView() {
		mgr = new DBManager(this);
		SharedPreferences sp = getSharedPreferences("USER", Context.MODE_PRIVATE);
    	uid = sp.getString("uuid", null);
    	delmpl_url = getResources().getString(R.string.server_url)+getResources().getString(R.string.hd_delmpl_url);
    	space_vote_url = getResources().getString(R.string.server_url)+getResources().getString(R.string.space_vote_url);
    	inflater = getLayoutInflater();
		pullDownView = (PullDownView) findViewById(R.id.pd_space_carlife);
        pullDownView.enableAutoFetchMore(true, 0);
        pullDownView.enableLoadMore(false);
        pullDownView.enablePullDown(false);
        listView = pullDownView.getListView();
        listView.setDivider(getResources().getDrawable(R.color.list_view_divider));
        listView.setDividerHeight(1);
        listView.setCacheColorHint(Color.TRANSPARENT);
    	pb_carlife = (ProgressBar) findViewById(R.id.pb_space_carlife);
	}
	
	
	/**
	 * 初始化数据
	 * @param handler
	 */
	private void initArrays(final Handler handler) {
		new Thread(new Runnable() {
			List<VoteData> lst = new ArrayList<VoteData>();
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
			List<VoteData> lst = new ArrayList<VoteData>();
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
			List<VoteData> lst = new ArrayList<VoteData>();
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
	private List<VoteData> getDataFromNetwork(int start,int end) throws Exception{
		List<VoteData> lst = new ArrayList<VoteData>();
		
		String resultString = HttpUtil.GetStringFromUrl(space_vote_url+"&connected_uid="+uid+"&startindex="+start+"&endindex="+end);
		if(resultString != null&&!"".equals(resultString)){
			Type listType = new TypeToken<List<VoteData>>(){}.getType();
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
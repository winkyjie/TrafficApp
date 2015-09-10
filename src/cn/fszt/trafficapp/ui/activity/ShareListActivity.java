package cn.fszt.trafficapp.ui.activity;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.message.BasicNameValuePair;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import cn.fszt.trafficapp.R;
import cn.fszt.trafficapp.adapter.ShareListAdapter;
import cn.fszt.trafficapp.adapter.ShareListHeaderAdapter;
import cn.fszt.trafficapp.db.DBManager;
import cn.fszt.trafficapp.domain.BaoliaoAndBaoguangData;
import cn.fszt.trafficapp.domain.ReplydetailData;
import cn.fszt.trafficapp.domain.ShareListHeaderData;
import cn.fszt.trafficapp.util.Constant;
import cn.fszt.trafficapp.util.HttpUtil;
import cn.fszt.trafficapp.widget.MyListview;
import cn.fszt.trafficapp.widget.PullDownView;
import cn.fszt.trafficapp.widget.ScrollOverListView;
import cn.fszt.trafficapp.widget.PullDownView.OnPullDownListener;
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
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewConfiguration;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

/**
 * 车友分享列表
 * @author AeiouKong
 *
 */
public class ShareListActivity extends BaseBackActivity implements OnItemClickListener,OnClickListener{
	
	private LayoutInflater inflater,inflaterhead;
	private List<BaoliaoAndBaoguangData> arrays;
	private List<ShareListHeaderData> headarrays;
	private PullDownView pullDownView;
	private ScrollOverListView listView;
	private MyListview mylistview;
	private ShareListAdapter adapter;
	private ShareListHeaderAdapter headadapter;
	private String getbaoguang_url,deldetail_url,uid,api_url,delradiocomment_url,mobile;
	private ProgressBar pb_baoguanglist;
	
	private int startindex = 1;
	private int endindex = 15;
	private int listtype = 0;
	
	private DBManager mgr;
	private List<BaoliaoAndBaoguangData> sql_arrays;
	private List<ShareListHeaderData> sql_headarrays;
	private static final String TYPE = "bg";
	int densityDpi,position;
	private SharedPreferences sp;
	
	private ImageView iv_input;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		DisplayMetrics metric = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metric);
        densityDpi = metric.densityDpi;
		setContentView(R.layout.activity_share);
		
		initView();
        
		sql_arrays = mgr.queryBaoliaoandBaoguangByType(TYPE);
		sql_headarrays = mgr.queryShareListHeader();
		
		if(sql_arrays.size()==0){  //当数据库没有数据时，从网络更新并插入数据库
	       	initArrays(new Handler(){
	           	@Override
	           	public void handleMessage(Message msg) {
	           		arrays = (List<BaoliaoAndBaoguangData>) msg.obj;
	           		adapter = new ShareListAdapter(ShareListActivity.this,arrays,inflater,densityDpi,deldetail_url,uid);
	                listView.setAdapter(adapter);
	           		adapter.notifyDataSetChanged();
	           		pullDownView.notifyDidDataLoad(false);
	           		
	           		if(arrays!=null&&arrays.size()>0){
	           			mgr.addBaoliaoandbaoguangByType(arrays, TYPE);
	           			pb_baoguanglist.setVisibility(View.GONE);
	           		}else{
	           			pb_baoguanglist.setVisibility(View.GONE);
	           		}
	           	}
	           },0);
	       }else{  //查找数据库显示历史数据，然后再从网络更新并插入数据库
		       	arrays = sql_arrays;
		       	adapter = new ShareListAdapter(ShareListActivity.this,arrays,inflater,densityDpi,deldetail_url,uid);
                listView.setAdapter(adapter);
		       	adapter.notifyDataSetChanged();
		   		pullDownView.notifyDidDataLoad(false);
	   		initArrays(new Handler(){
	           	@Override
	           	public void handleMessage(Message msg) {
	           		arrays = (List<BaoliaoAndBaoguangData>) msg.obj;
	           		
	           		if(arrays!=null){
	           		//网络更新数据成功，更新数据库的数据
	           			mgr.deleteBaoliaoandbaoguangByType(TYPE);
	           			mgr.addBaoliaoandbaoguangByType(arrays, TYPE);
						pb_baoguanglist.setVisibility(View.GONE);
	           		}else{             
	           			arrays = sql_arrays;
	           			pb_baoguanglist.setVisibility(View.GONE);
	           		}
	           		adapter = new ShareListAdapter(ShareListActivity.this,arrays,inflater,densityDpi,deldetail_url,uid);
	    	        listView.setAdapter(adapter);
	           		adapter.notifyDataSetChanged();
	           		pullDownView.notifyDidDataLoad(false);
	           	}
	           },0);
	       }
		
		//加载头部置顶数据
		if(sql_headarrays.size() == 0){
			
			initHeader(new Handler(){
				@Override
				public void handleMessage(Message msg) {
					super.handleMessage(msg);
					headarrays = (List<ShareListHeaderData>) msg.obj;
					headadapter = new ShareListHeaderAdapter(ShareListActivity.this,
							headarrays, inflaterhead,densityDpi,delradiocomment_url,uid);
					mylistview.setAdapter(headadapter);
					headadapter.notifyDataSetChanged();
					
					if(headarrays!=null){
	           			mgr.addShareListHeader(headarrays);
	           		}
				}
			});
		}
		else{
			headarrays = sql_headarrays;
			headadapter = new ShareListHeaderAdapter(ShareListActivity.this,
					headarrays, inflaterhead,densityDpi,delradiocomment_url,uid);
            mylistview.setAdapter(headadapter);
			headadapter.notifyDataSetChanged();
   		initHeader(new Handler(){
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				headarrays = (List<ShareListHeaderData>) msg.obj;
				if(headarrays!=null){
					mgr.deleteShareListHeader();
					mgr.addShareListHeader(headarrays);
				}
//				else{
//					headarrays = sql_headarrays;
//				}
				headadapter = new ShareListHeaderAdapter(ShareListActivity.this,
						headarrays, inflaterhead,densityDpi,delradiocomment_url,uid);
				mylistview.setAdapter(headadapter);
				headadapter.notifyDataSetChanged();
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
		        		arrays = (List<BaoliaoAndBaoguangData>) msg.obj;
		        		
		        		if(arrays==null){
		        			arrays = sql_arrays;    //下拉更新失败，还是显示数据库历史数据
		        		}else{                      //下拉更新数据成功，更新数据库的数据
		        			mgr.deleteBaoliaoandbaoguangByType(TYPE);
		        			mgr.addBaoliaoandbaoguangByType(arrays, TYPE);
	            		}
		        		adapter = new ShareListAdapter(ShareListActivity.this,arrays,inflater,densityDpi,deldetail_url,uid);
		                listView.setAdapter(adapter);
		        		adapter.notifyDataSetChanged();
		        		pullDownView.notifyDidRefresh(false);
		        	}
		        },listtype);
			}

			@Override
			public void onLoadMore() {
				getLoadMore(new Handler(){
					@Override
					public void handleMessage(Message msg) {
						if((List<BaoliaoAndBaoguangData>)msg.obj!=null){
							for(BaoliaoAndBaoguangData s :(List<BaoliaoAndBaoguangData>)msg.obj){
								arrays.add(s);
							}
						}
						adapter.notifyDataSetChanged();
						pullDownView.notifyDidLoadMore((List<BaoliaoAndBaoguangData>)msg.obj==null);
					}
				},listtype);
			}
		});
        
        getActionBar().setTitle(getResources().getString(R.string.item_baoguang));
		getActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.titlebar)));
		getActionBar().setDisplayHomeAsUpEnabled(true);
		
		getOverflowMenu();
	}
	
	private void getOverflowMenu() {
        try {
           ViewConfiguration config = ViewConfiguration.get(this);
           Field menuKeyField = ViewConfiguration.class.getDeclaredField("sHasPermanentMenuKey");
           if(menuKeyField != null) {
               menuKeyField.setAccessible(true);
               menuKeyField.setBoolean(config, false);
           }
       } catch (Exception e) {
           e.printStackTrace();
       }
   }
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		
		MenuInflater inflater = getMenuInflater();

	    inflater.inflate(R.menu.baoguanglist, menu);
	    
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch(item.getItemId()){
		case android.R.id.home:
			finish();
			break;
			
//		case R.id.item_bg_baoguang:
//			if(uid!=null){
//				Intent intent = new Intent(ShareListActivity.this,ShareEditActivity.class);
//				startActivityForResult(intent,Constant.PINGLUN_REQUESTCODE);
//			}else{
//				Intent intent = new Intent(this,StLoginActivity.class);
//	        	startActivityForResult(intent, Constant.LOGIN_REQUESTCODE);
//			}
//			break;
			
		case R.id.item_bg_myshared:
			if(uid == null){
				Intent intent_myshared = new Intent(this,StLoginActivity.class);
	        	startActivityForResult(intent_myshared, Constant.LOGIN_REQUESTCODE);
			}else{
				pb_baoguanglist.setVisibility(View.VISIBLE);
				searchShared(2);
				listtype = 2;
			}
			break;
			
		case R.id.item_bg_djshared:
			searchShared(1);
			listtype = 1;
			break;
			
		case R.id.item_bg_shared:
			pb_baoguanglist.setVisibility(View.VISIBLE);
			searchShared(0);
			listtype = 0;
			break;
		}
		return true;
	}
	
	/**
	 * 用于筛选我的分享与车友分享
	 * @param uid
	 */
	private void searchShared(int listtype){
		if(listtype == 0){
			//全部分享
			getActionBar().setTitle(getString(R.string.item_baoguang));
		}else if(listtype == 2){
			//我的分享
			getActionBar().setTitle(getString(R.string.item_bg_myshared));
		}else if(listtype == 1){
			//主持人的分享
			getActionBar().setTitle(getString(R.string.item_bg_djshared));
		}
		getRefresh(new Handler(){
        	@Override
        	public void handleMessage(Message msg) {
        		arrays = (List<BaoliaoAndBaoguangData>) msg.obj;
        		pb_baoguanglist.setVisibility(View.GONE);
        		adapter = new ShareListAdapter(ShareListActivity.this,arrays,inflater,densityDpi,deldetail_url,uid);
                listView.setAdapter(adapter);
        		adapter.notifyDataSetChanged();
        		pullDownView.notifyDidRefresh(false);
        	}
        },listtype);
	}

	private void initView() {
		mgr = new DBManager(this);
		sp = getSharedPreferences("USER", Context.MODE_PRIVATE);
    	uid = sp.getString("uuid", null);
    	mobile = sp.getString("mobile", null);
    	getbaoguang_url = getString(R.string.server_url)+getString(R.string.getbaoguang_url);
    	deldetail_url = getString(R.string.server_url)+getString(R.string.delbgdetail_url);
    	delradiocomment_url = getString(R.string.server_url)
				+ getString(R.string.hud_delmchat_url);
    	api_url = getString(R.string.api_url);
    	iv_input = (ImageView) findViewById(R.id.iv_input);
    	iv_input.setOnClickListener(this);
    	inflater = getLayoutInflater();
		pullDownView = (PullDownView) findViewById(R.id.pd_space_carlife);
        pullDownView.enableAutoFetchMore(true, 2);
        pullDownView.enableLoadMore(true);
        pullDownView.enablePullDown(true);
        //TODO
        inflaterhead = getLayoutInflater();
		View headview = inflaterhead.inflate(
				R.layout.sharelistheader, null);
		mylistview = (MyListview) headview.findViewById(R.id.list_share);
		mylistview.setDividerHeight(1);
		mylistview.setCacheColorHint(Color.TRANSPARENT);
		mylistview.setOnItemClickListener(this);
        listView = pullDownView.getListView();
        listView.addHeaderView(headview);
        listView.setOnItemClickListener(this);
    	pb_baoguanglist = (ProgressBar) findViewById(R.id.pb_space_carlife);
	}
	
	
	/**
	 * 初始化数据
	 * @param handler
	 */
	private void initArrays(final Handler handler,final int listtype) {
		new Thread(new Runnable() {
			List<BaoliaoAndBaoguangData> lst = new ArrayList<BaoliaoAndBaoguangData>();
			@Override
			public void run() {
				
				try {  
					lst = string2BaoliaoAndBaoguangData(startindex,endindex,listtype);
				} catch (Exception e) {
					e.printStackTrace();
				}
					handler.obtainMessage(0, lst).sendToTarget();
			}
		}).start();
	}
	/**
	 * 初始化数据
	 * @param handler
	 */
	private void initHeader(final Handler handler) {
		new Thread(new Runnable() {
			List<ShareListHeaderData> lst = new ArrayList<ShareListHeaderData>();
			@Override
			public void run() {
				
				try {  
					lst = getHeaderData();
				} catch (Exception e) {
					e.printStackTrace();
				}
				handler.obtainMessage(0, lst).sendToTarget();
			}
		}).start();
	}
	
	private void getRefresh(final Handler handler,final int listtype) {
		new Thread(new Runnable() {
			List<BaoliaoAndBaoguangData> lst = new ArrayList<BaoliaoAndBaoguangData>();
			@Override
			public void run() {
				
					try {  
						startindex = 1;
						endindex = 15;
						lst = string2BaoliaoAndBaoguangData(startindex,endindex,listtype);
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				handler.obtainMessage(0, lst).sendToTarget();
			}
		}).start();
	}
	
	private void getLoadMore(final Handler handler,final int listtype) {
		new Thread(new Runnable() {
			List<BaoliaoAndBaoguangData> lst = new ArrayList<BaoliaoAndBaoguangData>();
			@Override
			public void run() {
					//每次上拉更新15条数据
					startindex += 15;
					endindex += 15;
					try {
						lst = string2BaoliaoAndBaoguangData(startindex,endindex,listtype);
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
	private String getDataFromNetwork(int start,int end,int listtype) throws Exception{
		
		String resultString = HttpUtil.GetStringFromUrl(getbaoguang_url+"&connected_uid="+uid+"&startindex="+start+"&endindex="+end+"&listtype="+listtype);
		return resultString;
	}
	
	/**
	 * 获取头部数据
	 * @return
	 */
	private List<ShareListHeaderData> getHeaderData(){
		List<ShareListHeaderData> lst = new ArrayList<ShareListHeaderData>();
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("action", "GetHdCommentListByIsshare"));
		params.add(new BasicNameValuePair("islive", "1"));
		String result = HttpUtil.PostStringFromUrl(api_url, params);
		if(result!=null){
			Type listType = new TypeToken<List<ShareListHeaderData>>(){}.getType();
	        Gson gson = new Gson();
	        lst = gson.fromJson(result, listType);
	        return lst;
		}else{
			return null;
		}
		
	}
	
	private List<BaoliaoAndBaoguangData> string2BaoliaoAndBaoguangData(int start,int end,int listtype){
		List<BaoliaoAndBaoguangData> lst = new ArrayList<BaoliaoAndBaoguangData>();
		try {
			String result = getDataFromNetwork(start,end,listtype);
			if(result!=null){
				Type listType = new TypeToken<List<BaoliaoAndBaoguangData>>(){}.getType();
		        Gson gson = new Gson();
		        lst = gson.fromJson(result, listType);
		        return lst;
			}else{
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	private List<ReplydetailData> string2ReplydetailData(List<BaoliaoAndBaoguangData> arrays,int position){
		List<ReplydetailData> replydetailList = new ArrayList<ReplydetailData>();
		Gson gson = new Gson();
		BaoliaoAndBaoguangData b = arrays.get(position);
    	List replydetail = b.getReplydetail();
    	//3条回复
    	String s = gson.toJson(replydetail);
    	String replycount = b.getReplycount();
    	if(!"0".equals(replycount)){
    		List<ReplydetailData> ReplydetailList = gson.fromJson(s,  
                    new TypeToken<List<ReplydetailData>>() {  
                    }.getType()); 
    		replydetailList = ReplydetailList;
    	}
		return replydetailList;
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		
		 if(requestCode == Constant.LOGIN_REQUESTCODE && resultCode == RESULT_OK){
			 uid = data.getStringExtra("uid");
		 }
		 
		//评论提交成功，在对话框增加自己的评论内容
		if(requestCode == Constant.PINGLUN_REQUESTCODE && resultCode == RESULT_OK){
			initArrays(new Handler(){
	        	@Override
	        	public void handleMessage(Message msg) {
	        		arrays = (List<BaoliaoAndBaoguangData>) msg.obj;
	        		adapter = new ShareListAdapter(ShareListActivity.this,arrays,inflater,densityDpi,deldetail_url,uid);
        	        listView.setAdapter(adapter);
	        		adapter.notifyDataSetChanged();
//	        		listView.setSelection(adapter.getCount());
	        		pullDownView.notifyDidDataLoad(false);
	        	}
	        },0);
		}
		
		if(requestCode == Constant.REPLY_REQUESTCODE && resultCode == RESULT_OK){
//			int replynum = data.getIntExtra("replynum", 0);
//    		if(replynum!=0){
//    			List<ReplydetailData> list = string2ReplydetailData(arrays,position);
//    			arrays.get(position).setReplycount(data.getIntExtra("replynum", 0)+"");
//    			ReplydetailData replydetaildata = new ReplydetailData();
//    			replydetaildata.setReply_headimg(data.getStringExtra("headimg"));
//    			replydetaildata.setReply_nickname(data.getStringExtra("nickname"));
//    			replydetaildata.setReply_content(data.getStringExtra("content"));
//    			list.add(replydetaildata);
//    		}else{
//    			arrays.get(position).setReplycount(arrays.get(position).getReplycount());
//    		}
    		String goodnum = data.getStringExtra("goodnum");
    		if(goodnum!=null){
    			arrays.get(position).setLikecount(data.getStringExtra("goodnum"));
    		}else{
    			arrays.get(position).setLikecount(arrays.get(position).getLikecount());
    		}
//    		adapter = new ShareListAdapter(ShareListActivity.this,arrays,inflater,densityDpi,deldetail_url,uid);
//	        listView.setAdapter(adapter);
//	        listView.setSelection(position);
    		adapter.notifyDataSetChanged();
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		if(id != Constant.LISTVIEWHEADER_ITEMID){
			if(arrays!=null){
				Intent intent = new Intent(ShareListActivity.this,ShareDetailActivity.class);
				Bundle mBundle = new Bundle(); 
				mBundle.putSerializable("BaoguangdetailData",arrays.get(position-1)); 
				intent.putExtras(mBundle);
				this.position = position-1;
				startActivityForResult(intent,Constant.REPLY_REQUESTCODE);
			}
		}else{
			if(headarrays!=null){
				String hdcommentid = headarrays.get(position).getHdcommentid();
				Intent intent = new Intent(ShareListActivity.this,Main2RadioCommentDetailActivity.class);
				intent.putExtra("hdcommentid", hdcommentid);
				startActivity(intent);
			}
		}
	}

	@Override
	public void onClick(View v) {
		if(v == iv_input){
			if(uid!=null&&mobile!=null&&!"".equals(mobile)){
				Intent intent = new Intent(ShareListActivity.this,ShareEditActivity.class);
				startActivityForResult(intent,Constant.PINGLUN_REQUESTCODE);
			}else if(uid==null){
				Intent intent = new Intent(this,StLoginActivity.class);
	        	startActivityForResult(intent, Constant.LOGIN_REQUESTCODE);
			}else if(uid!=null&&"".equals(mobile)){
				Toast.makeText(this, "请先绑定手机号", Toast.LENGTH_LONG).show();
			}
		}
	}
	
	@Override
	protected void onRestart() {
		super.onRestart();
		uid = sp.getString("uuid", null);
    	mobile = sp.getString("mobile", null);
    	if(mobile==null){
    		mobile = "";
    	}
	}
}

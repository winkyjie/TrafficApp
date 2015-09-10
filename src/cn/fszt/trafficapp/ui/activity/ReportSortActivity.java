package cn.fszt.trafficapp.ui.activity;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.client.ClientProtocolException;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.umeng.analytics.MobclickAgent;

import cn.fszt.trafficapp.R;
import cn.fszt.trafficapp.domain.BaoliaoSortData;
import cn.fszt.trafficapp.util.HttpUtil;
import cn.fszt.trafficapp.widget.PullDownView;
import cn.fszt.trafficapp.widget.ScrollOverListView;
import cn.fszt.trafficapp.widget.msg.AppMsg;
import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * 报料排行榜
 * @author AeiouKong
 *
 */
public class ReportSortActivity extends Activity{
	
	private String getbaoliaosort_url;
	private List<BaoliaoSortData> arrays;
	private PullDownView pullDownView;
	private ScrollOverListView listView;
	private MyAdapter adapter;
	private LayoutInflater inflater;
	private int startindex = 1;
	private int endindex = 10;
	int densityDpi;
	
	private ImageLoader imageLoader;
	private DisplayImageOptions options_head;
	
	private ProgressBar pb_bls;
	
	AppMsg.Style style = AppMsg.STYLE_INFO;
	AppMsg appMsg;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		DisplayMetrics metric = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metric);
        densityDpi = metric.densityDpi;
       
		setContentView(R.layout.activity_baoliaosort);
        initView();
        init();
        
        getActionBar().setTitle(getResources().getString(R.string.baoliaosort));
		getActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.titlebar)));
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setDisplayShowHomeEnabled(false);
		
        initArrays(new Handler(){
        	@Override
        	public void handleMessage(Message msg) {
        		arrays = (List<BaoliaoSortData>) msg.obj;
        		if(arrays==null){
        			showMsg(getResources().getString(R.string.response_fail), "info", "bottom");
        		}
        		adapter.notifyDataSetChanged();
        		pullDownView.notifyDidDataLoad(false);
        		pb_bls.setVisibility(View.GONE);
        	}
        });
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		if(item.getItemId() == android.R.id.home)
        {
            finish();
            return true;
        }
		return super.onOptionsItemSelected(item);
	}
	
	private void init(){
		imageLoader = ImageLoader.getInstance();
		
		options_head = new DisplayImageOptions.Builder()
		.showImageForEmptyUri(R.drawable.default_head)
		.showImageOnFail(R.drawable.default_head)
		.cacheOnDisc(true)
		.displayer(new RoundedBitmapDisplayer(15))
		.build();
	}

	private void initView() {
		getbaoliaosort_url = getResources().getString(R.string.server_url)+getResources().getString(R.string.getbaoliaosort_url);
		inflater = getLayoutInflater();
        pullDownView = (PullDownView) findViewById(R.id.pd_bls);
        pullDownView.enableAutoFetchMore(true, 0);
        pullDownView.enableLoadMore(false);
        pullDownView.enablePullDown(false);
        listView = pullDownView.getListView();
        adapter = new MyAdapter();
        listView.setAdapter(adapter);
        listView.setCacheColorHint(Color.TRANSPARENT);
        listView.setDivider(getResources().getDrawable(R.color.list_view_divider));
        listView.setDividerHeight(1);
        pb_bls = (ProgressBar) findViewById(R.id.pb_bls);
	}
	
	
	/**
	 * 初始化数据
	 * @param handler
	 */
	private void initArrays(final Handler handler) {
		new Thread(new Runnable() {
			List<BaoliaoSortData> lst = new ArrayList<BaoliaoSortData>();
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
	 * 从网络获取数据
	 * @return
	 * @throws IOException 
	 * @throws ClientProtocolException 
	 * @throws Exception
	 */
	private List<BaoliaoSortData> getDataFromNetwork(int start,int end) throws Exception{
		List<BaoliaoSortData> lst = new ArrayList<BaoliaoSortData>();
		
		String resultString = HttpUtil.GetStringFromUrl(getbaoliaosort_url+"&startindex="+start+"&endindex="+end);
            
            Type listType = new TypeToken<List<BaoliaoSortData>>(){}.getType();
            Gson gson = new Gson();
            lst = gson.fromJson(resultString, listType);
            
        return lst;
	}
	
	
	private class MyAdapter extends BaseAdapter {
		@Override
		public int getCount() {
			return arrays == null ? 0 : arrays.size();
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}
		
		

		@Override
		public int getItemViewType(int position) {
			return super.getItemViewType(position);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if (convertView == null) {
				holder = new ViewHolder();
				if(densityDpi == 240){
					convertView = inflater.inflate(R.layout.item_baoliaosort_hdip, null); 
				}else{
					convertView = inflater.inflate(R.layout.item_baoliaosort, null);
				}
				holder.tv_bls_nickname = (TextView) convertView.findViewById(R.id.tv_bls_nickname);
				holder.tv_bls_count = (TextView) convertView.findViewById(R.id.tv_bls_count);
				holder.iv_bls_head = (ImageView) convertView.findViewById(R.id.iv_bls_head);
				holder.iv_bls_sort = (ImageView) convertView.findViewById(R.id.iv_bls_sort);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			
			if(arrays.size()>0){
				  holder.tv_bls_nickname.setText(arrays.get(position).getNickname());
				  holder.tv_bls_count.setText(arrays.get(position).getRecordcount()+"次");
//				  ImageManager.from(BaoliaoSort.this).displayImage(holder.iv_bls_head, arrays.get(position).getHeadimg(), R.drawable.default_head, 250, 250);
				  imageLoader.displayImage(arrays.get(position).getHeadimg(), holder.iv_bls_head, options_head, null);
				  
				  //判断不同排名显示的图标
				if(arrays.get(position).getNum().equals("1")){
					holder.iv_bls_sort.setBackgroundResource(R.drawable.bls_1);
				}else if(arrays.get(position).getNum().equals("2")){
					holder.iv_bls_sort.setBackgroundResource(R.drawable.bls_2);
				}else if(arrays.get(position).getNum().equals("3")){
					holder.iv_bls_sort.setBackgroundResource(R.drawable.bls_3);
				}else if(arrays.get(position).getNum().equals("4")){
					holder.iv_bls_sort.setBackgroundResource(R.drawable.bls_4);
				}else if(arrays.get(position).getNum().equals("5")){
					holder.iv_bls_sort.setBackgroundResource(R.drawable.bls_5);
				}else if(arrays.get(position).getNum().equals("6")){
					holder.iv_bls_sort.setBackgroundResource(R.drawable.bls_6);
				}else if(arrays.get(position).getNum().equals("7")){
					holder.iv_bls_sort.setBackgroundResource(R.drawable.bls_7);
				}else if(arrays.get(position).getNum().equals("8")){
					holder.iv_bls_sort.setBackgroundResource(R.drawable.bls_8);
				}else if(arrays.get(position).getNum().equals("9")){
					holder.iv_bls_sort.setBackgroundResource(R.drawable.bls_9);
				}else if(arrays.get(position).getNum().equals("10")){
					holder.iv_bls_sort.setBackgroundResource(R.drawable.bls_10);
				}
				 
			}else{
				holder = null;
			}
			
			return convertView;
		}
	}
	
	private static class ViewHolder {
		TextView tv_bls_count;
		TextView tv_bls_nickname;
		ImageView iv_bls_head;
		ImageView iv_bls_sort;
	}
	
	private void showMsg(String msg,String styletype,String gravity){
		
		if(styletype.equals("alert")){
			style = AppMsg.STYLE_ALERT;
		}else if(styletype.equals("info")){
			style = AppMsg.STYLE_INFO;
		}
		
		appMsg = AppMsg.makeText(ReportSortActivity.this, msg, style);
		
		if(gravity.equals("bottom")){
			appMsg.setLayoutGravity(Gravity.BOTTOM);
		}else if(gravity.equals("top")){
		}
        appMsg.show();
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

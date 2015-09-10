package cn.fszt.trafficapp.ui.activity;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.umeng.analytics.MobclickAgent;

import cn.fszt.trafficapp.R;
import cn.fszt.trafficapp.db.DBManager;
import cn.fszt.trafficapp.domain.ReplaysItemData;
import cn.fszt.trafficapp.util.HttpUtil;
import cn.fszt.trafficapp.widget.PullDownView;
import cn.fszt.trafficapp.widget.ScrollOverListView;
import cn.fszt.trafficapp.widget.photoview.DisplayImageOptionsUtil;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

/**
 * 点播节目最近7天节目单
 * @author AeiouKong
 *
 */
public class ReplayLiveListActivity extends Activity implements OnItemClickListener,OnClickListener{
	private String replayitem_url,hdreplaytypeid;
	private List<ReplaysItemData> arrays;
	private PullDownView pullDownView;
	private ScrollOverListView listView;
	private MyAdapter adapter;
	private LayoutInflater inflater;
	private Intent intent;
	private int startindex = 1;
	private int endindex = 7;
	private Button btn_cancel;
	
	private DBManager mgr;
	private List<ReplaysItemData> sql_arrays;
	
	private ImageLoader imageLoader;
	private DisplayImageOptions options;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_replayitem);
		initView();
		
		imageLoader = ImageLoader.getInstance();
		options = DisplayImageOptionsUtil.getOptions(
				R.drawable.ic_empty, R.drawable.ic_error,R.drawable.default_image);
		
		if(hdreplaytypeid!=null){
			sql_arrays = mgr.queryReplayitemById(hdreplaytypeid);
	       if(sql_arrays.size()==0){  //当数据库没有数据时，从网络更新并插入数据库
	       	initArrays(new Handler(){
	           	@Override
	           	public void handleMessage(Message msg) {
	           		arrays = (List<ReplaysItemData>) msg.obj;
	        		if(arrays!=null){
	        			mgr.addReplayitem(arrays);
	        		}else{
	        		}
	        		adapter.notifyDataSetChanged();
	        		pullDownView.notifyDidDataLoad(false);
	           	}
	           });
	       }else{  //查找数据库显示历史数据，然后再从网络更新并插入数据库
		       	arrays = sql_arrays;
		       	adapter.notifyDataSetChanged();
		   		pullDownView.notifyDidDataLoad(false);
	   		initArrays(new Handler(){
	           	@Override
	           	public void handleMessage(Message msg) {
	           		
	           		arrays = (List<ReplaysItemData>) msg.obj;
	           		if(arrays==null){  //网络更新失败时，还是显示数据库的历史数据
	           			arrays = sql_arrays;
	           		}else{             //网络更新数据成功，更新数据库的数据
	           			mgr.deleteReplayitemById(hdreplaytypeid);
	           			mgr.addReplayitem(arrays);
	           		}
	           		adapter.notifyDataSetChanged();
	           		pullDownView.notifyDidDataLoad(false);
	           	}
	           });
	       }
		}
	}
	
	private void initView(){
		mgr = new DBManager(this);
		intent = getIntent();
		hdreplaytypeid = intent.getStringExtra("id");
		replayitem_url = getResources().getString(R.string.server_url)+getResources().getString(R.string.replayitem_url);
		inflater = getLayoutInflater();
		pullDownView = (PullDownView) findViewById(R.id.ra_replayitem);
        pullDownView.enableAutoFetchMore(true, 0);
        pullDownView.enableLoadMore(false);
        pullDownView.enablePullDown(false);
        listView = pullDownView.getListView();
        adapter = new MyAdapter();
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);
        listView.setCacheColorHint(Color.TRANSPARENT);
        listView.setDivider(new ColorDrawable(R.color.dialog_list_view_divider));
        listView.setDividerHeight(1);
        btn_cancel = (Button) findViewById(R.id.btn_cancel);
        btn_cancel.setOnClickListener(this);
	}
	
	private List<ReplaysItemData> getReplayList(int start, int end){
		List<ReplaysItemData> lst = new ArrayList<ReplaysItemData>();
		String req = replayitem_url+"&hdreplaytypeid="+hdreplaytypeid+"&startindex="+start+"&endindex="+end;
		String resultString = HttpUtil.GetStringFromUrl(req);
            Type listType = new TypeToken<List<ReplaysItemData>>(){}.getType();
            Gson gson = new Gson();
            lst = gson.fromJson(resultString, listType);
            
        return lst;
	}
	
	private void initArrays(final Handler handler) {
		new Thread(new Runnable() {
			List<ReplaysItemData> lst = new ArrayList<ReplaysItemData>();
			@Override
			public void run() {
				
					try {  
						lst = getReplayList(startindex,endindex);
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				handler.obtainMessage(0, lst).sendToTarget();
			}
		}).start();
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
				convertView = inflater.inflate(R.layout.item_replayitem_list, null);
				holder.tv_title = (TextView) convertView.findViewById(R.id.tv_ri_title);
				holder.tv_createdate = (TextView) convertView.findViewById(R.id.tv_ri_createdate);
				holder.iv_imagepath = (ImageView) convertView.findViewById(R.id.iv_ri_imagepath);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			String imageUrl = arrays.get(position).getImagepath();  
        	imageLoader.displayImage(imageUrl, holder.iv_imagepath,options,null);
        	holder.tv_title.setText(arrays.get(position).getTitle());
        	holder.tv_createdate.setText(arrays.get(position).getCreatedate());
			
			return convertView;
		}
	}
	
	private static class ViewHolder {
		TextView tv_title;
		TextView tv_createdate;
		ImageView iv_imagepath;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		Intent intent_item = getIntent();
		intent_item.putExtra("voicepath", arrays.get(position).getVoicepath());
		intent_item.putExtra("title", arrays.get(position).getTitle());
		intent_item.putExtra("imagepath", arrays.get(position).getImagepath());
		intent_item.putExtra("childtag", position);
		setResult(Activity.RESULT_OK,intent_item);
		finish();
	}

	@Override
	public void onClick(View v) {
		if(v == btn_cancel){
			finish();
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

package cn.fszt.trafficapp.ui.activity;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.ProgressBar;
import cn.fszt.trafficapp.R;
import cn.fszt.trafficapp.adapter.RadioSecretListAdapter;
import cn.fszt.trafficapp.domain.RadioCommentListData;
import cn.fszt.trafficapp.util.Constant;
import cn.fszt.trafficapp.util.HttpUtil;
import cn.fszt.trafficapp.widget.PullDownView;
import cn.fszt.trafficapp.widget.PullDownView.OnPullDownListener;

/**
 * 私信列表
 */
public class RadioSecretListActivity extends Activity {
	private PullDownView secretListView; // 上拉更多，下拉刷新
	private ProgressBar secretProgressBar; // 加载条。。
	private int startindex = 1, endindex = 15; // 记载数据条数
	private String uid, api_url;
	private SharedPreferences sp_user = null;
	private SharedPreferences sp_push = null;
	private Set<String> sconnected_uids;
	private Set<String> programnames;
	private String hdreplaytypeid = "", programname; // 节目ID,节目名称
	private int densityDpi; // 手机密度
	private List<RadioCommentListData> arrays = new ArrayList<RadioCommentListData>();
	private RadioSecretListAdapter adapter;
	private ListView listView; // 私信列表
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_radiosecretlist);
		getActionBar().setTitle(getResources().getString(R.string.secret));
		getActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.titlebar)));
		getActionBar().setDisplayHomeAsUpEnabled(true);
		initView();
		initData();
	}

	private void initView() {
		DisplayMetrics metric = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metric);
		densityDpi = metric.densityDpi;
		api_url = getString(R.string.api_url);
		secretProgressBar = (ProgressBar) findViewById(R.id.secret_pb);
		secretListView = (PullDownView) findViewById(R.id.secret_reh);
		secretListView.enableAutoFetchMore(true, 2);
		secretListView.enableLoadMore(true);
		secretListView.enablePullDown(true);
		secretListView.setOnPullDownListener(new UpOrDown());
		listView = secretListView.getListView();
		listView.setOnItemClickListener(new OnListViewItemClick());
		
		
		sp_user = getSharedPreferences("USER", Context.MODE_PRIVATE);
		uid = sp_user.getString("uuid", null);
		sp_push = getSharedPreferences("PUSH", Context.MODE_PRIVATE);
		sconnected_uids = sp_push.getStringSet("sconnected_uid", null);
		programnames = sp_push.getStringSet("programname", null);

		Intent in = this.getIntent();
		hdreplaytypeid = in.getStringExtra("hdreplaytypeid");
		programname = in.getStringExtra("programname");
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			break;
		}
		return true;
	}

	/**
	 * 加载私信列表数据
	 */
	private void initData() {
		initArrays(new Handler() {
			@Override
			public void handleMessage(Message msg) {
				arrays = (List<RadioCommentListData>) msg.obj;
				if (arrays != null) {
					if (sconnected_uids != null) {
						for (int i = 0; i < arrays.size(); i++) {
							String sconnected_uid = arrays.get(i).getConnected_uid();
							Iterator<String> it = sconnected_uids.iterator();
							while (it.hasNext()) {
								if (sconnected_uid.equals(it.next())) {
									arrays.get(i).setVisiable(true);
								}
							}
						}
					}
				}
				adapter = new RadioSecretListAdapter(RadioSecretListActivity.this, arrays, uid, densityDpi);
				listView.setAdapter(adapter);
				secretProgressBar.setVisibility(View.GONE);
				adapter.notifyDataSetChanged();
				secretListView.notifyDidDataLoad(false);
			}
		});
	}

	/**
	 * 初始化数据
	 */
	private void initArrays(final Handler handler) {
		new Thread(new Runnable() {
			List<RadioCommentListData> lst = new ArrayList<RadioCommentListData>();

			@Override
			public void run() {
				try {
					lst = getDataFromNetwork(startindex, endindex);
				} catch (Exception e) {
					e.printStackTrace();
				}
				handler.obtainMessage(0, lst).sendToTarget();
			}
		}).start();
	}

	class OnListViewItemClick implements OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			if (arrays != null) {
				if (sconnected_uids != null) {
					if (sconnected_uids.contains(arrays.get(position).getConnected_uid())) {
						//删除单挑私信提醒红点
						sconnected_uids.remove(arrays.get(position).getConnected_uid());
						adapter.setCircleVisibile(View.GONE);
						//判断如果推送私信条数为0则清楚当前节目的私信提示
						if (sconnected_uids.isEmpty() || sconnected_uids.size() == 0) {
							if (programnames != null && programnames.contains(programname)) {
								programnames.remove(programname);
							}
						}
					}
				}
				adapter.notifyDataSetChanged();
				Intent intent = new Intent(RadioSecretListActivity.this, RadioSecretDetailActivity.class);
				Bundle mBundle = new Bundle();
				mBundle.putSerializable("RadioCommentListData", arrays.get(position));
				intent.putExtra("hdreplaytypeid", hdreplaytypeid);
				intent.putExtras(mBundle);
				// this.position = position - 1;
				startActivityForResult(intent, Constant.REPLY_REQUESTCODE);
			}
		}
	}

	/**
	 * 调用接口拿数据
	 */
	private List<RadioCommentListData> getDataFromNetwork(int start, int end) throws Exception {
		List<RadioCommentListData> lst = new ArrayList<RadioCommentListData>();
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("action", "GetHdsecretletterList"));
		params.add(new BasicNameValuePair("connected_uid", uid));
		params.add(new BasicNameValuePair("noncurrentConnected_uid", hdreplaytypeid));
		params.add(new BasicNameValuePair("hdreplaytypeid", hdreplaytypeid));
		params.add(new BasicNameValuePair("startindex", start + ""));
		params.add(new BasicNameValuePair("endindex", end + ""));
		String result = HttpUtil.PostStringFromUrl(api_url, params);
		if (!result.isEmpty()) {
			Type listType = new TypeToken<List<RadioCommentListData>>() {
			}.getType();
			Gson gson = new Gson();
			lst = gson.fromJson(result, listType);
			return lst;
		} else {
			return null;
		}
	}

	private void getLoadMore(final Handler handler) {
		new Thread(new Runnable() {
			List<RadioCommentListData> lst = new ArrayList<RadioCommentListData>();

			@Override
			public void run() {
				// 每次上拉更新15条数据
				startindex += 15;
				endindex += 15;
				try {
					lst = getDataFromNetwork(startindex, endindex);
				} catch (Exception e) {
					e.printStackTrace();
				}

				handler.obtainMessage(0, lst).sendToTarget();
			}
		}).start();
	}

	/**
	 * 上拉更多，下拉刷新
	 */
	class UpOrDown implements OnPullDownListener {
		@Override
		public void onRefresh() {
			startindex = 1;
			endindex = 15;
			initData();
			secretListView.notifyDidRefresh(false);
		}

		@Override
		public void onLoadMore() {
			getLoadMore(new Handler() {
				@Override
				public void handleMessage(Message msg) {
					startindex += 15;
					endindex += 15;
					if ((List<RadioCommentListData>) msg.obj != null) {
						for (RadioCommentListData s : (List<RadioCommentListData>) msg.obj) {
							arrays.add(s);
						}
					}
					adapter.notifyDataSetChanged();
					secretListView.notifyDidLoadMore((List<RadioCommentListData>) msg.obj == null);
				}
			});
		}
	}
}

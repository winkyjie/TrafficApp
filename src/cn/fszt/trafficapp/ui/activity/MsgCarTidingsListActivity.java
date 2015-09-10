package cn.fszt.trafficapp.ui.activity;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONArray;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.umeng.analytics.MobclickAgent;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import cn.fszt.trafficapp.R;
import cn.fszt.trafficapp.adapter.MsgCarTidingsListAdapter;
import cn.fszt.trafficapp.domain.MsgCarTidingsData;
import cn.fszt.trafficapp.util.HttpUtil;
import cn.fszt.trafficapp.widget.PullDownView;
import cn.fszt.trafficapp.widget.PullDownView.OnPullDownListener;
import cn.fszt.trafficapp.widget.ScrollOverListView;

/**
 * 代办通知
 */
public class MsgCarTidingsListActivity extends BaseBackActivity {

	private LayoutInflater inflater;
	private List<MsgCarTidingsData> arrays;
	private PullDownView pullDownView;
	private ScrollOverListView listView;
	private MsgCarTidingsListAdapter adapter;
	private String request_url, uid;
	private ProgressBar pb_carlife;

	private int startindex = 1;
	private int endindex = 10;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.space_carlife);
		initView();

		initArrays(new Handler() {
			@Override
			public void handleMessage(Message msg) {
				arrays = (List<MsgCarTidingsData>) msg.obj;
				adapter = new MsgCarTidingsListAdapter(MsgCarTidingsListActivity.this, arrays, inflater);
				listView.setAdapter(adapter);
				adapter.notifyDataSetChanged();
				pullDownView.notifyDidDataLoad(false);

				if (arrays != null) {
					pb_carlife.setVisibility(View.GONE);
				} else {
					pb_carlife.setVisibility(View.GONE);
				}
			}
		});

		pullDownView.setOnPullDownListener(new OnPullDownListener() {
			// 下拉刷新
			@Override
			public void onRefresh() {
				getRefresh(new Handler() {
					@Override
					public void handleMessage(Message msg) {
						arrays = (List<MsgCarTidingsData>) msg.obj;

						adapter = new MsgCarTidingsListAdapter(MsgCarTidingsListActivity.this, arrays, inflater);
						listView.setAdapter(adapter);
						adapter.notifyDataSetChanged();
						pullDownView.notifyDidRefresh(false);
					}
				});
			}

			@Override
			public void onLoadMore() {
				getLoadMore(new Handler() {
					@Override
					public void handleMessage(Message msg) {
						// arrays.add((String) msg.obj);
						if ((List<MsgCarTidingsData>) msg.obj != null) {
							for (MsgCarTidingsData s : (List<MsgCarTidingsData>) msg.obj) {
								arrays.add(s);
							}
						}
						adapter.notifyDataSetChanged();
						pullDownView.notifyDidLoadMore((List<MsgCarTidingsData>) msg.obj == null);
					}
				});
			}

		});
	}

	private void initView() {
		SharedPreferences sp = getSharedPreferences("USER", Context.MODE_PRIVATE);
		uid = sp.getString("uuid", null);
		request_url = getResources().getString(R.string.server_url)
				+ getResources().getString(R.string.message_msgcardtid_url);
		inflater = getLayoutInflater();
		pullDownView = (PullDownView) findViewById(R.id.pd_space_carlife);
		pullDownView.enableAutoFetchMore(true, 2);
		pullDownView.enableLoadMore(false);
		pullDownView.enablePullDown(true);
		listView = pullDownView.getListView();
		pb_carlife = (ProgressBar) findViewById(R.id.pb_space_carlife);
	}

	/**
	 * 初始化数据
	 * 
	 * @param handler
	 */
	private void initArrays(final Handler handler) {
		new Thread(new Runnable() {
			List<MsgCarTidingsData> lst = new ArrayList<MsgCarTidingsData>();

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

	private void getRefresh(final Handler handler) {
		new Thread(new Runnable() {
			List<MsgCarTidingsData> lst = new ArrayList<MsgCarTidingsData>();

			@Override
			public void run() {

				try {
					startindex = 1;
					endindex = 10;
					lst = getDataFromNetwork(startindex, endindex);
				} catch (Exception e) {
					e.printStackTrace();
				}

				handler.obtainMessage(0, lst).sendToTarget();
			}
		}).start();
	}

	private void getLoadMore(final Handler handler) {
		new Thread(new Runnable() {
			List<MsgCarTidingsData> lst = new ArrayList<MsgCarTidingsData>();

			@Override
			public void run() {
				// 每次上拉更新10条数据
				startindex += 10;
				endindex += 10;
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
	 * 从网络获取数据
	 * 
	 * @return
	 * @throws IOException
	 * @throws ClientProtocolException
	 * @throws Exception
	 */
	private List<MsgCarTidingsData> getDataFromNetwork(int start, int end) throws Exception {
		List<MsgCarTidingsData> lst = new ArrayList<MsgCarTidingsData>();
		String resultString = HttpUtil
				.GetStringFromUrl(request_url + "&connected_uid=" + uid + "&startindex=" + start + "&endindex=" + end);
		if (resultString != null && !"".equals(resultString)) {
			Type listType = new TypeToken<List<MsgCarTidingsData>>() {
			}.getType();
			Gson gson = new Gson();
			lst = gson.fromJson(resultString, listType);
			return lst;
		} else {
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

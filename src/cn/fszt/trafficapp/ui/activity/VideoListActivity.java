package cn.fszt.trafficapp.ui.activity;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import cn.fszt.trafficapp.R;
import cn.fszt.trafficapp.adapter.ActListAdapter;
import cn.fszt.trafficapp.adapter.VideoListAdapter;
import cn.fszt.trafficapp.db.DBManager;
import cn.fszt.trafficapp.domain.ActListData;
import cn.fszt.trafficapp.ui.fragment.MainFragment;
import cn.fszt.trafficapp.util.HttpUtil;
import cn.fszt.trafficapp.widget.PullDownView;
import cn.fszt.trafficapp.widget.ScrollOverListView;
import cn.fszt.trafficapp.widget.PullDownView.OnPullDownListener;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.AdapterView;
import android.widget.ProgressBar;
import android.widget.AdapterView.OnItemClickListener;

/**
 * 音视频--视频
 * 
 * @author AeiouKong
 *
 */
public class VideoListActivity extends BaseBackActivity implements
		OnItemClickListener {

	private LayoutInflater inflater;
	private List<ActListData> arrays;
	private PullDownView pullDownView;
	private ScrollOverListView listView;
	private VideoListAdapter adapter;
	private String requestURL,uid;
	private String searchparam = "important"; // 判断筛选的文章类别all 全部文章,hdvote 有投票的文章,
										// hdactivity 有报名的文章
	private int startindex = 1;
	private int endindex = 15;
	private SharedPreferences sp;
	private DBManager mgr;
	private List<ActListData> sql_arrays;

	private ProgressBar pb_hd;
	private String contenttype; // 文章类型 "carlife" 车生活，"huodong" FUN互动

	private MenuItem item_hd_tonews, item_hd_vote, item_hd_baoming,
			item_hd_all, item_hd_general, item_hd_important;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_videolist);
		Intent intent_type = getIntent();
		contenttype = "huodong";

		initView();

//		sql_arrays = mgr.queryHuodongByContenttype(contenttype);
//		if (sql_arrays.size() == 0) { // 当数据库没有数据时，从网络更新并插入数据库
//			searchBySearchparam();
//		} else { // 查找数据库显示历史数据，然后再从网络更新并插入数据库
//			arrays = sql_arrays;
//			adapter = new ActListAdapter(arrays, inflater);
//			listView.setAdapter(adapter);
//			adapter.notifyDataSetChanged();
//			pullDownView.notifyDidDataLoad(false);
//			initArrays(new Handler() {
//				@Override
//				public void handleMessage(Message msg) {
//
//					arrays = (List<ActListData>) msg.obj;
//					if (arrays == null) { // 网络更新失败时，还是显示数据库的历史数据
//						arrays = sql_arrays;
//						pb_hd.setVisibility(View.GONE);
//					} else { // 网络更新数据成功，更新数据库的数据
//						mgr.deleteHuodongByContenttype(contenttype);
//						mgr.addHuodong(arrays, contenttype);
//						pb_hd.setVisibility(View.GONE);
//					}
//					adapter = new ActListAdapter(arrays, inflater);
//					listView.setAdapter(adapter);
//					adapter.notifyDataSetChanged();
//					pullDownView.notifyDidDataLoad(false);
//				}
//			}, searchparam);
//		}

		searchBySearchparam();
		
//		pullDownView.setOnPullDownListener(new OnPullDownListener() {
//			// 下拉刷新
//			@Override
//			public void onRefresh() {
//				getRefresh(new Handler() {
//					@Override
//					public void handleMessage(Message msg) {
//
//						arrays = (List<ActListData>) msg.obj;
//						if (arrays == null) {
//							arrays = sql_arrays; // 下拉更新失败，还是显示数据库历史数据
//						} else { // 下拉更新数据成功，更新数据库的数据
//							mgr.deleteHuodongByContenttype(contenttype);
//							mgr.addHuodong(arrays, contenttype);
//						}
//						adapter.notifyDataSetChanged();
//						pullDownView.notifyDidRefresh(false);
//					}
//				}, searchparam);
//			}
//
//			// 上拉更多
//			@Override
//			public void onLoadMore() {
//				getLoadMore(new Handler() {
//					@Override
//					public void handleMessage(Message msg) {
//						if ((List<ActListData>) msg.obj != null) {
//							for (ActListData s : (List<ActListData>) msg.obj) {
//								arrays.add(s);
//							}
//						}
//						adapter.notifyDataSetChanged();
//						pullDownView
//								.notifyDidLoadMore((List<ActListData>) msg.obj == null);
//					}
//				}, searchparam);
//			}
//
//		});
//		if (contenttype != null && contenttype.equals("huodong")) {
//			getActionBar().setTitle(
//					getResources().getString(R.string.n_qingbao));
//		} else if (contenttype != null && contenttype.equals("carlife")) {
//			getActionBar().setTitle(getResources().getString(R.string.carlife));
//		}

//		getActionBar().setBackgroundDrawable(
//				new ColorDrawable(getResources().getColor(R.color.titlebar)));
//		getActionBar().setDisplayHomeAsUpEnabled(true);
//
//		getOverflowMenu();
	}


//	@Override
//	public boolean onCreateOptionsMenu(Menu menu) {
//
//		MenuInflater inflater = getMenuInflater();
//
//		inflater.inflate(R.menu.huodong, menu);
//
//		item_hd_tonews = menu.findItem(R.id.item_hd_tonews);
//		item_hd_vote = menu.findItem(R.id.item_hd_vote);
//		item_hd_baoming = menu.findItem(R.id.item_hd_baoming);
//		item_hd_all = menu.findItem(R.id.item_hd_all);
//		item_hd_general = menu.findItem(R.id.item_hd_general);
//		item_hd_important = menu.findItem(R.id.item_hd_important);
//
//		if (contenttype != null && "carlife".equals(contenttype)) {
//			item_hd_tonews.setVisible(false);
//			item_hd_vote.setVisible(false);
//			item_hd_baoming.setVisible(false);
//			item_hd_all.setVisible(false);
//			item_hd_general.setVisible(false);
//			item_hd_important.setVisible(false);
//		}
//		return true;
//	}

	private void initView() {
		sp = getSharedPreferences("USER", Context.MODE_PRIVATE);
		uid = sp.getString("uuid", null);
		mgr = new DBManager(this);
		if (contenttype != null && contenttype.equals("huodong")) {
			requestURL = getResources().getString(R.string.server_url)
					+ getResources().getString(R.string.huodong_url);
		} else if (contenttype != null && contenttype.equals("carlife")) {
			requestURL = getResources().getString(R.string.server_url)
					+ getResources().getString(R.string.carlife_url);
		}
		inflater = getLayoutInflater();
		pullDownView = (PullDownView) findViewById(R.id.pd_huodong);
		pullDownView.enableAutoFetchMore(true, 2);
		pullDownView.enableLoadMore(false);
		pullDownView.enablePullDown(false);
		listView = pullDownView.getListView();
		listView.setOnItemClickListener(this);

		pb_hd = (ProgressBar) findViewById(R.id.pb_huodong);
	}

	private void searchBySearchparam() {
		pb_hd.setVisibility(View.VISIBLE);
		startindex = 1;
		endindex = 15;
		initArrays(new Handler() {
			@Override
			public void handleMessage(Message msg) {
				arrays = (List<ActListData>) msg.obj;
				adapter = new VideoListAdapter(arrays, inflater);
				listView.setAdapter(adapter);
				adapter.notifyDataSetChanged();
				pullDownView.notifyDidDataLoad(false);
				pb_hd.setVisibility(View.GONE);
//				if (arrays != null) {
//					mgr.addHuodong(arrays, contenttype);
//					pb_hd.setVisibility(View.GONE);
//				} else {
//					pb_hd.setVisibility(View.GONE);
//				}
			}
		}, searchparam);
	}

	private List<ActListData> getDataFromNetwork(int start, int end,
			String searchparam) throws Exception {
		List<ActListData> lst = new ArrayList<ActListData>();

		String resultString = HttpUtil.GetStringFromUrl(requestURL
				+ "&startindex=" + start + "&endindex=" + end + "&searchparam="
				+ searchparam);
		if (resultString != null) {
			Type listType = new TypeToken<List<ActListData>>() {
			}.getType();
			Gson gson = new Gson();
			lst = gson.fromJson(resultString, listType);
			return lst;
		} else {
			return null;
		}
	}

	/**
	 * 初始化数据
	 * 
	 * @param handler
	 */
	private void initArrays(final Handler handler, final String searchparam) {
		new Thread(new Runnable() {
			List<ActListData> lst = new ArrayList<ActListData>();

			@Override
			public void run() {

				try {
					lst = getDataFromNetwork(startindex, endindex, searchparam);
				} catch (Exception e) {
					e.printStackTrace();
				}
				handler.obtainMessage(0, lst).sendToTarget();
			}
		}).start();
	}

	private void getRefresh(final Handler handler, final String searchparam) {
		new Thread(new Runnable() {
			List<ActListData> lst = new ArrayList<ActListData>();

			@Override
			public void run() {

				try {
					startindex = 1;
					endindex = 15;
					lst = getDataFromNetwork(startindex, endindex, searchparam);
				} catch (Exception e) {
					e.printStackTrace();
				}

				handler.obtainMessage(0, lst).sendToTarget();
			}
		}).start();
	}

	private void getLoadMore(final Handler handler, final String searchparam) {
		new Thread(new Runnable() {
			List<ActListData> lst = new ArrayList<ActListData>();

			@Override
			public void run() {
				// 每次上拉更新15条数据
				startindex += 15;
				endindex += 15;
				try {
					lst = getDataFromNetwork(startindex, endindex, searchparam);
				} catch (Exception e) {
					e.printStackTrace();
				}

				handler.obtainMessage(0, lst).sendToTarget();
			}
		}).start();
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View view, int position,
			long id) {
		Intent intent_item = new Intent(this, ActDetailActivity.class);
		intent_item.putExtra("hdinfoid", arrays.get(position).getHdinfoid());
		intent_item.putExtra("contenttype", contenttype);
		intent_item.putExtra("uid", uid);
		startActivity(intent_item);
	}

//	@Override
//	public boolean onKeyDown(int keyCode, KeyEvent event) {
//		if (keyCode == KeyEvent.KEYCODE_BACK
//				&& event.getAction() == KeyEvent.ACTION_DOWN) {
//			if (push != null && "true".equals(push)) {
//				Intent intent = new Intent(VideoListActivity.this,
//						MainFragment.class);
//				intent.putExtra("push", "true");
//				startActivity(intent);
//				finish();
//			} else {
//				finish();
//			}
//			return true;
//		}
//		return super.onKeyDown(keyCode, event);
//	}
}

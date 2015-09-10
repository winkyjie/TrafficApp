package cn.fszt.trafficapp.ui.activity;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.message.BasicNameValuePair;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import cn.fszt.trafficapp.R;
import cn.fszt.trafficapp.adapter.RadioCommentListAdapter;
import cn.fszt.trafficapp.domain.AudioListData;
import cn.fszt.trafficapp.domain.RadioCommentListData;
import cn.fszt.trafficapp.util.AppClickCount;
import cn.fszt.trafficapp.util.Constant;
import cn.fszt.trafficapp.util.HttpUtil;
import cn.fszt.trafficapp.widget.PullDownView;
import cn.fszt.trafficapp.widget.PullDownView.OnPullDownListener;
import cn.fszt.trafficapp.widget.ScrollOverListView;
import cn.fszt.trafficapp.widget.photoview.DisplayImageOptionsUtil;

/**
 * 节目互动每个节目评论列表界面
 * 
 * @author AeiouKong
 *
 */
public class RadioCommentListActivity extends BaseBackActivity implements OnItemClickListener, OnClickListener {

	private String id, imagebackground, image, djname, uid, mobile; // 节目名称，用于评论节目
	private String hdreplaytypeid; // 节目ID
	private TextView tv_reh_pgname, tv_reh_djname;
	private ImageView iv_reh_bg, iv_reh_head, iv_input, iv_circle;
	private Button btn_reh_secret, btn_reh_essence;
	private String delmchat_url, reportychat_url, api_url;
	private Intent intent;

	private int startindex = 1;
	private int endindex = 15;

	private LayoutInflater inflater, inflaterhead;
	private List<RadioCommentListData> arrays;
	private PullDownView pullDownView;
	private ScrollOverListView listView;
	private RadioCommentListAdapter adapter;

	private ImageLoader imageLoader;
	private DisplayImageOptions options, options_head;
	int densityDpi, position;
	private ProgressBar pb_replayhud;
	private SharedPreferences sp_user;
	private SharedPreferences sp_push;
	private Set<String> sconnected_uids;
	private Set<String> programname;

	private String allOrBest = "0"; // 获取节目回帖，全部或精华 0全部，1精华

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		DisplayMetrics metric = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metric);
		densityDpi = metric.densityDpi;
		setContentView(R.layout.activity_replayhudong);
		getActionBar().setTitle(getResources().getString(R.string.item_rehudong));
		getActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.titlebar)));
		getActionBar().setDisplayHomeAsUpEnabled(true);

		imageLoader = ImageLoader.getInstance();
		options_head = DisplayImageOptionsUtil.getOptions(R.drawable.ic_empty, R.drawable.ic_error,
				R.drawable.default_image, new RoundedBitmapDisplayer(15));
		options = DisplayImageOptionsUtil.getOptions(R.drawable.ic_empty, R.drawable.ic_error,
				R.drawable.default_image);

		initView();

		pullDownView.setOnPullDownListener(new OnPullDownListener() {
			// 下拉刷新
			@Override
			public void onRefresh() {
				startindex = 1;
				endindex = 15;
				initData();
				pullDownView.notifyDidRefresh(false);
			}

			// 上拉更多
			@Override
			public void onLoadMore() {
				getLoadMore(new Handler() {
					@Override
					public void handleMessage(Message msg) {
						if ((List<RadioCommentListData>) msg.obj != null) {
							for (RadioCommentListData s : (List<RadioCommentListData>) msg.obj) {
								arrays.add(s);
							}
						}
						adapter.notifyDataSetChanged();
						pullDownView.notifyDidLoadMore((List<RadioCommentListData>) msg.obj == null);
					}
				});
			}
		});

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		MenuInflater inflater = getMenuInflater();

		inflater.inflate(R.menu.replayhudong, menu);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			break;

		case R.id.item_reh_sentpinglun:
			Intent intent = new Intent(this, RadioCommentEditActivity.class);
			intent.putExtra("id", id);
			intent.putExtra("islive", "1"); // 0=直播，1=点播
			startActivityForResult(intent, Constant.PINGLUN_REQUESTCODE);
			break;
		}
		return true;
	}

	private void initView() {
		pb_replayhud = (ProgressBar) findViewById(R.id.pb_replayhud);
		intent = getIntent();
		id = intent.getStringExtra("pgname");
		djname = intent.getStringExtra("djname");
		hdreplaytypeid = intent.getStringExtra("hdreplaytypeid");
		image = intent.getStringExtra("image");
		imagebackground = intent.getStringExtra("imagebackground");
		delmchat_url = getString(R.string.server_url) + getString(R.string.hud_delmchat_url) + "&hdcommentid=";
		reportychat_url = getString(R.string.server_url) + getString(R.string.reportradio_url) + "&hdcommentid=";
		api_url = getString(R.string.api_url);

		sp_user = getSharedPreferences("USER", Context.MODE_PRIVATE);
		uid = sp_user.getString("uuid", null);
		mobile = sp_user.getString("mobile", null);

		inflater = getLayoutInflater();
		pullDownView = (PullDownView) findViewById(R.id.pd_reh);
		pullDownView.enableAutoFetchMore(true, 2);
		pullDownView.enableLoadMore(true);
		pullDownView.enablePullDown(true);
		listView = pullDownView.getListView();
		listView.setOnItemClickListener(this);
		inflaterhead = getLayoutInflater();
		View headview = inflaterhead.inflate(R.layout.item_radiocommentheadview, null);
		tv_reh_pgname = (TextView) headview.findViewById(R.id.tv_reh_pgname);
		tv_reh_pgname.setText(id);
		tv_reh_djname = (TextView) headview.findViewById(R.id.tv_reh_djname);
		tv_reh_djname.setText(djname);
		iv_reh_head = (ImageView) headview.findViewById(R.id.iv_reh_head);
		imageLoader.displayImage(image, iv_reh_head, options_head, null);
		iv_reh_bg = (ImageView) headview.findViewById(R.id.iv_reh_bg);
		imageLoader.displayImage(imagebackground, iv_reh_bg, options, null);
		btn_reh_secret = (Button) headview.findViewById(R.id.btn_reh_secret);
		btn_reh_secret.setText(getResources().getString(R.string.secret));
		btn_reh_secret.setOnClickListener(this);
		btn_reh_essence = (Button) headview.findViewById(R.id.btn_reh_essence);
		btn_reh_essence.setText(getResources().getString(R.string.item_essence));
		btn_reh_essence.setOnClickListener(this);
		iv_circle = (ImageView) headview.findViewById(R.id.iv_circle); // 推送消息提醒标记(红点)

		listView.addHeaderView(headview);
		iv_input = (ImageView) findViewById(R.id.iv_input);
		iv_input.setOnClickListener(this);
	}

	/**
	 * 从网络获取数据
	 * 
	 * @return
	 * @throws IOException
	 * @throws ClientProtocolException
	 * @throws Exception
	 */
	private List<RadioCommentListData> getDataFromNetwork(int start, int end) throws Exception {
		List<RadioCommentListData> lst = new ArrayList<RadioCommentListData>();

		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("action", "GetBestHdCommentList"));
		params.add(new BasicNameValuePair("connected_uid", uid));
		params.add(new BasicNameValuePair("hdmenuid", id));
		params.add(new BasicNameValuePair("islive", "1"));
		params.add(new BasicNameValuePair("bestOrAll", allOrBest));
		params.add(new BasicNameValuePair("startindex", start + ""));
		params.add(new BasicNameValuePair("endindex", end + ""));
		String result = HttpUtil.PostStringFromUrl(api_url, params);
		if (result != null) {
			Type listType = new TypeToken<List<RadioCommentListData>>() {
			}.getType();
			Gson gson = new Gson();
			lst = gson.fromJson(result, listType);
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

	// private void getRefresh(final Handler handler) {
	// new Thread(new Runnable() {
	// List<RadioCommentListData> lst = new ArrayList<RadioCommentListData>();
	//
	// @Override
	// public void run() {
	// try {
	// startindex = 1;
	// endindex = 15;
	// lst = getDataFromNetwork(startindex, endindex);
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// handler.obtainMessage(0, lst).sendToTarget();
	// }
	// }).start();
	// }

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

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (requestCode == Constant.LOGIN_REQUESTCODE && resultCode == RESULT_OK) {
			uid = data.getStringExtra("uid");
		}
		// 评论提交成功，在对话框增加自己的评论内容
		if (requestCode == Constant.PINGLUN_REQUESTCODE && resultCode == RESULT_OK) {
			initData();
		}

		if (requestCode == Constant.REPLY_REQUESTCODE && resultCode == RESULT_OK) {
			int replynum = data.getIntExtra("replynum", 0);
			if (replynum != 0) {
				arrays.get(position).setReplycount(data.getIntExtra("replynum", 0) + "");
			} else {
				arrays.get(position).setReplycount(arrays.get(position).getReplycount());
			}
			String goodnum = data.getStringExtra("goodnum");
			if (goodnum != null) {
				arrays.get(position).setLikecount(data.getStringExtra("goodnum"));
			} else {
				arrays.get(position).setLikecount(arrays.get(position).getLikecount());
			}
			adapter = new RadioCommentListAdapter(RadioCommentListActivity.this, arrays, inflater, delmchat_url,
					reportychat_url, uid, densityDpi, id);
			listView.setAdapter(adapter);
			listView.setSelection(position);
			adapter.notifyDataSetChanged();
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	protected void onResume() {
		super.onResume();
		initData();
	}

	/**
	 * 加载评论数据
	 */
	private void initData() {
		sp_push = getSharedPreferences("PUSH", Context.MODE_PRIVATE);
		programname = sp_push.getStringSet("programname", null);
		sconnected_uids = sp_push.getStringSet("sconnected_uid", null);
		//设置默认红点隐藏，私信新的才会显示
		iv_circle.setVisibility(View.GONE);
		if (sconnected_uids != null && programname != null) {
			Iterator<String> it = programname.iterator();
			while (it.hasNext()) {
				if (id.equals(it.next())) {
					if (sconnected_uids.size() > 0) {
						iv_circle.setVisibility(View.VISIBLE);
					} 
				}
			}
		}
		initArrays(new Handler() {
			@Override
			public void handleMessage(Message msg) {
				arrays = (List<RadioCommentListData>) msg.obj;
				adapter = new RadioCommentListAdapter(RadioCommentListActivity.this, arrays, inflater, delmchat_url,
						reportychat_url, uid, densityDpi, id);
				listView.setAdapter(adapter);
				pb_replayhud.setVisibility(View.GONE);
				adapter.notifyDataSetChanged();
				pullDownView.notifyDidDataLoad(false);
			}
		});
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		// TODO Auto-generated method stub
		if (arrays != null) {
			if (position == 0) {
			} else {
				Intent intent = new Intent(RadioCommentListActivity.this, RadioCommentDetailActivity.class);
				Bundle mBundle = new Bundle();
				mBundle.putSerializable("RadioCommentListData", arrays.get(position - 1));
				intent.putExtras(mBundle);
				this.position = position - 1;
				startActivityForResult(intent, Constant.REPLY_REQUESTCODE);
			}
		}
	}

	@Override
	protected void onRestart() {
		super.onRestart();
		uid = sp_user.getString("uuid", null);
		mobile = sp_user.getString("mobile", null);
		if (mobile == null) {
			mobile = "";
		}
	}

	public boolean isLogIn() {
		if (uid != null && mobile != null && !"".equals(mobile)) {
			return true;
		} else if (uid == null) {
			Intent intent = new Intent(this, StLoginActivity.class);
			startActivityForResult(intent, Constant.LOGIN_REQUESTCODE);
		} else if (uid != null && "".equals(mobile)) {
			Toast.makeText(this, "请先绑定手机号", Toast.LENGTH_LONG).show();
		}
		return false;
	}

	@Override
	public void onClick(View v) {
		if (v == iv_input) {
			if (isLogIn()) {
				Intent intent = new Intent(RadioCommentListActivity.this, RadioCommentEditActivity.class);
				intent.putExtra("id", id);
				intent.putExtra("islive", "1"); // 0=直播，1=点播
				intent.putExtra("interface_url", getResources().getString(R.string.hud_url));
				startActivityForResult(intent, Constant.PINGLUN_REQUESTCODE);
			}
		} else if (v == btn_reh_secret) {
			if (isLogIn()) {
				// 跳转私信
				new AppClickCount(this, "节目互动私信");
				Intent intent = new Intent(RadioCommentListActivity.this, RadioSecretListActivity.class);
				intent.putExtra("hdreplaytypeid", hdreplaytypeid);
				intent.putExtra("programname", id);
				this.startActivity(intent);
			}
		} else if (v == btn_reh_essence) {
			if (btn_reh_essence.getText().toString().equals(getResources().getString(R.string.item_essence))) {
				new AppClickCount(this, "节目互动精华帖");
				allOrBest = "1";
				startindex = 1;
				endindex = 15;
				btn_reh_essence.setText(getResources().getString(R.string.item_all));
				pb_replayhud.setVisibility(View.VISIBLE);
				initData();
			} else {
				new AppClickCount(this, "节目互动全部贴");
				allOrBest = "0";
				startindex = 1;
				endindex = 15;
				btn_reh_essence.setText(getResources().getString(R.string.item_essence));
				pb_replayhud.setVisibility(View.VISIBLE);
				initData();
			}
		}
	}

}

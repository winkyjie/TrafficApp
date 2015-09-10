package cn.fszt.trafficapp.ui.activity;

import java.io.IOException;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.message.BasicNameValuePair;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.SpannableString;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;
import cn.fszt.trafficapp.R;
import cn.fszt.trafficapp.adapter.RadioSecretDetailListAdapter;
import cn.fszt.trafficapp.domain.RadioCommentListData;
import cn.fszt.trafficapp.domain.RadioSecretDetailData;
import cn.fszt.trafficapp.ui.activity.RadioSecretListActivity.UpOrDown;
import cn.fszt.trafficapp.util.Constant;
import cn.fszt.trafficapp.util.HttpUtil;
import cn.fszt.trafficapp.util.UploadUtil;
import cn.fszt.trafficapp.util.UploadUtil.OnUploadProcessListener;
import cn.fszt.trafficapp.widget.PullDownView;
import cn.fszt.trafficapp.widget.PullDownView.OnPullDownListener;
import cn.fszt.trafficapp.widget.expression.ExpressionUtil;

/**
 * 私信聊天
 */
public class RadioSecretDetailActivity extends BaseBackActivity implements OnClickListener {
	private RadioCommentListData data;
	private int densityDpi;
	private PullDownView secretListView;
	private ListView listView;
	private List<RadioSecretDetailData> arrays;
	private RadioSecretDetailListAdapter adapter;
	private String api_url, delhdsecretletter_url;
	private String uid, hdreplaytypeid;
	private ProgressBar secretlet_pb;
	private SharedPreferences sp;
	private ImageView secretInput;

	private int startindex = 1, endindex = 15;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_secretletterdetail);

		data = (RadioCommentListData) getIntent().getSerializableExtra("RadioCommentListData");
		hdreplaytypeid = getIntent().getStringExtra("hdreplaytypeid");

		getActionBar().setTitle(data.getNickname());
		getActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.titlebar)));
		getActionBar().setDisplayHomeAsUpEnabled(true);

		initView();
		showExpression();
		subCreatetime();
		// 执行加载评论内容
		initData();
	}

	private void initView() {
		api_url = getString(R.string.api_url);
		delhdsecretletter_url = getResources().getString(R.string.delhdsecretletter_url); // 删除私信

		secretListView = (PullDownView) findViewById(R.id.secretlet_list);
		secretListView.enableAutoFetchMore(true, 2);
		secretListView.enableLoadMore(true);
		secretListView.enablePullDown(true);
		secretListView.setOnPullDownListener(new UpOrDown());

		listView = secretListView.getListView();

		secretInput = (ImageView) findViewById(R.id.secretlet_input);
		secretInput.setOnClickListener(this);

		sp = getSharedPreferences("USER", Context.MODE_PRIVATE);
		uid = sp.getString("uuid", null);

		secretlet_pb = (ProgressBar) findViewById(R.id.secretlet_pb);
	}

	/**
	 * 格式化日期
	 */
	private void subCreatetime() {
		String date = data.getCreatedate();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			Date ddate = sdf.parse(date);
			SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd HH:mm");
			date = dateFormat.format(ddate);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		// tv_bgdetail_createtime.setText(date);
	}

	/**
	 * 内容中显示表情
	 */
	private void showExpression() {
		String zhengze = getString(R.string.expression_matches); // 正则表达式，用来判断消息内是否有表情
		String content = data.getTextcontent();
		SpannableString spannableString = ExpressionUtil.getExpressionString(this, content, zhengze, densityDpi);
		// tv_bgdetail_content.setText(spannableString);
	}

	public void initData(){
		secretlet_pb.setVisibility(View.VISIBLE);
		initArrays(new Handler() {
			@Override
			public void handleMessage(Message msg) {
				arrays = (List<RadioSecretDetailData>) msg.obj;
				adapter = new RadioSecretDetailListAdapter(RadioSecretDetailActivity.this, arrays, densityDpi, uid,
						api_url, delhdsecretletter_url);
				listView.setAdapter(adapter);
				
				secretlet_pb.setVisibility(View.GONE);
				adapter.notifyDataSetChanged();
				secretListView.notifyDidDataLoad(false);
			}
		});
	}
	
	/**
	 * 初始化数据
	 * 
	 * @param handler
	 */
	private void initArrays(final Handler handler) {
		new Thread(new Runnable() {
			List<RadioSecretDetailData> lst = new ArrayList<RadioSecretDetailData>();

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

	/**
	 * 从网络获取数据
	 * 
	 * @return
	 * @throws IOException
	 * @throws ClientProtocolException
	 * @throws Exception
	 */
	private List<RadioSecretDetailData> getDataFromNetwork(int start, int end) throws Exception {
		List<RadioSecretDetailData> lst = new ArrayList<RadioSecretDetailData>();
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("action", "GetHdsecretletterChatRecords"));
		params.add(new BasicNameValuePair("currentConnected_uid", uid));
		params.add(new BasicNameValuePair("noncurrentConnected_uid", data.getConnected_uid()));
		params.add(new BasicNameValuePair("hdreplaytypeid", hdreplaytypeid));
		params.add(new BasicNameValuePair("startindex", start + ""));
		params.add(new BasicNameValuePair("endindex", end + ""));
		String resultString = HttpUtil.PostStringFromUrl(api_url, params);
		if (resultString != null) {
			Type listType = new TypeToken<List<RadioSecretDetailData>>() {
			}.getType();
			Gson gson = new Gson();
			lst = gson.fromJson(resultString, listType);
			return lst;
		} else {
			return null;
		}
	}

	/**
	 * ActionBar监听
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			break;
		}
		return true;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.secretlet_input:
			Intent intent = new Intent(RadioSecretDetailActivity.this, CommonEditActivity.class);
			intent.putExtra("interface_url", getResources().getString(R.string.inserthdsecretletter_url));
			intent.putExtra("sconnected_uid", uid);
			intent.putExtra("rconnected_uid", data.getConnected_uid());
			intent.putExtra("hdreplaytypeid", hdreplaytypeid);

			startActivityForResult(intent, Constant.PINGLUN_REQUESTCODE);
			break;
		}
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
					if ((List<RadioSecretDetailData>) msg.obj != null) {
						for (RadioSecretDetailData s : (List<RadioSecretDetailData>) msg.obj) {
							arrays.add(s);
						}
					}
					adapter.notifyDataSetChanged();
					secretListView.notifyDidLoadMore((List<RadioSecretDetailData>) msg.obj == null);
				}
			});
		}
	}

	private void getLoadMore(final Handler handler) {
		new Thread(new Runnable() {
			List<RadioSecretDetailData> lst = new ArrayList<RadioSecretDetailData>();

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
	protected void onDestroy() {

		super.onDestroy();
	}
}

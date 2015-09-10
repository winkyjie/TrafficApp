package cn.fszt.trafficapp.ui.activity;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import cn.fszt.trafficapp.R;
import cn.fszt.trafficapp.adapter.VoteSurveyListAdapter;
import cn.fszt.trafficapp.domain.SurveyListData;
import cn.fszt.trafficapp.util.HttpUtil;
import cn.fszt.trafficapp.widget.PullDownView;
import cn.fszt.trafficapp.widget.ScrollOverListView;
import cn.fszt.trafficapp.widget.msg.AppMsg;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * 复合投票调查列表
 * 
 * @author AeiouKong
 *
 */
public class VoteSurveyListActivity extends Activity implements OnItemClickListener {

	private static final int GETSURVEY_SUCCEED = 1;
	private static final int GETSURVEY_FAILED = 3;
	private TextView tv_surveytitle;
	private String getsurveylist_url, hdsurveyid, title, hdsurveyvoteid,
			getsurveyoption_url, maxcount;
	private List<SurveyListData> arrays = new ArrayList<SurveyListData>();
	private ArrayList<String> optioncontents, hdsurveyoptionids; // 复合调查
	private ProgressBar pb_survey;

	private LayoutInflater inflater;
	private PullDownView pullDownView;
	private ScrollOverListView listView;
	private VoteSurveyListAdapter adapter;
	private Intent intent_survey;

	AppMsg.Style style = AppMsg.STYLE_INFO;
	AppMsg appMsg;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_surveylist);

		initView();

		new Thread() {
			public void run() {
				getDataFromNetwork();
			}
		}.start();

		getActionBar().setTitle(getResources().getString(R.string.vote));
		getActionBar().setBackgroundDrawable(
				new ColorDrawable(getResources().getColor(R.color.titlebar)));
		getActionBar().setDisplayHomeAsUpEnabled(true);
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

	private void initView() {
		Intent intent = getIntent();
		pb_survey = (ProgressBar) findViewById(R.id.pb_survey);
		hdsurveyid = intent.getStringExtra("hdsurveyid");
		getsurveylist_url = getString(R.string.server_url)
				+ getString(R.string.getsurveylist_url);
		getsurveyoption_url = getString(R.string.server_url)
				+ getString(R.string.getsurveyoption_url);
		tv_surveytitle = (TextView) findViewById(R.id.tv_surveytitle);

		inflater = getLayoutInflater();
		pullDownView = (PullDownView) findViewById(R.id.pdv_survey);
		pullDownView.enableAutoFetchMore(true, 2);
		pullDownView.enableLoadMore(false);
		pullDownView.enablePullDown(false);
		listView = pullDownView.getListView();
		listView.setOnItemClickListener(this);
		listView.setCacheColorHint(Color.TRANSPARENT);
		listView.setBackgroundColor(Color.WHITE);
		listView.setDivider(getResources().getDrawable(
				R.color.list_view_divider));
		listView.setDividerHeight(1);
	}

	private void getDataFromNetwork() {
		JSONObject jsonObject;
		String result = HttpUtil.GetStringFromUrl(getsurveylist_url
				+ "&hdsurveyid=" + hdsurveyid);
		if (result != null) {
			try {
				jsonObject = new JSONObject(result);
				title = jsonObject.getString("title");
				String hdsurveyvote = jsonObject.getJSONArray("hdsurveyvote")
						.toString();
				if (hdsurveyvote != null) {
					Type listType = new TypeToken<List<SurveyListData>>() {
					}.getType();
					Gson gson = new Gson();
					arrays = gson.fromJson(hdsurveyvote, listType);
				}
				handler.sendEmptyMessage(2);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		} else {
			handler.sendEmptyMessage(GETSURVEY_FAILED);
		}
	}

	Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			if (msg.what == 2) {
				tv_surveytitle.setText(title);
				pb_survey.setVisibility(View.GONE);
				adapter = new VoteSurveyListAdapter(arrays, inflater);
				listView.setAdapter(adapter);
				adapter.notifyDataSetChanged();
				pullDownView.notifyDidDataLoad(false);
			} else if (msg.what == GETSURVEY_SUCCEED) {
				intent_survey.putStringArrayListExtra("hdvoteoptions",
						optioncontents);
				intent_survey.putStringArrayListExtra("hdvoteoptionids",
						hdsurveyoptionids);
				intent_survey.putExtra("hdvote_maxcount", maxcount);
				startActivity(intent_survey);
				pb_survey.setVisibility(View.GONE);
			} else if (msg.what == GETSURVEY_FAILED) {
				pb_survey.setVisibility(View.GONE);
				showMsg(getString(R.string.response_fail), "info", "bottom");
			}
		}
	};

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		pb_survey.setVisibility(View.VISIBLE);
		String hdsurveyvote_votetype = arrays.get(position)
				.getHdsurveyvote_votetype();
		if (hdsurveyvote_votetype != null && "0".equals(hdsurveyvote_votetype)) {
			intent_survey = new Intent(this, VoteSingleChoiceActivity.class);
		} else {
			intent_survey = new Intent(this, VoteMultipleChoiceActivity.class);
		}
		intent_survey.putExtra("votetype", "survey");
		hdsurveyvoteid = arrays.get(position).getHdsurveyvote_hdsurveyvoteid();
		intent_survey.putExtra("hdsurveyvoteid", hdsurveyvoteid);
		intent_survey.putExtra("hdvote_title", arrays.get(position)
				.getHdsurveyvote_votetitle());
		// intent_survey.putExtra("hdsurveyvote_votetype",
		// arrays.get(position).getHdsurveyvote_votetype());
		new Thread() {
			public void run() {
				getSurveyFromNetWork();
			}
		}.start();
	}

	private void getSurveyFromNetWork() {
		String result = null;
		result = HttpUtil.GetStringFromUrl(getsurveyoption_url
				+ "&hdsurveyvoteid=" + hdsurveyvoteid);
		// System.out.println("resultsurvey==="+result);
		if (result != null) {
			optioncontents = new ArrayList<String>();
			hdsurveyoptionids = new ArrayList<String>();
			try {
				JSONArray array = new JSONArray(result);
				for (int i = 0; i < array.length(); i++) {
					String optioncontent = array.getJSONObject(i).getString(
							"optioncontent");
					String hdsurveyoptionid = array.getJSONObject(i).getString(
							"hdsurveyoptionid");
					String maxcount = array.getJSONObject(i).getString(
							"maxcount");
					this.maxcount = maxcount;
					optioncontents.add(optioncontent);
					hdsurveyoptionids.add(hdsurveyoptionid);
				}
				handler.sendEmptyMessage(GETSURVEY_SUCCEED);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		} else {
			handler.sendEmptyMessage(GETSURVEY_FAILED);
		}
	}

	private void showMsg(String msg, String styletype, String gravity) {

		if (styletype.equals("alert")) {
			style = AppMsg.STYLE_ALERT;
		} else if (styletype.equals("info")) {
			style = AppMsg.STYLE_INFO;
		}

		appMsg = AppMsg.makeText(VoteSurveyListActivity.this, msg, style);

		if (gravity.equals("bottom")) {
			appMsg.setLayoutGravity(Gravity.BOTTOM);
		} else if (gravity.equals("top")) {
		}
		appMsg.show();
	}
}

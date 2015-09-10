package cn.fszt.trafficapp.ui.activity;

import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;

import org.apache.http.util.EncodingUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ProgressBar;
import cn.fszt.trafficapp.R;
import cn.fszt.trafficapp.util.HttpUtil;
import cn.fszt.trafficapp.widget.bargraph.BarBean;

public class VoteResultActivity extends Activity {

	private String mHtmlTemplate = "";
	private WebView mWebView;
	private String request_url, getsurvey_url;
	private String hdvoteid, hdsurveyvoteid, hdvote_title;
	private ArrayList<BarBean> bbs;
	private ProgressBar pb_voteresult;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_voteresult);

		initView();
		initData();

		initArrays(new Handler() {
			@Override
			public void handleMessage(Message msg) {
				String content = "[";
				String count = "[";
				int sum = 0;
				String height = getH() * 1.5 + "";
				String isshow = "true";
				bbs = (ArrayList<BarBean>) msg.obj;
				if (bbs == null) {
				} else {
					for (int i = 0; i < bbs.size(); i++) {
						content += "'" + bbs.get(i).getOptioncontent() + "',";
						count += bbs.get(i).getOptioncount() + ",";
						isshow = bbs.get(i).getIsshow();
						sum += Integer.parseInt(bbs.get(i).getOptioncount());
					}
					content = content.substring(0, content.length() - 1) + "]";
					count = count.substring(0, count.length() - 1) + "]";
					content = content.replace("\r\n", " ");
					if ("0".equals(isshow)) {
						isshow = "false";
					} else if ("1".equals(isshow)) {
						isshow = "true";
					}
					if (bbs.size() <= 5) {
						height = getH() / 1.5 + "";
					} else if (5 < bbs.size() && bbs.size() < 10) {
						height = getH() + "";
					} else {
						height = getH() * 1.5 + "";
					}
					showCharts(content, count, sum, hdvote_title, height,
							isshow);

				}
			}
		});

		getActionBar().setTitle("投票结果");
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
		request_url = getResources().getString(R.string.server_url)
				+ getResources().getString(R.string.getvote_url);
		getsurvey_url = getResources().getString(R.string.server_url)
				+ getResources().getString(R.string.getsurvey_url);
		Intent intent = getIntent();
		hdvoteid = intent.getStringExtra("hdvoteid");
		hdvote_title = intent.getStringExtra("hdvote_title");
		hdsurveyvoteid = intent.getStringExtra("hdsurveyvoteid");
		pb_voteresult = (ProgressBar) findViewById(R.id.pb_voteresult);
		mWebView = (WebView) findViewById(R.id.webView);
		WebSettings webSettings = mWebView.getSettings();
		webSettings.setBuiltInZoomControls(true);
		webSettings.setJavaScriptEnabled(true);
		webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
	}

	private void initData() {
		// 读取初始模版信息
		try {
			InputStream in = getResources().getAssets().open("quarter.html"); // 从Assets中的文件获取输入流
			int length = in.available(); // 获取文件的字节数
			byte[] buffer = new byte[length]; // 创建byte数组
			in.read(buffer); // 将文件中的数据读取到byte数组中
			mHtmlTemplate = EncodingUtils.getString(buffer, "UTF-8"); // 将byte数组转换成指定格式的字符串
		} catch (Exception e) {
			e.printStackTrace(); // 捕获异常并打印
		}
	}

	private void showCharts(String categories, String data, int sum,
			String title, String height, String isshow) {
		String content = mHtmlTemplate;
		String seriesdata;
		content = content.replaceAll("\\$w", getW() + "");
		content = content.replaceAll("\\$h", height);
		content = content.replaceAll("\\$showLabels", isshow);
		content = content.replaceAll("\\$title1", title);
		content = content.replaceAll("\\$xCategories", categories);
		if ("true".equals(isshow)) {
			seriesdata = "[{'name':'总数:" + sum + "','data':" + data + "}]";
		} else {
			seriesdata = "[{'name':'投票结果','data':" + data + "}]";
		}
		content = content.replaceAll("\\$series1", seriesdata);

//		 System.out.println("====" + content);

		mWebView.loadDataWithBaseURL("file:///android_asset", content,
				"text/html", "UTF-8", null);
		pb_voteresult.setVisibility(View.GONE);
	}

	private ArrayList<BarBean> getDataFromNetwork() throws Exception {
		ArrayList<BarBean> lst = new ArrayList<BarBean>();
		String resultString = null;
		if (hdsurveyvoteid == null && hdvoteid != null) {
			resultString = HttpUtil.GetStringFromUrl(request_url + "&hdvoteid="
					+ hdvoteid);
//			System.out.println("resultString=="+resultString);
		} else if (hdsurveyvoteid != null && hdvoteid == null) {
			resultString = HttpUtil.GetStringFromUrl(getsurvey_url
					+ "&hdsurveyvoteid=" + hdsurveyvoteid);
		}

		if (resultString != null) {
			Type listType = new TypeToken<ArrayList<BarBean>>() {
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
	private void initArrays(final Handler handler) {
		new Thread(new Runnable() {
			ArrayList<BarBean> lst = new ArrayList<BarBean>();

			@Override
			public void run() {

				try {
					lst = getDataFromNetwork();
				} catch (Exception e) {
					e.printStackTrace();
				}
				handler.obtainMessage(0, lst).sendToTarget();
			}
		}).start();
	}

	public int getW() {
		int w = getResources().getDisplayMetrics().widthPixels;
		return px2dip(w);
	}

	public int getH() {
		int h = getResources().getDisplayMetrics().heightPixels;
		return px2dip(h);
	}

	public int px2dip(float pxValue) {
		final float scale = getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}

}
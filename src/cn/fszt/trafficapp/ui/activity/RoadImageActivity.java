package cn.fszt.trafficapp.ui.activity;

import org.json.JSONException;
import org.json.JSONObject;

import com.umeng.analytics.MobclickAgent;

import cn.fszt.trafficapp.R;
import cn.fszt.trafficapp.util.HttpUtil;
import cn.fszt.trafficapp.widget.msg.AppMsg;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

/**
 * 查路况--路面快照
 * 
 * @author AeiouKong
 *
 */
public class RoadImageActivity extends Activity {

	private static final int SUCCEED = 0;
	private static final int FAILED = 1;
	private WebView roadinfoimage;
	private ProgressBar pb_roadinfoimage;
	AppMsg.Style style = AppMsg.STYLE_INFO;
	AppMsg appMsg;
	private String getroadinfoimage_url;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.webview_roadinfoimage);
		initView();
	}

	private void initView() {
		getroadinfoimage_url = getString(R.string.server_url)
				+ getString(R.string.getroadinfoimage_url);
		pb_roadinfoimage = (ProgressBar) findViewById(R.id.pb_roadinfoimage);
		roadinfoimage = (WebView) findViewById(R.id.wv_roadinfoimage);
		roadinfoimage.getSettings().setJavaScriptEnabled(true);
		roadinfoimage.setWebViewClient(new MyWebViewClient());
		// 获取url
		new Thread() {
			public void run() {
				String url = geturl();
				if (url != null) {
					handler.obtainMessage(SUCCEED, url).sendToTarget();
				} else {
					handler.sendEmptyMessage(FAILED);
				}
			}
		}.start();

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

	Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case SUCCEED:
				roadinfoimage.loadUrl((String) msg.obj);
				break;
			case FAILED:
				pb_roadinfoimage.setVisibility(View.GONE);
				showMsg(getResources().getString(R.string.response_fail),
						"info", "bottom");
				break;
			}
		}
	};

	class MyWebViewClient extends WebViewClient {

		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			view.loadUrl(url);
			return true;
		}

		@Override
		public void onPageFinished(WebView view, String url) {
			pb_roadinfoimage.setVisibility(View.GONE);
		}

		@Override
		public void onReceivedError(WebView view, int errorCode,
				String description, String failingUrl) {
			showMsg(getResources().getString(R.string.response_fail), "info",
					"bottom");
		}
	}

	private void showMsg(String msg, String styletype, String gravity) {

		if (styletype.equals("alert")) {
			style = AppMsg.STYLE_ALERT;
		} else if (styletype.equals("info")) {
			style = AppMsg.STYLE_INFO;
		}

		appMsg = AppMsg.makeText(RoadImageActivity.this, msg, style);

		if (gravity.equals("bottom")) {
			appMsg.setLayoutGravity(Gravity.BOTTOM);
		} else if (gravity.equals("top")) {
		}
		appMsg.show();
	}

	// 从服务端获取路面快照url
	private String geturl() {
		String lmkzpath = null;
		String result = HttpUtil.GetStringFromUrl(getroadinfoimage_url);
		if (result != null) {
			try {
				JSONObject jsonObject = new JSONObject(result);
				lmkzpath = jsonObject.getString("lmkzpath");
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return lmkzpath;
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

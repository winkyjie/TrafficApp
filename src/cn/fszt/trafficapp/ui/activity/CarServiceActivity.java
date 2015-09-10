package cn.fszt.trafficapp.ui.activity;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import cn.fszt.trafficapp.R;
import cn.fszt.trafficapp.util.HttpUtil;
import cn.fszt.trafficapp.widget.msg.AppMsg;

/**
 * 车主服务
 * 
 * @author AeiouKong
 *
 */
public class CarServiceActivity extends BaseBackActivity {

	private static final int SUCCEED = 0;
	private static final int FAILED = 1;
	private WebView carframe;
	private ProgressBar pb_carframe;
	AppMsg.Style style = AppMsg.STYLE_INFO;
	AppMsg appMsg;
	private String getcarframe_url, uid;
	private SharedPreferences sp;
	private MenuItem item_bindlicenceplate;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.webview_carframe);

		initView();

		getActionBar().setTitle(getResources().getString(R.string.n_weizhang));
		getActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.titlebar)));
		getActionBar().setDisplayHomeAsUpEnabled(true);
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.carservice_licenseplate, menu);
		item_bindlicenceplate = menu.findItem(R.id.item_bindlicenceplate);
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			break;
		case R.id.item_bindlicenceplate:
			Intent intent = new Intent(this, SpLicencePlateActivity.class);
			this.startActivity(intent);
			break;
		}
		return true;
	}

	private void initView() {
		sp = getSharedPreferences("USER", Context.MODE_PRIVATE);
		uid = sp.getString("uuid", null);
		getcarframe_url = getString(R.string.server_url) + getString(R.string.getcarframe_url) + "&connected_uid="
				+ uid;
		pb_carframe = (ProgressBar) findViewById(R.id.pb_carframe);
		carframe = (WebView) findViewById(R.id.wv_carframe);
		carframe.getSettings().setJavaScriptEnabled(true);
		carframe.setWebViewClient(new MyWebViewClient());
		carframe.setWebChromeClient(m_chromeClient);
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

	Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case SUCCEED:
				carframe.loadUrl((String) msg.obj);
				break;
			case FAILED:
				pb_carframe.setVisibility(View.GONE);
				showMsg(getResources().getString(R.string.response_fail), "info", "bottom");
				break;
			}
		}
	};

	private WebChromeClient m_chromeClient = new WebChromeClient() {
		@Override
		public void onShowCustomView(View view, CustomViewCallback callback) {
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
			pb_carframe.setVisibility(View.GONE);
		}

		@Override
		public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
			showMsg(getResources().getString(R.string.response_fail), "info", "bottom");
		}
	}

	private void showMsg(String msg, String styletype, String gravity) {

		if (styletype.equals("alert")) {
			style = AppMsg.STYLE_ALERT;
		} else if (styletype.equals("info")) {
			style = AppMsg.STYLE_INFO;
		}

		appMsg = AppMsg.makeText(CarServiceActivity.this, msg, style);

		if (gravity.equals("bottom")) {
			appMsg.setLayoutGravity(Gravity.BOTTOM);
		} else if (gravity.equals("top")) {
		}
		appMsg.show();
	}

	// 从服务端获取车主服务url
	private String geturl() {
		String cwzpath = null;
		String result = HttpUtil.GetStringFromUrl(getcarframe_url);
		if (result != null) {
			try {
				JSONObject jsonObject = new JSONObject(result);
				cwzpath = jsonObject.getString("czfwpath");
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return cwzpath;
	}

}

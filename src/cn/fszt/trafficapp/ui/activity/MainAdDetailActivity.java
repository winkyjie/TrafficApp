package cn.fszt.trafficapp.ui.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import cn.fszt.trafficapp.R;
import cn.fszt.trafficapp.util.Constant;
import cn.fszt.trafficapp.util.HttpUtil;
import cn.fszt.trafficapp.widget.msg.AppMsg;
import cn.fszt.trafficapp.widget.sharesdk.onekeyshare.OnekeyShare;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

/**
 * 首页广告区详情
 * 
 * @author AeiouKong
 *
 */
public class MainAdDetailActivity extends BaseBackActivity {
	
	private WebView carframe;
	private ProgressBar pb_carframe;
	private Intent intent;
	AppMsg.Style style = AppMsg.STYLE_INFO;
	AppMsg appMsg;
	private String api_url,uid,hdinfoid,contenttype,transmiturl;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.webview_carframe);

		initView();

		getActionBar().setTitle(getResources().getString(R.string.item_detail));
		getActionBar().setBackgroundDrawable(
				new ColorDrawable(getResources().getColor(R.color.titlebar)));
		getActionBar().setDisplayHomeAsUpEnabled(true);
	}

//	@Override
//	public boolean onCreateOptionsMenu(Menu menu) {
//
//		MenuInflater inflater = getMenuInflater();
//
//		inflater.inflate(R.menu.huodongitem, menu);
//
//		return true;
//	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			break;
//		case R.id.item_hd_share:
//			if (transmiturl != null && !transmiturl.equals("")) {
//				showShare();
//			} else {
//				Toast.makeText(this, "转发失败", Toast.LENGTH_SHORT).show();
//			}
//			break;
		}
		return true;
	}
	
	

	private void initView() {
		intent = getIntent();
		hdinfoid = intent.getStringExtra("hdinfoid");
		contenttype = intent.getStringExtra("contenttype");
		uid = intent.getStringExtra("uid");
		api_url = getString(R.string.api_url);
		pb_carframe = (ProgressBar) findViewById(R.id.pb_carframe);
		carframe = (WebView) findViewById(R.id.wv_carframe);
		carframe.getSettings().setJavaScriptEnabled(true);
		carframe.setWebViewClient(new MyWebViewClient());
		carframe.setWebChromeClient(m_chromeClient);
		// 获取url
		new Thread() {
			public void run() {
				if("huodong".equals(contenttype)){
					getActDetail("GetJxhdDetailByHdinfoid",hdinfoid);
				}else if("carlife".equals(contenttype)){
					getActDetail("GetcshDetailByHdinfoid",hdinfoid);
				}
			}
		}.start();
	}

	Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case Constant.GETDATA_SUCCEED:
				carframe.loadUrl((String) msg.obj);
				break;
			case Constant.GETDATA_FAILED:
				pb_carframe.setVisibility(View.GONE);
				showMsg(getResources().getString(R.string.response_fail),
						"info", "bottom");
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
		public void onReceivedError(WebView view, int errorCode,
				String description, String failingUrl) {
			showMsg(getResources().getString(R.string.response_fail), "info",
					"bottom");
		}
	}
	
	private void getActDetail(String action,String hdinfoid){
		
		JSONObject jsonObject;
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("action", action));
		params.add(new BasicNameValuePair("hdinfoid", hdinfoid));
		params.add(new BasicNameValuePair("connected_uid", uid));
		String result = HttpUtil.PostStringFromUrl(api_url, params);
		if(result!=null){
			try {
				jsonObject = new JSONObject(result);
				String titlepath = jsonObject.getString("titlepath");
				transmiturl = jsonObject.getString("transmiturl");
				handler.obtainMessage(Constant.GETDATA_SUCCEED, titlepath).sendToTarget();
			} catch (JSONException e) {
				handler.sendEmptyMessage(Constant.GETDATA_FAILED);
				e.printStackTrace();
			}
		}else{
			handler.sendEmptyMessage(Constant.GETDATA_FAILED);
		}
	}
	
	private void showShare() {
		ShareSDK.initSDK(this);
		OnekeyShare oks = new OnekeyShare();
//		oks.setTitle(weibocontent);
//		oks.setText(weibocontent + " " + transmiturl);
//		oks.setUrl(transmiturl);
//		oks.setTitleUrl(transmiturl);
//		if (newimage != null && !newimage.equals("")) {
//			oks.setImageUrl(newimage);
//		} else {
//			oks.setImageUrl("http://app.fm924.com/ztmedia/images/qrcode.png");
//		}
		oks.setCallback(new PlatformActionListener() {

			@Override
			public void onError(Platform platform, int arg1, Throwable arg2) {
			}

			@Override
			public void onComplete(Platform platform, int arg1,
					HashMap<String, Object> arg2) {
				Toast.makeText(MainAdDetailActivity.this, "分享成功", Toast.LENGTH_SHORT)
						.show();
			}

			@Override
			public void onCancel(Platform platform, int arg1) {
			}
		});
		// 启动分享GUI
		oks.show(this);
	}

	private void showMsg(String msg, String styletype, String gravity) {

		if (styletype.equals("alert")) {
			style = AppMsg.STYLE_ALERT;
		} else if (styletype.equals("info")) {
			style = AppMsg.STYLE_INFO;
		}

		appMsg = AppMsg.makeText(MainAdDetailActivity.this, msg, style);

		if (gravity.equals("bottom")) {
			appMsg.setLayoutGravity(Gravity.BOTTOM);
		} else if (gravity.equals("top")) {
		}
		appMsg.show();
	}

}

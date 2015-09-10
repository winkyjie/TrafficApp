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
import cn.fszt.trafficapp.util.ShareCountUtil;
import cn.fszt.trafficapp.widget.msg.AppMsg;
import cn.fszt.trafficapp.widget.sharesdk.onekeyshare.OnekeyShare;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebChromeClient.CustomViewCallback;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

/**
 * 精选活动、车生活详情
 * 
 * @author AeiouKong
 *
 */
public class ActDetailActivity extends BaseBackActivity {

	private WebView carframe;
	private ProgressBar pb_carframe;
	private Intent intent;
	AppMsg.Style style = AppMsg.STYLE_INFO;
	AppMsg appMsg;
	private String api_url, uid, hdinfoid, contenttype, transmiturl, title,
			imagepath;
	private View myView = null;
	private CustomViewCallback myCallback = null;
	private MenuItem item_hd_share;
	private boolean isCustomViewShow;
	private String sharetype;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.webview_actdetail);

		initView();
		getWindow().setFlags(
				WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
				WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);

		getActionBar().setTitle(getResources().getString(R.string.item_detail));
		getActionBar().setBackgroundDrawable(
				new ColorDrawable(getResources().getColor(R.color.titlebar)));
		getActionBar().setDisplayHomeAsUpEnabled(true);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		MenuInflater inflater = getMenuInflater();

		inflater.inflate(R.menu.huodongitem, menu);

		// TODO
		item_hd_share = menu.findItem(R.id.item_hd_share);

		if ("ad".equals(contenttype)) {
			item_hd_share.setVisible(false);
		}

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case android.R.id.home:
			if(isCustomViewShow){
				if (myView != null) {
					if (myCallback != null) {
						myCallback.onCustomViewHidden();
						myCallback = null;
					}
					
					ViewGroup parent = (ViewGroup) myView.getParent();
					parent.removeView(myView);
					parent.addView(carframe);
					myView = null;
				}
				isCustomViewShow = false;
			}else{
				if(carframe.canGoBack()){
					carframe.goBack();
				}else{
					carframe.loadData("", "text/html; charset=UTF-8", null);
					finish();
				}
			}

			break;
		case R.id.item_hd_share:
			if (transmiturl != null && !transmiturl.equals("")) {
				showShare();
			} else {
				Toast.makeText(this, "转发失败", Toast.LENGTH_SHORT).show();
			}
			break;
		}
		return true;
	}

	private void initView() {
		intent = getIntent();
		hdinfoid = intent.getStringExtra("hdinfoid");
		contenttype = intent.getStringExtra("contenttype");
		uid = intent.getStringExtra("uid");
		api_url = getString(R.string.api_url);
		pb_carframe = (ProgressBar) findViewById(R.id.pb_actdetail);
		carframe = (WebView) findViewById(R.id.webview);
		carframe.getSettings().setJavaScriptEnabled(true);
		carframe.setWebViewClient(new MyWebViewClient());
		carframe.setWebChromeClient(m_chromeClient);
		// carframe.setLayerType(WebView.LAYER_TYPE_NONE, null);
		// 获取url
		new Thread() {
			public void run() {
				if ("huodong".equals(contenttype)) {
					getActDetail("GetJxhdDetailByHdinfoid", hdinfoid);
				} else if ("carlife".equals(contenttype)) {
					getActDetail("GetcshDetailByHdinfoid", hdinfoid);
				} else if ("ad".equals(contenttype)) {
					getActDetail("GetADDetailByHdinfoid", hdinfoid);
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

		private int mOriginalOrientation = 2; 
		
		@Override
		public void onShowCustomView(View view, CustomViewCallback callback) {
			
		onShowCustomView(view, mOriginalOrientation, callback);
		}

		public void onShowCustomView(View view, int requestedOrientation, 
				CustomViewCallback callback) {
			isCustomViewShow = true;
			getActionBar().hide();
			if (myCallback != null) {
				myCallback.onCustomViewHidden();
				myCallback = null;
				return;
			}

			ViewGroup parent = (ViewGroup) carframe.getParent();
			parent.removeView(carframe);
			parent.addView(view);
			myView = view;
			myCallback = callback;
			m_chromeClient = this;
			
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);

		};
		
		@Override
		public void onHideCustomView() {
			System.out.println("Hide==========");
//			getActionBar().show();
			// setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

//			if (myView != null) {
//				if (myCallback != null) {
//					myCallback.onCustomViewHidden();
//					myCallback = null;
//				}
//
//				ViewGroup parent = (ViewGroup) myView.getParent();
//				parent.removeView(myView);
//				parent.addView(carframe);
//				myView = null;
//			}
		}

	};

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		if(this.getResources().getConfiguration().orientation ==Configuration.ORIENTATION_LANDSCAPE) {
       } else if(this.getResources().getConfiguration().orientation ==Configuration.ORIENTATION_PORTRAIT) {
       }
	}
	
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
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK
				&& event.getAction() == KeyEvent.ACTION_DOWN){
			if(isCustomViewShow){
				getActionBar().show();
				setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
				if (myView != null) {
					if (myCallback != null) {
						myCallback.onCustomViewHidden();
						myCallback = null;
					}

					ViewGroup parent = (ViewGroup) myView.getParent();
					parent.removeView(myView);
					parent.addView(carframe);
					myView = null;
				}
				isCustomViewShow = false;
			}else{
				if(carframe.canGoBack()){
					carframe.goBack();
				}else{
					carframe.loadData("", "text/html; charset=UTF-8", null);
					finish();
				}
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	private void getActDetail(String action, String hdinfoid) {

		JSONObject jsonObject;
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("action", action));
		params.add(new BasicNameValuePair("hdinfoid", hdinfoid));
		params.add(new BasicNameValuePair("connected_uid", uid));
		String result = HttpUtil.PostStringFromUrl(api_url, params);
		if (result != null) {
			try {
				jsonObject = new JSONObject(result);
				String titlepath = jsonObject.getString("titlepath");
				if (!"GetADDetailByHdinfoid".equals(action)) {
					transmiturl = jsonObject.getString("transmiturl");
					title = jsonObject.getString("title");
					imagepath = jsonObject.getString("imagepath");
				}
				handler.obtainMessage(Constant.GETDATA_SUCCEED, titlepath)
						.sendToTarget();
			} catch (JSONException e) {
				handler.sendEmptyMessage(Constant.GETDATA_FAILED);
				e.printStackTrace();
			}
		} else {
			handler.sendEmptyMessage(Constant.GETDATA_FAILED);
		}
	}

	private void showShare() {
		ShareSDK.initSDK(this);
		OnekeyShare oks = new OnekeyShare();
		if (title.length() > 70) {
			title = title.substring(0, 70) + "...";
		}
		oks.setTitle(title);
		oks.setText(title + " " + transmiturl);
		oks.setUrl(transmiturl);
		oks.setTitleUrl(transmiturl);
		if (imagepath != null && !imagepath.equals("")) {
			oks.setImageUrl(imagepath);
		} else {
			oks.setImageUrl(getString(R.string.qrcode_url));
		}
		oks.setCallback(new PlatformActionListener() {

			@Override
			public void onError(Platform platform, int arg1, Throwable arg2) {
				Toast.makeText(ActDetailActivity.this, "分享失败",Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onComplete(Platform platform, int arg1,
					HashMap<String, Object> arg2) {
				
				final String plat = platform.toString();
				
				if ("huodong".equals(contenttype)) {
					sharetype = "0";
				} else if ("carlife".equals(contenttype)) {
					sharetype = "1";
				} else if ("ad".equals(contenttype)) {
					sharetype = "0";
				}
				new Thread(){
					public void run() {
						ShareCountUtil.ShareCount(api_url, uid, hdinfoid, sharetype, 
								plat, "113.129151,23.024514");
					}
				}.start();
			}

			@Override
			public void onCancel(Platform platform, int arg1) {
				Toast.makeText(ActDetailActivity.this, "取消分享",Toast.LENGTH_SHORT).show();
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

		appMsg = AppMsg.makeText(ActDetailActivity.this, msg, style);

		if (gravity.equals("bottom")) {
			appMsg.setLayoutGravity(Gravity.BOTTOM);
		} else if (gravity.equals("top")) {
		}
		appMsg.show();
	}

	// @Override
	// protected void onPause() {
	// System.out.println("onPause====");
	// // carframe.reload();
	// super.onPause();
	// }

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		carframe.onResume();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		carframe.onPause();
	}
}

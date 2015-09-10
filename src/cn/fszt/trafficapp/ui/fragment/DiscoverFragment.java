package cn.fszt.trafficapp.ui.fragment;

import cn.fszt.trafficapp.R;
import cn.fszt.trafficapp.util.AppClickCount;
import cn.fszt.trafficapp.util.DiscoverWebJs;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

/**
 * 发现
 * 
 * @author AeiouKong
 *
 */
public class DiscoverFragment extends Fragment {

	private WebView webView;
	private SharedPreferences sp;
	private ProgressBar pb_web;
	private View rl_back;
	private Activity act;
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View layout = inflater.inflate(R.layout.web_shake, container, false);
		pb_web = (ProgressBar) layout.findViewById(R.id.pb_web);
		webView = (WebView) layout.findViewById(R.id.webView);
		rl_back = layout.findViewById(R.id.rl_back);
		act=this.getActivity();
		rl_back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				webView.goBack();
			}
		});
		initView();
		return layout;
	}

	private void initView() {

		sp = getActivity().getSharedPreferences("USER", Context.MODE_PRIVATE);
		String mobile = sp.getString("mobile", null);
		String nickname = sp.getString("nickname", null);

		webView.setWebViewClient(new WebViewClient() {
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				if(url.contains("files/web/shake.html")){
					new AppClickCount(act, "摇一摇");
				}else if(url.contains("http://wd.koudai.com")){
					new AppClickCount(act, "佛山电台微店");
				}
				view.loadUrl(url);
				return true;
			}
		});

		WebSettings webSettings = webView.getSettings();
		webSettings.setBuiltInZoomControls(false);
		webSettings.setSupportZoom(false);
		webSettings.setDisplayZoomControls(false);
		webSettings.setJavaScriptEnabled(true);
		webView.addJavascriptInterface(new DiscoverWebJs(getActivity(), nickname, mobile), "Shake");
		webView.getSettings().setUserAgentString(webSettings.getUserAgentString() + "android");
		// webView.loadUrl("file:"
		// + Environment.getExternalStorageDirectory().getAbsolutePath()
		// + getString(R.string.web_dir) + "index.html");
		webView.loadUrl("file:" + getActivity().getFilesDir().getPath() + "/web/" + "index.html");
		pb_web.setVisibility(View.GONE);
	}

}

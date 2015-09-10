package cn.fszt.trafficapp.util;

import java.util.ArrayList;
import java.util.List;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.util.Log;

public class ShareCountUtil {

	public static void ShareCount(String api_url, String uid, String shareid, String sharetype, String plat,
			String latLng) {

		String sharedestination = null;

		if (plat.contains("WechatMoments")) {
			sharedestination = "微信朋友圈";
		} else if (plat.contains("SinaWeibo")) {
			sharedestination = "新浪微博";
		} else if (plat.contains("QQ")) {
			sharedestination = "qq好友";
		} else if (plat.contains("Wechat")) {
			sharedestination = "微信好友";
		} else if (plat.contains("WechatFavorite")) {
			sharedestination = "微信收藏";
		} else if (plat.contains("ShortMessage")) {
			sharedestination = "信息";
		}

		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("action", "InsertHdsharedetail"));
		params.add(new BasicNameValuePair("connected_uid", uid));
		params.add(new BasicNameValuePair("shareid", shareid));
		params.add(new BasicNameValuePair("sharetype", sharetype));
		params.add(new BasicNameValuePair("sharedevice", "android"));
		params.add(new BasicNameValuePair("sharedestination", sharedestination));
		params.add(new BasicNameValuePair("latLng", latLng));

		String result = HttpUtil.PostStringFromUrl(api_url, params);
	}
}

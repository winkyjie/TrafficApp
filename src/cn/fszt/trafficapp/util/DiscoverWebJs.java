package cn.fszt.trafficapp.util;

import android.app.Service;
import android.content.Context;
import android.os.Vibrator;

public class DiscoverWebJs {

	private Context mContext;
	private Vibrator vibrator;
	private String nickname,mobile;

	public DiscoverWebJs(Context context,String nickname,String mobile) {
		this.mContext = context;
		vibrator = (Vibrator) mContext.getSystemService(Service.VIBRATOR_SERVICE);
		this.nickname = nickname;
		this.mobile = mobile;
	}

	public String getNickName() {
		return nickname;
	}

	public String getMobile() {
		return mobile;
	}

	public void vibrate(int duration) {
		vibrator.vibrate(duration);
	}

}

package cn.fszt.trafficapp.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import cn.fszt.trafficapp.domain.UserInfoData;

public class StUserInfoUtil {

	public static void initSpInfo(Context context,SharedPreferences sp){
		Editor editor = sp.edit();
		UserInfoData userinfo;
		userinfo = GetUserInfo.userinfo(context);
		if(userinfo!=null){
			editor.putString("uuid", userinfo.getConnected_uid());
			editor.putString("nickname", userinfo.getNickname());
			editor.putString("headimg", userinfo.getHeadimg());
			editor.putString("driveage", userinfo.getDriveage());
			editor.putString("sex", userinfo.getSex());
			editor.putString("birthday", userinfo.getBirthday());
			editor.putString("qq", userinfo.getQq());
			editor.putString("blog", userinfo.getBlog());
			editor.putString("hobby", userinfo.getHobby());
			editor.putString("mobile", userinfo.getMobile());
			editor.putString("fircar", userinfo.getFircar());
			editor.putString("fircarimg", userinfo.getFircarimg());
			editor.putString("seccar", userinfo.getSeccar());
			editor.putString("seccarimg", userinfo.getSeccarimg());
			editor.putString("email", userinfo.getEmail());
			editor.putString("paltype", userinfo.getPaltype());
			editor.putString("job", userinfo.getJob());
			editor.putString("income", userinfo.getIncome());
			editor.putString("intro", userinfo.getIntro());
			editor.putString("isdj", userinfo.getIsdj());
			editor.putString("isdjpath", userinfo.getIsdjpath());
			editor.putString("recname", userinfo.getRecname());
			editor.putString("recaddress", userinfo.getRecaddress());
			editor.putString("isblacklist", userinfo.getIsblacklist());
			editor.putString("isauth", userinfo.getIsauth());
			editor.putString("authtime", userinfo.getAuthtime());
			editor.putString("qqopenid", userinfo.getQqopenid());
			editor.putString("weixinopenid", userinfo.getWeixinopenid());
			editor.putString("sinaopenid", userinfo.getSinaopenid());
			editor.putString("tencentopenid", userinfo.getTencentopenid());
			editor.putString("totalscore", userinfo.getTotalscore());
			editor.commit();
		}
	}
}

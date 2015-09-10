package cn.fszt.trafficapp.widget;

import java.util.HashSet;
import java.util.Set;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.util.Log;
import cn.fszt.trafficapp.ui.MainActivity;
import cn.fszt.trafficapp.ui.activity.CarServiceActivity;
import cn.fszt.trafficapp.ui.activity.MyMessageActivity;
import cn.fszt.trafficapp.ui.activity.WelcomeActivity;
import cn.fszt.trafficapp.util.CommonUtils;
import cn.jpush.android.api.JPushInterface;

/**
 * 自定义接收器
 * 
 * 如果不定义这个 Receiver，则： 1) 默认用户会打开主界面 2) 接收不到自定义消息
 */
public class MyPushReceiver extends BroadcastReceiver {
	private static final String TAG = "JPush";
	SharedPreferences sp;
	Editor editor;
	Set<String> programnames;
	Set<String> sconnected_uids;

	@Override
	public void onReceive(Context context, Intent intent) {
		Bundle bundle = intent.getExtras();
		String extra = bundle.getString(JPushInterface.EXTRA_EXTRA);
		Log.d("TAG", "extra：" + extra);
		sp = context.getSharedPreferences("PUSH", Context.MODE_PRIVATE);
		editor = sp.edit();
		// programnames = sp.getStringSet("programname", null);
		if (!CommonUtils.isEmpty(extra)) {
			// editor.putString("extra", extra);
			// editor.commit();

			try {
				JSONObject extraJson = new JSONObject(extra);
				if (null != extraJson && extraJson.length() > 0) {
					String module = extraJson.getString("module");
					// 保存推送消息
					if ("HdcommentReply".equals(module)) {
						programnames = sp.getStringSet("programname", new HashSet<String>());
						sconnected_uids = sp.getStringSet("sconnected_uid", new HashSet<String>());
						String programname = extraJson.getString("programname");
						if (extraJson.has("sconnected_uid")) {
							String sconnected_uid = extraJson.getString("sconnected_uid");
							sconnected_uids.add(sconnected_uid);
							editor.putStringSet("sconnected_uid", sconnected_uids);
						}
						editor.putBoolean("HdcommentReply", true);
						programnames.add(programname);
						editor.putStringSet("programname", programnames);
						editor.commit();
					} else if ("StcollectReply".equals(module)) {
						editor.putBoolean("StcollectReply", true);
						editor.commit();
					} else if ("LatestNotification".equals(module)) {
						editor.putBoolean("LatestNotification", true);
						editor.commit();
					} else if ("AwardNotification".equals(module)) {
						editor.putBoolean("AwardNotification", true);
						editor.commit();
					}else if ("AgentNotification".equals(module)) {
						editor.putBoolean("AgentNotification", true);
						editor.commit();
					}
					// 处理推送结果
					if (JPushInterface.ACTION_REGISTRATION_ID.equals(intent.getAction())) {
//						String regId = bundle.getString(JPushInterface.EXTRA_REGISTRATION_ID);
//						Log.d("TAG", "[MyReceiver] 接收Registration Id : " + regId);
						// send the Registration Id to your server...

					} else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent.getAction())) {
//						Log.d("TAG", "[MyReceiver] 接收到推送下来的自定义消息: " + bundle.getString(JPushInterface.EXTRA_MESSAGE));
						// processCustomMessage(context, bundle);

					} else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent.getAction())) {
//						Log.d("TAG", "[MyReceiver] 接收到推送下来的通知");
//						int notifactionId = bundle.getInt(JPushInterface.EXTRA_NOTIFICATION_ID);
						// Log.d(TAG, "[MyReceiver] 接收到推送下来的通知的ID: " +
						// notifactionId);

					} else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent.getAction())) {
						Log.d("TAG", "[MyReceiver] 用户点击打开了通知");
						Intent in = new Intent();
						// 打开自定义的Activity
						if ("AgentNotification".equals(module)) {
							in.setClass(context, MyMessageActivity.class);
						}else{
							in.setClass(context, WelcomeActivity.class);
						}
						// i.putExtras(bundle);
						// Receive不是activity，需要此项设置，否则不能跳转
						in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						context.startActivity(in);

					} else if (JPushInterface.ACTION_RICHPUSH_CALLBACK.equals(intent.getAction())) {
						// Log.d(TAG, "[MyReceiver] 用户收到到RICH PUSH CALLBACK: " +
						// bundle.getString(JPushInterface.EXTRA_EXTRA));
						// 在这里根据 JPushInterface.EXTRA_EXTRA
						// 的内容处理代码，比如打开新的Activity，
						// 打开一个网页等..

					} else if (JPushInterface.ACTION_CONNECTION_CHANGE.equals(intent.getAction())) {
//						boolean connected = intent.getBooleanExtra(JPushInterface.EXTRA_CONNECTION_CHANGE, false);
						// Log.w(TAG, "[MyReceiver]" + intent.getAction() + "
						// connected state change to " + connected);
					} else {
						// Log.d(TAG, "[MyReceiver] Unhandled intent - " +
						// intent.getAction());
					}

				}
			} catch (JSONException e) {
			}
		}
		// System.out.println("resextraeidver Programnames === ===" +
		// programnames);
		// Log.d("TAG", "[MyReceiver] onReceive - " + intent.getAction() + ",
		// extras: " + printBundle(bundle));
		// Log.d(TAG, "[MyReceiver] 接收extra : " + extra);

	}

	// 打印所有的 intent extra 数据
	private static String printBundle(Bundle bundle) {
		StringBuilder sb = new StringBuilder();
		for (String key : bundle.keySet()) {
			if (key.equals(JPushInterface.EXTRA_NOTIFICATION_ID)) {
				sb.append("\nkey:" + key + ", value:" + bundle.getInt(key));
			} else if (key.equals(JPushInterface.EXTRA_CONNECTION_CHANGE)) {
				sb.append("\nkey:" + key + ", value:" + bundle.getBoolean(key));
			} else {
				sb.append("\nkey:" + key + ", value:" + bundle.getString(key));
			}
		}
		return sb.toString();
	}

	// send msg to MainActivity
	private void processCustomMessage(Context context, Bundle bundle) {
		if (MainActivity.isForeground) {
			// String message = bundle.getString(JPushInterface.EXTRA_MESSAGE);
			String extras = bundle.getString(JPushInterface.EXTRA_EXTRA);
			Intent msgIntent = new Intent(MainActivity.MESSAGE_RECEIVED_ACTION);
			// msgIntent.putExtra(MainActivity.KEY_MESSAGE, message);
			if (!CommonUtils.isEmpty(extras)) {
				try {
					JSONObject extraJson = new JSONObject(extras);
					if (null != extraJson && extraJson.length() > 0) {
						msgIntent.putExtra(MainActivity.KEY_EXTRAS, extras);
					}
				} catch (JSONException e) {
				}
			}
			context.sendBroadcast(msgIntent);
		}
	}

	// public boolean isRunningForeground(){
	// String packageName=getPackageName(this);
	// String topActivityClassName=getTopActivityName(this);
	// System.out.println("packageName="+packageName+",topActivityClassName="+topActivityClassName);
	// if
	// (packageName!=null&&topActivityClassName!=null&&topActivityClassName.startsWith(packageName))
	// {
	// System.out.println("---> isRunningForeGround");
	// return true;
	// } else {
	// System.out.println("---> isRunningBackGround");
	// return false;
	// }
	// }
	//
	//
	// public String getTopActivityName(Context context){
	// String topActivityClassName=null;
	// ActivityManager activityManager =
	// (ActivityManager)(context.getSystemService(android.content.Context.ACTIVITY_SERVICE
	// )) ;
	// List<RunningTaskInfo> runningTaskInfos =
	// activityManager.getRunningTasks(1) ;
	// if(runningTaskInfos != null){
	// ComponentName f=runningTaskInfos.get(0).topActivity;
	// topActivityClassName=f.getClassName();
	// }
	// return topActivityClassName;
	// }
	//
	// public String getPackageName(Context context){
	// String packageName = context.getPackageName();
	// return packageName;
	// }
}

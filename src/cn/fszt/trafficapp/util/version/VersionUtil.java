package cn.fszt.trafficapp.util.version;

import java.io.File;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;

import cn.fszt.trafficapp.R;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Environment;
import android.provider.Settings;

public class VersionUtil {
	
	Context context;
	
	public VersionUtil(Context context){
		this.context = context;
	}
	/*
	 * 获取当前程序的版本号
	 */
	public String getVersionName() throws Exception {
		
		PackageManager packageManager = context.getPackageManager();
		PackageInfo packInfo = packageManager.getPackageInfo(context.getPackageName(),0);
		return packInfo.versionName;
	}

	public ResourceInfo getResourceInfo() throws Exception{
		ResourceInfo info;
		// 从资源文件获取服务器 地址
		String path = context.getResources().getString(R.string.url_server);
		// 包装成url的对象
		URL url = new URL(path);
		HttpURLConnection conn = (HttpURLConnection) url
				.openConnection();
		conn.setConnectTimeout(5000);
		InputStream is = conn.getInputStream();
		info = ResourceInfoParser.getResourceInfo(is);
		return info;
	}
	
	// 安装apk
	public void installApk(File file) {
		Intent intent = new Intent();
		// 执行动作
		intent.setAction(Intent.ACTION_VIEW);
		// 执行的数据类型
		intent.setDataAndType(Uri.fromFile(file),
				"application/vnd.android.package-archive");
		context.startActivity(intent);
	}
	
	/*
	 * 从服务器中下载APK
	 */
	public void downLoadApk(final ResourceInfo info) {
		final ProgressDialog pd; // 进度条对话框
		pd = new ProgressDialog(context);
		pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		pd.setMessage("正在下载更新");
		if (!Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
//			handler.sendEmptyMessage(SDCARD_NOMOUNTED);
		} else {
			pd.show();
			new Thread() {
				@Override
				public void run() {
					try {
						File file = DownLoadManager.getFileFromServer(
								info.getDownloadurl(), pd);
						sleep(1000);
						installApk(file);
						pd.dismiss(); // 结束掉进度条对话框

					} catch (Exception e) {
//						handler.sendEmptyMessage(DOWN_ERROR);
						e.printStackTrace();
					}
				}
			}.start();
		}
	}
	
	/*
	 * 
	 * 弹出对话框通知用户更新程序
	 */
	public void showUpdataDialog(final ResourceInfo info) {
		AlertDialog.Builder builer = new Builder(context);
		try {
		builer.setTitle("版本升级： v "+info.getVersion());
		String in = new String(info.getDescription().getBytes(),"UTF-8");
     	builer.setMessage(in);
		builer.setCancelable(false);
		// 当点确定按钮时从服务器上下载 新的apk 然后安装
		builer.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				downLoadApk(info);
			}
		});
		// 当点取消按钮时退出软件
		builer.setNegativeButton("退出", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
                System.exit(0);
			}
		});
		
		AlertDialog dialog = builer.create();
		dialog.show();
		}catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
	}
	
	/**
     * 检查打开gps
     */
	public void openGPSSettings() {  
    	LocationManager alm = (LocationManager) context  
    			.getSystemService(Context.LOCATION_SERVICE);  
    	if (alm.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER)) {  
    		return;  
    	}  
    	else{  
    		showGPSSettings();
    		return;  
    	}
    } 
    
	public void showGPSSettings(){
    	
    	AlertDialog.Builder builer = new Builder(context);
		builer.setTitle("提示");
     	builer.setMessage("打开GPS功能定位更准确哦！");
		builer.setCancelable(false);
		
		builer.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);  
				context.startActivity(intent); 
			}
		});
		builer.setNegativeButton("取消", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
			}
		});
		
		AlertDialog dialog = builer.create();
		dialog.show();
    }
}

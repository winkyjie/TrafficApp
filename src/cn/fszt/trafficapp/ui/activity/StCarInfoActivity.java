package cn.fszt.trafficapp.ui.activity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.umeng.analytics.MobclickAgent;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;
import cn.fszt.trafficapp.R;
import cn.fszt.trafficapp.util.HttpUtil;
import cn.fszt.trafficapp.util.PhotoUtil;
import cn.fszt.trafficapp.util.UploadUtil;
import cn.fszt.trafficapp.util.UploadUtil.OnUploadProcessListener;
import cn.fszt.trafficapp.widget.SelectSinglePhoto;
import cn.fszt.trafficapp.widget.photoview.DisplayImageOptionsUtil;

/**
 * 我的座驾
 */

public class StCarInfoActivity extends BaseBackActivity implements OnClickListener,
		OnUploadProcessListener {
	private static final int UPLOAD_FILE_DONE = 8;
	RelativeLayout rl_firstcar;
	RelativeLayout rl_secondcar;
	private boolean flag_input;
	ImageView iv_firstcar, iv_secondcar;
	private EditText et_car_car, et_car_car2, et_car_driveage,
			et_car_carfriend;
	private ProgressDialog pd, pd_head, pd_car;
	private String updateinfo_url, fircarimg_url, seccarimg_url, uid;
	String picPath1, picPath2;
	private UploadUtil upload;
	private String fircarimg, seccarimg;
	private SharedPreferences sp;
	private String _cameraDir;
	private String imgFileName1 = null;
	private String imgFileName2 = null;
	private Uri imguri1, imguri2;

	// 选择文件
	public static final int FIRSTCAR_PHOTO = 3;
	public static final int SECONDCAR_PHOTO = 4;

	private MenuItem item_carinfo_update;

	private ImageLoader imageLoader;
	private DisplayImageOptions options_head;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.carinfo_settings);
		initView();

		imageLoader = ImageLoader.getInstance();

		options_head = DisplayImageOptionsUtil.getOptions(
				R.drawable.default_head, R.drawable.default_head,
				R.drawable.default_image, new RoundedBitmapDisplayer(15));

		initUser();

		getActionBar().setTitle(getResources().getString(R.string.mycar));
		getActionBar().setBackgroundDrawable(
				new ColorDrawable(getResources().getColor(R.color.titlebar)));
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setDisplayShowHomeEnabled(false);
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {

		MenuInflater inflater = getMenuInflater();

		inflater.inflate(R.menu.carinfoactivity, menu);

		item_carinfo_update = menu.findItem(R.id.item_carinfo_update);

		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			break;

		case R.id.item_carinfo_update:
			if (!flag_input) {
				et_car_car.setEnabled(true);
				et_car_car.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
				et_car_car.setTextColor(getResources().getColor(
						R.color.options_item_text));
				et_car_car2.setEnabled(true);
				et_car_car2.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
				et_car_car2.setTextColor(getResources().getColor(
						R.color.options_item_text));
				et_car_driveage.setEnabled(true);
				et_car_driveage.setGravity(Gravity.CENTER_VERTICAL
						| Gravity.LEFT);
				et_car_driveage.setTextColor(getResources().getColor(
						R.color.options_item_text));
				et_car_carfriend.setEnabled(true);
				et_car_carfriend.setGravity(Gravity.CENTER_VERTICAL
						| Gravity.LEFT);
				et_car_carfriend.setTextColor(getResources().getColor(
						R.color.options_item_text));
				item_carinfo_update.setTitle(R.string.item_userinfo_save);
				item_carinfo_update.setIcon(R.drawable.btn_item_save);
				flag_input = true;
			} else {
				pd_car.show();
				// 上传资料
				new Thread() {
					public void run() {
						String car = et_car_car.getText().toString();
						String car2 = et_car_car2.getText().toString();
						String driveage = et_car_driveage.getText().toString();
						String carfriend = et_car_carfriend.getText()
								.toString();
						if (car.equals("")) {
							car = " ";
						}
						if (car2.equals("")) {
							car2 = " ";
						}
						if (driveage.equals("")) {
							driveage = " ";
						}
						if (carfriend.equals("")) {
							carfriend = " ";
						}

						JSONObject jsonObject;
						try {
							// 修改成功，再提交业务服务器修改资料
							List<NameValuePair> params_car = new ArrayList<NameValuePair>();
							params_car.add(new BasicNameValuePair(
									"connected_uid", uid));
							params_car
									.add(new BasicNameValuePair("fircar", car));
							params_car.add(new BasicNameValuePair("seccar",
									car2));
							params_car.add(new BasicNameValuePair("driveage",
									driveage));
							params_car.add(new BasicNameValuePair("paltype",
									carfriend));
							String result_car = HttpUtil.PostStringFromUrl(
									updateinfo_url, params_car);
							if (result_car != null) {
								jsonObject = new JSONObject(result_car);
								String code_car = jsonObject.getString("code");
								if (code_car.equals("200")) {
									handler.sendEmptyMessage(1);
								} else {
									handler.sendEmptyMessage(2);
								}
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
				}.start();
			}
			break;
		}
		return true;
	}

	private void initView() {
		_cameraDir = Environment.getExternalStorageDirectory()
				.getAbsolutePath()
				+ getResources().getString(R.string.phototake_dir);
		updateinfo_url = getResources().getString(R.string.server_url)
				+ getResources().getString(R.string.updateuserinfo_url);
		fircarimg_url = getResources().getString(R.string.server_url)
				+ getResources().getString(R.string.fircarimg_url);
		seccarimg_url = getResources().getString(R.string.server_url)
				+ getResources().getString(R.string.seccarimg_url);
		rl_firstcar = (RelativeLayout) findViewById(R.id.rl_firstcar);
		rl_firstcar.setOnClickListener(this);
		rl_secondcar = (RelativeLayout) findViewById(R.id.rl_secondcar);
		rl_secondcar.setOnClickListener(this);
		iv_firstcar = (ImageView) findViewById(R.id.iv_firstcar);
		iv_firstcar.setOnClickListener(this);
		iv_secondcar = (ImageView) findViewById(R.id.iv_secondcar);
		iv_secondcar.setOnClickListener(this);
		et_car_car = (EditText) findViewById(R.id.et_car_car);
		et_car_car2 = (EditText) findViewById(R.id.et_car_car2);
		et_car_driveage = (EditText) findViewById(R.id.et_car_driveage);
		et_car_carfriend = (EditText) findViewById(R.id.et_car_carfriend);
		pd_head = new ProgressDialog(this);
		pd_head.setMessage(getResources().getString(R.string.upload));
		pd_head.setCanceledOnTouchOutside(false);

		pd_car = new ProgressDialog(this);
		pd_car.setMessage(getResources().getString(R.string.saving));
		pd_car.setCanceledOnTouchOutside(false);

		sp = getSharedPreferences("USER", Context.MODE_PRIVATE);
		uid = sp.getString("uuid", null);

		upload = UploadUtil.getInstance();
		upload.setOnUploadProcessListener(this);
	}

	private void initUser() {
		fircarimg = sp.getString("fircarimg", null);
		seccarimg = sp.getString("seccarimg", null);
		String fircar = sp.getString("fircar", null);
		String seccar = sp.getString("seccar", null);
		String driveage = sp.getString("driveage", null);
		String paltype = sp.getString("paltype", null);

		if (fircarimg != null) {
			imageLoader
					.displayImage(fircarimg, iv_firstcar, options_head, null);
		}
		if (seccarimg != null) {
			imageLoader.displayImage(seccarimg, iv_secondcar, options_head,
					null);
		}
		et_car_car.setText(fircar);
		et_car_car2.setText(seccar);
		et_car_driveage.setText(driveage);
		et_car_carfriend.setText(paltype);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_firstcar:
			if (fircarimg != null && !fircarimg.equals("")) {
				Intent intent = new Intent(StCarInfoActivity.this,
						ImageTouchActivity.class);
				intent.putExtra("imgurl", fircarimg);
				startActivity(intent);
			} else {
				Intent intent = new Intent(StCarInfoActivity.this,
						ImageTouchActivity.class);
				intent.putExtra("imgid", R.drawable.default_head);
				startActivity(intent);
			}
			break;

		case R.id.iv_secondcar:
			if (seccarimg != null && !seccarimg.equals("")) {
				Intent intent = new Intent(StCarInfoActivity.this,
						ImageTouchActivity.class);
				intent.putExtra("imgurl", seccarimg);
				startActivity(intent);
			} else {
				Intent intent = new Intent(StCarInfoActivity.this,
						ImageTouchActivity.class);
				intent.putExtra("imgid", R.drawable.default_head);
				startActivity(intent);
			}
			break;

		case R.id.rl_firstcar:
			Intent intent_first = new Intent(this, SelectSinglePhoto.class);
			startActivityForResult(intent_first, FIRSTCAR_PHOTO);
			break;

		case R.id.rl_secondcar:
			Intent intent_second = new Intent(this, SelectSinglePhoto.class);
			startActivityForResult(intent_second, SECONDCAR_PHOTO);
			break;

		}
	}

	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			pd_car.dismiss();
			switch (msg.what) {
			case 1:
				saveInfo();
				setResult(RESULT_OK); // 通知上级Activity更新用户信息
				Toast.makeText(StCarInfoActivity.this,
						getResources().getString(R.string.update_success),
						Toast.LENGTH_LONG).show();
				break;
			case 2:
				Toast.makeText(StCarInfoActivity.this,
						getResources().getString(R.string.update_fail),
						Toast.LENGTH_LONG).show();
				saveInfo();
				break;
			}
		}
	};

	private void saveInfo() {
		et_car_car.setEnabled(false);
		et_car_car.setGravity(Gravity.CENTER_VERTICAL | Gravity.RIGHT);
		et_car_car.setTextColor(getResources().getColor(
				R.color.userinfo_disable));
		et_car_car2.setEnabled(false);
		et_car_car2.setGravity(Gravity.CENTER_VERTICAL | Gravity.RIGHT);
		et_car_car2.setTextColor(getResources().getColor(
				R.color.userinfo_disable));
		et_car_driveage.setEnabled(false);
		et_car_driveage.setGravity(Gravity.CENTER_VERTICAL | Gravity.RIGHT);
		et_car_driveage.setTextColor(getResources().getColor(
				R.color.userinfo_disable));
		et_car_carfriend.setEnabled(false);
		et_car_carfriend.setGravity(Gravity.CENTER_VERTICAL | Gravity.RIGHT);
		et_car_carfriend.setTextColor(getResources().getColor(
				R.color.userinfo_disable));
		item_carinfo_update.setTitle(R.string.item_userinfo_update);
		item_carinfo_update.setIcon(R.drawable.btn_item_alter);
		flag_input = false;
	}

	/**
	 * 用当前时间给取得的图片命名
	 * 
	 */
	private String getImageFileName() {
		Date date = new Date(System.currentTimeMillis());
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmssSSS");
		return dateFormat.format(date) + ".jpg";
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (resultCode == RESULT_OK && requestCode == FIRSTCAR_PHOTO) {
			picPath1 = data.getStringExtra(SelectSinglePhoto.KEY_PHOTO_PATH);
			File temp1 = new File(picPath1);
			// 裁剪图片
			imguri1 = Uri.fromFile(temp1);
			startPhotoZoom1(imguri1);
		}
		if (resultCode == RESULT_OK && requestCode == SECONDCAR_PHOTO) {
			picPath2 = data.getStringExtra(SelectSinglePhoto.KEY_PHOTO_PATH);
			File temp2 = new File(picPath2);
			// 裁剪图片
			imguri2 = Uri.fromFile(temp2);
			startPhotoZoom2(imguri2);
		}

		if (requestCode == 1 && resultCode == -1) {
			if (data != null) {
				pd_head.show();
				Map<String, String> map = new HashMap<String, String>();

				setPicToView1(data);
				map.put("connected_uid", uid);
				List<String> list = new ArrayList<String>();
				list.add(imgFileName1);
				upload.uploadFile(list, "img", fircarimg_url, map);

			}
		}
		if (requestCode == 2 && resultCode == -1) {
			if (data != null) {
				pd_head.show();
				Map<String, String> map = new HashMap<String, String>();

				setPicToView2(data);
				map.put("connected_uid", uid);
				List<String> list = new ArrayList<String>();
				list.add(imgFileName2);
				upload.uploadFile(list, "img", seccarimg_url, map);

			}
		}

		super.onActivityResult(requestCode, resultCode, data);
	}

	/**
	 * 裁剪图片方法实现
	 * 
	 * @param uri
	 */
	public void startPhotoZoom1(Uri uri) {

		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/*");
		intent.putExtra("crop", "true");
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		intent.putExtra("outputX", 450);
		intent.putExtra("outputY", 450);
		intent.putExtra("noFaceDetection", true);
		intent.putExtra("scale", true);
		intent.putExtra("return-data", false);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
		intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
		startActivityForResult(intent, 1);
	}

	/**
	 * 保存裁剪之后的图片数据
	 * 
	 * @param picdata
	 */
	private void setPicToView1(Intent picdata) {

		Bitmap bitmap;
		try {
			bitmap = MediaStore.Images.Media.getBitmap(
					this.getContentResolver(), imguri1);
			iv_firstcar.setImageBitmap(bitmap);

			File cameraDir = new File(_cameraDir);
			if (!cameraDir.exists()) {
				cameraDir.mkdirs();
			}

			imgFileName1 = _cameraDir + getImageFileName();
			PhotoUtil.saveBitmap2file(bitmap, imgFileName1);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 裁剪图片方法实现
	 * 
	 * @param uri
	 */
	public void startPhotoZoom2(Uri uri) {

		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/*");
		intent.putExtra("crop", "true");
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		intent.putExtra("outputX", 450);
		intent.putExtra("outputY", 450);
		intent.putExtra("noFaceDetection", true);
		intent.putExtra("scale", true);
		intent.putExtra("return-data", false);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
		intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
		startActivityForResult(intent, 2);
	}

	/**
	 * 保存裁剪之后的图片数据
	 * 
	 * @param picdata
	 */
	private void setPicToView2(Intent picdata) {

		Bitmap bitmap;
		try {
			bitmap = MediaStore.Images.Media.getBitmap(
					this.getContentResolver(), imguri2);
			iv_secondcar.setImageBitmap(bitmap);

			File cameraDir = new File(_cameraDir);
			if (!cameraDir.exists()) {
				cameraDir.mkdirs();
			}

			imgFileName2 = _cameraDir + getImageFileName();
			PhotoUtil.saveBitmap2file(bitmap, imgFileName2);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private Handler handler_upload = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {

			case UPLOAD_FILE_DONE:
				pd_head.dismiss();
				Toast.makeText(StCarInfoActivity.this, msg.obj + "",
						Toast.LENGTH_SHORT).show();
				if (imgFileName1 != null) {
					Bitmap bm = PhotoUtil.createThumbFromFile(imgFileName1,
							450, 450);
					iv_firstcar.setImageBitmap(bm);
				}

				if (imgFileName2 != null) {
					Bitmap bm = PhotoUtil.createThumbFromFile(imgFileName2,
							450, 450);
					iv_secondcar.setImageBitmap(bm);
				}
				setResult(Activity.RESULT_OK); // 通知上级activity更新
				break;
			}
			super.handleMessage(msg);
		}

	};

	@Override
	public void onUploadDone(int responseCode, String result, String message) {
		Message msg = Message.obtain();
		msg.what = UPLOAD_FILE_DONE;
		msg.arg1 = responseCode;
		msg.obj = message;
		handler_upload.sendMessage(msg);
	}

	@Override
	public void onUploadProcess(int uploadSize) {

	}

	@Override
	public void initUpload(int fileSize) {

	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		try {
			super.onConfigurationChanged(newConfig);
			if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
				// land
			} else if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
				// port
			}
		} catch (Exception ex) {
		}
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

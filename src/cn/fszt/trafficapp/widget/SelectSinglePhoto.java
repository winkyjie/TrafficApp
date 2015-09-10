package cn.fszt.trafficapp.widget;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import com.umeng.analytics.MobclickAgent;
import cn.fszt.trafficapp.R;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

/**
 * 选择单张图片
 * @author AeiouKong
 *
 */
public class SelectSinglePhoto extends Activity implements OnClickListener{

	//使用照相机拍照获取图片
	public static final int SELECT_PIC_BY_TACK_PHOTO = 1;
	//使用相册中的图片
	public static final int SELECT_PIC_BY_PICK_PHOTO = 2;
	//从Intent获取图片路径的KEY
	public static final String KEY_PHOTO_PATH = "photo_path";	
	private static final File PHOTO_DIR = new File(
			Environment.getExternalStorageDirectory() + "/DCIM");
	private String picName = "";
	private static final String TAG = "SelectPicActivity";	
	private Button takePhotoBtn,pickPhotoBtn,cancelBtn;

	/**获取到的图片路径*/
	private String picPath;
	private String takpicPath;
	private String keepPath;
	private Intent lastIntent ;	
	private Uri photoUri;
	
	private View outside;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.select_pic_layout);
		
		takePhotoBtn = (Button) findViewById(R.id.btn_take_photo);
		takePhotoBtn.setOnClickListener(this);
		pickPhotoBtn = (Button) findViewById(R.id.btn_pick_photo);
		pickPhotoBtn.setOnClickListener(this);
		cancelBtn = (Button) findViewById(R.id.btn_cancel);
		cancelBtn.setOnClickListener(this);	
		outside = findViewById(R.id.outside);
		outside.setOnClickListener(this);
		
		lastIntent = getIntent();
		if(!PHOTO_DIR.exists()){
			PHOTO_DIR.mkdir();
		}
	}

	 
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_cancel:
			finish();
			break;
		case R.id.btn_take_photo:
			takePhoto();
			break;
		case R.id.btn_pick_photo:
			pickPhoto();
			break;
		case R.id.outside:
			finish();
			break;
		}
	}

	/**
	 * 用当前时间给取得的图片命名
	 * 
	 */
	private String getPhotoFileName() {
		Date date = new Date(System.currentTimeMillis());
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmssSSS");
		return dateFormat.format(date) + ".jpg";
	}
	
	/**
	 * 拍照获取图片
	 */
	private void takePhoto() {
			
			Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			picName = getPhotoFileName();
			File out = new File(PHOTO_DIR, picName);
			Uri uri = Uri.fromFile(out);
			intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
			intent.putExtra("picPath", picPath);
			startActivityForResult(intent, SELECT_PIC_BY_TACK_PHOTO);
	}
				
	/***
	 * 从相册中取图片
	 */
	private void pickPhoto() {
		Intent intent = new Intent();
		intent.setType("image/*");
		intent.setAction(Intent.ACTION_GET_CONTENT);
		startActivityForResult(intent, SELECT_PIC_BY_PICK_PHOTO);
	}
	
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		finish();
		return super.onTouchEvent(event);
	}
	
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(resultCode == Activity.RESULT_OK && requestCode == SELECT_PIC_BY_PICK_PHOTO)
		{
			doPhoto(requestCode,data);
		}
		if(resultCode == Activity.RESULT_OK && requestCode == SELECT_PIC_BY_TACK_PHOTO){
			doPath();
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	/**
	 * 选择图片后，获取图片的路径
	 * @param requestCode
	 * @param data
	 */
	private void doPhoto(int requestCode,Intent data){
		
		if(requestCode == SELECT_PIC_BY_PICK_PHOTO )  
		{
			if(data == null){
				Toast.makeText(this, "选择图片文件出错", Toast.LENGTH_LONG).show();
				return;
			}
			photoUri = data.getData();
			if(photoUri == null ){
				Toast.makeText(this, "选择图片文件出错", Toast.LENGTH_LONG).show();
				return;
			}
		}
		String[] pojo = {MediaStore.Images.Media.DATA};
//		Cursor cursor = managedQuery(photoUri, pojo, null, null,null);   
		ContentResolver cr = getContentResolver();
		Cursor cursor = cr.query(photoUri, pojo,null, null, null);
		if(cursor != null ){
			int columnIndex = cursor.getColumnIndexOrThrow(pojo[0]);
			cursor.moveToFirst();
			picPath = cursor.getString(columnIndex);
			//4.0以上的版本会自动关闭
//			cursor.close();
		}
		Log.i(TAG, "imagePath = "+picPath);
		if(picPath !=null){
			lastIntent.putExtra(KEY_PHOTO_PATH, picPath);
			setResult(Activity.RESULT_OK, lastIntent);
			finish();
		}else{
			Toast.makeText(this, "选择文件不正确!", Toast.LENGTH_LONG).show();
			
		}
	}
	//回传路径
	private void doPath(){
		takpicPath = PHOTO_DIR+"/"+picName;
		if(takpicPath==null){
			takpicPath = keepPath;
		}
		lastIntent.putExtra(KEY_PHOTO_PATH, takpicPath);
		setResult(Activity.RESULT_OK, lastIntent);
		finish();
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		
		takpicPath = savedInstanceState.getString("keepPath");
		super.onRestoreInstanceState(savedInstanceState);
	}


	@Override
	protected void onSaveInstanceState(Bundle outState) {
		keepPath = PHOTO_DIR+"/"+picName;
		outState.putString("keepPath", keepPath);
		super.onSaveInstanceState(outState);
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

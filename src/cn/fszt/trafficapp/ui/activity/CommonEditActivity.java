package cn.fszt.trafficapp.ui.activity;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.umeng.analytics.MobclickAgent;
import cn.fszt.trafficapp.R;
import cn.fszt.trafficapp.domain.ExpressionData;
import cn.fszt.trafficapp.util.GetBlackList;
import cn.fszt.trafficapp.util.PhotoUtil;
import cn.fszt.trafficapp.util.UploadUtil;
import cn.fszt.trafficapp.util.UploadUtil.OnUploadProcessListener;
import cn.fszt.trafficapp.widget.expression.ExpressionImageAdapter;
import cn.fszt.trafficapp.widget.expression.ExpressionUtil;
import cn.fszt.trafficapp.widget.expression.MyPagerAdapter;
import cn.fszt.trafficapp.widget.msg.AppMsg;
import cn.fszt.trafficapp.widget.muphoto.AlbumActivity;
import cn.fszt.trafficapp.widget.muphoto.Bimp;
import cn.fszt.trafficapp.widget.muphoto.FileUtils;
import cn.fszt.trafficapp.widget.muphoto.GridAdapter;
import cn.fszt.trafficapp.widget.muphoto.PhotoActivity;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

/**
 * 编辑框(图片文字)。
 * 注意:intent入参必填-interface_url(接口名)
 * 在getSendParames()方法中补全需要的参数
 */
public class CommonEditActivity extends Activity implements OnClickListener, OnUploadProcessListener {

	private String _imageDir;
	private String _cameraDir;
	private static final int UPLOAD_FILE_DONE = 2;

	private ProgressDialog progressDialog;
	private Map<String, String> params = new HashMap<String, String>();
	private String server_url, commonInterface_url, uid, isdj;
	private UploadUtil upload;
	private EditText et_plhud_content;
	private String content = "";
	boolean isupload, isuploaddone;
	private Button btn_submit;
	private GridView gridView;
	private ArrayList<String> dataList = new ArrayList<String>();

	private static final int TAKE_PICTURE = 0x000000;
	private String path = "";
	private GridAdapter adapter;
	private LinearLayout ll_share;
	private CheckBox cb_share;
	private View outside;
	AppMsg.Style style;
	AppMsg appMsg;

	int columns = 6, rows = 4, pageExpressionCount = 4 * 6 - 1;
	ArrayList<GridView> grids;
	MyPagerAdapter myPagerAdapter;
	ViewPager vp_id;
	LinearLayout ll_expression;
	public LinearLayout ll_vp_selected_index;
	TextView tv_expression;
	int densityDpi;

	private Intent commonIntent;		//页面入参
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		DisplayMetrics metric = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metric);
		densityDpi = metric.densityDpi;
		setContentView(R.layout.activity_pinglun_hudong);
		initView();
		Init();

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		MenuInflater inflater = getMenuInflater();

		inflater.inflate(R.menu.pinglunhudongactivity, menu);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			break;

		}
		return true;
	}

	public void Init() {
		gridView = (GridView) findViewById(R.id.myGrid);
		gridView.setSelector(new ColorDrawable(Color.TRANSPARENT));
		adapter = new GridAdapter(this);
		adapter.update();
		gridView.setAdapter(adapter);
		gridView.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				Bimp.limit = 9;
				((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(
						CommonEditActivity.this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

				if (arg2 == Bimp.bmp.size()) {
					new PopupWindows(CommonEditActivity.this, gridView);
				} else {
					Intent intent = new Intent(CommonEditActivity.this, PhotoActivity.class);
					intent.putExtra("ID", arg2);
					startActivity(intent);
				}
			}
		});
	}

	private void initView() {
		server_url = getResources().getString(R.string.server_url);
		_imageDir = Environment.getExternalStorageDirectory().getAbsolutePath()
				+ getResources().getString(R.string.phototemp_dir);
		_cameraDir = Environment.getExternalStorageDirectory().getAbsolutePath()
				+ getResources().getString(R.string.phototake_dir);

		commonIntent = getIntent();
		commonInterface_url = commonIntent.getStringExtra("interface_url");
		
		SharedPreferences sp = getSharedPreferences("USER", Context.MODE_PRIVATE);
		uid = sp.getString("uuid", null);
		isdj = sp.getString("isdj", null);

		upload = UploadUtil.getInstance();
		upload.setOnUploadProcessListener(this);
		progressDialog = new ProgressDialog(this);
		progressDialog.setCanceledOnTouchOutside(false);
		cb_share = (CheckBox) findViewById(R.id.cb_share);
		ll_share = (LinearLayout) findViewById(R.id.ll_share);
		// 主持人可以同步到车友分享
		if ("1".equals(isdj)) {
			// TODO
			ll_share.setVisibility(View.VISIBLE);
		}
		et_plhud_content = (EditText) findViewById(R.id.et_plhud_content);
		et_plhud_content.setOnClickListener(this);
		ll_vp_selected_index = (LinearLayout) findViewById(R.id.ll_vp_selected_index);
		ll_expression = (LinearLayout) findViewById(R.id.ll_expression);
		vp_id = (ViewPager) findViewById(R.id.vp_id);
		vp_id.setOnPageChangeListener(new MyOnPageChangeListener());
		tv_expression = (TextView) findViewById(R.id.tv_expression);
		tv_expression.setOnClickListener(this);
		btn_submit = (Button) findViewById(R.id.btn_submit);
		btn_submit.setOnClickListener(this);
		outside = findViewById(R.id.outside);
		outside.setOnClickListener(this);
	}
	
	/**
	 * 获取提交需要的参数
	 */
	public void getSendParames(){
		content = et_plhud_content.getText().toString();
		if(commonInterface_url.equals("InsertHdsecretletter")){
			params.put("action", commonInterface_url);
			params.put("sconnected_uid", commonIntent.getStringExtra("sconnected_uid"));
			params.put("rconnected_uid", commonIntent.getStringExtra("rconnected_uid"));
			params.put("hdreplaytypeid", commonIntent.getStringExtra("hdreplaytypeid"));
			params.put("textcontent", content);
		}
		
	}
	
	@Override
	public void onClick(View v) {
		if (v == outside) {
			finish();
		}
		if (v == btn_submit) {
			// 是否同步到车友分享
			String isshare;
			boolean checked = cb_share.isChecked();
			if (checked) {
				isshare = "1";
			} else {
				isshare = "0";
			}
			if (!isuploaddone) {
				// 提交
				getSendParames();	
				uploadPinglun();
			}
		}

		if (v == tv_expression) {
			if (ll_expression.getVisibility() == View.GONE) {
				((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(
						this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
				ll_expression.setVisibility(View.VISIBLE);
				tv_expression.setBackgroundResource(R.drawable.expression_pressed);
			} else {
				ll_expression.setVisibility(View.GONE);
				tv_expression.setBackgroundResource(R.drawable.expression);
			}
		}
		if (v == et_plhud_content) {
			if (ll_expression.getVisibility() == View.VISIBLE) {
				ll_expression.setVisibility(View.GONE);
				tv_expression.setBackgroundResource(R.drawable.expression);
			}
		}
	}

	private void uploadPinglun() {
		if (!isupload) {
			for (int i = 0; i < Bimp.drr.size(); i++) {
				String Str = Bimp.drr.get(i).substring(Bimp.drr.get(i).lastIndexOf("/") + 1,
						Bimp.drr.get(i).lastIndexOf("."));
				dataList.add(FileUtils.SDPATH + Str + ".jpg");
			}

		}

		String fileKey = "img";
		if (dataList.size() == 0 && content.equals("")) {
			style = AppMsg.STYLE_ALERT;
			appMsg = AppMsg.makeText(this, getResources().getString(R.string.tips_pl_nonull), style);
			appMsg.show();
			isupload = false;
		} else {
			progressDialog.setMessage(getResources().getString(R.string.uploading));
			progressDialog.show();
			upload.uploadFile(dataList, fileKey, server_url + commonInterface_url, params);
			isupload = true;
			isuploaddone = true;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (requestCode == TAKE_PICTURE) {
			if (Bimp.drr.size() < Bimp.limit && resultCode == RESULT_OK) {
				int degree = PhotoUtil.readPictureDegree(path);
				Bitmap bm = PhotoUtil.createThumbFromFile(path, 600, 600);
				bm = PhotoUtil.rotaingImageView(degree, bm);

				File imageDir = new File(_imageDir);
				if (!imageDir.exists()) {
					imageDir.mkdirs();
				}

				String rotaingpaths = _imageDir + String.valueOf(System.currentTimeMillis()) + ".jpg";
				boolean df = PhotoUtil.saveBitmap2file(bm, rotaingpaths);
				if (df) {
					Bimp.drr.add(rotaingpaths);
				}
			}
		}

		super.onActivityResult(requestCode, resultCode, data);
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

	@Override
	public void onUploadDone(int responseCode, String result, String message) {
		progressDialog.dismiss();
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

	private Handler handler_upload = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {

			case UPLOAD_FILE_DONE:
				if (getResources().getString(R.string.upload_success).equals(msg.obj + "")) {
					setResult(RESULT_OK);
					style = AppMsg.STYLE_ALERT;
					appMsg = AppMsg.makeText(CommonEditActivity.this, getResources().getString(R.string.submit_success),
							style);
					appMsg.show();
					finish();
				} else {
					isuploaddone = false;
					style = AppMsg.STYLE_ALERT;
					appMsg = AppMsg.makeText(CommonEditActivity.this, getResources().getString(R.string.submit_fail),
							style);
					appMsg.show();
				}
				break;

			case GetBlackList.NOTBLACKLIST:
				// 提交
				content = et_plhud_content.getText().toString();
//				params.put("pinglun", id + ";" + content + ";" + uid + ";" + islive);
				if (!isuploaddone) {
					uploadPinglun();
				}
				break;
			}
			super.handleMessage(msg);
		}
	};

	public class PopupWindows extends PopupWindow {

		public PopupWindows(Context mContext, View parent) {

			View view = View.inflate(mContext, R.layout.item_popupwindows, null);
			view.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.fade_ins));
			LinearLayout ll_popup = (LinearLayout) view.findViewById(R.id.ll_popup);
			ll_popup.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.push_bottom_in_2));

			setWidth(LayoutParams.MATCH_PARENT);
			setHeight(LayoutParams.MATCH_PARENT);
			setBackgroundDrawable(new BitmapDrawable());
			setFocusable(true);
			setOutsideTouchable(true);
			setContentView(view);
			showAtLocation(parent, Gravity.BOTTOM, 0, 0);
			update();

			View outside = view.findViewById(R.id.outside);
			Button bt1 = (Button) view.findViewById(R.id.item_popupwindows_camera);
			Button bt2 = (Button) view.findViewById(R.id.item_popupwindows_Photo);
			Button bt3 = (Button) view.findViewById(R.id.item_popupwindows_cancel);
			bt1.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					photo();
					dismiss();
				}
			});
			bt2.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					Intent intent = new Intent(CommonEditActivity.this, AlbumActivity.class);
					startActivity(intent);
					dismiss();
				}
			});
			bt3.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					dismiss();
				}
			});
			outside.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					dismiss();
				}
			});

		}
	}

	public void photo() {
		Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

		File cameraDir = new File(_cameraDir);
		if (!cameraDir.exists()) {
			cameraDir.mkdirs();
		}

		File file = new File(_cameraDir, String.valueOf(System.currentTimeMillis()) + ".jpg");
		path = file.getPath();
		Uri imageUri = Uri.fromFile(file);
		openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
		startActivityForResult(openCameraIntent, TAKE_PICTURE);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		path = savedInstanceState.getString("path");
		// System.out.println("onRestoreInstanceStatepicPath~!!!!====" + path);
		super.onRestoreInstanceState(savedInstanceState);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putString("path", path);
		// System.out.println("onSaveInstanceStatepicPath~!!!!====" + path);
		super.onSaveInstanceState(outState);
	}

	@Override
	protected void onDestroy() {
		Bimp.bmp.clear();
		Bimp.drr.clear();
		Bimp.max = 0;
		FileUtils.deleteDir();
		super.onDestroy();
	}

	// protected void onRestart() {
	// adapter.update();
	// super.onRestart();
	// }

	@Override
	protected void onStop() {
		ExpressionUtil.expressionList.clear();
		super.onStop();
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (null == myPagerAdapter) {
			ExpressionUtil.initExpression();
			List<List<ExpressionData>> lists = initGridViewData();// 填充GridView数据
			grids = new ArrayList<GridView>();
			int gv_padding_lr = (int) getResources().getDimension(R.dimen.chat_gv_padding_lr);
			int gv_padding_bt = (int) getResources().getDimension(R.dimen.chat_gv_padding_bt);
			int gv_spacing = (int) getResources().getDimension(R.dimen.chat_gv_spacing);
			int chat_dot_margin_lr = (int) getResources().getDimension(R.dimen.chat_dot_margin_lr);
			int chat_dot_wh = (int) getResources().getDimension(R.dimen.chat_dot_wh);
			for (int i = 0; i < lists.size(); i++) {
				List<ExpressionData> l = lists.get(i);
				if (null != l) {
					// --生成当前GridView--//
					final GridView gv = new GridView(this);
					gv.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
					gv.setNumColumns(columns);
					gv.setGravity(Gravity.CENTER);
					gv.setPadding(gv_padding_lr, gv_padding_bt, gv_padding_lr, 0);
					gv.setHorizontalSpacing(gv_spacing);
					gv.setVerticalSpacing(gv_spacing);
					ExpressionImageAdapter expressionImageAdapter = new ExpressionImageAdapter(this, l);
					gv.setAdapter(expressionImageAdapter);
					// --点击列表事件处理--//
					gv.setOnItemClickListener(new OnItemClickListener() {
						@Override
						public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
							ExpressionData e = (ExpressionData) gv.getItemAtPosition(arg2);
							int index = et_plhud_content.getSelectionStart();
							Editable edit = et_plhud_content.getEditableText();// 获取EditText的文字
							String content_all = edit.toString();
							String content_forward = content_all.substring(0, index);
							String reg = "\\[em_[0-9]{1,3}?\\]";
							if (e.getDrableId() < 0) {// 点击删除按钮
								if (index > 0) {
									boolean delExpression = false;
									Pattern p = Pattern.compile(reg);
									Matcher matcher = p.matcher(content_forward);
									boolean found = false;
									if (content_forward.length() >= 7) {// 如果光标前字符少于6个，说明不可能为表情
										if (content_forward.length() == 7) {
											found = matcher.find(content_forward.length() - 7);
										} else {
											found = matcher.find(content_forward.length() - 8);
										}
										if (found) {
											String flag = matcher.group();
											if (content_forward.substring(content_forward.length() - flag.length(),
													content_forward.length()).equals(flag)) {
												delExpression = true;
												edit.delete(index - flag.length(), index);
											}
										}
									}
									if (!delExpression) {
										edit.delete(index - 1, index);
									}
								}
							} else {
								Bitmap bitmap = BitmapFactory.decodeResource(getResources(), e.drableId);
								if (densityDpi == 320) {
									bitmap = ExpressionUtil.big(bitmap, 75, 75);
								} else if (densityDpi == 240) {
									bitmap = ExpressionUtil.big(bitmap, 40, 40);
								} else if (densityDpi == 160) {
									bitmap = ExpressionUtil.big(bitmap, 24, 24);
								} else if (densityDpi == 440) {
									bitmap = ExpressionUtil.big(bitmap, 130, 130);
								} else if (densityDpi == 480) {
									bitmap = ExpressionUtil.big(bitmap, 160, 160);
								} else if (densityDpi == 640) {
									bitmap = ExpressionUtil.big(bitmap, 280, 280);
								} else {
									bitmap = ExpressionUtil.big(bitmap, 40, 40);
								}

								ImageSpan imageSpan = new ImageSpan(bitmap);
								SpannableString spannableString = new SpannableString(e.code);
								spannableString.setSpan(imageSpan, 0, e.code.length(),
										Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

								if (index < 0 || index >= edit.length()) {
									edit.append(spannableString);
								} else {
									edit.insert(index, spannableString);
								}
							}
						}
					});
					grids.add(gv);
					// --生成索引图--//
					ImageView iv = new ImageView(this);
					android.widget.LinearLayout.LayoutParams lp = new android.widget.LinearLayout.LayoutParams(
							chat_dot_wh, chat_dot_wh);
					lp.leftMargin = chat_dot_margin_lr;
					lp.rightMargin = chat_dot_margin_lr;
					iv.setLayoutParams(lp);
					if (i == 0) {
						iv.setBackgroundResource(R.drawable.page_focused);
					} else {
						iv.setBackgroundResource(R.drawable.page_unfocused);
					}
					ll_vp_selected_index.addView(iv);
				}
			}
			myPagerAdapter = new MyPagerAdapter(grids);
			vp_id.setAdapter(myPagerAdapter);
		}
		adapter.update();
		gridView.setAdapter(adapter);
		MobclickAgent.onResume(this);
	}

	/**
	 * 填充GridView所需要的数据
	 */
	private List<List<ExpressionData>> initGridViewData() {
		List<List<ExpressionData>> lists = new ArrayList<List<ExpressionData>>();
		List<ExpressionData> list = null;
		for (int i = 0; i < ExpressionUtil.expressionList.size(); i++) {
			if (i % pageExpressionCount == 0) {// 一页数据已填充完成
				if (null != list) {
					list.add(new ExpressionData(-1, "backSpace"));// 添加删除键
					lists.add(list);
				}
				list = new ArrayList<ExpressionData>();
			}
			list.add(ExpressionUtil.expressionList.get(i));
			// 最后一个表情，并且不是当前页最后一个表情时，后面添加删除键
			if (i >= ExpressionUtil.expressionList.size() - 1) {
				list.add(new ExpressionData(-1, "backSpace"));// 添加删除键
				lists.add(list);
			}
		}
		return lists;
	}

	class MyOnPageChangeListener implements OnPageChangeListener {

		@Override
		public void onPageScrollStateChanged(int arg0) {
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
		}

		@Override
		public void onPageSelected(int arg0) {
			updateSelectedIndex(arg0);
		}

		/**
		 * 更新当前ViewPager索引
		 * 
		 * @param currentSelectIndex
		 */
		private void updateSelectedIndex(int currentSelectIndex) {
			if (null != CommonEditActivity.this) {
				int childCount = CommonEditActivity.this.ll_vp_selected_index.getChildCount();
				for (int i = 0; i < childCount; i++) {
					if (currentSelectIndex == i) {
						CommonEditActivity.this.ll_vp_selected_index.getChildAt(i)
								.setBackgroundResource(R.drawable.page_focused);
					} else {
						CommonEditActivity.this.ll_vp_selected_index.getChildAt(i)
								.setBackgroundResource(R.drawable.page_unfocused);
					}
				}
			}
		}
	}
}

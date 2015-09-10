package cn.fszt.trafficapp.ui.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONException;
import org.json.JSONObject;

import com.umeng.analytics.MobclickAgent;

import cn.fszt.trafficapp.R;
import cn.fszt.trafficapp.domain.ExpressionData;
import cn.fszt.trafficapp.util.Constant;
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
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

/**
 * 精选活动 评论界面
 * 
 * @author AeiouKong
 *
 */
public class ActCommentEditActivity extends Activity implements OnClickListener,
		OnUploadProcessListener {
	private String _imageDir;
	private String _cameraDir;
	private static final int LOGININ = 99;
	private static final int VALIDATE = 98;
	private static final int REQ_LOGIN = 10;

	private ProgressDialog progressDialog;
	private Map<String, String> params = new HashMap<String, String>();
	private int uid; // 用户通行证id
	private String addpl_url;
	private UploadUtil upload;
	private EditText et_pl_content;
	private String content = "";
	private String hdinfoid, pingluntype, nickname;

	private String validate; // 是否认证用户
	boolean isupload, isuploaddone;
	private GridView gridView;
	private ArrayList<String> dataList = new ArrayList<String>();

	private static final int TAKE_PICTURE = 0x000000;
	private String path = "";
	private GridAdapter adapter;

	AppMsg.Style style = AppMsg.STYLE_INFO;
	AppMsg appMsg;

	int columns = 6, rows = 4, pageExpressionCount = 4 * 6 - 1;
	ArrayList<GridView> grids;
	MyPagerAdapter myPagerAdapter;
	ViewPager vp_id;
	LinearLayout ll_expression;
	public LinearLayout ll_vp_selected_index;
	TextView tv_expression;

	int densityDpi;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		DisplayMetrics metric = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metric);
		densityDpi = metric.densityDpi;
		setContentView(R.layout.activity_pinglun);

		initView();
		Init();

		getActionBar()
				.setTitle(getResources().getString(R.string.woyaopinglun));
		getActionBar().setBackgroundDrawable(
				new ColorDrawable(getResources().getColor(R.color.titlebar)));
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setDisplayShowHomeEnabled(false);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		MenuInflater inflater = getMenuInflater();

		inflater.inflate(R.menu.pinglunactivity, menu);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			break;

		case R.id.item_pinglun_submit:
			if (uid == 0) {
				Intent intent = new Intent(this, StLoginActivity.class);
				startActivityForResult(intent, LOGININ);
			} else {
				// 已登录并且已认证
				if ("true".equals(validate)) {

					// 判断是否黑名单
					new Thread() {
						public void run() {
							HashMap<String, String> map = new HashMap<String, String>();
							GetBlackList blacklist = new GetBlackList(
									ActCommentEditActivity.this, uid);
							map = blacklist.getblacklist();
							if (map != null && map.size() > 0) {
								String isblacklist = map.get("isblacklist");
								String message = map.get("message");
								if ("1".equals(isblacklist)) { // 是黑名单
									handler_upload.obtainMessage(
											GetBlackList.ISBLACKLIST, message)
											.sendToTarget();
								} else {// 不是黑名单
									handler_upload
											.sendEmptyMessage(GetBlackList.NOTBLACKLIST);
								}
							}
						}
					}.start();
				}
				// 跳转到验证界面
				else {
					showValidateDialog(
							getResources().getString(R.string.validatefirst),
							"确定", "取消");
				}
			}
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

			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {

				((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE))
						.hideSoftInputFromWindow(ActCommentEditActivity.this
								.getCurrentFocus().getWindowToken(),
								InputMethodManager.HIDE_NOT_ALWAYS);

				if (arg2 == Bimp.bmp.size()) {
					new PopupWindows(ActCommentEditActivity.this, gridView);
				} else {
					Intent intent = new Intent(ActCommentEditActivity.this,
							PhotoActivity.class);
					intent.putExtra("ID", arg2);
					startActivity(intent);
				}
			}
		});
	}

	private void initView() {
		_imageDir = Environment.getExternalStorageDirectory().getAbsolutePath()
				+ getResources().getString(R.string.phototemp_dir);
		_cameraDir = Environment.getExternalStorageDirectory()
				.getAbsolutePath()
				+ getResources().getString(R.string.phototake_dir);

		Intent intent = getIntent();
		hdinfoid = intent.getStringExtra("id");
		pingluntype = intent.getStringExtra("pingluntype");
		nickname = intent.getStringExtra("nickname");
		SharedPreferences sp = getSharedPreferences("USER",
				Context.MODE_PRIVATE);
		uid = sp.getInt("uid", 0);
		validate = sp.getString("validate", null);
		if (pingluntype.equals("huodong")) {
			addpl_url = getResources().getString(R.string.server_url)
					+ getResources().getString(R.string.hd_pl_add_url);
		} else if (pingluntype.equals("news")) {
			addpl_url = getResources().getString(R.string.server_url)
					+ getResources().getString(R.string.news_pl_add_url);
		}

		upload = UploadUtil.getInstance();
		upload.setOnUploadProcessListener(this);
		progressDialog = new ProgressDialog(this);
		progressDialog.setCancelable(true);
		progressDialog.setCanceledOnTouchOutside(false);
		et_pl_content = (EditText) findViewById(R.id.et_pl_content);
		if (nickname != null) {
			String content_nickname = "@" + nickname + "：";
			et_pl_content.setText(content_nickname);
			et_pl_content.setSelection(content_nickname.length());
		}
		et_pl_content.setOnClickListener(this);
		ll_vp_selected_index = (LinearLayout) findViewById(R.id.ll_vp_selected_index);
		ll_expression = (LinearLayout) findViewById(R.id.ll_expression);
		vp_id = (ViewPager) findViewById(R.id.vp_id);
		vp_id.setOnPageChangeListener(new MyOnPageChangeListener());
		tv_expression = (TextView) findViewById(R.id.tv_expression);
		tv_expression.setOnClickListener(this);
	}

	public void showValidateDialog(String message, String yes, String no) {

		AlertDialog.Builder builer = new Builder(ActCommentEditActivity.this);
		builer.setTitle("提示");
		builer.setMessage(message);
		builer.setCancelable(false);

		builer.setPositiveButton(yes, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				Intent intent = new Intent(ActCommentEditActivity.this,
						StUserValidateActivity.class);
				startActivityForResult(intent, VALIDATE);
			}
		});

		builer.setNegativeButton(no, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
			}
		});

		AlertDialog dialog = builer.create();
		dialog.show();
	}

	@Override
	public void onClick(View v) {
		if (v == tv_expression) {
			if (ll_expression.getVisibility() == View.GONE) {
				((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE))
						.hideSoftInputFromWindow(this.getCurrentFocus()
								.getWindowToken(),
								InputMethodManager.HIDE_NOT_ALWAYS);
				ll_expression.setVisibility(View.VISIBLE);
				tv_expression
						.setBackgroundResource(R.drawable.expression_pressed);
			} else {
				ll_expression.setVisibility(View.GONE);
				tv_expression.setBackgroundResource(R.drawable.expression);
			}
		}
		if (v == et_pl_content) {
			if (ll_expression.getVisibility() == View.VISIBLE) {
				ll_expression.setVisibility(View.GONE);
				tv_expression.setBackgroundResource(R.drawable.expression);
			}
		}
	}

	private void uploadPinglun() {
		if (!isupload) {
			for (int i = 0; i < Bimp.drr.size(); i++) {
				String Str = Bimp.drr.get(i).substring(
						Bimp.drr.get(i).lastIndexOf("/") + 1,
						Bimp.drr.get(i).lastIndexOf("."));
				dataList.add(FileUtils.SDPATH + Str + ".jpg");
			}
		}

		String fileKey = "img";
		if (dataList.size() == 0 && content.equals("")) {
			style = AppMsg.STYLE_ALERT;
			appMsg = AppMsg.makeText(this,
					getResources().getString(R.string.tips_pl_nonull), style);
			appMsg.show();
			isupload = false;
		} else {
			progressDialog.setMessage(getResources().getString(
					R.string.uploading));
			progressDialog.show();
			upload.uploadFile(dataList, fileKey, addpl_url, params);
			isupload = true;
			isuploaddone = true;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (requestCode == VALIDATE && resultCode == RESULT_OK) {
			if (data != null) {
				validate = data.getStringExtra("validate");
			}
		}

		if (requestCode == LOGININ && resultCode == REQ_LOGIN) {
			uid = data.getIntExtra("uid", 0);
			validate = data.getStringExtra("validate");
			style = AppMsg.STYLE_INFO;
			appMsg = AppMsg.makeText(this,
					getResources().getString(R.string.login_success), style);
			appMsg.setLayoutGravity(Gravity.BOTTOM);
			appMsg.show();
		}

		if (requestCode == TAKE_PICTURE) {
			if (Bimp.drr.size() < 4 && resultCode == -1) {
				int degree = PhotoUtil.readPictureDegree(path);
				Bitmap bm = PhotoUtil.createThumbFromFile(path, 400, 400);
				if (bm != null) {
					bm = PhotoUtil.rotaingImageView(degree, bm);

					File imageDir = new File(_imageDir);
					if (!imageDir.exists()) {
						imageDir.mkdirs();
					}

					String rotaingpaths = _imageDir
							+ String.valueOf(System.currentTimeMillis())
							+ ".jpg";
					boolean df = PhotoUtil.saveBitmap2file(bm, rotaingpaths);
					if (df) {
						Bimp.drr.add(rotaingpaths);
					}
				}
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
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
		msg.what = Constant.UPLOAD_FILE_DONE;
		msg.arg1 = responseCode;
		msg.obj = result;
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

			case Constant.UPLOAD_FILE_DONE:
				String result = msg.obj + "";
				JSONObject jsonObject;
				String message = "";
				if (result != null) {
					try {
						jsonObject = new JSONObject(result);
						message = jsonObject.getString("message");
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
				if (msg.arg1 == Constant.UPLOAD_SUCCESS_CODE) {
					setResult(Activity.RESULT_OK);
					finish();
				} else {
					isuploaddone = false;
					style = AppMsg.STYLE_INFO;
					appMsg = AppMsg.makeText(ActCommentEditActivity.this, message,
							style);
					appMsg.setLayoutGravity(Gravity.BOTTOM);
					appMsg.show();
				}
				break;

			case GetBlackList.ISBLACKLIST:
				Toast.makeText(ActCommentEditActivity.this, (String) msg.obj,
						Toast.LENGTH_SHORT).show();
				break;

			case GetBlackList.NOTBLACKLIST:
				// 提交
				content = et_pl_content.getText().toString();
				params.put("pinglun", hdinfoid + ";" + content + ";" + uid);
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

			View view = View
					.inflate(mContext, R.layout.item_popupwindows, null);
			view.startAnimation(AnimationUtils.loadAnimation(mContext,
					R.anim.fade_ins));
			LinearLayout ll_popup = (LinearLayout) view
					.findViewById(R.id.ll_popup);
			ll_popup.startAnimation(AnimationUtils.loadAnimation(mContext,
					R.anim.push_bottom_in_2));

			setWidth(LayoutParams.MATCH_PARENT);
			setHeight(LayoutParams.MATCH_PARENT);
			setBackgroundDrawable(new BitmapDrawable());
			setFocusable(true);
			setOutsideTouchable(true);
			setContentView(view);
			showAtLocation(parent, Gravity.BOTTOM, 0, 0);
			update();

			Button bt1 = (Button) view
					.findViewById(R.id.item_popupwindows_camera);
			Button bt2 = (Button) view
					.findViewById(R.id.item_popupwindows_Photo);
			Button bt3 = (Button) view
					.findViewById(R.id.item_popupwindows_cancel);
			bt1.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					photo();
					dismiss();
				}
			});
			bt2.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					Intent intent = new Intent(ActCommentEditActivity.this,
							AlbumActivity.class);
					startActivity(intent);
					dismiss();
				}
			});
			bt3.setOnClickListener(new OnClickListener() {
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

		File file = new File(_cameraDir, String.valueOf(System
				.currentTimeMillis()) + ".jpg");
		path = file.getPath();
		Uri imageUri = Uri.fromFile(file);
		openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
		startActivityForResult(openCameraIntent, TAKE_PICTURE);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		path = savedInstanceState.getString("path");
		// System.out.println("onRestoreInstanceStatepicPath~!!!!===="+path);
		super.onRestoreInstanceState(savedInstanceState);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putString("path", path);
		// System.out.println("onSaveInstanceStatepicPath~!!!!===="+path);
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

	protected void onRestart() {
		adapter.update();
		super.onRestart();
	}

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
			int gv_padding_lr = (int) getResources().getDimension(
					R.dimen.chat_gv_padding_lr);
			int gv_padding_bt = (int) getResources().getDimension(
					R.dimen.chat_gv_padding_bt);
			int gv_spacing = (int) getResources().getDimension(
					R.dimen.chat_gv_spacing);
			int chat_dot_margin_lr = (int) getResources().getDimension(
					R.dimen.chat_dot_margin_lr);
			int chat_dot_wh = (int) getResources().getDimension(
					R.dimen.chat_dot_wh);
			for (int i = 0; i < lists.size(); i++) {
				List<ExpressionData> l = lists.get(i);
				if (null != l) {
					// --生成当前GridView--//
					final GridView gv = new GridView(this);
					gv.setLayoutParams(new LayoutParams(
							LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
					gv.setNumColumns(columns);
					gv.setGravity(Gravity.CENTER);
					gv.setPadding(gv_padding_lr, gv_padding_bt, gv_padding_lr,
							0);
					gv.setHorizontalSpacing(gv_spacing);
					gv.setVerticalSpacing(gv_spacing);
					ExpressionImageAdapter expressionImageAdapter = new ExpressionImageAdapter(
							this, l);
					gv.setAdapter(expressionImageAdapter);
					// --点击列表事件处理--//
					gv.setOnItemClickListener(new OnItemClickListener() {
						@Override
						public void onItemClick(AdapterView<?> arg0, View arg1,
								int arg2, long arg3) {
							ExpressionData e = (ExpressionData) gv
									.getItemAtPosition(arg2);
							int index = et_pl_content.getSelectionStart();
							Editable edit = et_pl_content.getEditableText();// 获取EditText的文字
							String content_all = edit.toString();
							String content_forward = content_all.substring(0,
									index);
							String reg = "\\[em_[0-9]{1,3}?\\]";
							if (e.getDrableId() < 0) {// 点击删除按钮
								if (index > 0) {
									boolean delExpression = false;
									Pattern p = Pattern.compile(reg);
									Matcher matcher = p
											.matcher(content_forward);
									// 因为这里表情代码最长为5，所以这里减5
									boolean found = false;
									if (content_forward.length() >= 7) {// 如果光标前字符少于4个，说明不可能为表情
										if (content_forward.length() == 7) {
											found = matcher
													.find(content_forward
															.length() - 7);
										} else {
											found = matcher
													.find(content_forward
															.length() - 8);
										}
										if (found) {
											String flag = matcher.group();
											if (content_forward.substring(
													content_forward.length()
															- flag.length(),
													content_forward.length())
													.equals(flag)) {
												delExpression = true;
												edit.delete(
														index - flag.length(),
														index);
											}
										}
									}
									if (!delExpression) {
										edit.delete(index - 1, index);
									}
								}
							} else {
								Bitmap bitmap = BitmapFactory.decodeResource(
										getResources(), e.drableId);
								if (densityDpi == 320) {
									bitmap = ExpressionUtil.big(bitmap, 75, 75);
								} else if (densityDpi == 240) {
									bitmap = ExpressionUtil.big(bitmap, 40, 40);
								} else if (densityDpi == 160) {
									bitmap = ExpressionUtil.big(bitmap, 24, 24);
								} else if (densityDpi == 440) {
									bitmap = ExpressionUtil.big(bitmap, 130,
											130);
								} else if (densityDpi == 480) {
									bitmap = ExpressionUtil.big(bitmap, 160,
											160);
								} else if (densityDpi == 640) {
									bitmap = ExpressionUtil.big(bitmap, 280,
											280);
								} else {
									bitmap = ExpressionUtil.big(bitmap, 40, 40);
								}
								ImageSpan imageSpan = new ImageSpan(bitmap);
								SpannableString spannableString = new SpannableString(
										e.code);
								spannableString.setSpan(imageSpan, 0,
										e.code.length(),
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
		MobclickAgent.onResume(this);
	}

	public void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
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
			if (null != ActCommentEditActivity.this) {
				int childCount = ActCommentEditActivity.this.ll_vp_selected_index
						.getChildCount();
				for (int i = 0; i < childCount; i++) {
					if (currentSelectIndex == i) {
						ActCommentEditActivity.this.ll_vp_selected_index.getChildAt(i)
								.setBackgroundResource(R.drawable.page_focused);
					} else {
						ActCommentEditActivity.this.ll_vp_selected_index.getChildAt(i)
								.setBackgroundResource(
										R.drawable.page_unfocused);
					}
				}
			}
		}
	}
}

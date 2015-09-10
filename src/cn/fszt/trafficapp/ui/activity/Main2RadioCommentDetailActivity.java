package cn.fszt.trafficapp.ui.activity;

import java.io.IOException;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.format.DateFormat;
import android.text.style.ImageSpan;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import cn.fszt.trafficapp.R;
import cn.fszt.trafficapp.adapter.MyGridViewAdapter;
import cn.fszt.trafficapp.adapter.RadioCommentDetailAdapter;
import cn.fszt.trafficapp.domain.ExpressionData;
import cn.fszt.trafficapp.domain.RadioCommentDetailReplyData;
import cn.fszt.trafficapp.domain.RadioCommentListData;
import cn.fszt.trafficapp.util.Constant;
import cn.fszt.trafficapp.util.HttpUtil;
import cn.fszt.trafficapp.util.ShareCountUtil;
import cn.fszt.trafficapp.util.UploadUtil;
import cn.fszt.trafficapp.util.UploadUtil.OnUploadProcessListener;
import cn.fszt.trafficapp.widget.MyGridView2;
import cn.fszt.trafficapp.widget.MyListview;
import cn.fszt.trafficapp.widget.expression.ExpressionImageAdapter;
import cn.fszt.trafficapp.widget.expression.ExpressionUtil;
import cn.fszt.trafficapp.widget.expression.MyPagerAdapter;
import cn.fszt.trafficapp.widget.photoview.DisplayImageOptionsUtil;
import cn.fszt.trafficapp.widget.photoview.ViewPagerActivity;
import cn.fszt.trafficapp.widget.sharesdk.onekeyshare.OnekeyShare;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.umeng.analytics.MobclickAgent;

/**
 * 从其他地方跳转到节目互动评论详情
 * 
 * @author AeiouKong
 *
 */
public class Main2RadioCommentDetailActivity extends BaseBackActivity implements
		OnClickListener, OnUploadProcessListener, OnItemClickListener {

	private static final int UPLOAD_FILE_DONE = 2;
	private static final int SET_GOOD_SUCCEED = 3;
	private RadioCommentListData data;
	private TextView tv_bgdetail_nickname, tv_bgdetail_content,
			tv_bgdetail_createtime, tv_bgdetail_replynum,
			tv_bgdetail_introduce, tv_bg_good;
	private ImageView iv_bgdetail_headimg, iv_bgdetail_expression,
			iv_bgdetail_dj, iv_bg_good;
	private ImageLoader imageLoader;
	private DisplayImageOptions options_head, options,options_dj;
	private int densityDpi;
	private EditText et_bgdetail;
	private String[] imageUrls_small, imageUrls;
	private MyGridView2 gridview;
	private int imgheight;
	private int replynum = 0;
	private String goodnum;
	private MyListview listView;
	private LayoutInflater inflater;
	private List<RadioCommentDetailReplyData> arrays;
	private RadioCommentDetailAdapter adapter;
	private String getradiodetailpl_url, hdcommentid, setradiodetailpl_url,
			delbgpl_url, setgood_url, uid, api_url, transmiturl;
	private UploadUtil uploadUtil;
	private ProgressDialog progressDialog;
	private Map<String, String> params = new HashMap<String, String>();
	private Button btn_bgdetail_send;
	private ProgressBar pb_bgdetail;
	private SharedPreferences sp;
	private boolean isupload, isuploaddone;
	private RelativeLayout rl_send;
	private Intent intent;

	// 表情相关---------
	int columns = 6, rows = 4, pageExpressionCount = 4 * 6 - 1;
	ArrayList<GridView> grids;
	MyPagerAdapter myPagerAdapter;
	ViewPager vp_id;
	public LinearLayout ll_vp_selected_index;
	private LinearLayout ll_expression;
	// 表情相关---------

	private Intent bgdetailintent;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		DisplayMetrics metric = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metric);
		densityDpi = metric.densityDpi;

		setContentView(R.layout.activity_baoguangdetail);

		imageLoader = ImageLoader.getInstance();
		options = DisplayImageOptionsUtil.getOptions(R.drawable.ic_empty,
				R.drawable.ic_error, R.drawable.default_image);
		options_dj = DisplayImageOptionsUtil.getOptions(R.drawable.ic_transparent,
				R.drawable.ic_transparent, R.drawable.ic_transparent);
		options_head = DisplayImageOptionsUtil.getOptions(
				R.drawable.default_head_sq, R.drawable.default_head_sq,
				R.drawable.default_image, new RoundedBitmapDisplayer(15));

		initView();

		initData(new Handler(){
			@Override
			public void handleMessage(Message msg) {
				data = (RadioCommentListData) msg.obj;
				if(data!=null){
					showExpression();
					subCreatetime();
					tv_bgdetail_nickname.setText(data.getNickname());
					//TODO
					goodnum = data.getLikecount();
					tv_bg_good.setText(goodnum);
					transmiturl = data.getTransmiturl();
					String intro = data.getIntro();
					tv_bgdetail_introduce.setText(intro);
					imageLoader.displayImage(data.getHeadimg(), iv_bgdetail_headimg,
									options_head);
					imageLoader.displayImage(data.getIsdjpath(), iv_bgdetail_dj,
							options_dj);
					subImagepath_small();
					subImagepath();
					if (imageUrls != null && !imageUrls.equals("")) {
						gridview = (MyGridView2) findViewById(R.id.gridview);
						gridview.setVisibility(View.VISIBLE);
						MyGridViewAdapter imgAdapter = new MyGridViewAdapter(Main2RadioCommentDetailActivity.this);
						imgAdapter.setUrls(data.getImagepath_small());
						gridview.setAdapter(imgAdapter);
						gridview.setOnItemClickListener(new OnItemClickListener() {
							@Override
							public void onItemClick(AdapterView<?> parent, View view,
									int position, long id) {
								final Intent intent = new Intent(Main2RadioCommentDetailActivity.this,
										ViewPagerActivity.class);
								intent.putExtra("ID", position);
								intent.putExtra("imgurl", imageUrls);
								startActivity(intent);
							}
						});
					}
				}
			}
		});
		
		// 执行加载评论内容
		initArrays(new Handler() {
			@Override
			public void handleMessage(Message msg) {
				arrays = (List<RadioCommentDetailReplyData>) msg.obj;
				adapter = new RadioCommentDetailAdapter(Main2RadioCommentDetailActivity.this,
						arrays, inflater, densityDpi, delbgpl_url, uid);
				listView.setAdapter(adapter);
				if (arrays != null) {
					replynum = arrays.size();
				} else {
				}
				tv_bgdetail_replynum.setText(replynum + "");
				pb_bgdetail.setVisibility(View.GONE);
				adapter.notifyDataSetChanged();
			}
		});

		getActionBar().setTitle(getResources().getString(R.string.item_detail));
		getActionBar().setBackgroundDrawable(
				new ColorDrawable(getResources().getColor(R.color.titlebar)));
		getActionBar().setDisplayHomeAsUpEnabled(true);
	}

	private void initView() {
		bgdetailintent = getIntent();
		api_url = getString(R.string.api_url);
		getradiodetailpl_url = getString(R.string.server_url)
				+ getString(R.string.getradiodetailpl_url);
		setradiodetailpl_url = getString(R.string.server_url)
				+ getString(R.string.setradiodetailpl_url);
		delbgpl_url = getString(R.string.server_url)
				+ getString(R.string.delbgpl_url);
		setgood_url = getString(R.string.server_url)
				+ getString(R.string.setradiogood_url);
//		data = (RadioCommentListData) getIntent().getSerializableExtra(
//				"RadioCommentListData");
		tv_bgdetail_nickname = (TextView) findViewById(R.id.tv_bgdetail_nickname);
		
		tv_bg_good = (TextView) findViewById(R.id.tv_bg_good);
		
		tv_bgdetail_content = (TextView) findViewById(R.id.tv_bgdetail_content);
		tv_bgdetail_createtime = (TextView) findViewById(R.id.tv_bgdetail_createtime);
		tv_bgdetail_replynum = (TextView) findViewById(R.id.tv_bgdetail_replynum);
		tv_bgdetail_introduce = (TextView) findViewById(R.id.tv_bgdetail_introduce);
		iv_bgdetail_headimg = (ImageView) findViewById(R.id.iv_bgdetail_headimg);
		intent = getIntent();
		hdcommentid = intent.getStringExtra("hdcommentid");
		iv_bgdetail_expression = (ImageView) findViewById(R.id.iv_bgdetail_expression);
		iv_bgdetail_expression.setOnClickListener(this);
		iv_bg_good = (ImageView) findViewById(R.id.iv_bg_good);
		iv_bg_good.setOnClickListener(this);
		rl_send = (RelativeLayout) findViewById(R.id.rl_send);
		rl_send.setOnClickListener(this);
		iv_bgdetail_dj = (ImageView) findViewById(R.id.iv_bgdetail_dj);
		ll_expression = (LinearLayout) findViewById(R.id.ll_expression);
		et_bgdetail = (EditText) findViewById(R.id.et_bgdetail);
		et_bgdetail.setOnClickListener(this);
		et_bgdetail.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					if (uid != null) {
					} else {
						Intent intent = new Intent(Main2RadioCommentDetailActivity.this,
								StLoginActivity.class);
						startActivityForResult(intent,
								Constant.LOGIN_REQUESTCODE);
					}
				}
			}
		});
		ll_vp_selected_index = (LinearLayout) findViewById(R.id.ll_vp_selected_index);
		vp_id = (ViewPager) findViewById(R.id.vp_id);
		vp_id.setOnPageChangeListener(new MyOnPageChangeListener());

		inflater = getLayoutInflater();
		listView = (MyListview) findViewById(R.id.pdv_bgdetail);
		listView.setDivider(getResources().getDrawable(
				R.color.list_view_divider));
		listView.setDividerHeight(1);
		listView.setFocusable(false);
		listView.setCacheColorHint(Color.TRANSPARENT);
		listView.setOnItemClickListener(this);
		listView.setBackgroundColor(getResources().getColor(R.color.setting_bg));
		uploadUtil = UploadUtil.getInstance();
		uploadUtil.setOnUploadProcessListener(this);
		progressDialog = new ProgressDialog(this);
		progressDialog.setCanceledOnTouchOutside(false);
		progressDialog.setMessage(getResources().getString(R.string.uploading));
		sp = getSharedPreferences("USER", Context.MODE_PRIVATE);
		uid = sp.getString("uuid", null);
		btn_bgdetail_send = (Button) findViewById(R.id.btn_bgdetail_send);
		btn_bgdetail_send.setOnClickListener(this);
		pb_bgdetail = (ProgressBar) findViewById(R.id.pb_bgdetail);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		MenuInflater inflater = getMenuInflater();

		inflater.inflate(R.menu.baoguangdetail, menu);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			break;
		case R.id.item_bg_share:
			if (transmiturl != null && !transmiturl.equals("")) {
				showShare();
			} else {
				Toast.makeText(this, "转发失败", Toast.LENGTH_SHORT).show();
			}
			break;
		}
		return true;
	}


	private void showShare() {
		ShareSDK.initSDK(this);
		OnekeyShare oks = new OnekeyShare();
		oks.setTitle(data.getContent());
		String content = data.getContent();
		if (content.length() > 70) {
			content = content.substring(0, 70) + "...";
		}
		oks.setText(content + " " + transmiturl);
		if (imageUrls_small != null && imageUrls_small.length > 0) {
			oks.setImageUrl(imageUrls_small[0]);
		} else {
			oks.setImageUrl(getString(R.string.qrcode_url));
		}
		oks.setUrl(transmiturl);
		oks.setTitleUrl(transmiturl);
		oks.setCallback(new PlatformActionListener() {

			@Override
			public void onError(Platform platform, int arg1, Throwable arg2) {
				Toast.makeText(Main2RadioCommentDetailActivity.this, "分享失败",Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onComplete(Platform platform, int arg1,
					HashMap<String, Object> arg2) {
				
				final String plat = platform.toString();
				
				new Thread(){
					public void run() {
						ShareCountUtil.ShareCount(api_url, uid, hdcommentid, "4", 
								plat, "113.129151,23.024514");
					}
				}.start();
			}

			@Override
			public void onCancel(Platform platform, int arg1) {
				Toast.makeText(Main2RadioCommentDetailActivity.this, "取消分享",Toast.LENGTH_SHORT).show();
			}
		});
		// 启动分享GUI
		oks.show(this);
	}
	
	/**
	 * 内容中显示表情
	 */
	private void showExpression() {
		String zhengze = getString(R.string.expression_matches); // 正则表达式，用来判断消息内是否有表情
		String content = data.getContent();
		if(content!=null){
			SpannableString spannableString = ExpressionUtil
					.getExpressionString(Main2RadioCommentDetailActivity.this, content, zhengze, densityDpi);
			tv_bgdetail_content.setText(spannableString);
		}
	}

	/**
	 * 获取字符出现次数
	 * 
	 * @param src
	 * @param find
	 * @return
	 */
	public int getOccur(String src, String find) {
		int o = 0;
		int index = -1;
		while ((index = src.indexOf(find, index)) > -1) {
			++index;
			++o;
		}
		return o;
	}

	/**
	 * 格式化日期
	 */
	private void subCreatetime() {
		String date = data.getCreatedate();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		if(date!=null){
			try {
				Date ddate = sdf.parse(date);
				SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd HH:mm");
				date = dateFormat.format(ddate);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			tv_bgdetail_createtime.setText(date);
		}
	}

	/**
	 * 格式化图片
	 */
	private void subImagepath_small() {
		String imagepath = data.getImagepath_small();
		if (imagepath != null && !imagepath.equals("")) {
			imageUrls_small = imagepath.split(",");
		}
	}

	private void subImagepath() {
		String imagepath = data.getImagepath();
		if (imagepath != null && !imagepath.equals("")) {
			imageUrls = imagepath.split(",");
		}
	}

	/**
	 * 初始化数据
	 * 
	 * @param handler
	 */
	private void initArrays(final Handler handler) {
		new Thread(new Runnable() {
			List<RadioCommentDetailReplyData> lst = new ArrayList<RadioCommentDetailReplyData>();

			@Override
			public void run() {
				try {
					lst = getDataFromNetwork(1, 1000);
				} catch (Exception e) {
					e.printStackTrace();
				}
				handler.obtainMessage(0, lst).sendToTarget();
			}
		}).start();
	}
	/**
	 * 初始化数据
	 * 
	 * @param handler
	 */
	private void initData(final Handler handler) {
		new Thread(new Runnable() {
			
			RadioCommentListData data;
			
			@Override
			public void run() {
				try {
					data = getRadioCommentData();
				} catch (Exception e) {
					e.printStackTrace();
				}
				handler.obtainMessage(0, data).sendToTarget();
			}
		}).start();
	}

	/**
	 * 从网络获取数据
	 * 
	 * @return
	 * @throws IOException
	 * @throws ClientProtocolException
	 * @throws Exception
	 */
	private List<RadioCommentDetailReplyData> getDataFromNetwork(int start, int end)
			throws Exception {
		List<RadioCommentDetailReplyData> lst = new ArrayList<RadioCommentDetailReplyData>();

		String resultString = HttpUtil.GetStringFromUrl(getradiodetailpl_url
				+ "&hdcommentid=" + hdcommentid + "&startindex=" + start
				+ "&endindex=" + end);
		if (resultString != null) {
			Type listType = new TypeToken<List<RadioCommentDetailReplyData>>() {
			}.getType();
			Gson gson = new Gson();
			lst = gson.fromJson(resultString, listType);
			return lst;
		} else {
			return null;
		}
	}
	
	private RadioCommentListData getRadioCommentData(){
		
		RadioCommentListData data = new RadioCommentListData();
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("action", "GetHdCommentDetailByHdCommentid"));
		params.add(new BasicNameValuePair("hdcommentid", hdcommentid));
		String result = HttpUtil.PostStringFromUrl(api_url, params);
		if(result!=null){
			Gson gson = new Gson();
			data = gson.fromJson(result, RadioCommentListData.class);
			return data;
		}else{
			return null;
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_bg_good:
			if (uid != null) {
				new Thread() {
					public void run() {
						String result = HttpUtil.GetStringFromUrl(setgood_url
								+ "&connected_uid=" + uid + "&hdcommentid="
								+ hdcommentid);
						if (result != null) {
							try {
								JSONObject jsonObject = new JSONObject(result);
								String code = jsonObject.getString("code");
								if (("200").equals(code)) {
									handler_upload
											.sendEmptyMessage(SET_GOOD_SUCCEED);
								} 
							} catch (JSONException e) {
								e.printStackTrace();
							}
						}
					}
				}.start();
			}
			break;
		case R.id.iv_bgdetail_expression:
			if (ll_expression.getVisibility() == View.GONE) {
				((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE))
						.hideSoftInputFromWindow(this.getCurrentFocus()
								.getWindowToken(),
								InputMethodManager.HIDE_NOT_ALWAYS);
				ll_expression.setVisibility(View.VISIBLE);
				iv_bgdetail_expression
						.setBackgroundResource(R.drawable.expression_pressed);
			} else {
				ll_expression.setVisibility(View.GONE);
				iv_bgdetail_expression
						.setBackgroundResource(R.drawable.expression);
			}
			break;
		case R.id.et_bgdetail:
			if (uid != null) {
				ll_expression.setVisibility(View.GONE);
				iv_bgdetail_expression
						.setBackgroundResource(R.drawable.expression);
			} else {
				Intent intent = new Intent(this, StLoginActivity.class);
				startActivityForResult(intent, Constant.LOGIN_REQUESTCODE);
			}
			break;
		case R.id.btn_bgdetail_send:
			if (uid != null) {
				// 提交
				if (!isuploaddone) {
					String content = et_bgdetail.getText().toString().trim();
					if (content.equals("")) {
						Toast.makeText(Main2RadioCommentDetailActivity.this,
								getString(R.string.tips_pl_nonull),
								Toast.LENGTH_SHORT).show();
						isupload = false;
					} else {
						progressDialog.show();
						params.put("content", content);
						params.put("connected_uid", uid);
						params.put("hdcommentid", hdcommentid);
						uploadUtil.uploadFile(null, "pl", setradiodetailpl_url,
								params);
						isupload = true;
						isuploaddone = true;
					}
				}
			} else {
				Intent intent = new Intent(this, StLoginActivity.class);
				startActivityForResult(intent, Constant.LOGIN_REQUESTCODE);
			}

			break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (requestCode == Constant.LOGIN_REQUESTCODE
				&& resultCode == RESULT_OK) {
			uid = data.getStringExtra("uid");
		}

		super.onActivityResult(requestCode, resultCode, data);
	}


	@Override
	protected void onDestroy() {
		if (ExpressionUtil.expressionList != null) {
			ExpressionUtil.expressionList.clear();
		}
		super.onDestroy();
	}

	public void onResume() {
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
							LayoutParams.MATCH_PARENT,
							LayoutParams.WRAP_CONTENT));
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
							int index = et_bgdetail.getSelectionStart();
							Editable edit = et_bgdetail.getEditableText();// 获取EditText的文字
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
									boolean found = false;
									if (content_forward.length() >= 7) {// 如果光标前字符少于6个，说明不可能为表情
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
									bitmap = ExpressionUtil.big(bitmap, 150,
											150);
								} else if (densityDpi == 480) {
									bitmap = ExpressionUtil.big(bitmap, 160,
											160);
								} else if (densityDpi == 640) {
									bitmap = ExpressionUtil.big(bitmap, 320,
											320);
								} else {
									bitmap = ExpressionUtil.big(bitmap, 100,
											100);
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
			if (null != Main2RadioCommentDetailActivity.this) {
				int childCount = Main2RadioCommentDetailActivity.this.ll_vp_selected_index
						.getChildCount();
				for (int i = 0; i < childCount; i++) {
					if (currentSelectIndex == i) {
						Main2RadioCommentDetailActivity.this.ll_vp_selected_index
								.getChildAt(i).setBackgroundResource(
										R.drawable.page_focused);
					} else {
						Main2RadioCommentDetailActivity.this.ll_vp_selected_index
								.getChildAt(i).setBackgroundResource(
										R.drawable.page_unfocused);
					}
				}
			}
		}
	}

	public class ImageAdapter extends BaseAdapter {
		@Override
		public int getCount() {
			return imageUrls_small.length;
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			final ImageView imageView;
			if (convertView == null) {
				convertView = LayoutInflater.from(Main2RadioCommentDetailActivity.this)
						.inflate(R.layout.item_grid_image, null);

				AbsListView.LayoutParams param = new AbsListView.LayoutParams(
						android.view.ViewGroup.LayoutParams.MATCH_PARENT,
						imgheight);
				convertView.setLayoutParams(param);

				imageView = (ImageView) convertView;
			} else {
				imageView = (ImageView) convertView;
			}

			imageLoader.displayImage(imageUrls_small[position], imageView,
					options);

			return imageView;
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
			case SET_GOOD_SUCCEED:
				if (goodnum != null) {
					int int_goodnum = Integer.parseInt(goodnum);
					goodnum = int_goodnum + 1 + "";
					tv_bg_good.setText(goodnum);
					bgdetailintent.putExtra("goodnum", goodnum);
					setResult(RESULT_OK, bgdetailintent);
				}
				break;
			case UPLOAD_FILE_DONE:
				isuploaddone = false;
				if (getResources().getString(R.string.upload_success).equals(
						msg.obj + "")) {
					// 刷新listview
					// addChat();
					initArrays(new Handler() {
						@Override
						public void handleMessage(Message msg) {
							arrays = (List<RadioCommentDetailReplyData>) msg.obj;
							adapter = new RadioCommentDetailAdapter(
									Main2RadioCommentDetailActivity.this, arrays, inflater,
									densityDpi, delbgpl_url, uid);
							listView.setAdapter(adapter);
							if (arrays != null) {
								replynum = arrays.size();
								bgdetailintent.putExtra("replynum", replynum);
								setResult(RESULT_OK, bgdetailintent);
							} else {
							}
							tv_bgdetail_replynum.setText(replynum + "");
							pb_bgdetail.setVisibility(View.GONE);
							adapter.notifyDataSetChanged();
						}
					});
					et_bgdetail.setText("");
					((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE))
							.hideSoftInputFromWindow(Main2RadioCommentDetailActivity.this
									.getCurrentFocus().getWindowToken(),
									InputMethodManager.HIDE_NOT_ALWAYS);
					ll_expression.setVisibility(View.GONE);
				} else {
					Toast.makeText(Main2RadioCommentDetailActivity.this, "发送失败",
							Toast.LENGTH_SHORT).show();
				}
				break;

			}
			super.handleMessage(msg);
		}

	};

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO Auto-generated method stub
		String content = "@" + arrays.get(position).getNickname() + "：";
		et_bgdetail.setText(content);
		et_bgdetail.setSelection(content.length());
	}

	private void addChat() {
		long sysTime = System.currentTimeMillis();
		CharSequence sysTimeStr = DateFormat.format("yyyy-MM-dd kk:mm:ss",
				sysTime);
		RadioCommentDetailReplyData d = new RadioCommentDetailReplyData();
		d.setContent(et_bgdetail.getText().toString());
		d.setCreatedate((String) sysTimeStr);
		d.setNickname(sp.getString("nickname", null));
		d.setHeadimg(sp.getString("headimg", null));
		if (arrays == null) {
			arrays = new ArrayList<RadioCommentDetailReplyData>();
		}
		arrays.add(0, d);
		adapter = new RadioCommentDetailAdapter(Main2RadioCommentDetailActivity.this, arrays,
				inflater, densityDpi, delbgpl_url, uid);
		listView.setAdapter(adapter);
		adapter.notifyDataSetChanged();
		replynum = replynum + 1;
		tv_bgdetail_replynum.setText(replynum + "");
	}

}

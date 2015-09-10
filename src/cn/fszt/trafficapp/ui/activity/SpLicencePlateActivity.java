package cn.fszt.trafficapp.ui.activity;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cn.fszt.trafficapp.R;
import cn.fszt.trafficapp.db.DBManager;
import cn.fszt.trafficapp.domain.CarInfoData;
import cn.fszt.trafficapp.util.CarTypeDictionary;
import cn.fszt.trafficapp.util.Constant;
import cn.fszt.trafficapp.util.HttpUtil;
import cn.fszt.trafficapp.widget.SpinerAdapter;
import cn.fszt.trafficapp.widget.SpinerAdapter.IOnItemSelectListener;
import cn.fszt.trafficapp.widget.SpinerPopWindow;
import cn.fszt.trafficapp.widget.msg.AppMsg;
import cn.fszt.trafficapp.widget.photoview.ViewPagerActivity;

/**
 * 快捷查违章
 */
@SuppressLint("ShowToast")
public class SpLicencePlateActivity extends Activity implements OnClickListener, OnCheckedChangeListener {

	private SharedPreferences sp_user;
	private DBManager db;
	List<CarInfoData> carlist;
	private EditText et_licence_no;
	private EditText et_licence_validation;
	private MenuItem item_carinfo_update;
	private ImageView iv_licence_hint;
	private boolean flag_input = false;
	private String urls[] = { "http://fsgajj.cn/fsjj/images/jsysfzfb.jpg" };
	private String uid, carnum, api_url;
	private ProgressDialog pd;

	// --start 下拉选择
	private TextView tvsp_licence_no;
	private TextView tvsp_licence_type;
	private RelativeLayout rlsp_licence_no, rlsp_licence_type, rl_more;
	private Button btn_querypeccancy, btn_queryagency;
	private List<String> car_noList = new ArrayList<String>();
	private List<String> car_typeList = new ArrayList<String>();
	private SpinerAdapter noAdapter;
	private SpinerAdapter typeAdapter;
	private SpinerPopWindow spinerView_No;
	private SpinerPopWindow spinerView_Type;
	// --end

	private int screen_height; // 屏幕高度

	private RadioGroup rg_licence_cars;
	private RadioButton rg_licence_frist, rg_licence_second, rg_licence_third;

	private static String cur_carAttribution, cur_carNum, cur_carType, cur_validation, cur_hdpersoncarid;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splicenceplate);
		WindowManager wm = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
		screen_height = wm.getDefaultDisplay().getHeight();
		initView();
		initSpinnerData();
		initCarInfo();
		if (carnum != null) {
			
		}
		getActionBar().setTitle(getResources().getString(R.string.mylicenceplate));
		getActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.titlebar)));
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setDisplayShowHomeEnabled(false);
	}

	private void initView() {
		sp_user = getSharedPreferences("USER", Context.MODE_PRIVATE);
		uid = sp_user.getString("uuid", null);
		carnum = sp_user.getString("carnum", null);
		db = new DBManager(this);
		api_url = this.getResources().getString(R.string.api_url);
		et_licence_no = (EditText) findViewById(R.id.et_licence_no);
		et_licence_validation = (EditText) findViewById(R.id.et_licence_validation);
		rlsp_licence_no = (RelativeLayout) findViewById(R.id.rlsp_licence_no);
		rlsp_licence_no.setOnClickListener(this);
		rlsp_licence_no.setEnabled(false);
		rlsp_licence_type = (RelativeLayout) findViewById(R.id.rlsp_licence_type);
		rlsp_licence_type.setOnClickListener(this);
		rlsp_licence_type.setEnabled(false);
		tvsp_licence_no = (TextView) findViewById(R.id.tvsp_licence_no);
		tvsp_licence_type = (TextView) findViewById(R.id.tvsp_licence_type);
		rg_licence_cars = (RadioGroup) findViewById(R.id.rg_licence_cars);
		rg_licence_cars.setOnCheckedChangeListener(this);
		rg_licence_frist = (RadioButton) findViewById(R.id.rg_licence_frist);
		rg_licence_second = (RadioButton) findViewById(R.id.rg_licence_second);
		rg_licence_third = (RadioButton) findViewById(R.id.rg_licence_third);
		iv_licence_hint = (ImageView) findViewById(R.id.iv_licence_hint);
		iv_licence_hint.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(SpLicencePlateActivity.this, ViewPagerActivity.class);
				intent.putExtra("imgurl", urls);
				startActivity(intent);
			}
		});
		btn_querypeccancy = (Button) findViewById(R.id.btn_querypeccancy);
		btn_querypeccancy.setOnClickListener(this);
		btn_queryagency = (Button) findViewById(R.id.btn_queryagency);
		btn_queryagency.setOnClickListener(this);
		rl_more = (RelativeLayout) findViewById(R.id.rl_more);
		rl_more.setOnClickListener(this);
		pd = new ProgressDialog(this);
		pd.setMessage(getResources().getString(R.string.connecting));
		pd.setCanceledOnTouchOutside(false);
	}

	public void initCarInfo() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				String url = getResources().getString(R.string.server_url) + "GetHdpersoncar&" + "connected_uid=" + uid;
				String result = HttpUtil.GetStringFromUrl(url);
				if (!result.isEmpty()) {
					Gson gson = new Gson();
					Type type = new TypeToken<List<CarInfoData>>() {
					}.getType();
					carlist = gson.fromJson(result, type);
					db.deleteCarInfoAll();
					for (int i = 0; i < carlist.size(); i++) {
						db.addCarInfo(carlist.get(i));
					}
				} else {
					carlist = db.queryCarInfoAll();
				}
				handler.sendEmptyMessage(Constant.GETDATA_SUCCEED);
			}
		}).start();
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.mycarnum, menu);
		item_carinfo_update = menu.findItem(R.id.item_carinfo_update);
		item_carinfo_update.setTitle(R.string.item_userinfo_edit);
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			this.finish();
			break;
		case R.id.item_carinfo_delete:
			pd.show();
			deleteCarInfo();
			break;
		case R.id.item_carinfo_update:
			if (flag_input) {
				pd.show();
				// 上传资料
				updateCarInfo();
			} else {
				rlsp_licence_no.setEnabled(true);
				rlsp_licence_type.setEnabled(true);
				et_licence_no.setEnabled(true);
				et_licence_no.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
				et_licence_no.setTextColor(getResources().getColor(R.color.options_item_text));
				et_licence_validation.setEnabled(true);
				et_licence_validation.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
				et_licence_validation.setTextColor(getResources().getColor(R.color.options_item_text));

				item_carinfo_update.setTitle(R.string.item_userinfo_save);
				flag_input = true;
			}
			break;
		}
		return true;
	}

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			pd.dismiss();
			String message = msg.obj + "";
			switch (msg.what) {
			case Constant.UPDATEUSERINFO_SUCCEED:
				initCarInfo();
				CheckedChanged(rg_licence_cars.getCheckedRadioButtonId());
				showMsg(message);
				break;
			case Constant.UPDATEUSERINFO_FAILED:
				showMsg(message);
				break;
			case Constant.UPDATEUSERINFO_FAILED_NULL:
				showMsg(getString(R.string.returndata_null));
				break;
			case Constant.GETDATA_SUCCEED:
				CheckedChanged(rg_licence_cars.getCheckedRadioButtonId());
				break;
			case Constant.DELETE_SUCCEED:
				initCarInfo();
				CheckedChanged(rg_licence_cars.getCheckedRadioButtonId());
				showMsg(message);
				db.deleteCarInfoByCarInfoId(cur_hdpersoncarid);
				break;
			case Constant.DELETE_FAILED:
				showMsg(message);
				break;
			}
		}
	};

	private void updateCarInfo() {
		et_licence_no.setEnabled(false);
		et_licence_validation.setEnabled(false);
		rlsp_licence_no.setEnabled(false);
		rlsp_licence_type.setEnabled(false);
		item_carinfo_update.setTitle(R.string.item_userinfo_edit);
		flag_input = false;
		final String car_no = tvsp_licence_no.getText().toString() + "," + et_licence_no.getText().toString().trim();
		final String validation = et_licence_validation.getText().toString().trim();
		final String car_type = new CarTypeDictionary().GetMapValueByKey(tvsp_licence_type.getText().toString());
		if (et_licence_no.getText().toString().trim().isEmpty()) {
			showMsg("车牌不能为空！");
			pd.dismiss(); // 隐藏加载条
			return;
		}
		if (validation.isEmpty() || validation.length() != 4) {
			showMsg("验证资料为车架号或发动机号后4位,点击问号查看样例！");
			pd.dismiss();
			return;
		}
		if (cur_carNum.isEmpty() && cur_validation.isEmpty()) {
			new Thread() {
				public void run() {
					List<NameValuePair> params = new ArrayList<NameValuePair>();
					params.add(new BasicNameValuePair("action", "InsertHdpersoncar"));
					params.add(new BasicNameValuePair("connected_uid", uid));
					params.add(new BasicNameValuePair("carnum", car_no));
					params.add(new BasicNameValuePair("cartypecode", car_type));
					params.add(new BasicNameValuePair("lastfour", validation));
					String result = HttpUtil.PostStringFromUrl(api_url, params);
					try {
						if (!result.isEmpty()) {
							JSONObject jsonObject;
							jsonObject = new JSONObject(result);
							String code = jsonObject.getString("code");
							String message = jsonObject.getString("message");
							if ("200".equals(code)) {
								handler.obtainMessage(Constant.UPDATEUSERINFO_SUCCEED, message).sendToTarget();
							} else {
								handler.obtainMessage(Constant.UPDATEUSERINFO_FAILED, message).sendToTarget();
							}
						} else {
							handler.sendEmptyMessage(Constant.UPDATEUSERINFO_FAILED_NULL);
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			}.start();
		} else {
			if ((cur_carAttribution + "," + cur_carNum).equals(car_no) && cur_validation.equals(validation)
					&& cur_carType.equals(tvsp_licence_type.getText().toString().trim())) {
				showMsg("您还没有修改任何信息！");
				pd.dismiss();
				return;
			}
			new Thread() {
				public void run() {
					List<NameValuePair> params = new ArrayList<NameValuePair>();
					params.add(new BasicNameValuePair("action", "UpdateHdpersoncarById"));
					params.add(new BasicNameValuePair("hdpersoncarid", cur_hdpersoncarid));
					params.add(new BasicNameValuePair("carnum", car_no));
					params.add(new BasicNameValuePair("cartypecode", car_type));
					params.add(new BasicNameValuePair("lastfour", validation));
					String result = HttpUtil.PostStringFromUrl(api_url, params);
					try {
						if (!result.isEmpty()) {
							JSONObject jsonObject;
							jsonObject = new JSONObject(result);
							String code = jsonObject.getString("code");
							String message = jsonObject.getString("message");
							if ("200".equals(code)) {
								handler.obtainMessage(Constant.UPDATEUSERINFO_SUCCEED, message).sendToTarget();
							} else {
								handler.obtainMessage(Constant.UPDATEUSERINFO_FAILED, message).sendToTarget();
							}
						} else {
							handler.sendEmptyMessage(Constant.UPDATEUSERINFO_FAILED_NULL);
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			}.start();
		}
	}

	private void deleteCarInfo() {
		String car_no = et_licence_no.getText().toString().trim();
		String validation = et_licence_validation.getText().toString().trim();
		if (car_no.isEmpty() || validation.isEmpty()) {
			showMsg("删除失败！");
			pd.dismiss(); // 隐藏加载条
			return;
		}
		new Thread() {
			public void run() {
				List<NameValuePair> params = new ArrayList<NameValuePair>();
				params.add(new BasicNameValuePair("action", "DeleteHdpersoncar"));
				params.add(new BasicNameValuePair("hdpersoncarid", cur_hdpersoncarid));
				String result = HttpUtil.PostStringFromUrl(api_url, params);
				try {
					if (!result.isEmpty()) {
						JSONObject jsonObject;
						jsonObject = new JSONObject(result);
						String code = jsonObject.getString("code");
						String message = jsonObject.getString("message");
						if ("200".equals(code)) {
							handler.obtainMessage(Constant.DELETE_SUCCEED, message).sendToTarget();
						} else {
							handler.obtainMessage(Constant.DELETE_FAILED, message).sendToTarget();
						}
					} else {
						handler.sendEmptyMessage(Constant.UPDATEUSERINFO_FAILED_NULL);
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}.start();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.rlsp_licence_no:
			spinerView_No.setWidth(rlsp_licence_no.getWidth());
			spinerView_No.setHeight(screen_height / 2);
			spinerView_No.showAsDropDown(rlsp_licence_no);
			break;
		case R.id.rlsp_licence_type:
			spinerView_Type.setWidth(rlsp_licence_type.getWidth() / 2);
			spinerView_Type.setHeight(screen_height / 2);
			spinerView_Type.showAsDropDown(rlsp_licence_type);
			break;
		case R.id.btn_querypeccancy:

			break;
		case R.id.btn_queryagency:

			break;
		case R.id.rl_more:
			Intent intent = new Intent(SpLicencePlateActivity.this, CarServiceActivity.class);
			startActivity(intent);
			break;
		}
	}

	private void setCarInfo() {
		Editor editor = sp_user.edit();
		editor.putString("carnum", "");
		editor.commit();
	}

	private void initSpinnerData() {
		String[] car_nos = getResources().getStringArray(R.array.car_no);
		String[] car_types = getResources().getStringArray(R.array.car_type);
		for (int i = 0; i < car_nos.length; i++) {
			car_noList.add(car_nos[i]);
		}
		for (int i = 0; i < car_types.length; i++) {
			car_typeList.add(car_types[i]);
		}
		// 加载车牌号前面的下拉菜单
		noAdapter = new SpinerAdapter(this);
		noAdapter.refreshData(car_noList, 0);
		spinerView_No = new SpinerPopWindow(this);
		spinerView_No.setAdatper(noAdapter);
		spinerView_No.setItemListener(new CarNoSpinner());
		// 加载车型
		typeAdapter = new SpinerAdapter(this);
		typeAdapter.refreshData(car_typeList, 0);
		spinerView_Type = new SpinerPopWindow(this);
		spinerView_Type.setAdatper(typeAdapter);
		spinerView_Type.setItemListener(new CarTypeSpinner());
	}

	/**
	 * 车牌下拉监听
	 */
	class CarNoSpinner implements IOnItemSelectListener {
		@Override
		public void onItemClick(int pos) {
			tvsp_licence_no.setText(car_noList.get(pos));
		}
	}

	/**
	 * 车类型下拉监听
	 */
	class CarTypeSpinner implements IOnItemSelectListener {
		@Override
		public void onItemClick(int pos) {
			tvsp_licence_type.setText(car_typeList.get(pos));
		}
	}

	/*
	 * 根据选择的车辆绑定数据
	 */
	private void CheckedChanged(int checkid) {
		if (carlist.size() > 0 && !carlist.get(0).getCarnum().isEmpty()) {
			rg_licence_frist.setText(carlist.get(0).getCarnum().replace(",", ""));
		} else {
			rg_licence_frist.setText(getResources().getString(R.string.fristcar));
		}
		if (carlist.size() > 1 && !carlist.get(1).getCarnum().isEmpty()) {
			rg_licence_second.setText(carlist.get(1).getCarnum().replace(",", ""));
		} else {
			rg_licence_second.setText(getResources().getString(R.string.secondcar));
		}
		if (carlist.size() > 2 && !carlist.get(2).getCarnum().isEmpty()) {
			rg_licence_third.setText(carlist.get(2).getCarnum().replace(",", ""));
		} else {
			rg_licence_third.setText(getResources().getString(R.string.thirdcar));
		}
		int location = 0;
		switch (checkid) {
		case R.id.rg_licence_frist:
			location = 0;
			break;
		case R.id.rg_licence_second:
			location = 1;
			break;
		case R.id.rg_licence_third:
			location = 2;
			break;
		}
		if (carlist.size() > location && carlist.get(location) != null) {
			// ui赋值
			String cartype = new CarTypeDictionary().GetMapkeyByValue(carlist.get(location).getCartypecode());
			String[] carnum = carlist.get(location).getCarnum().split(",");
			tvsp_licence_no.setText(carnum[0].toString());
			et_licence_no.setText(carnum[1].toString());
			tvsp_licence_type.setText(cartype);
			et_licence_validation.setText(carlist.get(location).getLastfour());
			// 数据静态缓存...用于判断用户修改情况
			cur_carAttribution = carnum[0].toString();
			cur_carNum = carnum[1].toString();
			cur_carType = cartype;
			cur_validation = carlist.get(location).getLastfour();
			cur_hdpersoncarid = carlist.get(location).getHdpersoncarid();
		} else {
			tvsp_licence_no.setText("粤");
			et_licence_no.setText("");
			tvsp_licence_type.setText("小型汽车");
			et_licence_validation.setText("");
			cur_carAttribution = "";
			cur_carNum = "";
			cur_carType = "";
			cur_validation = "";
			cur_hdpersoncarid = "";
		}
	}

	/*
	 * 监听选择的第几辆车
	 */
	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		CheckedChanged(checkedId);
	}

	/*
	 * 设置弹框
	 */
	private void showMsg(String msg) {
		AppMsg appMsg = AppMsg.makeText(this, msg, AppMsg.STYLE_INFO);
		appMsg.setLayoutGravity(Gravity.BOTTOM);
		appMsg.show();
	}
}

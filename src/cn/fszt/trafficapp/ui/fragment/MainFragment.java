package cn.fszt.trafficapp.ui.fragment;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.search.MKAddrInfo;
import com.baidu.mapapi.search.MKBusLineResult;
import com.baidu.mapapi.search.MKDrivingRouteResult;
import com.baidu.mapapi.search.MKGeocoderAddressComponent;
import com.baidu.mapapi.search.MKPoiResult;
import com.baidu.mapapi.search.MKSearch;
import com.baidu.mapapi.search.MKSearchListener;
import com.baidu.mapapi.search.MKShareUrlResult;
import com.baidu.mapapi.search.MKSuggestionResult;
import com.baidu.mapapi.search.MKTransitRouteResult;
import com.baidu.mapapi.search.MKWalkingRouteResult;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import cn.fszt.trafficapp.R;
import cn.fszt.trafficapp.adapter.MainAdListGridViewAdapter;
import cn.fszt.trafficapp.adapter.MainAdapter;
import cn.fszt.trafficapp.adapter.MainHotListAdapter;
import cn.fszt.trafficapp.db.DBManager;
import cn.fszt.trafficapp.domain.HomeInfoData;
import cn.fszt.trafficapp.domain.MainAdListData;
import cn.fszt.trafficapp.domain.MainHotListData;
import cn.fszt.trafficapp.ui.MyApplication;
import cn.fszt.trafficapp.ui.activity.ActDetailActivity;
import cn.fszt.trafficapp.ui.activity.ActListActivity;
import cn.fszt.trafficapp.ui.activity.CarServiceActivity;
import cn.fszt.trafficapp.ui.activity.Main2RadioCommentDetailActivity;
import cn.fszt.trafficapp.ui.activity.Main2ShareDetailActivity;
import cn.fszt.trafficapp.ui.activity.MapRoadInfoActivity;
import cn.fszt.trafficapp.ui.activity.MultimediaActivity;
import cn.fszt.trafficapp.ui.activity.MyMessageActivity;
import cn.fszt.trafficapp.ui.activity.ReportEditActivity;
import cn.fszt.trafficapp.ui.activity.SettingActivity;
import cn.fszt.trafficapp.ui.activity.ShareListActivity;
import cn.fszt.trafficapp.ui.activity.SpLicencePlateActivity;
import cn.fszt.trafficapp.ui.activity.StLoginActivity;
import cn.fszt.trafficapp.ui.activity.StUserInfoActivity;
import cn.fszt.trafficapp.ui.activity.TrafficTotalActivity;
import cn.fszt.trafficapp.util.AppClickCount;
import cn.fszt.trafficapp.util.Constant;
import cn.fszt.trafficapp.util.HttpUtil;
import cn.fszt.trafficapp.util.version.ResourceInfo;
import cn.fszt.trafficapp.util.version.VersionUtil;
import cn.fszt.trafficapp.widget.MyGridView2;
import cn.fszt.trafficapp.widget.MyListview;
import cn.fszt.trafficapp.widget.msg.AppMsg;
import cn.fszt.trafficapp.widget.photoview.DisplayImageOptionsUtil;
import cn.fszt.trafficapp.widget.viewflow.CircleFlowIndicator;
import cn.fszt.trafficapp.widget.viewflow.ImageAdapter;
import cn.fszt.trafficapp.widget.viewflow.ViewFlow;

/**
 * 首页
 * 
 * @author AeiouKong
 *
 */
public class MainFragment extends Fragment implements OnClickListener, OnItemClickListener {

	private MyGridView2 gv_main;
	private CircleFlowIndicator indic;
	private MainAdapter adapter;
	private MainAdListGridViewAdapter adadapter;
	private ArrayList<HomeInfoData> arr_homeinfo;
	private ViewFlow viewFlow;
	private ScrollView sv_viewflow;
	AppMsg.Style style = AppMsg.STYLE_INFO;
	AppMsg appMsg;
	private SharedPreferences sp_user, sp_push;
	private Editor edit;
	private DBManager mgr;
	private ResourceInfo info;
	private String localVersion, push, gethomeinfo_url, uid, mobile;
	VersionUtil version;
	private ImageLoader imageLoader;
	private DisplayImageOptions options;

	private LayoutInflater hotinflater;
	private List<MainHotListData> arrays;
	private List<MainHotListData> sql_arrays;
	private List<MainAdListData> adarrays;
	private List<MainAdListData> sql_adarrays;
	// private PullDownView pullDownView;
	private MyListview listView;
	private MainHotListAdapter hotadapter;
	private String api_url, strInfo;
	private ImageView iv_main_me, iv_main_message, iv_circle;
	private Activity act;
	private MyGridView2 gv_adlist;
	private boolean StcollectReply, LatestNotification, AwardNotification;
	private TextView tv_weather;
	private LocationClient mLocClient;
	private MKSearch mSearch = null;
	public MyLocationListenner myListener = new MyLocationListenner();
	private double lng, lat;
	private int densityDpi;

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		DisplayMetrics metric = new DisplayMetrics();
		getActivity().getWindowManager().getDefaultDisplay().getMetrics(metric);
		densityDpi = metric.densityDpi;

		View layout = inflater.inflate(R.layout.newmain, container, false);
		iv_main_me = (ImageView) layout.findViewById(R.id.iv_main_me);
		iv_main_me.setOnClickListener(this);
		iv_main_message = (ImageView) layout.findViewById(R.id.iv_main_message);
		iv_main_message.setOnClickListener(this);
		tv_weather = (TextView) layout.findViewById(R.id.tv_weather);
		iv_circle = (ImageView) layout.findViewById(R.id.iv_circle);
		if (LatestNotification | AwardNotification) {
			iv_circle.setVisibility(View.VISIBLE);
		}
		gv_main = (MyGridView2) layout.findViewById(R.id.gv_main);
		gv_adlist = (MyGridView2) layout.findViewById(R.id.gv_adlist);
		gv_adlist.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				new AppClickCount(act, "广告文章");
				Intent intent = new Intent(getActivity(), ActDetailActivity.class);
				intent.putExtra("hdinfoid", adarrays.get(position).getHdinfoid());
				intent.putExtra("contenttype", "ad");
				startActivity(intent);
			}
		});
		sv_viewflow = (ScrollView) layout.findViewById(R.id.sv_viewflow);
		viewFlow = (ViewFlow) layout.findViewById(R.id.viewflow);
		indic = (CircleFlowIndicator) layout.findViewById(R.id.viewflowindic);
		listView = (MyListview) layout.findViewById(R.id.newmain_hot);
		listView.setDivider(getResources().getDrawable(R.color.list_view_divider));
		listView.setDividerHeight(1);
		listView.setFocusable(false);
		listView.setCacheColorHint(Color.TRANSPARENT);
		listView.setOnItemClickListener(this);

		hotinflater = act.getLayoutInflater();

		updateData();

		initView();

		// initLoc();
		// initSearch();
		// getloc(1*1000);

		return layout;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mSearch = new MKSearch();

		act = getActivity();
		sp_user = act.getSharedPreferences("USER", Context.MODE_PRIVATE);
		sp_push = act.getSharedPreferences("PUSH", Context.MODE_PRIVATE);
		edit = sp_push.edit();
		// Intent intent = act.getIntent();
		arr_homeinfo = new ArrayList<HomeInfoData>();
		mgr = new DBManager(act);

		StcollectReply = sp_push.getBoolean("StcollectReply", false);
		LatestNotification = sp_push.getBoolean("LatestNotification", false);
		AwardNotification = sp_push.getBoolean("AwardNotification", false);
		sql_arrays = mgr.queryMainHotList();
		sql_adarrays = mgr.queryMainAdList();

		// 如果是从推送消息进入，则开启线程获取跑马灯内容
		// push = intent.getStringExtra("push");
		// if (push != null && "true".equals(push)) {
		// new MainThread().start();
		// }
		// else {
		//
		// }
		if (!getArguments().getSerializable("homeinfo").equals("null")) {
			Object[] cobjs = (Object[]) getArguments().getSerializable("homeinfo");
			for (int i = 0; i < cobjs.length; i++) {
				HomeInfoData homeinfo = (HomeInfoData) cobjs[i];
				arr_homeinfo.add(homeinfo);
			}
			mgr.deleteHomeinfo();
			mgr.addHomeinfo(arr_homeinfo);
			handler.sendEmptyMessage(Constant.GETHOMEINFO_SUCCEED);
		} else {
			// 从数据库加载
			arr_homeinfo = mgr.queryHomeinfo();
			handler.sendEmptyMessage(Constant.GETHOMEINFO_SUCCEED);
		}

		api_url = getString(R.string.api_url);

		updateHandler.postDelayed(new Runnable() {
			// 进入界面后延迟500毫秒检查版本更新
			@Override
			public void run() {
				try {
					localVersion = version.getVersionName();
					CheckVersionTask cv = new CheckVersionTask();
					new Thread(cv).start();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}, 500);

	}

	private Handler updateHandler = new Handler();

	private void initView() {
		imageLoader = ImageLoader.getInstance();

		options = DisplayImageOptionsUtil.getOptions(R.drawable.new_me, R.drawable.new_me, R.drawable.default_head,
				new RoundedBitmapDisplayer(90));

		uid = sp_user.getString("uuid", null);
		mobile = sp_user.getString("mobile", null);
		gethomeinfo_url = getResources().getString(R.string.server_url)
				+ getResources().getString(R.string.gethomeinfo_url);
		version = new VersionUtil(act);
		adapter = new MainAdapter(act);
		if (StcollectReply) {
			adapter.setCircleVisibile(View.VISIBLE);
		}
		adadapter = new MainAdListGridViewAdapter(act, adarrays);
		gv_main.setAdapter(adapter);
		gv_main.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				switch (position) {
				case 0:
					intentActivity(TrafficTotalActivity.class);
					break;
				case 1:
					new AppClickCount(act, "车主服务");
					intentActivity(CarServiceActivity.class);
					//检测是否登录
//					if (uid != null && !mobile.isEmpty()) {
//						intentActivity(SpLicencePlateActivity.class);
//					} else if (uid == null) {
//						Intent intent = new Intent(act, StLoginActivity.class);
//						dialog("您还没有登录,现在登录吗?", intent);
//					} else if (uid != null && mobile.isEmpty()) {
//						Intent intent = new Intent(act, StUserInfoActivity.class);
//						dialog("您还没绑定手机号,现在去绑定吗?", intent);
//					}
					break;
				case 2:
					new AppClickCount(act, "车友分享");
					intentActivity(ShareListActivity.class);
					adapter.setCircleVisibile(View.GONE);
					// gv_main.setAdapter(adapter);
					adapter.notifyDataSetChanged();
					edit.putBoolean("StcollectReply", false);
					edit.commit();
					break;
				case 3:
					new AppClickCount(act, "路况地图");
					intentActivity(MapRoadInfoActivity.class);
					break;
				case 4:
					if (isLogIn()) {
						intentActivityByExtra(ReportEditActivity.class,"ClickCount","报路况");
					}
					break;
				case 5:
					new AppClickCount(act, "精选活动");
					intentActivityByExtra(ActListActivity.class, "contenttype", "huodong");
					break;
				case 6:
					new AppClickCount(act, "新车资讯");
					intentActivityByExtra(ActListActivity.class, "contenttype", "carlife");
					break;
				case 7:
					// TODO 音视频
					intentActivity(MultimediaActivity.class);
					break;
				}
			}
		});

	}

	public boolean isLogIn() {
		if (uid != null && mobile != null && !"".equals(mobile)) {
			return true;
		} else if (uid == null) {
			Intent intent = new Intent(act, StLoginActivity.class);
			startActivityForResult(intent, Constant.LOGIN_REQUESTCODE);
		} else if (uid != null && "".equals(mobile)) {
			Toast.makeText(act, "请先绑定手机号", Toast.LENGTH_LONG).show();
		}
		return false;
	}

	private void intentActivity(Class<?> cls) {
		Intent intent = new Intent(act, cls);
		startActivity(intent);
	}

	private void intentActivityByExtra(Class<?> cls, String extra_key, String extra_value) {
		Intent intent = new Intent(act, cls);
		intent.putExtra(extra_key, extra_value);
		startActivity(intent);
	}

	Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case Constant.GETHOMEINFO_SUCCEED:
				if (arr_homeinfo != null && arr_homeinfo.size() > 0) {
					viewFlow.setAdapter(new ImageAdapter(act, arr_homeinfo, uid));
					viewFlow.setViewGroup(sv_viewflow);
					viewFlow.setmSideBuffer(arr_homeinfo.size());
					viewFlow.setFlowIndicator(indic);
					viewFlow.setTimeSpan(5000);
					viewFlow.setSelection(arr_homeinfo.size() * 1000); // 设置初始位置
					viewFlow.startAutoFlowTimer(); // 启动自动播放
				}
				break;
			case Constant.GETHOMEINFO_FAILED:
				viewFlow.startAutoFlowTimer();
				break;
			case Constant.UPDATA_CLIENT:
				version.showUpdataDialog(info);
				break;
			case Constant.GETWEATHER_SUCCEED:
				String weather = (String) msg.obj;
				if (weather != null) {
					tv_weather.setText(weather);
				} else {
					tv_weather.setVisibility(View.GONE);
				}

				break;
			}
		}
	};

	private List<MainHotListData> getDataFromNetwork() throws Exception {
		List<MainHotListData> lst = new ArrayList<MainHotListData>();
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("action", "GetHotList"));
		String result = HttpUtil.PostStringFromUrl(api_url, params);

		if (result != null) {
			Type listType = new TypeToken<List<MainHotListData>>() {
			}.getType();
			Gson gson = new Gson();
			lst = gson.fromJson(result, listType);
			return lst;
		} else {
			return null;
		}
	}

	private List<MainAdListData> getMainAdList() throws Exception {
		List<MainAdListData> lst = new ArrayList<MainAdListData>();
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("action", "GetADList"));
		String result = HttpUtil.PostStringFromUrl(api_url, params);
		if (result != null) {
			Type listType = new TypeToken<List<MainAdListData>>() {
			}.getType();
			Gson gson = new Gson();
			lst = gson.fromJson(result, listType);
			return lst;
		} else {
			return null;
		}
	}

	/**
	 * 初始化数据
	 * 
	 * @param handler
	 */
	private void initArrays(final Handler handler) {
		new Thread(new Runnable() {
			List<MainHotListData> lst = new ArrayList<MainHotListData>();

			@Override
			public void run() {
				try {
					lst = getDataFromNetwork();
				} catch (Exception e) {
					e.printStackTrace();
				}
				handler.obtainMessage(0, lst).sendToTarget();
			}
		}).start();
	}

	private void initAdArrays(final Handler handler) {
		new Thread(new Runnable() {
			List<MainAdListData> lst = new ArrayList<MainAdListData>();

			@Override
			public void run() {
				try {
					lst = getMainAdList();
				} catch (Exception e) {
					e.printStackTrace();
				}
				handler.obtainMessage(0, lst).sendToTarget();
			}
		}).start();
	}

	// class MainThread extends Thread {
	//
	// @Override
	// public void run() {
	// String req_gethomeinfo_url = HttpUtil
	// .GetStringFromUrl(gethomeinfo_url);
	// if (req_gethomeinfo_url != null && !req_gethomeinfo_url.equals("")) {
	// Type listType = new TypeToken<ArrayList<HomeInfoData>>() {
	// }.getType();
	// Gson gson = new Gson();
	// arr_homeinfo = gson.fromJson(req_gethomeinfo_url, listType);
	// }
	// handler.sendEmptyMessage(Constant.GETHOMEINFO_SUCCEED);
	// }
	// }

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == Constant.LOGIN_REQUESTCODE && resultCode == Activity.RESULT_OK) {
			uid = data.getStringExtra("uid");
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	/*
	 * 从服务器获取xml解析并进行比对版本号
	 */
	public class CheckVersionTask implements Runnable {

		public void run() {
			try {
				info = version.getResourceInfo();
				if (info.getVersion().equals(localVersion)) {
					// Log.i(TAG, "版本号相同无需升级");
				} else {
					// Log.i(TAG, "版本号不同 ,提示用户升级 ");
					handler.sendEmptyMessage(Constant.UPDATA_CLIENT);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	// @Override
	// public void onDestroy() {
	// super.onDestroy();
	// mgr.closeDB();
	// }

	public void onResume() {
		super.onResume();
		mobile = sp_user.getString("mobile", null);
		uid = sp_user.getString("uuid", null);
		if (arr_homeinfo != null && arr_homeinfo.size() > 0) {
			viewFlow.setAdapter(new ImageAdapter(act, arr_homeinfo, uid));
		}
		String headimgurl = sp_user.getString("headimg", null);
		imageLoader.displayImage(headimgurl, iv_main_me, options);

		StcollectReply = sp_push.getBoolean("StcollectReply", false);
		if (StcollectReply) {
			adapter.setCircleVisibile(View.VISIBLE);
		}
		adapter.notifyDataSetChanged();

		LatestNotification = sp_push.getBoolean("LatestNotification", false);
		AwardNotification = sp_push.getBoolean("AwardNotification", false);
		if (LatestNotification | AwardNotification) {
			iv_circle.setVisibility(View.VISIBLE);
		}
	}

	// @Override
	// public void onPause() {
	// super.onPause();
	// if (viewFlow != null) {
	// System.out.println("onPause=======");
	// viewFlow.stopAutoFlowTimer();
	// }
	// }
	//
	// @Override
	// public void onStart() {
	// super.onStart();
	// if (viewFlow != null) {
	// System.out.println("onStart=======");
	// viewFlow.startAutoFlowTimer();
	// }
	// // 刷新首页飞图
	// // TODO
	// }

	@Override
	public void onHiddenChanged(boolean hidden) {
		super.onHiddenChanged(hidden);

		if (viewFlow != null) {
			if (hidden) {
				viewFlow.stopAutoFlowTimer();
			} else {
				updateData();
				new Thread() {
					public void run() {
						String req_gethomeinfo_url = HttpUtil.GetStringFromUrl(gethomeinfo_url);
						if (req_gethomeinfo_url != null && !req_gethomeinfo_url.equals("")) {
							Type listType = new TypeToken<ArrayList<HomeInfoData>>() {
							}.getType();
							Gson gson = new Gson();
							arr_homeinfo = gson.fromJson(req_gethomeinfo_url, listType);
							// TODO
							if (arr_homeinfo != null) {
								handler.sendEmptyMessage(Constant.GETHOMEINFO_SUCCEED);
							} else {
								handler.sendEmptyMessage(Constant.GETHOMEINFO_FAILED);
							}
						}
					}
				}.start();
			}
		}
	}

	@Override
	public void onClick(View v) {
		if (v == iv_main_me) {
			intentActivity(SettingActivity.class);
		}
		if (v == iv_main_message) {
			if (uid == null) {
				Intent intent = new Intent(act, StLoginActivity.class);
				startActivityForResult(intent, Constant.LOGIN_REQUESTCODE);
			} else {
				intentActivity(MyMessageActivity.class);
				iv_circle.setVisibility(View.GONE);
				edit.putBoolean("LatestNotification", false);
				edit.putBoolean("AwardNotification", false);
				edit.commit();
			}
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		new AppClickCount(act, "热点区文章");
		String flag = arrays.get(position).getFlag();
		String sid = arrays.get(position).getId();
		Intent intent = new Intent(act, ActDetailActivity.class);
		// 节目互动
		if ("HDCOMMENT".equals(flag)) {
			intentActivityByExtra(Main2RadioCommentDetailActivity.class, "hdcommentid", sid);
		}
		// 车友分享
		else if ("STCOLLECT".equals(flag)) {
			intentActivityByExtra(Main2ShareDetailActivity.class, "stcollectid", sid);
		}
		// 精选互动
		else if ("JXHD".equals(flag)) {
			intent.putExtra("hdinfoid", sid);
			intent.putExtra("contenttype", "huodong");
			intent.putExtra("uid", uid);
			startActivity(intent);
		}
		// 车生活
		else if ("CSH".equals(flag)) {
			intent.putExtra("hdinfoid", sid);
			intent.putExtra("contenttype", "carlife");
			intent.putExtra("uid", uid);
			startActivity(intent);
		}
		// 广告
		else if ("AD".equals(flag)) {
			intent.putExtra("hdinfoid", sid);
			intent.putExtra("contenttype", "ad");
			intent.putExtra("uid", uid);
			startActivity(intent);
		}
	}

	private void updateData() {

		// 加载热点区
		if (sql_arrays.size() == 0) {
			initArrays(new Handler() {
				@Override
				public void handleMessage(Message msg) {

					arrays = (List<MainHotListData>) msg.obj;
					hotadapter = new MainHotListAdapter(act, arrays, hotinflater, densityDpi);
					listView.setAdapter(hotadapter);
					hotadapter.notifyDataSetChanged();

					if (arrays != null) {
						mgr.addMainHotList(arrays);
					}

				}
			});
		} else {
			arrays = sql_arrays;
			hotadapter = new MainHotListAdapter(act, arrays, hotinflater, densityDpi);
			listView.setAdapter(hotadapter);
			hotadapter.notifyDataSetChanged();

			initArrays(new Handler() {
				@Override
				public void handleMessage(Message msg) {

					arrays = (List<MainHotListData>) msg.obj;
					if (arrays == null) {
						// arrays = sql_arrays;
					} else { // 网络更新数据成功，更新数据库的数据
						mgr.deleteMainHotList();
						mgr.addMainHotList(arrays);
					}
					hotadapter = new MainHotListAdapter(act, arrays, hotinflater, densityDpi);
					listView.setAdapter(hotadapter);
					hotadapter.notifyDataSetChanged();
				}
			});
		}

		// 加载广告区
		if (sql_adarrays.size() == 0) {
			initAdArrays(new Handler() {
				@Override
				public void handleMessage(Message msg) {

					adarrays = (List<MainAdListData>) msg.obj;

					adadapter = new MainAdListGridViewAdapter(getActivity(), adarrays);
					gv_adlist.setAdapter(adadapter);

					if (adarrays != null) {
						mgr.addMainAdList(adarrays);
					}

				}
			});
		} else {
			adarrays = sql_adarrays;
			adadapter = new MainAdListGridViewAdapter(getActivity(), adarrays);
			gv_adlist.setAdapter(adadapter);

			initAdArrays(new Handler() {
				@Override
				public void handleMessage(Message msg) {

					adarrays = (List<MainAdListData>) msg.obj;
					if (adarrays == null) {
						// adarrays = sql_adarrays;
					} else { // 网络更新数据成功，更新数据库的数据
						mgr.deleteMainAdList();
						mgr.addMainAdList(adarrays);
					}
					adadapter = new MainAdListGridViewAdapter(getActivity(), adarrays);
					gv_adlist.setAdapter(adadapter);
				}
			});
		}

		new Thread() {
			public void run() {
				String weather = getWeather("113.129151,23.024514", "佛山市");
				handler.obtainMessage(Constant.GETWEATHER_SUCCEED, weather).sendToTarget();
			}
		}.start();
	}

	// TODO
	private String getWeather(String latLng, String address) {

		JSONObject jsonObject;
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("action", "GetWeather"));
		params.add(new BasicNameValuePair("latLng", latLng));
		params.add(new BasicNameValuePair("address", address));
		String result = HttpUtil.PostStringFromUrl(api_url, params);
		if (result != null) {
			try {
				jsonObject = new JSONObject(result);
				return jsonObject.getString("weather");
			} catch (JSONException e) {
				e.printStackTrace();
				return null;
			}
		} else {
			return null;
		}
	}

	/**
	 * 定位初始化
	 */
	private void initLoc() {

		mLocClient = new LocationClient(getActivity());
		mLocClient.registerLocationListener(myListener);
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true);// 打开gps
		option.setCoorType("bd09ll"); // 设置坐标类型
		option.setScanSpan(5000);
		mLocClient.setLocOption(option);
		mLocClient.start();
	}

	/**
	 * 定位SDK监听函数
	 */
	public class MyLocationListenner implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation location) {
			if (location == null)
				return;
		}

		public void onReceivePoi(BDLocation poiLocation) {
			if (poiLocation == null) {
				return;
			}
		}
	}

	/**
	 * 百度搜索反查地名初始化
	 */
	private void initSearch() {
		mSearch.init(MyApplication.getInstance().mBMapManager, new MKSearchListener() {
			@Override
			public void onGetPoiDetailSearchResult(int type, int error) {
			}

			public void onGetAddrResult(MKAddrInfo res, int error) {
				if (error != 0) {
					return;
				}
				if (res.type == MKAddrInfo.MK_REVERSEGEOCODE) {
					// 反地理编码：通过坐标点检索详细地址及周边poi
					MKGeocoderAddressComponent mc = res.addressComponents;
					// 只搜索路名
					strInfo = mc.city + mc.district + mc.street + mc.streetNumber;
					System.out.println("strInfo====" + strInfo);
				}
			}

			public void onGetPoiResult(MKPoiResult res, int type, int error) {
			}

			public void onGetDrivingRouteResult(MKDrivingRouteResult res, int error) {
			}

			public void onGetTransitRouteResult(MKTransitRouteResult res, int error) {
			}

			public void onGetWalkingRouteResult(MKWalkingRouteResult res, int error) {
			}

			public void onGetBusDetailResult(MKBusLineResult result, int iError) {
			}

			@Override
			public void onGetSuggestionResult(MKSuggestionResult res, int arg1) {
			}

			@Override
			public void onGetShareUrlResult(MKShareUrlResult result, int type, int error) {
			}
		});
	}

	private void getloc(int delay) {

		new Handler().postDelayed(new Runnable() {
			public void run() {
				requestLocClick();
			}
		}, delay);
	}

	/**
	 * 手动触发一次定位请求
	 */
	public void requestLocClick() {
		mLocClient.requestLocation();
		BDLocation loc = mLocClient.getLastKnownLocation();
		if (loc != null) {
			lat = loc.getLatitude();
			lng = loc.getLongitude();
			GeoPoint ptCenter = new GeoPoint((int) (lat * 1e6), (int) (lng * 1e6));
			mSearch.reverseGeocode(ptCenter);
		}
	}

	private void dialog(String msg, final Intent in) {
		AlertDialog.Builder builder = new Builder(act);
		builder.setMessage(msg);
		builder.setTitle("提示");
		builder.setPositiveButton(act.getResources().getString(R.string.dialog_confirm_yes),
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						startActivityForResult(in, Constant.LOGIN_REQUESTCODE);
					}
				});
		builder.setNegativeButton(act.getResources().getString(R.string.dialog_no),
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						intentActivity(CarServiceActivity.class);
					}
				});
		builder.create().show();
	}

}

package cn.fszt.trafficapp.ui;

import java.util.ArrayList;
import java.util.Set;
import org.json.JSONException;
import org.json.JSONObject;
import cn.fszt.trafficapp.R;
import cn.fszt.trafficapp.db.DBManager;
import cn.fszt.trafficapp.domain.HomeInfoData;
import cn.fszt.trafficapp.ui.activity.MainAddActivity;
import cn.fszt.trafficapp.ui.fragment.DiscoverFragment;
import cn.fszt.trafficapp.ui.fragment.MainFragment;
import cn.fszt.trafficapp.ui.fragment.RadioFragment;
import cn.fszt.trafficapp.ui.fragment.RadioProgrammeListFragment;
import cn.fszt.trafficapp.ui.service.IItemClickListener;
import cn.fszt.trafficapp.ui.service.RadioPlayerService;
import cn.fszt.trafficapp.ui.service.ReplayPlayerService;
import cn.fszt.trafficapp.util.AppClickCount;
import cn.fszt.trafficapp.util.CommonUtils;
import cn.fszt.trafficapp.util.Constant;
import cn.fszt.trafficapp.widget.msg.AppMsg;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;

/**
 * 项目的主Activity，所有的Fragment都嵌入在这里。
 * 
 * @author AeiouKong
 */
public class MainActivity extends FragmentActivity implements OnClickListener,IItemClickListener {

	private IItemClickListener itemClickListener;
	public static boolean isForeground = false;
	private MessageReceiver mMessageReceiver;
	public static final String MESSAGE_RECEIVED_ACTION = "com.example.jpushdemo.MESSAGE_RECEIVED_ACTION";
	public static final String KEY_TITLE = "title";
	public static final String KEY_MESSAGE = "message";
	public static final String KEY_EXTRAS = "extras";
	private ArrayList<HomeInfoData> arr_homeinfo;
	private long exitTime = 0;
	AppMsg.Style style = AppMsg.STYLE_INFO;
	AppMsg appMsg;
	private SharedPreferences sp,sp_push;
	private DBManager mgr;
	private ImageView iv_circle;
	private Editor edit;
	private boolean HdcommentReply;
	Set<String> programname;
	
	/**
	 * 用于展示首页的Fragment
	 */
	private MainFragment newmainFragment;

	/**
	 * 用于展示听广播的Fragment
	 */
	private RadioFragment radioFragment;

	/**
	 * 用于展示节目互动的Fragment
	 */
	private RadioProgrammeListFragment choosereplayFragment;

	/**
	 * 用于展示发现的Fragment
	 */
	private DiscoverFragment shakeFragment;
	
	private View add_layout;

	/**
	 * 首页界面布局
	 */
	private View newmainLayout;

	/**
	 * 听广播界面布局
	 */
	private View radioLayout;

	/**
	 * 节目互动界面布局
	 */
	private View choosereplayLayout;

	/**
	 * 发现界面布局
	 */
	private View shakeLayout;

	/**
	 * 在Tab布局上显示首页图标的控件
	 */
	private ImageView newmainImage;

	/**
	 * 在Tab布局上显示听广播图标的控件
	 */
	private ImageView radioImage;

	/**
	 * 在Tab布局上显示节目互动图标的控件
	 */
	private ImageView choosereplayImage;

	/**
	 * 在Tab布局上显示发现图标的控件
	 */
	private ImageView shakeImage;

	/**
	 * 在Tab布局上显示首页标题的控件
	 */
	private TextView newmainText;

	/**
	 * 在Tab布局上显示听广播标题的控件
	 */
	private TextView radioText;

	/**
	 * 在Tab布局上显示节目互动标题的控件
	 */
	private TextView choosereplayText;

	/**
	 * 在Tab布局上显示发现标题的控件
	 */
	private TextView shakeText;

	/**
	 * 用于对Fragment进行管理
	 */
	private FragmentManager fragmentManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
		// 初始化布局元素
		initViews();
		registerMessageReceiver();
		fragmentManager = getFragmentManager();
		// 第一次启动时选中第0个tab
		setTabSelection(0);
	}

	/**
	 * 在这里获取到每个需要用到的控件的实例，并给它们设置好必要的点击事件。
	 */
	private void initViews() {
		mgr = new DBManager(this);
		sp = getSharedPreferences("REPLAY", Context.MODE_PRIVATE);
		sp_push = getSharedPreferences("PUSH", Context.MODE_PRIVATE);
		edit = sp_push.edit();
		HdcommentReply = sp_push.getBoolean("HdcommentReply", false);
		iv_circle = (ImageView) findViewById(R.id.iv_circle);
		if(HdcommentReply){
			iv_circle.setVisibility(View.VISIBLE);
		}
		newmainLayout = findViewById(R.id.message_layout);
		radioLayout = findViewById(R.id.contacts_layout);
		choosereplayLayout = findViewById(R.id.news_layout);
		shakeLayout = findViewById(R.id.setting_layout);
		add_layout = findViewById(R.id.add_layout);
		newmainImage = (ImageView) findViewById(R.id.message_image);
		radioImage = (ImageView) findViewById(R.id.contacts_image);
		choosereplayImage = (ImageView) findViewById(R.id.news_image);
		shakeImage = (ImageView) findViewById(R.id.setting_image);
		newmainText = (TextView) findViewById(R.id.message_text);
		radioText = (TextView) findViewById(R.id.contacts_text);
		choosereplayText = (TextView) findViewById(R.id.news_text);
		shakeText = (TextView) findViewById(R.id.setting_text);
		newmainLayout.setOnClickListener(this);
		radioLayout.setOnClickListener(this);
		choosereplayLayout.setOnClickListener(this);
		shakeLayout.setOnClickListener(this);
		add_layout.setOnClickListener(this);
		arr_homeinfo = new ArrayList<HomeInfoData>();
		//接收启动页传递的飞图数据
		Intent intent = getIntent();
		if(!intent.getSerializableExtra("homeinfo").equals("null")){
			Object[] cobjs = (Object[]) intent
					.getSerializableExtra("homeinfo");
			for (int i = 0; i < cobjs.length; i++) {
				HomeInfoData homeinfo = (HomeInfoData) cobjs[i];
				arr_homeinfo.add(homeinfo);
				mgr.deleteHomeinfo();
				mgr.addHomeinfo(arr_homeinfo);
			}
		}else {
			// 从数据库加载
			arr_homeinfo = mgr.queryHomeinfo();
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.message_layout:
			// 当点击了消息tab时，选中第1个tab
			setTabSelection(0);
			break;
		case R.id.contacts_layout:
			// 当点击了联系人tab时，选中第2个tab
			setTabSelection(1);
			break;
		case R.id.news_layout:
			// 当点击了动态tab时，选中第3个tab
			setTabSelection(2);
			iv_circle.setVisibility(View.GONE);
			edit.putBoolean("HdcommentReply", false);
			edit.commit();
			break;
		case R.id.setting_layout:
			// 当点击了设置tab时，选中第4个tab
			setTabSelection(3);
			break;
		case R.id.add_layout:
			Intent intent = new Intent(MainActivity.this,MainAddActivity.class);
			startActivityForResult(intent,Constant.PINGLUN_REQUESTCODE);
			break;
		default:
			break;
		}
	}

	/**
	 * 根据传入的index参数来设置选中的tab页。
	 * 
	 * @param index
	 *            
	 */
	private void setTabSelection(int index) {
		// 每次选中之前先清楚掉上次的选中状态
		clearSelection();
		// 开启一个Fragment事务
		FragmentTransaction transaction = fragmentManager.beginTransaction();
		// 先隐藏掉所有的Fragment，以防止有多个Fragment显示在界面上的情况
		hideFragments(transaction);
		switch (index) {
		case 0:
			Bundle bundle = new Bundle();
			bundle.putSerializable("homeinfo", arr_homeinfo.toArray());
			// 当点击了消息tab时，改变控件的图片和文字颜色
			newmainImage.setImageResource(R.drawable.m_shouye2);
			newmainText.setTextColor(getResources().getColor(R.color.orange_bg));
			if (newmainFragment == null) {
				// 如果MessageFragment为空，则创建一个并添加到界面上
				newmainFragment = new MainFragment();
				newmainFragment.setArguments(bundle);
				transaction.add(R.id.content, newmainFragment);
			} else {
				// 如果MessageFragment不为空，则直接将它显示出来
				transaction.show(newmainFragment);
			}
			break;
		case 1:
			// 当点击了联系人tab时，改变控件的图片和文字颜色
			radioImage.setImageResource(R.drawable.m_tingguangbo2);
			radioText.setTextColor(getResources().getColor(R.color.orange_bg));
			if (radioFragment == null) {
				// 如果ContactsFragment为空，则创建一个并添加到界面上
				radioFragment = new RadioFragment();
				transaction.add(R.id.content, radioFragment);
			} else {
				// 如果ContactsFragment不为空，则直接将它显示出来
				transaction.show(radioFragment);
			}
			break;
		case 2:
			// 当点击了动态tab时，改变控件的图片和文字颜色
			choosereplayImage.setImageResource(R.drawable.m_hudong2);
			choosereplayText.setTextColor(getResources().getColor(R.color.orange_bg));
			if (choosereplayFragment == null) {
				// 如果NewsFragment为空，则创建一个并添加到界面上
				choosereplayFragment = new RadioProgrammeListFragment();
				transaction.add(R.id.content, choosereplayFragment);
			} else {
				// 如果NewsFragment不为空，则直接将它显示出来
				transaction.show(choosereplayFragment);
			}
			break;
		case 3:
		default:
			// 当点击了设置tab时，改变控件的图片和文字颜色
			shakeImage.setImageResource(R.drawable.m_faxian2);
			shakeText.setTextColor(getResources().getColor(R.color.orange_bg));
			if (shakeFragment == null) {
				// 如果SettingFragment为空，则创建一个并添加到界面上
				shakeFragment = new DiscoverFragment();
				transaction.add(R.id.content, shakeFragment);
			} else {
				// 如果SettingFragment不为空，则直接将它显示出来
				transaction.show(shakeFragment);
			}
			break;
		}
		transaction.commit();
	}

	/**
	 * 清除掉所有的选中状态。
	 */
	private void clearSelection() {
		newmainImage.setImageResource(R.drawable.m_shouye);
		newmainText.setTextColor(Color.parseColor("#82858b"));
		radioImage.setImageResource(R.drawable.m_tingguangbo);
		radioText.setTextColor(Color.parseColor("#82858b"));
		choosereplayImage.setImageResource(R.drawable.m_hudong);
		choosereplayText.setTextColor(Color.parseColor("#82858b"));
		shakeImage.setImageResource(R.drawable.m_faxian);
		shakeText.setTextColor(Color.parseColor("#82858b"));
	}

	/**
	 * 将所有的Fragment都置为隐藏状态。
	 * 
	 * @param transaction
	 *            用于对Fragment执行操作的事务
	 */
	private void hideFragments(FragmentTransaction transaction) {
		if (newmainFragment != null) {
			transaction.hide(newmainFragment);
		}
		if (radioFragment != null) {
			transaction.hide(radioFragment);
		}
		if (choosereplayFragment != null) {
			transaction.hide(choosereplayFragment);
		}
		if (shakeFragment != null) {
			transaction.hide(shakeFragment);
		}
	}
	
	@Override
	public void onAttachFragment(Fragment fragment) {
		try {
			itemClickListener = (IItemClickListener) fragment;
		} catch (Exception e) {
			e.printStackTrace();
		}
		super.onAttachFragment(fragment);
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == Constant.PINGLUN_REQUESTCODE && resultCode == RESULT_OK) {
			Toast.makeText(MainActivity.this, "发送成功", Toast.LENGTH_SHORT).show();
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK
				&& event.getAction() == KeyEvent.ACTION_DOWN) {
			if ((System.currentTimeMillis() - exitTime) > 2000) {
				appMsg = AppMsg.makeText(this, "再按一次退出程序", style);
				appMsg.setLayoutGravity(Gravity.BOTTOM);
				appMsg.show();
				exitTime = System.currentTimeMillis();
			} else {
				// 清除点播正在播放的喇叭标记
				Editor editor = sp.edit();
				editor.clear();
				editor.commit();
				edit.clear();
				edit.commit();
				Intent intent = new Intent();
				intent.setClass(MainActivity.this, RadioPlayerService.class);
				stopService(intent);// 停止直播Service
				Intent intent_replay = new Intent();
				intent_replay.setClass(MainActivity.this,
						ReplayPlayerService.class);
				stopService(intent_replay);// 停止点播Service
				finish();
				System.exit(0);
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	
	@Override
	protected void onResume() {
		isForeground = true;
		super.onResume();
	}


	@Override
	protected void onPause() {
		isForeground = false;
		super.onPause();
	}
	
	@Override
	protected void onDestroy() {
		unregisterReceiver(mMessageReceiver);
		mgr.closeDB();
		super.onDestroy();
	}
	
	public void registerMessageReceiver() {
		mMessageReceiver = new MessageReceiver();
		IntentFilter filter = new IntentFilter();
		filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
		filter.addAction(MESSAGE_RECEIVED_ACTION);
		registerReceiver(mMessageReceiver, filter);
	}
	
	public class MessageReceiver extends BroadcastReceiver {
	/**
	 * 推送最新通知 LatestNotification
	推送中奖通知 AwardNotification
	推送最新回复的节目互动 HdcommentReply
	推送最新回复的车友分享 StcollectReply
	 */
		@Override
		public void onReceive(Context context, Intent intent) {
			if (MESSAGE_RECEIVED_ACTION.equals(intent.getAction())) {
              String extras = intent.getStringExtra(KEY_EXTRAS);
              StringBuilder showMsg = new StringBuilder();
              if (!CommonUtils.isEmpty(extras)) {
            	  showMsg.append(KEY_EXTRAS + " : " + extras + "\n");
            	  try {
					JSONObject jsonObject = new JSONObject(extras);
					String module = jsonObject.getString("module");
					if("HdcommentReply".equals(module)){
						String programname = jsonObject.getString("programname");
//						System.out.println("=======HdcommentReply=======");
//						System.out.println("=======programname======="+programname);
						iv_circle.setVisibility(View.VISIBLE);
					}
					else if("StcollectReply".equals(module)){
//						System.out.println("=======StcollectReply=======");
					}
					else if("LatestNotification".equals(module)){
//						System.out.println("=======LatestNotification=======");		
					}
					else if("AwardNotification".equals(module)){
//						System.out.println("=======AwardNotification=======");
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
              }
			}
		}
	}

	@Override
	public void itemclick(Set<String> programname) {
		// TODO Auto-generated method stub
		this.programname = programname;
	}
}

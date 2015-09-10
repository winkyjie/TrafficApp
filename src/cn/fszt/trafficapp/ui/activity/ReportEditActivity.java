package cn.fszt.trafficapp.ui.activity;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.json.JSONException;
import org.json.JSONObject;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.BMapManager;
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
import cn.fszt.trafficapp.R;
import cn.fszt.trafficapp.adapter.ReportEventGridViewAdapter;
import cn.fszt.trafficapp.adapter.ReportFlowGridViewAdapter;
import cn.fszt.trafficapp.ui.MyApplication;
import cn.fszt.trafficapp.util.AppClickCount;
import cn.fszt.trafficapp.util.Constant;
import cn.fszt.trafficapp.util.FileUtil;
import cn.fszt.trafficapp.util.PhotoUtil;
import cn.fszt.trafficapp.util.UploadUtil;
import cn.fszt.trafficapp.util.UploadUtil.OnUploadProcessListener;
import cn.fszt.trafficapp.util.version.VersionUtil;
import cn.fszt.trafficapp.widget.ImageLoadingDialog;
import cn.fszt.trafficapp.widget.RecordButton;
import cn.fszt.trafficapp.widget.RecordButton.OnFinishedRecordListener;
import cn.fszt.trafficapp.widget.msg.AppMsg;
import cn.fszt.trafficapp.widget.muphoto.AlbumActivity;
import cn.fszt.trafficapp.widget.muphoto.Bimp;
import cn.fszt.trafficapp.widget.muphoto.FileUtils;
import cn.fszt.trafficapp.widget.muphoto.GridAdapter;
import cn.fszt.trafficapp.widget.muphoto.PhotoActivity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
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
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

/**
 * 我报路况
 * @author AeiouKong
 *
 */
public class ReportEditActivity extends BaseBackActivity implements OnClickListener,MediaPlayer.OnCompletionListener,OnUploadProcessListener{

	private static final int PHOTO_DONE = 4; 
	private String _audioDir;
	private String _imageDir;
	private String _cameraDir;
	private Map<String, String> params = new HashMap<String, String>();
	private Button btn_bl_submit,btn_bl_playorstop,btn_e2w,btn_w2e,btn_s2n,btn_n2s;
	private ImageView btn_loc;
	private EditText et_loc,et_baoliao;
	private RecordButton btn_luyin;
	private MediaPlayer mPlayer = null; 
	private String mFileName = null;
	private ProgressDialog progressDialog;
	private ImageLoadingDialog dialog;
	private String audiopath = null;
	private String strInfo,uid;
	private LocationClient mLocClient;
	private MKSearch mSearch = null;
	public MyLocationListenner myListener = new MyLocationListenner();
	public static final int TO_SELECT_PHOTO = 3;
	private StringBuilder sb;
	boolean gaidao,guanzhi,shigong,huaiche,zhuangche,fenglu,jishui,isupload,isuploaddone;
	String loc,text_bl,event;
	String direction = "";
	private String requestURL;
	
	private GridView gridView;
	private ArrayList<String> dataList = new ArrayList<String>();
	private GridAdapter adapter;
	private double lng,lat;
	private static final int TAKE_PICTURE = 0x000000;
	private String path = "";
	
	AppMsg.Style style = AppMsg.STYLE_INFO;
	AppMsg appMsg;
	
	private GridView gv_event;
	private ReportEventGridViewAdapter eventadapter;
	private GridView gv_flow;
	private ReportFlowGridViewAdapter flowadapter;
	
	VersionUtil version;
	
	private boolean finished = false;
	
	boolean n2s_flag,s2n_flag,e2w_flag,w2e_flag,yongji_flag,huanman_flag,changtong_flag;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initMap();
		DisplayMetrics metric = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metric);
        int densityDpi = metric.densityDpi;
//        System.out.println("densityDpi==="+densityDpi);
//		 	if(densityDpi == 240){
//	        	setContentView(R.layout.activity_baoliao_hdpi);
//	        }else if(densityDpi == 160){
//	        	setContentView(R.layout.activity_baoliao_mdpi);
//	        }else{
//	        	setContentView(R.layout.activity_baoliao);
//	        }
        setContentView(R.layout.activity_baoliao);
		initView();
		
		Init();
		initLoc();
		initSearch();
		getloc(3*1000);
		
		getActionBar().setTitle(getResources().getString(R.string.n_baoliao));
		getActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.titlebar)));
		getActionBar().setDisplayHomeAsUpEnabled(true);
		if (!getIntent().getStringExtra("ClickCount").isEmpty()) {
			new AppClickCount(this, this.getIntent().getStringExtra("ClickCount"));
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		
		MenuInflater inflater = getMenuInflater();

	    inflater.inflate(R.menu.baoliaoactivity, menu);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch(item.getItemId()){
		case android.R.id.home:
			finish();
			break;
			
		case R.id.item_bl_sort:
			Intent intent_sort = new Intent(this,ReportSortActivity.class);
			startActivity(intent_sort);
			break;
		
		case R.id.item_bl_submit:
			sb = new StringBuilder();
			//提交
        	loc = et_loc.getText().toString();
        	text_bl = et_baoliao.getText().toString();
        	if("".equals(text_bl)){
        		text_bl = " ";
        	}
        	
        	Collection<String> co = flowadapter.map.values();
			Iterator<String> ite = co.iterator();
			for (; ite.hasNext();) {
				sb.append(ite.next());
        		sb.append(",");
			}
			Collection<String> c = eventadapter.map.values();
			Iterator<String> it = c.iterator();
			for (; it.hasNext();) {
				sb.append(it.next());
        		sb.append(",");
			}
			
        	event = sb.toString();
        	if(event!=null&&!"".equals(event)){
        		event = event.substring(0, event.lastIndexOf(","));
        	}
        	params.put("address", loc+" "+direction);
        	params.put("content", text_bl+" "+event);
        	params.put("connected_uid", uid);
        	params.put("pointlng", lng+"");
        	params.put("pointlat", lat+"");
        	params.put("clienttype", "android");
        	params.put("clientno", getString(R.string.version_code));
        	if(!isuploaddone){
        		uploadBaoliao();
        	}
			break;
		}
		return true;
	}
	
    public void Init() {
    	gridView = (GridView) findViewById(R.id.bl_myGrid);
    	gridView.setSelector(new ColorDrawable(Color.TRANSPARENT));
		adapter = new GridAdapter(this);
		adapter.update();
		gridView.setAdapter(adapter);
		gridView.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				Bimp.limit = 3;
				((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE))
					.hideSoftInputFromWindow(ReportEditActivity.this.getCurrentFocus().getWindowToken(),
					InputMethodManager.HIDE_NOT_ALWAYS);
				
				if (arg2 == Bimp.bmp.size()) {
					//拍照/相册选择照片
					new PopupWindows(ReportEditActivity.this, gridView);
				} else {
					//预览照片
					Intent intent = new Intent(ReportEditActivity.this,
							PhotoActivity.class);
					intent.putExtra("ID", arg2);
					startActivity(intent);
				}
			}
		});
	}
	
	private void initView(){
		gv_event = (GridView) findViewById(R.id.bl_event);
		eventadapter = new ReportEventGridViewAdapter(this);
		gv_flow = (GridView) findViewById(R.id.bl_flow);
		flowadapter = new ReportFlowGridViewAdapter(this);
        
		gv_event.setAdapter(eventadapter);
		gv_flow.setAdapter(flowadapter);
		
		version = new VersionUtil(ReportEditActivity.this);
		_imageDir = Environment.getExternalStorageDirectory()
				.getAbsolutePath()+ getResources().getString(R.string.phototemp_dir);
		_cameraDir = Environment.getExternalStorageDirectory()
				.getAbsolutePath()+ getResources().getString(R.string.phototake_dir);
		_audioDir = Environment.getExternalStorageDirectory()
				.getAbsolutePath()+ getResources().getString(R.string.audio_dir);
		File audioDir = new File(_audioDir);
	  	  if (!audioDir.exists()) {
	  		  audioDir.mkdirs();
	  	}
	  	mFileName = _audioDir + getAudioFileName();
		
	  	requestURL = getString(R.string.server_url)+getString(R.string.bl_req_url);
		SharedPreferences sp = getSharedPreferences("USER", Context.MODE_PRIVATE);
    	uid = sp.getString("uuid", null);
		progressDialog = new ProgressDialog(this);
		
		dialog = new ImageLoadingDialog(this);
		dialog.setCanceledOnTouchOutside(false);
		
		mPlayer = new MediaPlayer();
		mPlayer.setOnCompletionListener(this);
		btn_loc = (ImageView) findViewById(R.id.btn_loc);
		btn_loc.setOnClickListener(this);
		btn_bl_submit = (Button) findViewById(R.id.btn_bl_submit);
		btn_bl_submit.setOnClickListener(this);
		btn_bl_playorstop = (Button) findViewById(R.id.btn_bl_playorstop);
		btn_bl_playorstop.setOnClickListener(this);
		btn_bl_playorstop.setOnLongClickListener(new OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				if(finished){
					deldialog();
				}
				return false;
			}
		});
		btn_e2w = (Button) findViewById(R.id.btn_e2w);
		btn_e2w.setOnClickListener(this);
		btn_w2e = (Button) findViewById(R.id.btn_w2e);
		btn_w2e.setOnClickListener(this);
		btn_s2n = (Button) findViewById(R.id.btn_s2n);
		btn_s2n.setOnClickListener(this);
		btn_n2s = (Button) findViewById(R.id.btn_n2s);
		btn_n2s.setOnClickListener(this);
		
		mSearch = new MKSearch();
		et_loc = (EditText) findViewById(R.id.et_loc);
		et_baoliao = (EditText) findViewById(R.id.et_baoliao);
		btn_luyin = (RecordButton) findViewById(R.id.btn_luyin);
		btn_luyin.setSavePath(mFileName);
		btn_luyin.setOnClickListener(this);
		
		btn_luyin.setOnFinishedRecordListener(new OnFinishedRecordListener() {
			@Override
			public void onFinishedRecord(String audioPath) {
				audiopath = mFileName;
				finished = true;
				File file = new File(audiopath);
				try {
					long duration = FileUtil.getAmrDuration(file);
					int d = (int) (duration / 1000);
					Toast.makeText(ReportEditActivity.this, d+"秒", Toast.LENGTH_SHORT).show();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	
	@Override
	public void onClick(View v) {
		if(v == btn_bl_playorstop){
			if(mPlayer!=null&&mPlayer.isPlaying()){
				stopPlaying();
				btn_bl_playorstop.setText("播放");
			}else if(mPlayer!=null&&!mPlayer.isPlaying()&&finished){
				startPlaying();
				btn_bl_playorstop.setText("停止");
			}else if(mPlayer!=null&&!mPlayer.isPlaying()&&!finished){
				Toast.makeText(ReportEditActivity.this,
						getResources().getString(R.string.recordbutton_tips),
						Toast.LENGTH_SHORT).show();
			}
		}
		if(v == btn_loc){
			style = AppMsg.STYLE_INFO;
			appMsg = AppMsg.makeText(this, getResources().getString(R.string.locing), style);
			appMsg.setLayoutGravity(Gravity.BOTTOM);
			appMsg.show();
        	
			requestLocClick();
		}
		
		
		if(v == btn_e2w){
			if(!e2w_flag){
				direction = "东往西";
				btn_e2w.setBackgroundColor(getResources().getColor(R.color.titlebar));
				btn_w2e.setBackgroundColor(getResources().getColor(R.color.gray_overlay));
				btn_s2n.setBackgroundColor(getResources().getColor(R.color.gray_overlay));
				btn_n2s.setBackgroundColor(getResources().getColor(R.color.gray_overlay));
				e2w_flag = true;
				n2s_flag = false;
				s2n_flag = false;
				w2e_flag = false;
			}else{
				direction = "";
				btn_e2w.setBackgroundColor(getResources().getColor(R.color.gray_overlay));
				e2w_flag = false;
			}
		}
		if(v == btn_w2e){
			if(!w2e_flag){
				direction = "西往东";
				btn_w2e.setBackgroundColor(getResources().getColor(R.color.titlebar));
				btn_e2w.setBackgroundColor(getResources().getColor(R.color.gray_overlay));
				btn_s2n.setBackgroundColor(getResources().getColor(R.color.gray_overlay));
				btn_n2s.setBackgroundColor(getResources().getColor(R.color.gray_overlay));
				w2e_flag = true;
				n2s_flag = false;
				s2n_flag = false;
				e2w_flag = false;
			}else{
				direction = "";
				btn_w2e.setBackgroundColor(getResources().getColor(R.color.gray_overlay));
				w2e_flag = false;
			}
		}
		if(v == btn_s2n){
			if(!s2n_flag){
				direction = "南往北";
				btn_s2n.setBackgroundColor(getResources().getColor(R.color.titlebar));
				btn_e2w.setBackgroundColor(getResources().getColor(R.color.gray_overlay));
				btn_w2e.setBackgroundColor(getResources().getColor(R.color.gray_overlay));
				btn_n2s.setBackgroundColor(getResources().getColor(R.color.gray_overlay));
				s2n_flag = true;
				w2e_flag = false;
				n2s_flag = false;
				e2w_flag = false;
			}else{
				direction = "";
				btn_s2n.setBackgroundColor(getResources().getColor(R.color.gray_overlay));
				s2n_flag = false;
			}
			
		}
		if(v == btn_n2s){
			if(!n2s_flag){
				direction = "北往南";
				btn_n2s.setBackgroundColor(getResources().getColor(R.color.titlebar));
				btn_e2w.setBackgroundColor(getResources().getColor(R.color.gray_overlay));
				btn_s2n.setBackgroundColor(getResources().getColor(R.color.gray_overlay));
				btn_w2e.setBackgroundColor(getResources().getColor(R.color.gray_overlay));
				n2s_flag = true;
				w2e_flag = false;
				s2n_flag = false;
				e2w_flag = false;
			}else{
				direction = "";
				btn_n2s.setBackgroundColor(getResources().getColor(R.color.gray_overlay));
				n2s_flag = false;
			}
		}
	}
    
    private void startPlaying() { 
        try { 
        	//简便方法，每次点播放先reset一次，否则在player不同状态调不同方法容易出异常
        	mPlayer.reset();
            mPlayer.setDataSource(mFileName); 
            mPlayer.prepare(); 
            mPlayer.start(); 
        } catch (Exception e) { 
        	e.printStackTrace();
        } 
    }
    
    private void stopPlaying() { 
        mPlayer.stop();
    } 
    
    //============================照片相关=======================
    
    @Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		 if(requestCode == TAKE_PICTURE) {
			if (Bimp.drr.size() < Bimp.limit && resultCode == RESULT_OK) {
				int degree = PhotoUtil.readPictureDegree(path);
				Bitmap bm = PhotoUtil.createThumbFromFile(path, 350, 350);
				if(bm!=null){
					bm = PhotoUtil.rotaingImageView(degree, bm);
					
					File imageDir = new File(_imageDir);
					  if (!imageDir.exists()) {
						  imageDir.mkdirs();
					  }
					  
					String rotaingpaths = _imageDir+String.valueOf(System.currentTimeMillis())
							+ ".jpg";
					boolean df = PhotoUtil.saveBitmap2file(bm, rotaingpaths);
					if(df){
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
    
   
  //============================照片相关=======================
    
    
    //==========================上传相关=======================
    /**
	 * 提交报料内容
	 */
	private void uploadBaoliao(){
		if(!isupload){
			for (int i = 0; i < Bimp.drr.size(); i++) {
				String Str = Bimp.drr.get(i).substring( 
						Bimp.drr.get(i).lastIndexOf("/") + 1,
						Bimp.drr.get(i).lastIndexOf("."));
				dataList.add(FileUtils.SDPATH+Str+".jpg");				
			}
			
			//录音文件
			if(audiopath!=null&&!audiopath.equals("")){
				dataList.add(audiopath);
			}
		}
		
		String fileKey = "img";
		UploadUtil uploadUtil = UploadUtil.getInstance();
		uploadUtil.setOnUploadProcessListener(this);
		if(dataList.size()==0&&loc.equals("")&&text_bl.equals("")&&event.equals("")){
			style = AppMsg.STYLE_ALERT;
			appMsg = AppMsg.makeText(this, getResources().getString(R.string.tips_bl_nonull), style);
			appMsg.show();
			isupload = false;
		}else if(loc.equals("")){
			style = AppMsg.STYLE_ALERT;
			appMsg = AppMsg.makeText(this, getResources().getString(R.string.input_add), style);
			appMsg.show();
			isupload = false;
		}else{
			progressDialog.setMessage(getResources().getString(R.string.uploading));
			progressDialog.show();
			uploadUtil.uploadFile(dataList,fileKey,requestURL,params);
			isupload = true;
			isuploaddone = true;
		}
		
	}

    /**
	 * 用当前时间给取得的录音命名
	 * 
	 */
	private String getAudioFileName() {
		Date date = new Date(System.currentTimeMillis());
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmssSSS");
		return dateFormat.format(date) + ".amr";
	}
    
	
	private Handler handler_upload = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {

			case Constant.UPLOAD_FILE_DONE:
				String result = msg.obj+"";
				JSONObject jsonObject;
				String message = "";
				if(result!=null){
					try {
						jsonObject = new JSONObject(result);
						message = jsonObject.getString("message");
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
				if(msg.arg1 == Constant.UPLOAD_SUCCESS_CODE){
					showSuccessDialog(message,
							getResources().getString(R.string.dialog_confirm),
							getResources().getString(R.string.dialog_cancel));
				}
				else{
					isuploaddone = false;
					appMsg = AppMsg.makeText(ReportEditActivity.this, message, style);
					appMsg.setLayoutGravity(Gravity.BOTTOM);
					appMsg.show();
				}
				break;
				
			case PHOTO_DONE:
				dialog.dismiss();
			break;
			
			}
			super.handleMessage(msg);
		}
		
	};
	
	public void showSuccessDialog(String message,String yes,String no){
    	
    	AlertDialog.Builder builer = new Builder(ReportEditActivity.this);
		builer.setTitle("提示");
     	builer.setMessage(message);
		builer.setCancelable(false);
		
		builer.setPositiveButton(yes, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				onBackPressed();
			}
		});
		builer.setNegativeButton(no, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				Intent intent = new Intent(ReportEditActivity.this, ReportEditActivity.class); 
		        startActivity(intent); 
		        finish();
		        onDestroy();
			}
		});
		
		AlertDialog dialog = builer.create();
		dialog.show();
    }
	
    //==========================上传相关=======================
    
    
    
    //===============================定位相关==============================
	/**
	 * 地图初始化
	 */
	private void initMap(){
		MyApplication app = (MyApplication)this.getApplication();
        if (app.mBMapManager == null) {
            app.mBMapManager = new BMapManager(getApplicationContext());
            /**
             * 如果BMapManager没有初始化则初始化BMapManager
             */
            app.mBMapManager.init(new MyApplication.MyGeneralListener());
        }
    }
    
    /**
	 * 百度搜索反查地名初始化
	 */
	private void initSearch(){
		mSearch.init(MyApplication.getInstance().mBMapManager, new MKSearchListener() {
            @Override
            public void onGetPoiDetailSearchResult(int type, int error) {
            }
            
			public void onGetAddrResult(MKAddrInfo res, int error) {
				if (error != 0) {
					return;
				}
				if (res.type == MKAddrInfo.MK_REVERSEGEOCODE){
					//反地理编码：通过坐标点检索详细地址及周边poi
					MKGeocoderAddressComponent mc = res.addressComponents;
					//只搜索路名
					strInfo = mc.street+mc.streetNumber;
					//获取不了路名某些路段会显示s267,百度数据问题
					if(strInfo.equals("s267")||strInfo.equals("")||strInfo==null){
						strInfo = getResources().getString(R.string.locadd_fail);
					}
					et_loc.setText(strInfo);
					
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
			public void onGetShareUrlResult(MKShareUrlResult result, int type,
					int error) {
			}
        });
	}

	
	
	/**
	 * 定位初始化
	 */
	private void initLoc(){
        
        mLocClient = new LocationClient(this);
        mLocClient.registerLocationListener(myListener);
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true);//打开gps
        option.setCoorType("bd09ll");     //设置坐标类型
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
                return ;
        }
        public void onReceivePoi(BDLocation poiLocation) {
            if (poiLocation == null){
                return ;
            }
        }
    }
    
    /**
     * 手动触发一次定位请求
     */
    public void requestLocClick(){
        mLocClient.requestLocation();
        BDLocation loc = mLocClient.getLastKnownLocation();
        if(loc!=null){
        	lat = loc.getLatitude();
        	lng = loc.getLongitude();
        	GeoPoint ptCenter = new GeoPoint((int)(lat*1e6), (int)(lng*1e6));
    		mSearch.reverseGeocode(ptCenter);
        }else {
        	appMsg = AppMsg.makeText(this, getResources().getString(R.string.loc_fail), style);
        	appMsg.setLayoutGravity(Gravity.BOTTOM);
        	appMsg.show();
        }
    }
    
    
    private void getloc(int delay){
    	
    	new Handler().postDelayed(new Runnable(){    
    	    public void run() {    
    	    	requestLocClick(); 
    	    }    
    	 }, delay);
    }
    
    @Override
    protected void onDestroy() {
    	if (mLocClient != null){
            mLocClient.stop();
    	}
    	Bimp.bmp.clear();
		Bimp.drr.clear();
		Bimp.max = 0;
		FileUtils.deleteDir();
    	super.onDestroy();
    }
    
//    protected void onRestart() {
//		adapter.update();
//		super.onRestart();
//	}

	@Override
	public void onCompletion(MediaPlayer mp) {
		btn_bl_playorstop.setText("播放");
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

			View outside = view.findViewById(R.id.outside);
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
					Intent intent = new Intent(ReportEditActivity.this,
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
		  
		File file = new File(_cameraDir, String.valueOf(System.currentTimeMillis())
				+ ".jpg");
		path = file.getPath();
		Uri imageUri = Uri.fromFile(file);
		openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
		startActivityForResult(openCameraIntent, TAKE_PICTURE);
	}
	
	private void deldialog(){
		AlertDialog.Builder builder = new Builder(this);
		builder.setMessage("确认删除录音吗?");
		builder.setTitle("提示");
		builder.setPositiveButton(getResources().getString(R.string.dialog_confirm_yes), new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				finished = false;
				audiopath = null;
				Toast.makeText(ReportEditActivity.this, "删除成功！", Toast.LENGTH_SHORT).show();
			}
		});
		builder.setNegativeButton(getResources().getString(R.string.dialog_no), new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		builder.create().show();
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		path = savedInstanceState.getString("path");
		super.onRestoreInstanceState(savedInstanceState);
	}


	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putString("path", path);
		super.onSaveInstanceState(outState);
	}
	
	public void onResume() {
		super.onResume();
		adapter.update();
		gridView.setAdapter(adapter);
	}
 }

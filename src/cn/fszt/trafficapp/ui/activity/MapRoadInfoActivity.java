package cn.fszt.trafficapp.ui.activity;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.map.Geometry;
import com.baidu.mapapi.map.Graphic;
import com.baidu.mapapi.map.GraphicsOverlay;
import com.baidu.mapapi.map.ItemizedOverlay;
import com.baidu.mapapi.map.LocationData;
import com.baidu.mapapi.map.MKEvent;
import com.baidu.mapapi.map.MKMapViewListener;
import com.baidu.mapapi.map.MapController;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationOverlay;
import com.baidu.mapapi.map.OverlayItem;
import com.baidu.mapapi.map.PoiOverlay;
import com.baidu.mapapi.map.PopupClickListener;
import com.baidu.mapapi.map.PopupOverlay;
import com.baidu.mapapi.search.MKAddrInfo;
import com.baidu.mapapi.search.MKBusLineResult;
import com.baidu.mapapi.search.MKDrivingRouteResult;
import com.baidu.mapapi.search.MKPoiInfo;
import com.baidu.mapapi.search.MKPoiResult;
import com.baidu.mapapi.search.MKSearch;
import com.baidu.mapapi.search.MKSearchListener;
import com.baidu.mapapi.search.MKShareUrlResult;
import com.baidu.mapapi.search.MKSuggestionResult;
import com.baidu.mapapi.search.MKTransitRouteResult;
import com.baidu.mapapi.search.MKWalkingRouteResult;
import com.baidu.platform.comapi.basestruct.GeoPoint;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import cn.fszt.trafficapp.R;
import cn.fszt.trafficapp.ui.MyApplication;
import cn.fszt.trafficapp.util.HttpUtil;
import cn.fszt.trafficapp.util.MapSymbolUtil;
import cn.fszt.trafficapp.util.PhotoUtil;
import cn.fszt.trafficapp.util.version.VersionUtil;
import cn.fszt.trafficapp.widget.msg.AppMsg;


/**
 * 路况地图主界面
 * @author AeiouKong
 *
 */
public class MapRoadInfoActivity extends Activity implements OnClickListener{
	
//	BMapManager mBMapMan = null; 
	MKSearch mMKSearch = null;
	// 定位相关
	LocationClient mLocClient;
	LocationData locData = null;
	MKMapViewListener mMapListener = null;
	GraphicsOverlay graphicsOverlay = null;
	ItemizedOverlay<OverlayItem> itemOverlay = null;
	private PopupWindow popupWindow;
	private ViewGroup myView;
	public MyLocationListenner myListener = new MyLocationListenner();
	locationOverlay myLocationOverlay = null;
	private PopupOverlay pop = null;//弹出泡泡图层，浏览节点时使用
	private TextView  popupText;//泡泡view
	private View viewCache = null;
	private MyLocationMapView mMapView;	// 地图View
	private MapController mMapController = null;
	//UI相关
	OnCheckedChangeListener radioButtonListener = null;
	private ImageView btn_search;
	private LinearLayout ll_small,ll_big;
	private EditText et_search;
	private Handler handler_up;
	private Runnable runnable_up;
	private boolean isRequest = false;//是否手动触发请求定位
	private boolean isFirstLoc = true;//是否首次定位
	private SharedPreferences sp;
	int linecolor;
	private String requestURL;
	private boolean roadstatus_flag;
	
	private List<Graphic> lineGraphic = null;
	private List<OverlayItem> items = null;
	
	AppMsg.Style style = AppMsg.STYLE_INFO;
	AppMsg appMsg;
	
	VersionUtil version;
	int densityDpi;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initMap();
        setContentView(R.layout.activity_map);
        
        
        DisplayMetrics metric = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metric);
        densityDpi = metric.densityDpi;
        
        initLocMap();
        initView();
        initSearch();
        sp = getSharedPreferences("traffic", Context.MODE_PRIVATE);
        Editor ed = sp.edit();
        ed.commit();
        updateTimer(sp.getInt("updatetimer", 120),1);
        linecolor = sp.getInt("linecolor", 1);
        
        getActionBar().setTitle(getResources().getString(R.string.n_map));
        getActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.titlebar)));
		getActionBar().setDisplayHomeAsUpEnabled(true);
		
		getOverflowMenu();
    }
    

    private void getOverflowMenu() {
        try {
           ViewConfiguration config = ViewConfiguration.get(this);
           Field menuKeyField = ViewConfiguration.class.getDeclaredField("sHasPermanentMenuKey");
           if(menuKeyField != null) {
               menuKeyField.setAccessible(true);
               menuKeyField.setBoolean(config, false);
           }
       } catch (Exception e) {
           e.printStackTrace();
       }
   }
    
    /**
     * 初始化ui
     */
    private void initView(){
    	version = new VersionUtil(MapRoadInfoActivity.this);
    	lineGraphic = new ArrayList<Graphic>();
    	items = new ArrayList<OverlayItem>();
    	requestURL = getResources().getString(R.string.server_url)+getResources().getString(R.string.map_url);
    	ll_small = (LinearLayout) findViewById(R.id.ll_small);
    	ll_small.setOnClickListener(this);
    	ll_big = (LinearLayout) findViewById(R.id.ll_big);
    	ll_big.setOnClickListener(this);
    	btn_search = (ImageView) findViewById(R.id.btn_search);
    	btn_search.setOnClickListener(this);
    	et_search = (EditText) findViewById(R.id.et_search);
    	et_search.setOnKeyListener(new OnKeyListener() {
			
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
			if (KeyEvent.KEYCODE_ENTER == keyCode && event.getAction() == KeyEvent.ACTION_DOWN) {
				getSearch();
                return true;  
            }  
				return false;
			}
		});
    	//加载布局文件
        myView= (ViewGroup)getLayoutInflater().inflate(R.layout.popview, null);
        
        //后两个参数表示popupWindow的宽，高
        popupWindow = new PopupWindow(myView,160,200);
        popupWindow.setOutsideTouchable(true);
        ColorDrawable dw = new ColorDrawable(-00000);
        popupWindow.setBackgroundDrawable(dw);
        popupWindow.update();
    }
    
    @Override
	public boolean onCreateOptionsMenu(Menu menu) {
		
		MenuInflater inflater = getMenuInflater();

	    inflater.inflate(R.menu.locationoverlay, menu);

		return true;
	}
    
    @Override
	public boolean onOptionsItemSelected(MenuItem item) {

    	switch(item.getItemId()){
		case android.R.id.home:
			finish();
			break;
		case R.id.item_map_loc:
			requestLocClick();
			break;
		case R.id.item_map_baoliao:
			Intent intent_baoliao = new Intent(this,ReportEditActivity.class);
			startActivity(intent_baoliao);
			break;
		case R.id.item_map_text:
			Intent intent_text = new Intent(this,TextActivity.class);
			startActivity(intent_text);
			break;
		case R.id.item_map_roadinfoimage:
			Intent intent_roadinfoimage = new Intent(this,RoadImageActivity.class);
			startActivity(intent_roadinfoimage);
			break;
		}
		return true;
	}
    
    /**
     * 初始化地图
     */
    private void initMap(){
//		mBMapMan=new BMapManager(getApplication());  
    	MyApplication app = (MyApplication)this.getApplication();
        if (app.mBMapManager == null) {
            app.mBMapManager = new BMapManager(getApplicationContext());
            /**
             * 如果BMapManager没有初始化则初始化BMapManager
             */
            app.mBMapManager.init(new MyApplication.MyGeneralListener());
        }
//		mBMapMan.init(getResources().getString(R.string.bmapkey), null);
		mMKSearch = new MKSearch();  
    }
    
    private void initSearch(){
    	mMKSearch.init(MyApplication.getInstance().mBMapManager, new MKSearchListener(){
			@Override
			public void onGetAddrResult(MKAddrInfo arg0, int arg1) {
			}
			@Override
			public void onGetBusDetailResult(MKBusLineResult arg0, int arg1) {
			}
			@Override
			public void onGetDrivingRouteResult(MKDrivingRouteResult arg0,
					int arg1) {
			}
			@Override
			public void onGetPoiDetailSearchResult(int arg0, int arg1) {
			}
			@Override
			public void onGetPoiResult(MKPoiResult res, int arg1, int error) {
				// 错误号可参考MKEvent中的定义  
				if ( error == MKEvent.ERROR_RESULT_NOT_FOUND){  
					showMsg("没找到结果", "alert", "top");
					return ;  
				}  
				else if (error != 0 || res == null) {  
					showMsg("搜索出错", "alert", "top");
					return;
				}
				
				try{
					PoiOverlay poiOverlay = new PoiOverlay(MapRoadInfoActivity.this, mMapView);  
					poiOverlay.setData(res.getAllPoi());  
						mMapView.getOverlays().clear();  
						mMapView.getOverlays().add(poiOverlay);  
						mMapView.refresh();  
					for(MKPoiInfo info : res.getAllPoi() ){  
						if ( info.pt != null ){  
						mMapView.getController().animateTo(info.pt);  
						break; 
						}  
					 } 
				}catch(NullPointerException e){
					e.printStackTrace();
				}
			}

			@Override
			public void onGetShareUrlResult(MKShareUrlResult arg0, int arg1,
					int arg2) {
			}
			@Override
			public void onGetSuggestionResult(MKSuggestionResult res, int iError) {
			}
			@Override
			public void onGetTransitRouteResult(MKTransitRouteResult arg0,
					int arg1) {
			}
			@Override
			public void onGetWalkingRouteResult(MKWalkingRouteResult arg0,
					int arg1) {
			}});
    	
    }
    
    /**
     * 定位地图初始化
     */
    private void initLocMap(){
    	//地图初始化
        mMapView = (MyLocationMapView)findViewById(R.id.bmapsView);
        mMapController = mMapView.getController();
        //佛山为中心点
        GeoPoint point = new GeoPoint((int)(23.029241* 1E6),(int)(113.129714* 1E6));
        mMapController.setCenter(point);
        mMapController.setZoom(16);
        mMapController.enableClick(true);
        mMapController.setRotationGesturesEnabled(false);
        mMapController.setOverlookingGesturesEnabled(false);
        mMapController.setCompassMargin(mMapView.getWidth() - 100, 100);
        mMapView.setBuiltInZoomControls(false);
        mMapView.setTraffic(true);
        //创建 弹出泡泡图层
        createPaopao();
        
        //定位初始化
        mLocClient = new LocationClient(this);
        locData = new LocationData();
        mLocClient.registerLocationListener(myListener);
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true);
        option.setCoorType("bd09ll");     //设置坐标类型
        option.setScanSpan(1000);
        option.setOpenGps(true);
        mLocClient.setLocOption(option);
        mLocClient.start();
       
        itemOverlay = new ItemizedOverlay<OverlayItem>(getResources().getDrawable(R.drawable.shigong),mMapView);
        graphicsOverlay = new GraphicsOverlay(mMapView);
        
        //定位图层初始化
		myLocationOverlay = new locationOverlay(mMapView);
		//设置定位数据
	    myLocationOverlay.setData(locData);
	    //添加定位图层
		mMapView.getOverlays().add(myLocationOverlay);
		myLocationOverlay.enableCompass();
		mMapListener = new MKMapViewListener(){
			@Override
			public void onClickMapPoi(MapPoi arg0) {
			}
			@Override
			public void onGetCurrentMap(Bitmap arg0) {
			}
			@Override
			public void onMapAnimationFinish() {
			}
			@Override
			public void onMapLoadFinish() {
				//RoadTask task = new RoadTask(LocationOverlay.this);
				//task.execute(requestURL);
			}
			@Override
			public void onMapMoveFinish() {
			}
		};
		mMapView.regMapViewListener(MyApplication.getInstance().mBMapManager, mMapListener);
		//修改定位数据后刷新图层
		mMapView.refresh();
    }
    
    /**
     * 手动触发定位请求
     */
    public void requestLocClick(){
    	isRequest = true;
        mLocClient.requestLocation();
        showMsg(getResources().getString(R.string.locing), "info", "bottom");
    }
    
    public void requestLocAuto(){
    	isRequest = false;
        mLocClient.requestLocation();
    }
    
    
    /**
     * 获取当前屏幕的路线坐标并连线
     */
//    private void getLine(){
////    	Toast.makeText(LocationOverlay.this, 
////			       "拖放完成", 
////			       Toast.LENGTH_SHORT).show();
//		
//		GeoPoint centerPoint = mMapView.getMapCenter();
//		int tbSpan = mMapView.getLatitudeSpan();
//		int lrSpan = mMapView.getLongitudeSpan();
//		//左上角，右上角，左下角，右下角
//		GeoPoint ltPoint = new GeoPoint(centerPoint.getLatitudeE6() + tbSpan / 2, centerPoint.getLongitudeE6() - lrSpan / 2);
//		GeoPoint rtPoint = new GeoPoint(centerPoint.getLatitudeE6() + tbSpan / 2, centerPoint.getLongitudeE6() + lrSpan / 2);
//		GeoPoint lbPoint = new GeoPoint(centerPoint.getLatitudeE6() - tbSpan / 2, centerPoint.getLongitudeE6() - lrSpan / 2);
//		GeoPoint rbPoint = new GeoPoint(centerPoint.getLatitudeE6() - tbSpan / 2, centerPoint.getLongitudeE6() + lrSpan / 2);
//		System.out.println("左上角坐标:经度："+ltPoint.getLongitudeE6()*1e-6+",纬度："+ltPoint.getLatitudeE6()*1e-6);
//		System.out.println("右上角坐标:经度："+rtPoint.getLongitudeE6()*1e-6+",纬度："+rtPoint.getLatitudeE6()*1e-6);
//		System.out.println("左下角坐标:经度："+lbPoint.getLongitudeE6()*1e-6+",纬度："+lbPoint.getLatitudeE6()*1e-6);
//		System.out.println("右下角坐标:经度："+rbPoint.getLongitudeE6()*1e-6+",纬度："+rbPoint.getLatitudeE6()*1e-6);
//		
//		RoadTask task = new RoadTask(getBaseContext());
//		task.execute(requestURL+"&point1lng="+ltPoint.getLongitudeE6()*1e-6+"&point1lat="+ltPoint.getLatitudeE6()*1e-6+"&point2lng="+rtPoint.getLongitudeE6()*1e-6+
//						"&point2lat="+rtPoint.getLatitudeE6()*1e-6+"&point3lng="+lbPoint.getLongitudeE6()*1e-6+"&point3lat="+lbPoint.getLatitudeE6()*1e-6+
//						"&point4lng="+rbPoint.getLongitudeE6()*1e-6+"&point4lat="+rbPoint.getLatitudeE6()*1e-6);
//    	
//    }
   
    /**
     * 画路线
     * @param points 每条线的坐标点   格式 "113.222,23.22;113.232,23.33"
     * @param event  流量 "畅通、阻塞、缓慢"
     * @return
     */
    private Graphic drawLine(String points,String event){
    	
    	//String allpoint = "113.133917,23.054085;113.129572,23.054041;113.122973,23.0539;113.122843,23.053898;113.116727,23.053799";
    	String[] strArray = points.split(";");
    	GeoPoint[] linePoints = new GeoPoint[strArray.length];
    	Geometry lineGeometry = new Geometry();
    	Graphic lineGraphic = null;
    	for(int i=0;i<strArray.length;i++){
    		String[] sp = strArray[i].split(",");
    		double mlng = Double.parseDouble(sp[0]);
    		double mlat = Double.parseDouble(sp[1]);
    		int lat1 =  (int) (mlat*1E6);
    		int lng2 =  (int) (mlng*1E6);
     		linePoints[i] = new GeoPoint(lat1,lng2);
    	}
    	lineGeometry.setPolyLine(linePoints);
    	
    	//正常模式
    	if(linecolor==1){
    		if(event.equals(getResources().getString(R.string.changtong))){
        		lineGraphic = new Graphic(lineGeometry,MapSymbolUtil.lineSymbol_ct());
        	}if(event.equals(getResources().getString(R.string.zuse))){
        		lineGraphic = new Graphic(lineGeometry,MapSymbolUtil.lineSymbol_zs());
        	}if(event.equals(getResources().getString(R.string.huanman))){
        		lineGraphic = new Graphic(lineGeometry,MapSymbolUtil.lineSymbol_hm());
        	}
        //色弱模式	
    	}else if(linecolor==0){
    		if(event.equals(getResources().getString(R.string.changtong))){
        		lineGraphic = new Graphic(lineGeometry,MapSymbolUtil.lineSymbol_sr_ct());
        	}if(event.equals(getResources().getString(R.string.zuse))){
        		lineGraphic = new Graphic(lineGeometry,MapSymbolUtil.lineSymbol_sr_zs());
        	}if(event.equals(getResources().getString(R.string.huanman))){
        		lineGraphic = new Graphic(lineGeometry,MapSymbolUtil.lineSymbol_sr_hm());
        	}
    	}
    	return lineGraphic;
    }
    
    
    /**
     * 设置需要延迟多久才第一次加载路况，和之后相隔多久自动更新路况
     * @param delay
     * @param firsttime
     */
    private void updateTimer(final int delay,int firsttime){
    	
    	handler_up = new Handler(); 
    	runnable_up = new Runnable(){ 
	    	@Override 
	    	public void run() { 
	    		
//	    		mMapView.getOverlays().remove(graphicsOverlay);
	    		mMapView.getOverlays().remove(itemOverlay);
				mMapView.refresh(); 
				new roadstatus().start();
				handler_up.postDelayed(this, delay*1000); //每隔多久执行一次
	    	} 
    	}; 
    	handler_up.postDelayed(runnable_up, firsttime*1000);// 第一次延迟多久打开定时执行任务
    }
    
    /**
     * 自定义事件marker
     * @param marker
     */
    public void modifyLocationOverlayIcon(Drawable marker){
    	//当传入marker为null时，使用默认图标绘制
    	myLocationOverlay.setMarker(marker);
    	//修改图层，需要刷新MapView生效
    	mMapView.refresh();
    }
    /**
	 * 创建弹出泡泡图层
	 */
	public void createPaopao(){

		viewCache = getLayoutInflater().inflate(R.layout.custom_text_view, null);
        popupText =(TextView) viewCache.findViewById(R.id.textcache);
        //泡泡点击响应回调
        PopupClickListener popListener = new PopupClickListener(){
			@Override
			public void onClickedPopup(int index) {
			}
        };
        pop = new PopupOverlay(mMapView,popListener);
        MyLocationMapView.pop = pop;
	}
	
	/**
     * 定位SDK监听函数
     */
    public class MyLocationListenner implements BDLocationListener {
    	
        @Override
        public void onReceiveLocation(BDLocation location) {
            if (location == null)
                return ;
            
            locData.latitude = location.getLatitude();
            locData.longitude = location.getLongitude();
            //如果不显示定位精度圈，将accuracy
            locData.accuracy = location.getRadius();
            float derect = location.getDerect();
            locData.direction = derect;
            //更新定位数据
            myLocationOverlay.setData(locData);
            //更新图层数据执行刷新
            
            try{
            	mMapView.refresh();
            
	            GeoPoint centerPoint = mMapView.getMapCenter();
	    		int tbSpan = mMapView.getLatitudeSpan();
	    		int lrSpan = mMapView.getLongitudeSpan();
	    		
	    		int screemtlat = centerPoint.getLatitudeE6() + tbSpan / 2;  //屏幕最大纬度
	    		int screemblat = centerPoint.getLatitudeE6() - tbSpan / 2;  //屏幕最小纬度
	    		int screemllng = centerPoint.getLongitudeE6() - lrSpan / 2;  //屏幕最小经度
	    		int screemrlng = centerPoint.getLongitudeE6() + lrSpan / 2;   //屏幕最大经度
	    		
	    		int loclat = (int)(locData.latitude* 1e6);  //定位点纬度
	    		int loclng = (int)(locData.longitude * 1e6); //定位点经度
	    		GeoPoint locPoint = new GeoPoint(loclat, loclng);
	    		
	    		//自动更新位置并且当前位置在屏幕里面，则地图随位置移动
	    		if(!isRequest&&(loclat<screemtlat&&loclat>screemblat&&loclng>screemllng&&loclng<screemrlng)){
	    			//移动地图到定位点
	                mMapController.animateTo(locPoint);
	    		}
	            //是手动触发请求或首次定位时，移动到定位点
	            if (isRequest || isFirstLoc){
	            	//移动地图到定位点
	                mMapController.animateTo(locPoint);
	                isRequest = false;
	            }
	            //首次定位完成
	            isFirstLoc = false;
            }catch(NullPointerException e){
            	e.printStackTrace();
            }
        }
        
        public void onReceivePoi(BDLocation poiLocation) {
            if (poiLocation == null){
                return ;
            }
        }
    }
    
    //继承MyLocationOverlay重写dispatchTap实现点击处理
  	public class locationOverlay extends MyLocationOverlay{

  		public locationOverlay(MapView mapView) {
  			super(mapView);
  		}
  		@Override
  		protected boolean dispatchTap() {
  			//处理点击事件,弹出泡泡
  			popupText.setBackgroundResource(R.drawable.popup);
			popupText.setText("我的位置");
			pop.showPopup(PhotoUtil.getBitmapFromView(popupText),
					new GeoPoint((int)(locData.latitude*1e6), (int)(locData.longitude*1e6)),
					8);
  			return true;
  		}
  	}

    @Override
    protected void onPause() {
        mMapView.onPause();
        super.onPause();
    }
    
    @Override
    protected void onResume() {
        mMapView.onResume();
        super.onResume();
    }
    
    @Override
    protected void onDestroy() {
    	//退出时停止定时更新路况
        handler_up.removeCallbacks(runnable_up);
        roadstatus_flag = true;
        
        if (mLocClient != null)
            mLocClient.stop();
        if (mMapView!=null){
        	mMapView.destroy();
        }
        
        super.onDestroy();
    }
    
    @Override
    protected void onSaveInstanceState(Bundle outState) {
    	super.onSaveInstanceState(outState);
    	mMapView.onSaveInstanceState(outState);
    	
    }
    
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
    	super.onRestoreInstanceState(savedInstanceState);
    	mMapView.onRestoreInstanceState(savedInstanceState);
    }
    
	public void toSecond(View view){
		int[] location = new int[2];
		view.getLocationOnScreen(location);
		popupWindow.showAtLocation(view, Gravity.NO_GRAVITY, location[0], location[1]-popupWindow.getHeight());
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()){
			case R.id.btn_loc:
				requestLocClick();
			break;
			case R.id.ll_big:
				mMapController.setZoom(mMapView.getZoomLevel()+1);
				if(mMapView.getZoomLevel()==19.0){
					showMsg(getResources().getString(R.string.zoombiggest), "info", "bottom");
				}
			break;
			case R.id.ll_small:
				mMapController.setZoom(mMapView.getZoomLevel()-1);
				if(mMapView.getZoomLevel()==3.0){
					showMsg(getResources().getString(R.string.zoomleast), "info", "bottom");
				}
			break;
			case R.id.btn_search:
				getSearch();
			break;
		}
	}
	
	private void getSearch(){
		String content = et_search.getText().toString();
		if(!"".equals(content) && content!=null){
			mMKSearch.poiSearchInCity("佛山", content);
		}else{
			showMsg("请输入搜索内容", "alert", "top");
		}
	}
	
	class roadstatus extends Thread{
		@Override
		public void run() {
			if(!roadstatus_flag){
				String resultString = HttpUtil.GetStringFromUrl(requestURL);
	    	    if(resultString!=null){
	    	    	String road = null;
	                String event = null;
	                String eventlng;
	                String eventlat;
	                String tempicon;  //300*300
	                String tempicon_small;  //80*80
	                String tempicon_middle; //100*100
	                String tempicon_large;  //150*150
	                JSONObject jsonObject;
	                JSONArray jsonArray;
					try {
						jsonObject = new JSONObject(resultString).getJSONObject("roads");
//						boolean b_road = jsonObject.has("road");
//		                if(b_road){
//		                	lineGraphic.clear();
//		                	jsonArray = jsonObject.getJSONArray("road");
//			                for(int i = 0; i<jsonArray.length(); i++){
//			                    //新建一个JSON对象，该对象是某个数组里的其中一个对象
//			                    JSONObject jsonObject2 = (JSONObject)jsonArray.opt(i);
//			                    road = jsonObject2.getString("points"); //获取数据
//			                    event = jsonObject2.getString("event");
//			                    lineGraphic.add(drawLine(road,event));
//			                }
//		                }else{
//		                	lineGraphic.clear();
//		                }
		                boolean b_icon = jsonObject.has("icon");
		                if(b_icon){
		                	items.clear();
		                	jsonArray = jsonObject.getJSONArray("icon");
			                for(int j=0;j<jsonArray.length();j++){
			                	JSONObject jsonObject3 = (JSONObject) jsonArray.get(j);
			                	eventlng = jsonObject3.getString("eventlng");
			                	eventlat = jsonObject3.getString("eventlat");
			                	tempicon = jsonObject3.getString("tempicon");
			                	tempicon_small = jsonObject3.getString("tempicon_small");
			                	tempicon_middle = jsonObject3.getString("tempicon_middle");
			                	tempicon_large = jsonObject3.getString("tempicon_large");
			                	if(densityDpi == 320){
			                		items.add(drawevent(eventlng, eventlat, tempicon_middle));
			            		}else if(densityDpi == 240){
			            			items.add(drawevent(eventlng, eventlat, tempicon_small));
			            		}else if(densityDpi == 160){
			            			items.add(drawevent(eventlng, eventlat, tempicon_small));
			            		}else if(densityDpi == 440){
			            			items.add(drawevent(eventlng, eventlat, tempicon_large));
			            		}else if(densityDpi == 480){
			            			items.add(drawevent(eventlng, eventlat, tempicon_large));
			            		}else if(densityDpi == 640){
			            			items.add(drawevent(eventlng, eventlat, tempicon_large));
			            		}else{
			            			items.add(drawevent(eventlng, eventlat, tempicon_middle));
			            		}
//			                	items.add(drawevent(eventlng, eventlat, tempicon));
			                }
		                }else{
		                	items.clear();
		                }
		                handler.sendEmptyMessage(1);
					} catch (JSONException e) {
						handler.sendEmptyMessage(2);
						e.printStackTrace();
					}
	    	    }
			}
		}
	}
	
	
	private Handler handler = new Handler(){
		public void handleMessage(Message msg) {
			switch(msg.what){
			case 1:
//				updateroad(lineGraphic);
				updateevent(items);
				break;
			case 2:
				break;
			}
		}
	};
	
	//加载事件标注marker
	private OverlayItem drawevent(String lng,String lat,String mark){
		
		double dlat = Double.parseDouble(lat);
		double dlng = Double.parseDouble(lng);
		int ilat =  (int) (dlat*1E6);
		int ilng =  (int) (dlng*1E6);
		GeoPoint p = new GeoPoint (ilat,ilng);
		OverlayItem item = new OverlayItem(p,"","");
//		item.setMarker(getResources().getDrawable(mark));
		item.setMarker(PhotoUtil.getcontentPic(mark));
		return item;
	}
	
	
	private void updateevent(List<OverlayItem> items){
		if(items!=null&&items.size()>0){
			if(mMapView!=null){
				try{
					mMapView.getOverlays().remove(itemOverlay);
					mMapView.getOverlays().add(itemOverlay);
					itemOverlay.addItem(items);
					mMapView.refresh();
				}catch(NullPointerException e){
					e.printStackTrace();
				}
			}
		}else{
    	}
	}
	
	private void updateroad(List<Graphic> lineGraphic){
		if(lineGraphic!=null&&lineGraphic.size()>0){
			if(mMapView!=null){
				try{
					mMapView.getOverlays().remove(graphicsOverlay);
		            mMapView.getOverlays().add(graphicsOverlay);
			        	//添加折线
		            	try {
		    				for(Graphic g : lineGraphic){
		    					graphicsOverlay.setData(g);
		    				}
		    			} catch (Exception e) {
		    				e.printStackTrace();
		    			} 
		            //执行地图刷新使生效
		            mMapView.refresh();
				}catch(NullPointerException e){
					e.printStackTrace();
				}
			}
    	}
    	else{
    	}
	}
	
	private void showMsg(String msg,String styletype,String gravity){
		
		if(styletype.equals("alert")){
			style = AppMsg.STYLE_ALERT;
		}else if(styletype.equals("info")){
			style = AppMsg.STYLE_INFO;
		}
		
		appMsg = AppMsg.makeText(MapRoadInfoActivity.this, msg, style);
		
		if(gravity.equals("bottom")){
			appMsg.setLayoutGravity(Gravity.BOTTOM);
		}else if(gravity.equals("top")){
		}
        appMsg.show();
	}
}

/**
 * 继承MapView重写onTouchEvent实现泡泡处理操作
 * @author AeiouKong
 *
 */
class MyLocationMapView extends MapView{
	static PopupOverlay pop = null;//弹出泡泡图层，点击图标
	public MyLocationMapView(Context context) {
		super(context);
	}
	public MyLocationMapView(Context context, AttributeSet attrs){
		super(context,attrs);
	}
	public MyLocationMapView(Context context, AttributeSet attrs, int defStyle){
		super(context, attrs, defStyle);
	}
	@Override
    public boolean onTouchEvent(MotionEvent event){
		if (!super.onTouchEvent(event)){
			//消隐泡泡
			if (pop != null && event.getAction() == MotionEvent.ACTION_UP)
				pop.hidePop();
		}
		return true;
	}
}


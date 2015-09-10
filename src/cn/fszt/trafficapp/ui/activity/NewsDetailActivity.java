package cn.fszt.trafficapp.ui.activity;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.umeng.analytics.MobclickAgent;

import cn.fszt.trafficapp.R;
import cn.fszt.trafficapp.adapter.NewsDetailAdapter;
import cn.fszt.trafficapp.domain.PinglunNewsData;
import cn.fszt.trafficapp.ui.service.ReplayPlayerService;
import cn.fszt.trafficapp.util.Constant;
import cn.fszt.trafficapp.util.FileUtil;
import cn.fszt.trafficapp.util.HttpUtil;
import cn.fszt.trafficapp.widget.MyListview;
import cn.fszt.trafficapp.widget.msg.AppMsg;
import cn.fszt.trafficapp.widget.photoview.DisplayImageOptionsUtil;
import cn.fszt.trafficapp.widget.photoview.ViewPagerByList;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.VideoView;


/**
 * 924资讯快报正文详情页
 * @author AeiouKong
 *
 */
public class NewsDetailActivity extends Activity implements OnClickListener{
	Matrix matrix = new Matrix();
	Matrix saveMatrix = new Matrix();
	float minScaleR;//最小缩放比例
	static final float MAX_SCALE = 6f;//最大缩放比例
	
	private int startindex = 1;
	private int endindex = 2;
	private static final int PINGLUN = 99;
	private static final int FULLSCREEN = 98;
	private LayoutInflater inflater;
	private List<PinglunNewsData> arrays;
	private MyListview listView;
	private NewsDetailAdapter adapter;
	private ImageView iv_news_item_img,iv_news_item_img1,iv_news_item_img2,iv_news_item_img3,iv_news_item_img4,iv_news_item_img5;
	private ImageView iv_news_item_img6,iv_news_item_img7,iv_news_item_img8,iv_news_item_img9,iv_news_item_img10;
	private TextView tv_news_item_imgdesc1,tv_news_item_imgdesc2,tv_news_item_imgdesc3,tv_news_item_imgdesc4,tv_news_item_imgdesc5,tv_news_item_newimgdesc;
	private TextView tv_news_item_imgdesc6,tv_news_item_imgdesc7,tv_news_item_imgdesc8,tv_news_item_imgdesc9,tv_news_item_imgdesc10,tv_news_item_videodesc,tv_news_item_voicedesc;
	private TextView tv_item_title,tv_item_date,tv_item_content;
	private String id;
	private ImageView btn_news_pl_more,btn_fullscreen,btn_mediaplay;
	private String url,push;
	private String getpl_url,delmpl_url,weibopermission,weibocontent,reportnews_url;
	private int uid,densityDpi;
	private TextView tv_news_sofa;
	private LinearLayout ll_news_pinglun; //相关评论
	
	DisplayMetrics metric;
	Bitmap bitmap;
	
	private VideoView mVideoView;
    private Uri mUri = null;
    private int mPositionWhenPaused = -1;

    private MediaController mMediaController;

    private String videourl = "";
    private String voiceurl = "";
    
	private boolean video_flag;
	private RelativeLayout rl_videoview,rl_voiceview;
	
	private ImageLoader imageLoader;
	private DisplayImageOptions options;
	private ArrayList<String> imgurls = new ArrayList<String>();
	private ProgressBar pb_news,pb_videoview,pb_voice_huanchong;
	
	private boolean flag;  //标记是否正在播放，flase为停止，true为正在播放
	private boolean playflag; //标记播放暂停的图标
	private boolean progressflag; //标记进度条进程
	private boolean playorpause; //标记播放线程
	private TextView tv_voicetimer,tv_voicetimerbegin;
	public static SeekBar audioSeekBar = null;
	
	AppMsg.Style style = AppMsg.STYLE_INFO;
	AppMsg appMsg;
	private MenuItem item_news_shareweibo;
	private SharedPreferences sp;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		metric = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metric);
        densityDpi = metric.densityDpi;
		
		if(densityDpi == 240){
			setContentView(R.layout.activity_newsitem_hdpi);
        }else{
        	setContentView(R.layout.activity_newsitem);
        }
		
		Intent intent = getIntent();
		id = intent.getStringExtra("id");
		push = intent.getStringExtra("push");
		
		initView();
		
		NewsItemTask task = new NewsItemTask();
		task.execute(url);
		
		getActionBar().setTitle(getResources().getString(R.string.n_zixun));
		getActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.titlebar)));
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setDisplayShowHomeEnabled(false);
		
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
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		
		MenuInflater inflater = getMenuInflater();

	    inflater.inflate(R.menu.newsitem, menu);
	    
	    item_news_shareweibo = menu.findItem(R.id.item_news_shareweibo);
	    
	    weibopermission = sp.getString("weibopermission", "500");
	    
//	    if(weibopermission!=null&&"200".equals(weibopermission)){
//	    	item_news_shareweibo.setVisible(true);
//	    }
	    return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch(item.getItemId()){
		case android.R.id.home:
			if(push!=null&&"true".equals(push)){
				Intent intent = new Intent(NewsDetailActivity.this,NewsListActivity.class);
				intent.putExtra("push", "true");
				startActivity(intent);
				finish();
			}else{
				finish();
			}
			break;
			
		case R.id.item_news_pingluncontent:
			Intent intent_content = new Intent(this,NewsCommentListActivity.class);
			intent_content.putExtra("id", id);
			startActivityForResult(intent_content,PINGLUN);
			break;
			
		case R.id.item_news_sentpinglun:
			Intent intent_sent = new Intent(this,ActCommentEditActivity.class);
			intent_sent.putExtra("id", id);
			intent_sent.putExtra("pingluntype", "news");
			startActivityForResult(intent_sent,PINGLUN);
			break;
		
		case R.id.item_news_shareweibo:
//			send(weibocontent+getResources().getString(R.string.weibocontent));
			break;
	}
	return true;
	}
	
	private void initView(){
		pb_news = (ProgressBar) findViewById(R.id.pb_news);
		sp = getSharedPreferences("USER", Context.MODE_PRIVATE);
    	uid = sp.getInt("uid", 0);
    	weibopermission = sp.getString("weibopermission", "500");
		url = getResources().getString(R.string.server_url)+getResources().getString(R.string.news_item_url)+"&hdnewid=";
		getpl_url = getResources().getString(R.string.server_url)+getResources().getString(R.string.news_getpl_url)+"&hdnewid=";
		delmpl_url = getResources().getString(R.string.server_url)+getResources().getString(R.string.news_delmpl_url)+"&hdnewcommentid=";
		reportnews_url = getResources().getString(R.string.server_url)+getResources().getString(R.string.reportnews_url)+"&hdnewcommentid=";
		tv_item_title = (TextView) findViewById(R.id.tv_item_title);
		tv_item_date = (TextView) findViewById(R.id.tv_item_date);
		tv_item_content = (TextView) findViewById(R.id.tv_item_content);
		
		pb_videoview = (ProgressBar) findViewById(R.id.pb_videoview);
		rl_videoview = (RelativeLayout) findViewById(R.id.rl_videoview);
		rl_videoview.setOnClickListener(this);
		rl_voiceview = (RelativeLayout) findViewById(R.id.rl_voiceview);
		tv_voicetimer = (TextView) findViewById(R.id.tv_voicetimer);
		tv_voicetimerbegin = (TextView) findViewById(R.id.tv_voicetimerbegin);
		pb_voice_huanchong = (ProgressBar) findViewById(R.id.pb_voice_huanchong);
		
		btn_news_pl_more = (ImageView) findViewById(R.id.btn_news_pl_more);
		btn_news_pl_more.setOnClickListener(this);
		btn_mediaplay = (ImageView) findViewById(R.id.btn_voice_play);
		btn_mediaplay.setOnClickListener(this);
		tv_news_sofa = (TextView) findViewById(R.id.tv_news_sofa);
		ll_news_pinglun = (LinearLayout) findViewById(R.id.ll_news_pinglun);
		
		iv_news_item_img = (ImageView) findViewById(R.id.iv_news_item_img);
		
		iv_news_item_img1 = (ImageView) findViewById(R.id.iv_news_item_img1);
		iv_news_item_img2 = (ImageView) findViewById(R.id.iv_news_item_img2);
		iv_news_item_img3 = (ImageView) findViewById(R.id.iv_news_item_img3);
		iv_news_item_img4 = (ImageView) findViewById(R.id.iv_news_item_img4);
		iv_news_item_img5 = (ImageView) findViewById(R.id.iv_news_item_img5);
		iv_news_item_img6 = (ImageView) findViewById(R.id.iv_news_item_img6);
		iv_news_item_img7 = (ImageView) findViewById(R.id.iv_news_item_img7);
		iv_news_item_img8 = (ImageView) findViewById(R.id.iv_news_item_img8);
		iv_news_item_img9 = (ImageView) findViewById(R.id.iv_news_item_img9);
		iv_news_item_img10 = (ImageView) findViewById(R.id.iv_news_item_img10);
		
		tv_news_item_imgdesc1 = (TextView) findViewById(R.id.tv_news_item_imgdesc1);
		tv_news_item_imgdesc2 = (TextView) findViewById(R.id.tv_news_item_imgdesc2);
		tv_news_item_imgdesc3 = (TextView) findViewById(R.id.tv_news_item_imgdesc3);
		tv_news_item_imgdesc4 = (TextView) findViewById(R.id.tv_news_item_imgdesc4);
		tv_news_item_imgdesc5 = (TextView) findViewById(R.id.tv_news_item_imgdesc5);
		tv_news_item_newimgdesc = (TextView) findViewById(R.id.tv_news_item_newimgdesc);
		tv_news_item_imgdesc6 = (TextView) findViewById(R.id.tv_news_item_imgdesc6);
		tv_news_item_imgdesc7 = (TextView) findViewById(R.id.tv_news_item_imgdesc7);
		tv_news_item_imgdesc8 = (TextView) findViewById(R.id.tv_news_item_imgdesc8);
		tv_news_item_imgdesc9 = (TextView) findViewById(R.id.tv_news_item_imgdesc9);
		tv_news_item_imgdesc10 = (TextView) findViewById(R.id.tv_news_item_imgdesc10);
		tv_news_item_videodesc = (TextView) findViewById(R.id.tv_news_item_videodesc);
		tv_news_item_voicedesc = (TextView) findViewById(R.id.tv_hd_item_voicedesc);
		audioSeekBar = (SeekBar) findViewById(R.id.pb_voice);
		audioSeekBar.setOnSeekBarChangeListener(new SeekBarChangeEvent());
		
		btn_fullscreen = (ImageView) findViewById(R.id.btn_fullscreen);
		btn_fullscreen.setOnClickListener(this);
		
		inflater = getLayoutInflater();
		listView = (MyListview) findViewById(R.id.pd_news_item);
        listView.setDivider(null);
        listView.setFocusable(false);
        listView.setCacheColorHint(Color.TRANSPARENT);
        listView.setBackgroundColor(Color.WHITE);
        
        mVideoView = (VideoView)findViewById(R.id.video_view);
        mVideoView.setOnClickListener(this);
        mVideoView.setOnPreparedListener(new OnPreparedListener() {
			@Override
			public void onPrepared(MediaPlayer mp) {
				pb_videoview.setVisibility(View.GONE);
			}
		});
        
        mMediaController = new MediaController(this);
        
        imageLoader = ImageLoader.getInstance();
		
        options = DisplayImageOptionsUtil.getOptions(
				R.drawable.ic_empty, R.drawable.ic_error,R.drawable.default_image);
	}
	
	
	class NewsItemTask extends AsyncTask<String, Integer, HashMap<String,String>> {  
		  
		
	    @Override  
	    protected HashMap<String,String> doInBackground(String... params) {  
	        
	    	JSONObject jsonObject;
			HashMap<String,String> map = new HashMap<String,String>();
				String result = HttpUtil.GetStringFromUrl(params[0]+id);
				if(result != null){
					try {
						jsonObject = new JSONObject(result);
						String title = jsonObject.getString("title");
						String createdate = jsonObject.getString("createdate");
						String content = jsonObject.getString("content");
						String newimage = jsonObject.getString("newimage");
						
						String newimagedesc = jsonObject.getString("newimagedesc");
						String imagepath1 = jsonObject.getString("imagepath1");
						String imagedesc1 = jsonObject.getString("imagedesc1");
						String imagepath2 = jsonObject.getString("imagepath2");
						String imagedesc2 = jsonObject.getString("imagedesc2");
						String imagepath3 = jsonObject.getString("imagepath3");
						String imagedesc3 = jsonObject.getString("imagedesc3");
						String imagepath4 = jsonObject.getString("imagepath4");
						String imagedesc4 = jsonObject.getString("imagedesc4");
						String imagepath5 = jsonObject.getString("imagepath5");
						String imagedesc5 = jsonObject.getString("imagedesc5");
						String imagepath6 = jsonObject.getString("imagepath6");
						String imagedesc6 = jsonObject.getString("imagedesc6");
						String imagepath7 = jsonObject.getString("imagepath7");
						String imagedesc7 = jsonObject.getString("imagedesc7");
						String imagepath8 = jsonObject.getString("imagepath8");
						String imagedesc8 = jsonObject.getString("imagedesc8");
						String imagepath9 = jsonObject.getString("imagepath9");
						String imagedesc9 = jsonObject.getString("imagedesc9");
						String imagepath10 = jsonObject.getString("imagepath10");
						String imagedesc10 = jsonObject.getString("imagedesc10");
						String voicepath = jsonObject.getString("voicepath");
						String voicedesc = jsonObject.getString("voicedesc");
						String videopath = jsonObject.getString("videopath");
						String videodesc = jsonObject.getString("videodesc");
						
						map.put("title", title);
						map.put("content", content);
						map.put("createdate", createdate);
						map.put("newimage", newimage);
						
						map.put("newimagedesc", newimagedesc);
						map.put("imagepath1", imagepath1);
						map.put("imagedesc1", imagedesc1);
						map.put("imagepath2", imagepath2);
						map.put("imagedesc2", imagedesc2);
						map.put("imagepath3", imagepath3);
						map.put("imagedesc3", imagedesc3);
						map.put("imagepath4", imagepath4);
						map.put("imagedesc4", imagedesc4);
						map.put("imagepath5", imagepath5);
						map.put("imagedesc5", imagedesc5);
						map.put("imagepath6", imagepath6);
						map.put("imagedesc6", imagedesc6);
						map.put("imagepath7", imagepath7);
						map.put("imagedesc7", imagedesc7);
						map.put("imagepath8", imagepath8);
						map.put("imagedesc8", imagedesc8);
						map.put("imagepath9", imagepath9);
						map.put("imagedesc9", imagedesc9);
						map.put("imagepath10", imagepath10);
						map.put("imagedesc10", imagedesc10);
						map.put("voicepath", voicepath);
						map.put("voicedesc", voicedesc);
						map.put("videopath", videopath);
						map.put("videodesc", videodesc);
						return map;
					} catch (JSONException e) {
						return null;
					}
				}else{
					return null;
				}
	    }  
	    @Override  
	    protected void onCancelled() {  
	        super.onCancelled();  
	    }  
	    @Override  
	    protected void onPostExecute(HashMap<String,String> map) {
	    	if(map!=null){
	    		weibocontent = map.get("title");
	    		tv_item_title.setText(map.get("title"));
	    		tv_item_date.setText(map.get("createdate"));
	    		
	    		String content = map.get("content");
	    		content = FileUtil.ToDBC(content);
	    		tv_item_content.setText(content);
	    		
	    		String newimagedesc = map.get("newimagedesc");
	    		if(!newimagedesc.equals("")&&newimagedesc!=null){
	    			tv_news_item_newimgdesc.setText(newimagedesc);
	    			tv_news_item_newimgdesc.setVisibility(View.VISIBLE);
	    		}
	    		String imagedesc1 = map.get("imagedesc1");
	    		if(!imagedesc1.equals("")&&imagedesc1!=null){
	    			tv_news_item_imgdesc1.setText(imagedesc1);
	    			tv_news_item_imgdesc1.setVisibility(View.VISIBLE);
	    		}
	    		String imagedesc2 = map.get("imagedesc2");
	    		if(!imagedesc2.equals("")&&imagedesc2!=null){
	    			tv_news_item_imgdesc2.setText(imagedesc2);
	    			tv_news_item_imgdesc2.setVisibility(View.VISIBLE);
	    		}
	    		String imagedesc3 = map.get("imagedesc3");
	    		if(!imagedesc3.equals("")&&imagedesc3!=null){
	    			tv_news_item_imgdesc3.setText(imagedesc3);
	    			tv_news_item_imgdesc3.setVisibility(View.VISIBLE);
	    		}
	    		String imagedesc4 = map.get("imagedesc4");
	    		if(!imagedesc4.equals("")&&imagedesc4!=null){
	    			tv_news_item_imgdesc4.setText(imagedesc4);
	    			tv_news_item_imgdesc4.setVisibility(View.VISIBLE);
	    		}
	    		String imagedesc5 = map.get("imagedesc5");
	    		if(!imagedesc5.equals("")&&imagedesc5!=null){
	    			tv_news_item_imgdesc5.setText(imagedesc5);
	    			tv_news_item_imgdesc5.setVisibility(View.VISIBLE);
	    		}
	    		String videodesc = map.get("videodesc");
	    		if(!videodesc.equals("")&&videodesc!=null){
	    			tv_news_item_videodesc.setText(videodesc);
	    			tv_news_item_videodesc.setVisibility(View.VISIBLE);
	    		}
	    		String voicedesc = map.get("voicedesc");
	    		if(!voicedesc.equals("")&&voicedesc!=null){
	    			tv_news_item_voicedesc.setText(voicedesc);
	    			tv_news_item_voicedesc.setVisibility(View.VISIBLE);
	    		}
	    		String imagedesc6 = map.get("imagedesc6");
	    		if(!imagedesc6.equals("")&&imagedesc6!=null){
	    			tv_news_item_imgdesc6.setText(imagedesc6);
	    			tv_news_item_imgdesc6.setVisibility(View.VISIBLE);
	    		}
	    		String imagedesc7 = map.get("imagedesc7");
	    		if(!imagedesc7.equals("")&&imagedesc7!=null){
	    			tv_news_item_imgdesc7.setText(imagedesc7);
	    			tv_news_item_imgdesc7.setVisibility(View.VISIBLE);
	    		}
	    		String imagedesc8 = map.get("imagedesc8");
	    		if(!imagedesc8.equals("")&&imagedesc8!=null){
	    			tv_news_item_imgdesc8.setText(imagedesc8);
	    			tv_news_item_imgdesc8.setVisibility(View.VISIBLE);
	    		}
	    		String imagedesc9 = map.get("imagedesc9");
	    		if(!imagedesc9.equals("")&&imagedesc9!=null){
	    			tv_news_item_imgdesc9.setText(imagedesc9);
	    			tv_news_item_imgdesc9.setVisibility(View.VISIBLE);
	    		}
	    		String imagedesc10 = map.get("imagedesc10");
	    		if(!imagedesc10.equals("")&&imagedesc10!=null){
	    			tv_news_item_imgdesc10.setText(imagedesc10);
	    			tv_news_item_imgdesc10.setVisibility(View.VISIBLE);
	    		}
	    		
	    		videourl = map.get("videopath");
	    		if(!videourl.equals("")&&videourl!=null){
	    			mUri = Uri.parse(videourl);
	    			rl_videoview.setVisibility(View.VISIBLE);
	    		}
	    		
	    		voiceurl = map.get("voicepath");
	    		if(!voiceurl.equals("")&&voiceurl!=null){
//	    			mUri = Uri.parse(voiceurl);
	    			rl_voiceview.setVisibility(View.VISIBLE);
	    		}
	    		
	    		final String imageUrl = map.get("newimage");
	    		if(imageUrl!=null){
	    			imgurls.add(imageUrl);
//	    			ImageManager.from(NewsItem.this).displayImage(iv_news_item_img, imageUrl, R.drawable.default_image_white);
	    			imageLoader.displayImage(imageUrl, iv_news_item_img, options, null);
	    			iv_news_item_img.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							Intent intent = new Intent(NewsDetailActivity.this,ViewPagerByList.class);
							intent.putExtra("ID", 0);
							intent.putExtra("imgurl", imgurls);
							startActivity(intent);
							overridePendingTransition(R.anim.zoom_enter,0);
						}
					});
	    		}else{
	    			iv_news_item_img.setVisibility(View.GONE);
	    		}
	    		
	    		final String img1 = map.get("imagepath1");
	    		if(!img1.equals("")&&img1!=null){
	    			imgurls.add(img1);
//	    			ImageManager.from(NewsItem.this).displayImage(iv_news_item_img1, img1, R.drawable.default_image_white);
	    			imageLoader.displayImage(img1, iv_news_item_img1, options, null);
	    			iv_news_item_img1.setVisibility(View.VISIBLE);
	    			iv_news_item_img1.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							Intent intent = new Intent(NewsDetailActivity.this,ViewPagerByList.class);
							intent.putExtra("ID", 1);
							intent.putExtra("imgurl", imgurls);
							startActivity(intent);
							overridePendingTransition(R.anim.zoom_enter,0);
						}
					});
	    		}
	    		final String img2 = map.get("imagepath2");
	    		if(!img2.equals("")&&img2!=null){
	    			imgurls.add(img2);
//	    			ImageManager.from(NewsItem.this).displayImage(iv_news_item_img2, img2, R.drawable.default_image_white);
	    			imageLoader.displayImage(img2, iv_news_item_img2, options, null);
	    			iv_news_item_img2.setVisibility(View.VISIBLE);
	    			iv_news_item_img2.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							Intent intent = new Intent(NewsDetailActivity.this,ViewPagerByList.class);
							intent.putExtra("ID", 2);
							intent.putExtra("imgurl", imgurls);
							startActivity(intent);
							overridePendingTransition(R.anim.zoom_enter,0);
						}
					});
	    		}
	    		final String img3 = map.get("imagepath3");
	    		if(!img3.equals("")&&img3!=null){
	    			imgurls.add(img3);
//	    			ImageManager.from(NewsItem.this).displayImage(iv_news_item_img3, img3, R.drawable.default_image_white);
	    			imageLoader.displayImage(img3, iv_news_item_img3, options, null);
	    			iv_news_item_img3.setVisibility(View.VISIBLE);
	    			iv_news_item_img3.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							Intent intent = new Intent(NewsDetailActivity.this,ViewPagerByList.class);
							intent.putExtra("ID", 3);
							intent.putExtra("imgurl", imgurls);
							startActivity(intent);
							overridePendingTransition(R.anim.zoom_enter,0);
						}
					});
	    		}
	    		final String img4 = map.get("imagepath4");
	    		if(!img4.equals("")&&img4!=null){
	    			imgurls.add(img4);
//	    			ImageManager.from(NewsItem.this).displayImage(iv_news_item_img4, img4, R.drawable.default_image_white);
	    			imageLoader.displayImage(img4, iv_news_item_img4, options, null);
	    			iv_news_item_img4.setVisibility(View.VISIBLE);
	    			iv_news_item_img4.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							Intent intent = new Intent(NewsDetailActivity.this,ViewPagerByList.class);
							intent.putExtra("ID", 4);
							intent.putExtra("imgurl", imgurls);
							startActivity(intent);
							overridePendingTransition(R.anim.zoom_enter,0);
						}
					});
	    		}
	    		final String img5 = map.get("imagepath5");
	    		if(!img5.equals("")&&img5!=null){
	    			imgurls.add(img5);
//	    			ImageManager.from(NewsItem.this).displayImage(iv_news_item_img5, img5, R.drawable.default_image_white);
	    			imageLoader.displayImage(img5, iv_news_item_img5, options, null);
	    			iv_news_item_img5.setVisibility(View.VISIBLE);
	    			iv_news_item_img5.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							Intent intent = new Intent(NewsDetailActivity.this,ViewPagerByList.class);
							intent.putExtra("ID", 5);
							intent.putExtra("imgurl", imgurls);
							startActivity(intent);
							overridePendingTransition(R.anim.zoom_enter,0);
						}
					});
	    		}
	    		final String img6 = map.get("imagepath6");
	    		if(!img6.equals("")&&img6!=null){
	    			imgurls.add(img6);
//	    			ImageManager.from(NewsItem.this).displayImage(iv_hd_item_img1, img1, R.drawable.default_image_white);
	    			imageLoader.displayImage(img6, iv_news_item_img6, options, null);
	    			iv_news_item_img6.setVisibility(View.VISIBLE);
	    			iv_news_item_img6.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							Intent intent = new Intent(NewsDetailActivity.this,ViewPagerByList.class);
							intent.putExtra("ID", 6);
							intent.putExtra("imgurl", imgurls);
							startActivity(intent);
							overridePendingTransition(R.anim.zoom_enter,0);
						}
					});
	    		}
	    		final String img7 = map.get("imagepath7");
	    		if(!img7.equals("")&&img7!=null){
	    			imgurls.add(img7);
//	    			ImageManager.from(NewsItem.this).displayImage(iv_news_item_img2, img2, R.drawable.default_image_white);
	    			imageLoader.displayImage(img7, iv_news_item_img7, options, null);
	    			iv_news_item_img7.setVisibility(View.VISIBLE);
	    			iv_news_item_img7.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							Intent intent = new Intent(NewsDetailActivity.this,ViewPagerByList.class);
							intent.putExtra("ID", 7);
							intent.putExtra("imgurl", imgurls);
							startActivity(intent);
							overridePendingTransition(R.anim.zoom_enter,0);
						}
					});
	    		}
	    		final String img8 = map.get("imagepath8");
	    		if(!img8.equals("")&&img8!=null){
	    			imgurls.add(img8);
//	    			ImageManager.from(NewsItem.this).displayImage(iv_news_item_img3, img3, R.drawable.default_image_white);
	    			imageLoader.displayImage(img8, iv_news_item_img8, options, null);
	    			iv_news_item_img8.setVisibility(View.VISIBLE);
	    			iv_news_item_img8.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							Intent intent = new Intent(NewsDetailActivity.this,ViewPagerByList.class);
							intent.putExtra("ID", 8);
							intent.putExtra("imgurl", imgurls);
							startActivity(intent);
							overridePendingTransition(R.anim.zoom_enter,0);
						}
					});
	    		}
	    		final String img9 = map.get("imagepath9");
	    		if(!img9.equals("")&&img9!=null){
	    			imgurls.add(img9);
//	    			ImageManager.from(NewsItem.this).displayImage(iv_news_item_img4, img4, R.drawable.default_image_white);
	    			imageLoader.displayImage(img9, iv_news_item_img9, options, null);
	    			iv_news_item_img9.setVisibility(View.VISIBLE);
	    			iv_news_item_img9.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							Intent intent = new Intent(NewsDetailActivity.this,ViewPagerByList.class);
							intent.putExtra("ID", 9);
							intent.putExtra("imgurl", imgurls);
							startActivity(intent);
							overridePendingTransition(R.anim.zoom_enter,0);
						}
					});
	    		}
	    		final String img10 = map.get("imagepath10");
	    		if(!img10.equals("")&&img10!=null){
	    			imgurls.add(img10);
//	    			ImageManager.from(NewsItem.this).displayImage(iv_news_item_img5, img5, R.drawable.default_image_white);
	    			imageLoader.displayImage(img10, iv_news_item_img10, options, null);
	    			iv_news_item_img10.setVisibility(View.VISIBLE);
	    			iv_news_item_img10.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							Intent intent = new Intent(NewsDetailActivity.this,ViewPagerByList.class);
							intent.putExtra("ID", 10);
							intent.putExtra("imgurl", imgurls);
							startActivity(intent);
							overridePendingTransition(R.anim.zoom_enter,0);
						}
					});
	    		}
	    		
	    		pb_news.setVisibility(View.GONE);
	    		
	    		//正文线程执行完，再执行评论内容
	    		initArrays(new Handler(){
	            	@Override
	            	public void handleMessage(Message msg) {
	            		arrays = (List<PinglunNewsData>) msg.obj;
	            		adapter = new NewsDetailAdapter(NewsDetailActivity.this, arrays, inflater, delmpl_url,densityDpi,uid,reportnews_url);
            	        listView.setAdapter(adapter);
	            		if(arrays!=null){
//	            			Collections.reverse(arrays);
	            			ll_news_pinglun.setVisibility(View.VISIBLE);
	            			btn_news_pl_more.setVisibility(View.VISIBLE);
	            		}else{
	            			ll_news_pinglun.setVisibility(View.VISIBLE);
	            			tv_news_sofa.setVisibility(View.VISIBLE);
	            		}
	            		adapter.notifyDataSetChanged();
	            	}
	            });
	    	}
	    	else{
	    		pb_news.setVisibility(View.GONE);
	    		showMsg(getResources().getString(R.string.response_fail), "info", "bottom");
	    	}
	    }  
	    @Override  
	    protected void onPreExecute() {  
	    	
	    }  
	    @Override  
	    protected void onProgressUpdate(Integer... values) {  
	    }  
	}


	/**
	 * 初始化数据
	 * @param handler
	 */
	private void initArrays(final Handler handler) {
		new Thread(new Runnable() {
			List<PinglunNewsData> lst = new ArrayList<PinglunNewsData>();
			@Override
			public void run() {
				
					try {  
						lst = getDataFromNetwork(startindex,endindex);
				} catch (Exception e) {
					e.printStackTrace();
				}
					handler.obtainMessage(0, lst).sendToTarget();
			}
		}).start();
	}
	
	/**
	 * 从网络获取数据
	 * @return
	 * @throws IOException 
	 * @throws ClientProtocolException 
	 * @throws Exception
	 */
	private List<PinglunNewsData> getDataFromNetwork(int start,int end) throws Exception{
		List<PinglunNewsData> lst = new ArrayList<PinglunNewsData>();
		
		String resultString = HttpUtil.GetStringFromUrl(getpl_url+id+"&startindex="+start+"&endindex="+end+"&connected_uid="+uid);
		if(resultString != null){
			Type listType = new TypeToken<List<PinglunNewsData>>(){}.getType();
	        Gson gson = new Gson();
	        lst = gson.fromJson(resultString, listType);
	        return lst;
		}else{
			return null;
		}
	}
	
	public void playMusic(int action) {  
        Intent intent = new Intent();  
        intent.putExtra("MSG", action);  
        intent.putExtra("url", voiceurl); 
        intent.setClass(NewsDetailActivity.this, ReplayPlayerService.class);  
        startService(intent);  
    }
	
	private void playorpause(){
//    	//实现消息传递
        DelayThread delaythread=new DelayThread(500);
        delaythread.start();
    	if(!flag){
    		if(voiceurl!=null){
    			btn_mediaplay.setEnabled(false);
    			btn_mediaplay.setVisibility(View.GONE);
        		pb_voice_huanchong.setVisibility(View.VISIBLE);
    		new Thread(){
    			@Override
    			public void run() {
    				if(!playorpause){
	    				try {
							Thread.sleep(200);
					    	//开始点播
							playMusic(Constant.PLAY);
							Thread.sleep(200);
		    				mHandler.sendEmptyMessage(3);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
    				}
    			}
    		}.start();
			flag = true;
    		}else{
    		}
		}else{
			playMusic(Constant.PAUSE);
			if(!playflag){
				btn_mediaplay.setBackgroundResource(R.drawable.bl_btn_play);
				playflag = true;
			}else{
				btn_mediaplay.setBackgroundResource(R.drawable.bl_btn_pause);
				playflag = false;
			}
			
		}
    	
    }
	
	class DelayThread extends Thread{
        int milliseconds;
        public DelayThread(int i){
            milliseconds=i;
        }
        public void run(){
            while(!progressflag){
                try {
                    sleep(milliseconds);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                handler.sendEmptyMessage(0);
            }
        }
    }
	
	private Handler handler = new Handler(){
        public void handleMessage(Message msg){
          if(ReplayPlayerService.mMediaPlayer!=null){
            	int end = ReplayPlayerService.mMediaPlayer.getDuration();
            	int begin = ReplayPlayerService.mMediaPlayer.getCurrentPosition();
            	audioSeekBar.setMax(end);
            	audioSeekBar.setProgress(begin);
            	SimpleDateFormat sdf = null;
            	String str_begin = null;
            	String str_end = null;
            	if(end<3600000){
            		sdf = new SimpleDateFormat("mm:ss");
            	}else{
            		sdf = new SimpleDateFormat("HH:mm:ss");
            		TimeZone timezone = TimeZone.getTimeZone("GMT+00:00");
            		sdf.setTimeZone(timezone);
            	}
            	Date dend = new Date(end);
				str_end = sdf.format(dend);
				Date dbegin = new Date(begin);
				str_begin = sdf.format(dbegin);
				tv_voicetimerbegin.setText(str_begin);
				tv_voicetimer.setText(str_end);
          }
        }
    };
    
	//在主线程里面处理消息并更新UI界面
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
            case 3:
            	pb_voice_huanchong.setVisibility(View.GONE);
            	btn_mediaplay.setVisibility(View.VISIBLE);
            	btn_mediaplay.setEnabled(true);
    			btn_mediaplay.setBackgroundResource(R.drawable.bl_btn_pause);
            break;
            }
        }
    };
	
    class SeekBarChangeEvent implements SeekBar.OnSeekBarChangeListener {  
	    @Override  
	    public void onProgressChanged(SeekBar seekBar, int progress,  
	            boolean fromUser) {  
	    }  
	    @Override  
	    public void onStartTrackingTouch(SeekBar seekBar) {  
	    }  
	    @Override  
	    public void onStopTrackingTouch(SeekBar seekBar) {  
	    	if(ReplayPlayerService.mMediaPlayer!=null){
	    		ReplayPlayerService.mMediaPlayer.seekTo(seekBar.getProgress());
	    	}
	    }  
	}  
    
	@Override
	public void onClick(View v) {
		if(v == btn_mediaplay){
			playorpause();
		}
		if(v == rl_videoview){
			if(!video_flag){
				pb_videoview.setVisibility(View.VISIBLE);
				mVideoView.setMediaController(mMediaController);
				mVideoView.setVideoURI(mUri);
				mVideoView.start();
				video_flag = true;
			}
		}
		if(v == btn_fullscreen){
			Intent intent = new Intent(this,FullScreenVideoViewActivity.class);
			intent.putExtra("videourl", videourl);
			mPositionWhenPaused = mVideoView.getCurrentPosition();
			intent.putExtra("mPositionWhenPaused", mPositionWhenPaused);
			startActivityForResult(intent, FULLSCREEN);
		}
		if(v == btn_news_pl_more){
			Intent intent = new Intent(this,NewsCommentListActivity.class);
			intent.putExtra("id", id);
			startActivityForResult(intent,PINGLUN);
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(resultCode == Activity.RESULT_OK && requestCode == PINGLUN){
			//评论成功刷新评论列表
			initArrays(new Handler(){
            	@Override
            	public void handleMessage(Message msg) {
            		arrays = (List<PinglunNewsData>) msg.obj;
            		adapter = new NewsDetailAdapter(NewsDetailActivity.this, arrays, inflater, delmpl_url,densityDpi,uid,reportnews_url);
        	        listView.setAdapter(adapter);
            		if(arrays!=null){
            			Collections.reverse(arrays);
            			ll_news_pinglun.setVisibility(View.VISIBLE);
            			btn_news_pl_more.setVisibility(View.VISIBLE);
            			tv_news_sofa.setVisibility(View.GONE);
            		}else{
            			ll_news_pinglun.setVisibility(View.VISIBLE);
            			tv_news_sofa.setVisibility(View.VISIBLE);
            		}
            		adapter.notifyDataSetChanged();
            	}
            });
		}
		if(resultCode == Activity.RESULT_OK && requestCode == FULLSCREEN){
			mPositionWhenPaused = data.getIntExtra("mPosition", 0);
		}
		
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	public void onPause() {
        mPositionWhenPaused = mVideoView.getCurrentPosition();
        mVideoView.stopPlayback();
        MobclickAgent.onPause(this);
        super.onPause();
    }

    public void onResume() {
        if(mPositionWhenPaused >= 0) {
            mVideoView.seekTo(mPositionWhenPaused);
            mPositionWhenPaused = -1;
        }
        MobclickAgent.onResume(this);
        super.onResume();
    }
    @Override
	protected void onDestroy() {
		super.onDestroy();
		progressflag = true;
		playorpause = true;
		Intent intent = new Intent();  
        intent.setClass(NewsDetailActivity.this, ReplayPlayerService.class);  
        stopService(intent);//停止Service 
	}
    
    private void showMsg(String msg,String styletype,String gravity){
		
		if(styletype.equals("alert")){
			style = AppMsg.STYLE_ALERT;
		}else if(styletype.equals("info")){
			style = AppMsg.STYLE_INFO;
		}
		
		appMsg = AppMsg.makeText(NewsDetailActivity.this, msg, style);
		
		if(gravity.equals("bottom")){
			appMsg.setLayoutGravity(Gravity.BOTTOM);
		}else if(gravity.equals("top")){
		}
        appMsg.show();
	}
    
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN){   
        	if(push!=null&&"true".equals(push)){
				Intent intent = new Intent(NewsDetailActivity.this,NewsListActivity.class);
				intent.putExtra("push", "true");
				startActivity(intent);
				finish();
			}else{
				finish();
			}
            return true;   
        }
        return super.onKeyDown(keyCode, event);
    }
}

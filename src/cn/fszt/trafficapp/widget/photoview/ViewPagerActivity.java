
package cn.fszt.trafficapp.widget.photoview;

import java.io.File;

import cn.fszt.trafficapp.R;
import cn.fszt.trafficapp.util.PhotoUtil;
import cn.fszt.trafficapp.widget.photoview.PopupMenu.OnItemSelectedListener;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.umeng.analytics.MobclickAgent;

import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher.OnPhotoTapListener;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

public class ViewPagerActivity extends Activity {

	private ViewPager mViewPager;
	DisplayImageOptions options;
    ImageLoader imageLoader = ImageLoader.getInstance();
    String[] imgurl;
    private static final int SAVE2PHONE = 1;
    private static final int SAVEPHOTO = 2;
    private String _imageDir;
    private boolean save;
    int ID;
    

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent intent = getIntent();
		imgurl = intent.getStringArrayExtra("imgurl");
		mViewPager = new HackyViewPager(this);
		setContentView(mViewPager);
		if(savedInstanceState==null){
			ID = intent.getIntExtra("ID", 0);
		}else{
			ID = savedInstanceState.getInt("id");
		}
		
		mViewPager.setAdapter(new SamplePagerAdapter());
		mViewPager.setCurrentItem(ID);
		options = DisplayImageOptionsUtil.getOptions(
				R.drawable.ic_empty, R.drawable.ic_error,0);
		
		_imageDir = Environment.getExternalStorageDirectory()
				.getAbsolutePath()+ getResources().getString(R.string.phototake_dir);
		
		File imageDir = new File(_imageDir);
		  if (!imageDir.exists()) {
			  imageDir.mkdirs();
		  }
		
	}

	class SamplePagerAdapter extends PagerAdapter {

		private String[] sDrawables = imgurl;
		private LayoutInflater inflater = getLayoutInflater();
		
		@Override
		public int getCount() {
			return sDrawables.length;
		}

		@Override
		public View instantiateItem(ViewGroup container, final int position) {
			View imageLayout = inflater.inflate(R.layout.item_viewpager, container, false);
			PhotoView pv_viewpager = (PhotoView) imageLayout.findViewById(R.id.pv_viewpager);
			final ProgressBar pd = (ProgressBar) imageLayout.findViewById(R.id.loading);
			
			pv_viewpager.setOnPhotoTapListener(new OnPhotoTapListener() {
				@Override
				public void onPhotoTap(View arg0, float arg1, float arg2) {
					pd.setVisibility(View.GONE);
					finish();
					overridePendingTransition(0,R.anim.zoom_exit);
				}
			});
			pv_viewpager.setOnLongClickListener(new OnLongClickListener() {
				@Override
				public boolean onLongClick(View v) {
					PopupMenu menu = new PopupMenu(ViewPagerActivity.this);
					menu.setOnItemSelectedListener(new OnItemSelectedListener() {
						@Override
						public void onItemSelected(MenuItem item) {
							switch(item.getItemId()){
								case SAVE2PHONE:
									new Thread(){
										public void run() {
											Bitmap bm = PhotoUtil.getBitmapFromUrl(sDrawables[position]);
											String rotaingpaths = _imageDir+String.valueOf(System.currentTimeMillis())
													+ ".jpg";
											if(bm!=null){
												save = PhotoUtil.saveBitmap2file(bm, rotaingpaths);
												handler.sendEmptyMessage(SAVEPHOTO);
											}
										}
									}.start();
									
									break;
							}
						}
					});
			        menu.add(SAVE2PHONE, R.string.save2phone);
			        menu.show(null);
					return false;
				}
			});
				imageLoader.displayImage(sDrawables[position], pv_viewpager, options, new ImageLoadingListener() {
					
					@Override
					public void onLoadingStarted(String imageUri, View view) {
						pd.setVisibility(View.VISIBLE);
					}
					
					@Override
					public void onLoadingFailed(String imageUri, View view,
							FailReason failReason) {
						pd.setVisibility(View.GONE);
					}
					
					@Override
					public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
						pd.setVisibility(View.GONE);
					}
					
					@Override
					public void onLoadingCancelled(String imageUri, View view) {
						pd.setVisibility(View.GONE);
					}
				});
				
				((ViewPager) container).addView(imageLayout, 0);
			return imageLayout;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView((View) object);
		}

		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view == object;
		}

	}
	
	Handler handler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			if(msg.what==SAVEPHOTO){
				if(save){
					Toast.makeText(ViewPagerActivity.this, "图片已保存至"+_imageDir+" 文件夹", Toast.LENGTH_SHORT).show();
				}
			}
		}
	};
	
	@Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN){  
        	finish();
        	overridePendingTransition(0,R.anim.zoom_exit);
        }
        return true;
    }
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putInt("id", mViewPager.getCurrentItem());
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

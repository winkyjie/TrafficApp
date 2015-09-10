package cn.fszt.trafficapp.widget.photoview;

import android.graphics.Bitmap;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.BitmapDisplayer;

public class DisplayImageOptionsUtil {

	//普通图片
	public static DisplayImageOptions getOptions(int ImageForEmptyUri,int ImageOnFail,int ImageOnLoading){
		
		DisplayImageOptions options;
		options = new DisplayImageOptions.Builder()
		.showImageForEmptyUri(ImageForEmptyUri)
		.showImageOnFail(ImageOnFail)
		.showImageOnLoading(ImageOnLoading)
		.bitmapConfig(Bitmap.Config.RGB_565)
		.imageScaleType(ImageScaleType.EXACTLY)
		.cacheInMemory(true)
		.cacheOnDisk(true)
		.build();
		
		return options;
	}
	
	//头像
	public static DisplayImageOptions getOptions(int ImageForEmptyUri,
			int ImageOnFail,int ImageOnLoading,BitmapDisplayer displayer){
		
		DisplayImageOptions options;
		options = new DisplayImageOptions.Builder()
		.showImageForEmptyUri(ImageForEmptyUri)
		.showImageOnFail(ImageOnFail)
		.showImageOnLoading(ImageOnLoading)
		.bitmapConfig(Bitmap.Config.RGB_565)
		.imageScaleType(ImageScaleType.EXACTLY)
		.displayer(displayer)
		.cacheInMemory(true)
		.cacheOnDisk(true)
		.build();
		
		return options;
	}
}

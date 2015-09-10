package cn.fszt.trafficapp.ui.service;

import java.io.IOException;

import cn.fszt.trafficapp.util.Constant;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.util.Log;

public class RadioPlayerService extends Service implements MediaPlayer.OnPreparedListener{  
	
	public static MediaPlayer mMediaPlayer = null;  
	private int MSG;  

	@Override  
	public IBinder onBind(Intent intent) {  
	return null;
	}  

	@Override  
	public void onCreate() {
		
	super.onCreate();  
	if (mMediaPlayer != null) {  
	    mMediaPlayer.reset();  
	    mMediaPlayer.release();  
	    mMediaPlayer = null;  
	}  
	mMediaPlayer = new MediaPlayer();  
	mMediaPlayer.setOnPreparedListener(this);
	}  

	@Override  
	public void onDestroy() {  
	super.onDestroy();  
		if (mMediaPlayer != null) {  
		    mMediaPlayer.stop();  
		    mMediaPlayer.release();  
		    mMediaPlayer = null;  
		}  
	}  
	
	
	@Override  
	public int onStartCommand(Intent intent, int flags, int startId) {  
		
	if(intent!=null){
		MSG = intent.getIntExtra("MSG", Constant.PLAY);  
		if (MSG == Constant.PLAY) {  
		    playMusic(intent.getStringExtra("url"));  
		}  
		if (MSG == Constant.PAUSE) {  
		    if (mMediaPlayer.isPlaying()) {// 正在播放   
		    	mMediaPlayer.stop();
		    	mMediaPlayer.reset();
		    }  
		}  
	}
	return super.onStartCommand(intent, flags, startId);  
	}  

	public void playMusic(String url) {  
		try {  
		    mMediaPlayer.setDataSource(url);  
		    mMediaPlayer.prepareAsync();
		}catch (IOException e) {  
			e.printStackTrace();
		}catch (IllegalStateException ie){
			ie.printStackTrace();
		}  
	}

	@Override
	public void onPrepared(MediaPlayer mp) {
		mp.start(); 
	}
}  


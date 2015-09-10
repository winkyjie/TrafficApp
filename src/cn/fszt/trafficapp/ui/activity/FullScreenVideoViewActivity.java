package cn.fszt.trafficapp.ui.activity;

import com.umeng.analytics.MobclickAgent;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.VideoView;
import cn.fszt.trafficapp.R;
import cn.fszt.trafficapp.widget.ImageLoadingDialog;

public class FullScreenVideoViewActivity extends Activity implements
		MediaPlayer.OnErrorListener, MediaPlayer.OnCompletionListener {
	public static final String TAG = "VideoPlayer";
	private VideoView mVideoView;
	private Uri mUri;
	private int mPositionWhenPaused = -1;

	private MediaController mMediaController;

	private int old_duration;
	private ImageLoadingDialog dialog;
	private Intent intent;
	private ProgressBar pb_videoview;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.fullscreenvideoview);

		this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		intent = getIntent();
		String videourl = intent.getStringExtra("videourl");
		mPositionWhenPaused = intent.getIntExtra("mPositionWhenPaused", 0);
		mVideoView = (VideoView) findViewById(R.id.video_view);
		pb_videoview = (ProgressBar) findViewById(R.id.pb_videoview);
		mVideoView.setOnPreparedListener(new OnPreparedListener() {
			@Override
			public void onPrepared(MediaPlayer mp) {
				pb_videoview.setVisibility(View.GONE);
			}
		});
		if (videourl != null) {
			mUri = Uri.parse(videourl);
		}
		mMediaController = new MediaController(this);
		mVideoView.setMediaController(mMediaController);

		dialog = new ImageLoadingDialog(this);

		// bufferedtips();

	}

	public void onStart() {
		if (mUri != null) {
			pb_videoview.setVisibility(View.VISIBLE);
			mVideoView.setVideoURI(mUri);

			mVideoView.start();
		}
		if (mPositionWhenPaused >= 0) {
			mVideoView.seekTo(mPositionWhenPaused);
			mPositionWhenPaused = -1;
		}

		super.onStart();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// 如果按下的是返回键，并且没有重复
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			mPositionWhenPaused = mVideoView.getCurrentPosition();
			intent.putExtra("mPosition", mPositionWhenPaused);
			setResult(Activity.RESULT_OK, intent);
			mVideoView.stopPlayback();
			finish();
			return false;
		}
		return false;
	}

	public boolean onError(MediaPlayer player, int arg1, int arg2) {
		return false;
	}

	public void onCompletion(MediaPlayer mp) {
		this.finish();
	}

	private void bufferedtips() {

		final Handler handler = new Handler();
		Runnable runnable = new Runnable() {
			public void run() {
				int duration = mVideoView.getCurrentPosition();
				if (old_duration == duration && mVideoView.isPlaying()) {
					dialog.show();
				} else {
					dialog.dismiss();
				}
				old_duration = duration;

				handler.postDelayed(this, 1000);
			}
		};
		handler.postDelayed(runnable, 0);
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

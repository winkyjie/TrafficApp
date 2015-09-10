package cn.fszt.trafficapp.ui.activity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import cn.fszt.trafficapp.R;
import cn.fszt.trafficapp.ui.fragment.AudioDownLoadFragment;
import cn.fszt.trafficapp.ui.fragment.AudioIntroduceFragment;
import cn.fszt.trafficapp.ui.fragment.AudioProgrammeFragment;
import cn.fszt.trafficapp.ui.service.IBtnCallListener;
import cn.fszt.trafficapp.ui.service.ReplayPlayerService;
import cn.fszt.trafficapp.util.Constant;
import cn.fszt.trafficapp.widget.photoview.DisplayImageOptionsUtil;

/**
 * 音频播放界面
 * 
 * @author AeiouKong
 *
 */
public class AudioActivity extends FragmentActivity implements OnClickListener, IBtnCallListener {

	private IBtnCallListener mBtnCallListener;
	private ImageView audioJianJieImageView;
	private ImageView audioJieMuImageView;
	private ImageView audioDownLoadImageView;
	private ImageView audioShareImageView;
	private ViewPager mViewPager;
	private FragmentPagerAdapter mAdapter;
	private List<Fragment> mDatas;
	private TextView jianjieTextView;
	private TextView jiemuTextView;
	private TextView downloadTextview;
	private TextView shareTextView;
	private TextView tv_start, tv_end;
	private Intent intent;
	private String title, image;
	private ImageView riv_play;
	private ImageLoader imageLoader;
	private DisplayImageOptions options;
	private ImageView iv_playorpause, iv_previous, iv_next;
	private Timer timer;
	private TimerTask task;
	private int temp_playorpause;
	private SeekBar audioSeekBar;
	private boolean progressflag;
	private View ll_intro, ll_programme, ll_download;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_audio);

		imageLoader = ImageLoader.getInstance();
		options = DisplayImageOptionsUtil.getOptions(R.drawable.default_head, R.drawable.default_head,
				R.drawable.default_image, new RoundedBitmapDisplayer(90));

		initView();

		getActionBar().setTitle(title);
		getActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.titlebar)));
		getActionBar().setDisplayHomeAsUpEnabled(true);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			break;
		}
		return true;
	}

	private void initView() {

		riv_play = (ImageView) findViewById(R.id.riv_play);
		riv_play.setOnClickListener(this);
		iv_playorpause = (ImageView) findViewById(R.id.iv_playorpause);
		iv_previous = (ImageView) findViewById(R.id.iv_previous);
		iv_previous.setOnClickListener(this);
		iv_next = (ImageView) findViewById(R.id.iv_next);
		iv_next.setOnClickListener(this);
		ll_intro = findViewById(R.id.ll_intro);
		ll_intro.setOnClickListener(this);
		ll_programme = findViewById(R.id.ll_programme);
		ll_programme.setOnClickListener(this);
		ll_download = findViewById(R.id.ll_download);
		ll_download.setOnClickListener(this);
		audioSeekBar = (SeekBar) findViewById(R.id.pb_video);
		audioSeekBar.setOnSeekBarChangeListener(new SeekBarChangeEvent());
		tv_start = (TextView) findViewById(R.id.tv_start);
		tv_end = (TextView) findViewById(R.id.tv_end);
		intent = getIntent();
		title = intent.getStringExtra("title");
		image = intent.getStringExtra("image");
		imageLoader.displayImage(image, riv_play, options);
		audioJianJieImageView = (ImageView) findViewById(R.id.audiojianjie);
		audioJieMuImageView = (ImageView) findViewById(R.id.audiojiemu);
		audioDownLoadImageView = (ImageView) findViewById(R.id.audiodownload);
		audioShareImageView = (ImageView) findViewById(R.id.audioshare);
		mViewPager = (ViewPager) findViewById(R.id.id_viewpager);
		jianjieTextView = (TextView) findViewById(R.id.jianjieTextview);
		jiemuTextView = (TextView) findViewById(R.id.jiemuTextview);
		downloadTextview = (TextView) findViewById(R.id.downloadTextview);
		shareTextView = (TextView) findViewById(R.id.shareTextview);
		mDatas = new ArrayList<Fragment>();

		AudioIntroduceFragment tab01 = new AudioIntroduceFragment();
		AudioProgrammeFragment tab02 = new AudioProgrammeFragment();
		AudioDownLoadFragment tab03 = new AudioDownLoadFragment();
		mDatas.add(tab01);
		mDatas.add(tab02);
		mDatas.add(tab03);
		mAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
			
			@Override
			public int getCount() {
				return mDatas.size();
			}

			@Override
			public Fragment getItem(int arg0) {
				return mDatas.get(arg0);
			}
		};
		mViewPager.setAdapter(mAdapter);
		mViewPager.setCurrentItem(1);
		mViewPager.setOnPageChangeListener(new OnPageChangeListener() {
			@Override
			public void onPageSelected(int position) {
				resetView();
				switch (position) {
				case 0:
					audioJianJieImageView.setImageResource(R.drawable.audio_jianjie2);
					jianjieTextView.setTextColor(getResources().getColor(R.color.orange_bg));
					break;
				case 1:
					audioJieMuImageView.setImageResource(R.drawable.audio_jiemu2);
					jiemuTextView.setTextColor(getResources().getColor(R.color.orange_bg));
					
					break;
				case 2:
					audioDownLoadImageView.setImageResource(R.drawable.audio_download2);
					downloadTextview.setTextColor(getResources().getColor(R.color.orange_bg));
					break;
				}

			}

			private void resetView() {
				jianjieTextView.setTextColor(Color.WHITE);
				jiemuTextView.setTextColor(Color.WHITE);
				downloadTextview.setTextColor(Color.WHITE);
				shareTextView.setTextColor(Color.WHITE);
				audioJianJieImageView.setImageResource(R.drawable.audio_jianjie1);
				audioJieMuImageView.setImageResource(R.drawable.audio_jiemu1);
				audioDownLoadImageView.setImageResource(R.drawable.audio_download1);
				audioShareImageView.setImageResource(R.drawable.audio_share1);
			}

			@Override
			public void onPageScrolled(int position, float positionOffset, int positionOffsetPx) {

			}

			@Override
			public void onPageScrollStateChanged(int arg0) {

			}
		});

	}

	int i = -90;

	@Override
	public void onClick(View v) {
		if (v == riv_play) {
			if (temp_playorpause == Constant.PLAY) {
				temp_playorpause = Constant.PAUSE;
				mBtnCallListener.play(Constant.PAUSE);
				iv_playorpause.setBackgroundResource(R.drawable.bofang);
			} else if (temp_playorpause == Constant.PAUSE) {
				temp_playorpause = Constant.PLAY;
				mBtnCallListener.play(Constant.PLAY);
				iv_playorpause.setBackgroundResource(R.drawable.zanting);
			}
		}
		if (v == iv_next) {
			mBtnCallListener.next();
		}
		if (v == iv_previous) {
			mBtnCallListener.previous();
		}
		if (v == ll_intro) {
			mViewPager.setCurrentItem(0);
		}
		if (v == ll_programme) {
			mViewPager.setCurrentItem(1);
		}
		if (v == ll_download) {
			mViewPager.setCurrentItem(2);
		}
	}

	class SeekBarChangeEvent implements SeekBar.OnSeekBarChangeListener {
		@Override
		public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
		}

		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {
		}

		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {
			if (ReplayPlayerService.mMediaPlayer != null) {
				ReplayPlayerService.mMediaPlayer.seekTo(seekBar.getProgress());
			}
		}
	}

	Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {

			switch (msg.what) {
			case 0:
				// riv_play.setOrientation(i, true);
				// i-=90;
				break;
			case 1:
				if (ReplayPlayerService.mMediaPlayer != null) {
					int end = ReplayPlayerService.mMediaPlayer.getDuration();
					int begin = ReplayPlayerService.mMediaPlayer.getCurrentPosition();
					audioSeekBar.setMax(end);
					audioSeekBar.setProgress(begin);
					SimpleDateFormat sdf = null;
					String str_begin = null;
					String str_end = null;
					if (ReplayPlayerService.mMediaPlayer.isPlaying()) {
						if (end < 3600000) {
							sdf = new SimpleDateFormat("mm:ss");
						} else {
							sdf = new SimpleDateFormat("HH:mm:ss");
							TimeZone timezone = TimeZone.getTimeZone("GMT+00:00");
							sdf.setTimeZone(timezone);
						}
						Date dend = new Date(end);
						str_end = sdf.format(dend);
						Date dbegin = new Date(begin);
						str_begin = sdf.format(dbegin);
						tv_start.setText(str_begin);
						tv_end.setText(str_end);
					}

				}
				break;
			}

		}
	};

	class DelayThread extends Thread {
		int milliseconds;

		public DelayThread(int i) {
			milliseconds = i;
		}

		public void run() {
			while (!progressflag) {
				try {
					sleep(milliseconds);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				handler.sendEmptyMessage(1);
			}
		}
	}

	@Override
	public void onAttachFragment(Fragment fragment) {
		try {
			mBtnCallListener = (IBtnCallListener) fragment;
		} catch (Exception e) {
			e.printStackTrace();
		}
		super.onAttachFragment(fragment);
	}

	@Override
	public void play(int playorpause) {
		DelayThread delaythread = new DelayThread(500);
		delaythread.start();
		if (playorpause == Constant.PLAY) {
			temp_playorpause = Constant.PLAY;
			iv_playorpause.setBackgroundResource(R.drawable.zanting);
		} else if (playorpause == Constant.PAUSE) {
			temp_playorpause = Constant.PAUSE;
			iv_playorpause.setBackgroundResource(R.drawable.bofang);
		}
	}

	@Override
	public void previous() {
		// TODO Auto-generated method stub
	}

	@Override
	public void next() {
		// TODO Auto-generated method stub
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		progressflag = true;
	}
}

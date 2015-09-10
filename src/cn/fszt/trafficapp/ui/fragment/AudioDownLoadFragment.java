package cn.fszt.trafficapp.ui.fragment;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;
import cn.fszt.trafficapp.R;
import cn.fszt.trafficapp.adapter.AudioProgrammeListAdapter;
import cn.fszt.trafficapp.db.DBManager;
import cn.fszt.trafficapp.domain.AudioProgrammeData;
import cn.fszt.trafficapp.ui.service.IBtnCallListener;
import cn.fszt.trafficapp.ui.service.RadioPlayerService;
import cn.fszt.trafficapp.ui.service.ReplayPlayerService;
import cn.fszt.trafficapp.util.Constant;
import cn.fszt.trafficapp.util.HttpUtil;
import cn.fszt.trafficapp.widget.PullDownView;
import cn.fszt.trafficapp.widget.ScrollOverListView;

/**
 * 已下载音频
 */
public class AudioDownLoadFragment extends Fragment implements OnItemClickListener, IBtnCallListener {

	IBtnCallListener mbtnListener;
	private PullDownView pullDownView;
	private ScrollOverListView listview;
	private AudioProgrammeListAdapter audioProgrammeAdapter;
	private List<AudioProgrammeData> sql_arrays;
	private Intent intent;
	private String hdaudiotypeid, temp_playurl, api_url;

	private boolean playflag; // 标记播放暂停的图标
	private boolean playorpause; // 标记播放线程

	private DBManager mgr;
	private Context context;

	private static int temp_position = -1;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.tab_audioprogramme, container, false);
		context = getActivity();
		mgr = new DBManager(getActivity());
		intent = getActivity().getIntent();

		pullDownView = (PullDownView) view.findViewById(R.id.pdv_jiemulist);
		pullDownView.enableAutoFetchMore(true, 2);
		pullDownView.enableLoadMore(false);
		pullDownView.enablePullDown(false);

		listview = pullDownView.getListView();
		listview.setOnItemClickListener(this);

		api_url = getString(R.string.api_url);

		return view;
	}

	private void initData() {
		hdaudiotypeid = intent.getStringExtra("hdaudiotypeid");
		sql_arrays = mgr.queryAudioDownLoadByHdaudiotypeid(hdaudiotypeid);

		audioProgrammeAdapter = new AudioProgrammeListAdapter(context, sql_arrays, hdaudiotypeid,
				AudioProgrammeListAdapter.Entrance_DownLoad);
		listview.setAdapter(audioProgrammeAdapter);
		if (temp_position != -1) {
			sql_arrays.get(temp_position).setPlaying(true);
		}
		audioProgrammeAdapter.notifyDataSetChanged();
		pullDownView.notifyDidDataLoad(false);
		
	}

	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		if (isVisibleToUser) {
			initData();
		}
		super.setUserVisibleHint(isVisibleToUser);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		temp_position = position;
		audioProgrammeAdapter.location = -1; // 显示listView隐藏菜单的标识，小于0不进入逻辑判断
		if (sql_arrays != null) {
			for (int i = 0; i < sql_arrays.size(); i++) {
				sql_arrays.get(i).setPlaying(false);
			}
			sql_arrays.get(position).setPlaying(true);
			audioProgrammeAdapter.notifyDataSetChanged();
			GetMusicPath();
		}
	}

	/**
	 * 播放音乐。先判断是否下载，下载则播放本地文件
	 */
	private void GetMusicPath() {
		String filePath = audioProgrammeAdapter.map.get(sql_arrays.get(temp_position).getHdaudioid());
		if (new File(filePath).exists()) {
			playorpause(filePath);
		} else {
			Toast.makeText(getActivity(), "文件不存在", Toast.LENGTH_SHORT).show();
		}
		// playorpause(sql_arrays.get(temp_position).getFilepath());
	}

	private void playorpause(String playurl) {
		if (playurl.equals(temp_playurl)) {
			playMusic(Constant.PAUSE, playurl);
			if (!playflag) {
				mbtnListener.play(Constant.PAUSE);
				playflag = true;
			} else {
				mbtnListener.play(Constant.PLAY);
				playflag = false;
			}
		} else {
			mbtnListener.play(Constant.PLAY);
			play(playurl);
		}
	}

	private void play(final String playurl) {
		if (playurl != null) {
			new Thread() {
				@Override
				public void run() {
					if (!playorpause) {
						try {
							Thread.sleep(200);
							Intent intent_replay = new Intent();
							intent_replay.setClass(getActivity(), ReplayPlayerService.class);
							getActivity().stopService(intent_replay);// 停止Service
							// 停止直播
							Intent intent = new Intent();
							intent.setClass(getActivity(), RadioPlayerService.class);
							getActivity().stopService(intent);// 停止Service
							getActivity().setResult(Activity.RESULT_OK); // 通知直播界面切换暂停按钮
							// 开始点播
							playMusic(Constant.PLAY, playurl);
							Thread.sleep(200);
							handler.sendEmptyMessage(Constant.UPDATEPLAYCOUNT);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
			}.start();
			temp_playurl = playurl;
		} else {
			// showMsg(getResources().getString(R.string.no_dianbo), "info",
			// "bottom");
		}
	}

	Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case Constant.UPDATEPLAYCOUNT:
				new Thread() {
					public void run() {
						if (sql_arrays != null) {
							setPlayTimes(sql_arrays.get(temp_position).getHdaudioid());
						}
					}
				}.start();
				break;
			}
		}
	};

	private void setPlayTimes(String hdaudioid) {
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("action", "UpdateHdaudioCountByID"));
		params.add(new BasicNameValuePair("hdaudioid", hdaudioid));
		String resutl = HttpUtil.PostStringFromUrl(api_url, params);
	}

	public void playMusic(int action, String playurl) {
		Intent intent = new Intent();
		intent.putExtra("MSG", action);
		intent.putExtra("url", playurl);
		intent.setClass(getActivity(), ReplayPlayerService.class);
		getActivity().startService(intent);
	}

	@Override
	public void onAttach(Activity activity) {
		mbtnListener = (IBtnCallListener) activity;
		super.onAttach(activity);
	}

	@Override
	public void play(int playorpause) {
		// TODO Auto-generated method stub
		playMusic(Constant.PAUSE, temp_playurl);
		if (!playflag) {
			mbtnListener.play(Constant.PAUSE);
			playflag = true;
		} else {
			mbtnListener.play(Constant.PLAY);
			playflag = false;
		}
	}

	@Override
	public void previous() {
		// TODO Auto-generated method stub
		if (sql_arrays != null && temp_position != -1) {
			if (temp_position == 0) {
				temp_position = sql_arrays.size() - 1;
			} else {
				temp_position -= 1;
			}
			for (int i = 0; i < sql_arrays.size(); i++) {
				sql_arrays.get(i).setPlaying(false);
			}
			sql_arrays.get(temp_position).setPlaying(true);
			audioProgrammeAdapter.notifyDataSetChanged();
			GetMusicPath();
		}
	}

	@Override
	public void next() {
		// TODO Auto-generated method stub
		if (sql_arrays != null && temp_position != -1) {
			if (temp_position == sql_arrays.size() - 1) {
				temp_position = 0;
			} else {
				temp_position += 1;
			}
			for (int i = 0; i < sql_arrays.size(); i++) {
				sql_arrays.get(i).setPlaying(false);
			}
			sql_arrays.get(temp_position).setPlaying(true);
			audioProgrammeAdapter.notifyDataSetChanged();
			GetMusicPath();
		}
	}
}

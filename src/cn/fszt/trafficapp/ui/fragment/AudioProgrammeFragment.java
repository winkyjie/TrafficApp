package cn.fszt.trafficapp.ui.fragment;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.message.BasicNameValuePair;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

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
import cn.fszt.trafficapp.domain.AudioProgrammeData;
import cn.fszt.trafficapp.ui.service.IBtnCallListener;
import cn.fszt.trafficapp.ui.service.RadioPlayerService;
import cn.fszt.trafficapp.ui.service.ReplayPlayerService;
import cn.fszt.trafficapp.util.Constant;
import cn.fszt.trafficapp.util.HttpUtil;
import cn.fszt.trafficapp.widget.PullDownView;
import cn.fszt.trafficapp.widget.PullDownView.OnPullDownListener;
import cn.fszt.trafficapp.widget.ScrollOverListView;

/**
 * 音频节目列表
 * 
 * @author AeiouKong
 *
 */
public class AudioProgrammeFragment extends Fragment implements OnItemClickListener, IBtnCallListener {

	IBtnCallListener mbtnListener;
	private PullDownView pullDownView;
	private ScrollOverListView listview;
	private AudioProgrammeListAdapter audioProgrammeAdapter;
	private List<AudioProgrammeData> arrays;
	private Intent intent;
	private String hdaudiotypeid, gethdaudiolist_url, temp_playurl, api_url;
	private int temp_position = -1;
	private int startindex = 1;
	private int endindex = 15;

	private boolean playflag; // 标记播放暂停的图标
	private boolean playorpause; // 标记播放线程

	private Context context;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.tab_audioprogramme, container, false);
		context = getActivity();
		intent = getActivity().getIntent();
		hdaudiotypeid = intent.getStringExtra("hdaudiotypeid");
		pullDownView = (PullDownView) view.findViewById(R.id.pdv_jiemulist);
		pullDownView.enableAutoFetchMore(true, 2);
		pullDownView.enableLoadMore(true);
		pullDownView.enablePullDown(true);
		listview = pullDownView.getListView();
		listview.setOnItemClickListener(this);
		api_url = getString(R.string.api_url);
		gethdaudiolist_url = getString(R.string.server_url) + getString(R.string.gethdaudiolist_url);
		pullDownView.setOnPullDownListener(new OnPullDownListener() {
			@Override
			public void onRefresh() {
				startindex = 1;
				endindex = 15;
				initArrays(new Handler() {
					@Override
					public void handleMessage(Message msg) {
						arrays = (List<AudioProgrammeData>) msg.obj;
						if (arrays != null) {
							audioProgrammeAdapter = new AudioProgrammeListAdapter(context, arrays, hdaudiotypeid,
									AudioProgrammeListAdapter.Entrance_Programme);
							listview.setAdapter(audioProgrammeAdapter);
							audioProgrammeAdapter.notifyDataSetChanged();
							pullDownView.notifyDidRefresh(false);
						}
					}
				});
			}

			@Override
			public void onLoadMore() {
				getLoadMore(new Handler() {
					@Override
					public void handleMessage(Message msg) {
						if ((List<AudioProgrammeData>) msg.obj != null) {
							for (AudioProgrammeData s : (List<AudioProgrammeData>) msg.obj) {
								arrays.add(s);
							}
						}
						audioProgrammeAdapter.notifyDataSetChanged();
						pullDownView.notifyDidLoadMore((List<AudioProgrammeData>) msg.obj == null);
					}
				});
			}
		});
		initData();
		return view;
	}

	private void initData() {
		initArrays(new Handler() {
			@Override
			public void handleMessage(Message msg) {
				arrays = (List<AudioProgrammeData>) msg.obj;
				if (arrays != null) {
					audioProgrammeAdapter = new AudioProgrammeListAdapter(context, arrays, hdaudiotypeid,
							AudioProgrammeListAdapter.Entrance_Programme);
					listview.setAdapter(audioProgrammeAdapter);
					audioProgrammeAdapter.notifyDataSetChanged();
					pullDownView.notifyDidDataLoad(false);
				}
			}
		});
	}

	/**
	 * @return
	 * @throws IOException
	 * @throws ClientProtocolException
	 * @throws Exception
	 */
	private List<AudioProgrammeData> getDataFromNetwork(int start, int end) throws Exception {
		List<AudioProgrammeData> lst = new ArrayList<AudioProgrammeData>();
		String url = gethdaudiolist_url + "&startindex=" + start + "&endindex=" + end + "&hdaudiotypeid="
				+ hdaudiotypeid;
		String resultString = HttpUtil.GetStringFromUrl(url);
		if (resultString != null) {
			Type listType = new TypeToken<List<AudioProgrammeData>>() {
			}.getType();
			Gson gson = new Gson();
			lst = gson.fromJson(resultString, listType);
			return lst;
		} else {
			return null;
		}
	}

	/**
	 * @param handler
	 */
	private void initArrays(final Handler handler) {
		new Thread(new Runnable() {
			List<AudioProgrammeData> lst = new ArrayList<AudioProgrammeData>();

			@Override
			public void run() {

				try {
					lst = getDataFromNetwork(startindex, endindex);
				} catch (Exception e) {
					e.printStackTrace();
				}

				handler.obtainMessage(0, lst).sendToTarget();
			}
		}).start();
	}

	/**
	 * 
	 * @param handler
	 */
	private void getLoadMore(final Handler handler) {
		new Thread(new Runnable() {
			List<AudioProgrammeData> lst = new ArrayList<AudioProgrammeData>();

			@Override
			public void run() {

				startindex += 15;
				endindex += 15;
				try {
					lst = getDataFromNetwork(startindex, endindex);
				} catch (Exception e) {
					e.printStackTrace();
				}

				handler.obtainMessage(0, lst).sendToTarget();
			}
		}).start();
	}

	@Override
	public void onDestroy() {
		Intent intent_replay = new Intent();
		intent_replay.setClass(getActivity(), ReplayPlayerService.class);
		getActivity().stopService(intent_replay);// 停止Service
		super.onDestroy();
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		temp_position = position;
		audioProgrammeAdapter.location = -1; // 显示listView隐藏菜单的标识，小于0不进入逻辑判断
		if (arrays != null) {
			for (int i = 0; i < arrays.size(); i++) {
				arrays.get(i).setPlaying(false);
			}
			arrays.get(position).setPlaying(true);
			audioProgrammeAdapter.notifyDataSetChanged();
			GetMusicPath();
		}
	}

	public void playMusic(int action, String playurl) {
		Intent intent = new Intent();
		intent.putExtra("MSG", action);
		intent.putExtra("url", playurl);
		intent.setClass(getActivity(), ReplayPlayerService.class);
		getActivity().startService(intent);
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
		if (arrays != null && temp_position != -1) {
			if (temp_position == 0) {
				temp_position = arrays.size() - 1;
			} else {
				temp_position -= 1;
			}
			for (int i = 0; i < arrays.size(); i++) {
				arrays.get(i).setPlaying(false);
			}
			arrays.get(temp_position).setPlaying(true);
			audioProgrammeAdapter.notifyDataSetChanged();
			GetMusicPath();
		}
	}

	@Override
	public void next() {
		// TODO Auto-generated method stub
		if (arrays != null && temp_position != -1) {
			if (temp_position == arrays.size() - 1) {
				temp_position = 0;
			} else {
				temp_position += 1;
			}
			for (int i = 0; i < arrays.size(); i++) {
				arrays.get(i).setPlaying(false);
			}
			arrays.get(temp_position).setPlaying(true);
			audioProgrammeAdapter.notifyDataSetChanged();
			GetMusicPath();
		}
	}

	/**
	 * 播放音乐。先判断是否下载，下载则播放本地文件
	 */
	private void GetMusicPath() {
		if (audioProgrammeAdapter.map.size() > 0
				&& audioProgrammeAdapter.map.containsKey(arrays.get(temp_position).getHdaudioid())) {
			String filePath = audioProgrammeAdapter.map.get(arrays.get(temp_position).getHdaudioid());
			if (new File(filePath).exists()) {
				playorpause(filePath);
			} else {
				Toast.makeText(getActivity(), "文件不存在", Toast.LENGTH_SHORT).show();
			}
		} else {
			playorpause(arrays.get(temp_position).getVoicepath());
		}
	}

	Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case Constant.UPDATEPLAYCOUNT:
				new Thread() {
					public void run() {
						if (arrays != null) {
							setPlayTimes(arrays.get(temp_position).getHdaudioid());
						}
					}
				}.start();
				break;
			}
		}
	};

	// TODO
	private void setPlayTimes(String hdaudioid) {
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("action", "UpdateHdaudioCountByID"));
		params.add(new BasicNameValuePair("hdaudioid", hdaudioid));
		String resutl = HttpUtil.PostStringFromUrl(api_url, params);
		System.out.println("hdaudioidresutl==" + resutl);
	}

	// private void ismobiledialog(){
	// AlertDialog.Builder builder = new Builder(getActivity());
	// builder.setMessage(getString(R.string.networkinfo_mobile));
	// builder.setPositiveButton(getString(R.string.dialog_cancel), new
	// DialogInterface.OnClickListener() {
	// @Override
	// public void onClick(DialogInterface dialog, int which) {
	// dialog.dismiss();
	// play();
	// }
	// });
	// builder.setNegativeButton(getString(R.string.dialog_no), new
	// DialogInterface.OnClickListener() {
	// @Override
	// public void onClick(DialogInterface dialog, int which) {
	// dialog.dismiss();
	// }
	// });
	// builder.create().show();
	// }

	// private boolean checkNetworkInfo() {
	//
	// ConnectivityManager conMan = (ConnectivityManager) getActivity()
	// .getSystemService(Context.CONNECTIVITY_SERVICE);
	//
	// NetworkInfo info = conMan
	// .getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
	//
	// if (info != null) {
	// State mobile = info.getState();
	// if (mobile != null) {
	// return ("CONNECTED").equals(mobile.toString());
	// }
	// }
	// return false;
	// }
}

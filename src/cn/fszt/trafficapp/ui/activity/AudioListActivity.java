package cn.fszt.trafficapp.ui.activity;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.client.ClientProtocolException;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import cn.fszt.trafficapp.R;
import cn.fszt.trafficapp.adapter.AudioListAdapter;
import cn.fszt.trafficapp.db.DBManager;
import cn.fszt.trafficapp.domain.AudioListData;
import cn.fszt.trafficapp.util.HttpUtil;
import cn.fszt.trafficapp.widget.PullDownView;
import cn.fszt.trafficapp.widget.PullDownView.OnPullDownListener;
import cn.fszt.trafficapp.widget.ScrollOverListView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/**
 * 音视频列表
 * @author AeiouKong
 *
 */
public class AudioListActivity extends FragmentActivity {
	
	private PullDownView pullDownView;
	private ScrollOverListView mainListView;
	private List<AudioListData> arrays;
	private AudioListAdapter audiolistAdapter;
	private int startindex = 1;
	private int endindex = 15;
	private DBManager mgr;
	private List<AudioListData> sql_arrays;

	private String gethdaudio_url;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_video);
		initView();

//		getActionBar().setTitle(getResources().getString(R.string.n_video));
//		getActionBar().setBackgroundDrawable(
//				new ColorDrawable(getResources().getColor(R.color.titlebar)));
//		getActionBar().setDisplayHomeAsUpEnabled(true);
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
		mgr = new DBManager(this);
		gethdaudio_url = getString(R.string.server_url)
				+ getString(R.string.gethdaudio_url);
		pullDownView = (PullDownView) findViewById(R.id.pdv_mainlist);
		pullDownView.enableAutoFetchMore(true, 2);
		pullDownView.enableLoadMore(false);
		pullDownView.enablePullDown(true);
		mainListView = pullDownView.getListView();

		sql_arrays = mgr.queryAudioList();
		if (sql_arrays.size() == 0) {
			initArrays(new Handler() {
				@Override
				public void handleMessage(Message msg) {
					arrays = (List<AudioListData>) msg.obj;
					if (arrays != null) {
						audiolistAdapter = new AudioListAdapter(AudioListActivity.this,
								arrays);
						mainListView.setAdapter(audiolistAdapter);
						audiolistAdapter.notifyDataSetChanged();
						pullDownView.notifyDidDataLoad(false);
						mgr.addAudioList(arrays);
					}
				}
			});
		}else{
			arrays = sql_arrays;
			audiolistAdapter = new AudioListAdapter(AudioListActivity.this,
					arrays);
			mainListView.setAdapter(audiolistAdapter);
			audiolistAdapter.notifyDataSetChanged();
			pullDownView.notifyDidDataLoad(false);
			initArrays(new Handler() {
				@Override
				public void handleMessage(Message msg) {
					arrays = (List<AudioListData>) msg.obj;
					
					if (arrays == null) { 
						arrays = sql_arrays;
					} else { 
						mgr.deleteAudioList();
						mgr.addAudioList(arrays);
					}
					audiolistAdapter = new AudioListAdapter(AudioListActivity.this,
							arrays);
					mainListView.setAdapter(audiolistAdapter);
					audiolistAdapter.notifyDataSetChanged();
					pullDownView.notifyDidDataLoad(false);
				}
			});
		}
		
		
		
		pullDownView.setOnPullDownListener(new OnPullDownListener() {
			@Override
			public void onRefresh() {
				getRefresh(new Handler() {
					@Override
					public void handleMessage(Message msg) {

						arrays = (List<AudioListData>) msg.obj;
						if (arrays == null) {
							arrays = sql_arrays; // 下拉更新失败，还是显示数据库历史数据
						} else { // 下拉更新数据成功，更新数据库的数据
							mgr.deleteAudioList();
							mgr.addAudioList(arrays);
						}
						audiolistAdapter.notifyDataSetChanged();
						pullDownView.notifyDidRefresh(false);
					}
				});
			}

			@Override
			public void onLoadMore() {
				getLoadMore(new Handler() {
					@Override
					public void handleMessage(Message msg) {
						if ((List<AudioListData>) msg.obj != null) {
							for (AudioListData s : (List<AudioListData>) msg.obj) {
								arrays.add(s);
							}
						}
						audiolistAdapter.notifyDataSetChanged();
						pullDownView
								.notifyDidLoadMore((List<AudioListData>) msg.obj == null);
					}
				});
			}
		});

		mainListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent intent = new Intent(AudioListActivity.this,
						AudioActivity.class);
				intent.putExtra("hdaudiotypeid", arrays.get(position)
						.getHdaudiotypeid());
				intent.putExtra("desc", arrays.get(position).getTypedesc());
				intent.putExtra("title", arrays.get(position).getTypename());
				intent.putExtra("image", arrays.get(position).getImagepath());
				startActivity(intent);
			}
		});

	}

	/**
	 * @return
	 * @throws IOException
	 * @throws ClientProtocolException
	 * @throws Exception
	 */
	private List<AudioListData> getDataFromNetwork(int start, int end)
			throws Exception {
		List<AudioListData> lst = new ArrayList<AudioListData>();

		String resultString = HttpUtil.GetStringFromUrl(gethdaudio_url
				+ "&startindex=" + start + "&endindex=" + end);

		if(resultString!=null){
			Type listType = new TypeToken<List<AudioListData>>() {
			}.getType();
			Gson gson = new Gson();
			lst = gson.fromJson(resultString, listType);
			return lst;
		}else{
			return null;
		}
		
	}

	private void initArrays(final Handler handler) {
		new Thread(new Runnable() {
			List<AudioListData> lst = new ArrayList<AudioListData>();

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

	private void getLoadMore(final Handler handler) {
		new Thread(new Runnable() {
			List<AudioListData> lst = new ArrayList<AudioListData>();

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
	
	private void getRefresh(final Handler handler) {
		new Thread(new Runnable() {
			List<AudioListData> lst = new ArrayList<AudioListData>();

			@Override
			public void run() {

				try {
					startindex = 1;
					endindex = 15;
					lst = getDataFromNetwork(startindex, endindex);
				} catch (Exception e) {
					e.printStackTrace();
				}

				handler.obtainMessage(0, lst).sendToTarget();
			}
		}).start();
	}

}

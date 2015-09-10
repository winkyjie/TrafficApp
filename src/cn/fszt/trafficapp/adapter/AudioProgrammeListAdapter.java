package cn.fszt.trafficapp.adapter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import cn.fszt.trafficapp.R;
import cn.fszt.trafficapp.db.DBManager;
import cn.fszt.trafficapp.domain.AudioProgrammeData;
import cn.fszt.trafficapp.util.Constant;
import cn.fszt.trafficapp.util.HttpUtil;
import cn.fszt.trafficapp.util.ShareCountUtil;
import cn.fszt.trafficapp.widget.photoview.DisplayImageOptionsUtil;
import cn.fszt.trafficapp.widget.sharesdk.onekeyshare.OnekeyShare;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;

public class AudioProgrammeListAdapter extends BaseAdapter {
	// 入口是那个页面
	public final static int Entrance_Programme = 1;
	public final static int Entrance_DownLoad = 2;

	private List<AudioProgrammeData> frontAudios;
	private List<AudioProgrammeData> sql_arrays;
	private String hdaudiotypeid;
	private ViewHolder holder;
	private LayoutInflater inflater;
	private Context con;
	private DBManager mgr;
	private ImageLoader imageLoader;
	private DisplayImageOptions options;
	public Map<String, String> map = new HashMap<String, String>(); // 缓存已下载列表
	private Boolean isDown = false; // 当前音频是否已下载
	public int location = -1; // 记录当前点击的哪个item的view
	private int entrance = -1;

	public AudioProgrammeListAdapter(Context context, List<AudioProgrammeData> frontAudios, String hdaudiotypeid,
			int entrance) {
		super();
		this.frontAudios = frontAudios;
		this.hdaudiotypeid = hdaudiotypeid;
		this.con = context;
		this.entrance = entrance;
		inflater = LayoutInflater.from(con);
		imageLoader = ImageLoader.getInstance();
		options = DisplayImageOptionsUtil.getOptions(R.drawable.ic_empty, R.drawable.ic_error,
				R.drawable.default_image);
		getAlreadyDownLoadAudio();
	}

	@Override
	public int getCount() {
		return frontAudios.size();
	}

	@Override
	public Object getItem(int arg0) {
		return frontAudios.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	/**
	 * 将已下载的列表缓存
	 */
	private void getAlreadyDownLoadAudio() {
		mgr = new DBManager(con);
		sql_arrays = mgr.queryAudioDownLoadByHdaudiotypeid(hdaudiotypeid);
		if (sql_arrays.size() > 0) {
			for (int i = 0; i < sql_arrays.size(); i++) {
				map.put(sql_arrays.get(i).getHdaudioid(), sql_arrays.get(i).getFilepath());
			}
		}
	}

	/**
	 * 判断音频是否已经下载
	 */
	public void audioIsDown(int position) {
		if (map.size() > 0) {
			if (map.containsKey(frontAudios.get(position).getHdaudioid())) {
				isDown = true;
			} else {
				isDown = false;
			}
		} else {
			isDown = false;
		}
	}

	@Override
	public View getView(final int position, View contentView, final ViewGroup parent) {
		audioIsDown(position);
		String image = frontAudios.get(position).getImagepath();
		String title = frontAudios.get(position).getTitle();
		String date = frontAudios.get(position).getCreatedate();
		String playcount = frontAudios.get(position).getPlaycount();
		boolean playing = frontAudios.get(position).isPlaying();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			Date ddate = sdf.parse(date);
			SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd");
			date = dateFormat.format(ddate);
		} catch (ParseException e) {
			e.printStackTrace();
		}

		if (contentView == null) {
			holder = new ViewHolder();
			contentView = inflater.inflate(R.layout.tab_audioprogramme_item, null);
			holder.title = (TextView) contentView.findViewById(R.id.music_title);
			holder.time = (TextView) contentView.findViewById(R.id.music_time);
			holder.tv_playcount = (TextView) contentView.findViewById(R.id.tv_playcount);
			holder.show = (ImageView) contentView.findViewById(R.id.show);
			holder.iv_playing = (ImageView) contentView.findViewById(R.id.iv_playing);
			holder.music_menu = (LinearLayout) contentView.findViewById(R.id.music_menu);
			holder.iv_music_menu = (ImageView) contentView.findViewById(R.id.iv_music_menu);
			holder.iv_music_menu.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					location = position;
					AudioProgrammeListAdapter.this.notifyDataSetChanged();
				}
			});
			holder.music_share = (LinearLayout) contentView.findViewById(R.id.music_share);
			holder.music_download = (LinearLayout) contentView.findViewById(R.id.music_download);
			holder.music_delete = (LinearLayout) contentView.findViewById(R.id.music_delete);
			holder.music_local_title = (TextView) contentView.findViewById(R.id.music_local_title);
			// holder.music_loadcv = (LoadingCircleView)
			// contentView.findViewById(R.id.music_loadcv);
			holder.music_ll_download = (LinearLayout) contentView.findViewById(R.id.music_ll_download);
			contentView.setTag(holder);
		} else {
			holder = (ViewHolder) contentView.getTag();
		}
		if (playing) {
			holder.iv_playing.setVisibility(View.VISIBLE);
		} else {
			holder.iv_playing.setVisibility(View.GONE);
		}
		if (isDown) {
			holder.music_local_title.setVisibility(View.VISIBLE);
		} else {
			holder.music_local_title.setVisibility(View.GONE);
		}
		holder.title.setText(title);
		holder.time.setText(date);
		holder.tv_playcount.setText(playcount);
		imageLoader.displayImage(image, holder.show, options);

		if (location == position) {
			if (holder.music_menu.getVisibility() == View.VISIBLE) {
				holder.iv_music_menu.setBackgroundResource(R.drawable.music_menu_down);
				holder.music_menu.setVisibility(View.GONE);
			} else {
				// 音乐管理菜单可见
				holder.iv_music_menu.setBackgroundResource(R.drawable.music_menu_up);
				holder.music_menu.setVisibility(View.VISIBLE);
				holder.music_share.setClickable(true);
				holder.music_download.setClickable(true);
				// 分享
				holder.music_share.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						location = position;
						AudioProgrammeListAdapter.this.notifyDataSetChanged();
						if (!frontAudios.get(position).getVoicepath().isEmpty()) {
							showShare(frontAudios.get(position).getTitle(), frontAudios.get(position).getVoicepath(),
									frontAudios.get(position).getImagepath(), frontAudios.get(position).getHdaudioid());
						} else {
							Toast.makeText(con, "转发失败", Toast.LENGTH_SHORT).show();
						}
					}
				});
				// 下载
				holder.music_download.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						location = position;
						String voicePath = frontAudios.get(position).getVoicepath();
						String audioId = frontAudios.get(position).getHdaudioid();
						List<AudioProgrammeData> list = mgr.queryAudioDownLoadByHdaudioTypeIdAndHdaudioId(hdaudiotypeid,
								audioId);
						if (list.size() > 0) {
							Toast.makeText(con, "已下载,在已下载列表查看", Toast.LENGTH_SHORT).show();
						} else {
							// 音乐保存到手机的位置
							// holder.music_loadcv.setVisibility(View.VISIBLE);
							// holder.music_ll_download.setVisibility(View.GONE);

							AudioDownloadAsyncTask adat = new AudioDownloadAsyncTask(voicePath, audioId, position,
									holder.music_delete, AudioProgrammeListAdapter.this);
							adat.execute();
							// AudioProgrammeListAdapter.this.notifyDataSetChanged();
						}
					}
				});
				if (isDown) { // 如果没有下载则隐藏删除按钮
					holder.music_delete.setVisibility(View.VISIBLE);
					holder.music_download.setVisibility(View.GONE);
					holder.music_delete.setClickable(true);
					holder.music_delete.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							String voicePath = frontAudios.get(position).getVoicepath();
							String audioId = frontAudios.get(position).getHdaudioid();
							String loaclFilePath = GetMusicFileDir(voicePath) + GetMusicFileName(voicePath, audioId);
							location = position;
							// 删除本地数据
							mgr.deleteAudioDownLoadByHdaudiotypeid(hdaudiotypeid, audioId);
							// 更新view
							if (entrance == Entrance_Programme) {
								map.remove(audioId);
							} else if (entrance == Entrance_DownLoad) {
								frontAudios.remove(position);
							}
							AudioProgrammeListAdapter.this.notifyDataSetChanged();
							// 删除本地文件...
							File musicFile = new File(loaclFilePath);
							if (musicFile.exists()) {
								musicFile.delete();
								Toast.makeText(con, "删除成功！", Toast.LENGTH_SHORT).show();
							} else {
								Toast.makeText(con, "文件不存在！", Toast.LENGTH_SHORT).show();
							}
						}
					});
				} else {
					holder.music_delete.setVisibility(View.GONE); // 隐藏删除按钮
					holder.music_download.setVisibility(View.VISIBLE);
				}
			}

		}

		return contentView;
	}

	/**
	 * 获得音频存储目录
	 * 
	 * @param downUrl
	 * @return
	 */
	private String GetMusicFileDir(String downUrl) {
		return Environment.getExternalStorageDirectory().getAbsolutePath()
				+ con.getResources().getString(R.string.music_dir);
	}

	/**
	 * 获得音频存储名
	 * 
	 * @param downUrl
	 * @param audioId
	 * @return
	 */
	private String GetMusicFileName(String downUrl, String audioId) {
		return audioId + downUrl.substring(downUrl.lastIndexOf("."));
	}

	/**
	 * 分享
	 * 
	 * @param title
	 *            标题
	 * @param transmiturl
	 *            链接
	 * @param imagepath
	 *            图片路径
	 * @param audioId
	 *            音频ID
	 */
	private void showShare(String title, String musicUrl, String imagepath, final String audioId) {
		String url = con.getResources().getString(R.string.server_url)
				+ "UpdateShareHdreplayCountByHdreplayid&hdreplayid=" + audioId + "&voicepath=" + musicUrl;
		SharedPreferences sp_user = con.getSharedPreferences("USER", Context.MODE_PRIVATE);
		final String uid = sp_user.getString("uuid", null);
		ShareSDK.initSDK(con);
		OnekeyShare oks = new OnekeyShare();
		oks.setTitle(title);
		oks.setText("来自畅驾App分享");
		oks.setMusicUrl(url);
		oks.setUrl(url);
		oks.setTitleUrl(url);
		if (!imagepath.isEmpty()) {
			oks.setImageUrl(imagepath);
		} else {
			oks.setImageUrl(con.getResources().getString(R.string.qrcode_url));
		}
		oks.setCallback(new PlatformActionListener() {

			@Override
			public void onError(Platform platform, int arg1, Throwable arg2) {
				Toast.makeText(con, "分享失败", Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onComplete(Platform platform, int arg1, HashMap<String, Object> arg2) {

				final String plat = platform.toString();
				new Thread() {
					public void run() {
						// "5" 表示为 音频分享
						ShareCountUtil.ShareCount(con.getResources().getString(R.string.api_url), uid, audioId, "5",
								plat, "113.129151,23.024514");
					}
				}.start();
			}

			@Override
			public void onCancel(Platform platform, int arg1) {
				Toast.makeText(con, "取消分享", Toast.LENGTH_SHORT).show();
			}
		});
		// 启动分享GUI
		oks.show(con);
	}

	class AudioDownloadAsyncTask extends AsyncTask<Object, Integer, Long> {

		private String downUrl;
		private String audioId;
		private int position;
		private String fileDir;
		private String fileName;
		private File mDownloadDir;
		private File mDownloadFile;

		// private LoadingCircleView music_loadcv;
		private LinearLayout music_delete;
		private AudioProgrammeListAdapter apla;

		public AudioDownloadAsyncTask(String downUrl, String audioId, int position, LinearLayout music_delete,
				AudioProgrammeListAdapter apla) {
			this.downUrl = downUrl;
			this.audioId = audioId;
			this.position = position;
			this.fileDir = GetMusicFileDir(downUrl);
			this.fileName = GetMusicFileName(downUrl, audioId);
			// this.music_loadcv = music_loadcv;
			this.music_delete = music_delete;
			this.apla = apla;
		}

		@Override
		protected void onPostExecute(Long result) {
			super.onPostExecute(result);
			// music_loadcv.setVisibility(View.GONE);
			// music_delete.setVisibility(View.VISIBLE);
			// apla.notifyDataSetChanged();

			// String loaclFilePath = fileDir + fileName;
			// // 保存到数据库
			// AudioProgrammeData audio = new AudioProgrammeData();
			// audio.setHdaudioid(frontAudios.get(position).getHdaudioid());
			// audio.setHdaudiotypeid(hdaudiotypeid);
			// audio.setCreatedate(frontAudios.get(position).getCreatedate());
			// audio.setImagepath(frontAudios.get(position).getImagepath());
			// audio.setTitle(frontAudios.get(position).getTitle());
			// audio.setVoicepath(frontAudios.get(position).getVoicepath());
			// audio.setFilepath(loaclFilePath);
			// mgr.addAudioDownLoad(audio);
			// // 标识文件已下载
			// map.put(audioId, loaclFilePath);
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			super.onProgressUpdate(values);
			// Log.d("TAG", "values[0]:" + values[0]);
			// music_loadcv.setProgress(values[0]);
			// apla.notifyDataSetChanged();
		}

		@Override
		protected Long doInBackground(Object... params) {
			downLoadFile();
			return null;
		}

		private void downLoadFile() {
			if (downUrl != null && mDownloadDir != null && mDownloadFile != null) {
				InputStream inputStream = null;
				FileOutputStream fileOutputStream = null;
				HttpClient client = new DefaultHttpClient();
				HttpGet get = new HttpGet(downUrl);
				try {
					HttpResponse response = client.execute(get);
					StatusLine statusLine = response.getStatusLine();
					if (statusLine.getStatusCode() == 200) {
						// 先要获得文件的总长度
						// long file_length =
						// response.getEntity().getContentLength();
						fileOutputStream = new FileOutputStream(mDownloadFile, false);
						inputStream = response.getEntity().getContent();
						byte[] buff = new byte[1024];
						int len = 0;
						// int current_length = 0;
						while ((len = inputStream.read(buff)) > 0) {
							// current_length += len;
							// int value = (int) ((current_length / (float)
							// file_length) * 100);
							// publishProgress(value);// 把刻度发布出去
							fileOutputStream.write(buff, 0, len);
						}
						fileOutputStream.flush();
						fileOutputStream.close();
					}
				} catch (Exception e) {
				} finally {
					if (inputStream != null) {
						try {
							inputStream.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
					if (fileOutputStream != null) {
						try {
							fileOutputStream.flush();
							fileOutputStream.close();
						} catch (Exception e2) {
						}
					}
				}
			}
		}

		@Override
		protected void onPreExecute() {

			String loaclFilePath = fileDir + fileName;
			// 保存到数据库 如果有进度条则 在onPostExecute里面插入修改页面
			AudioProgrammeData audio = new AudioProgrammeData();
			audio.setHdaudioid(frontAudios.get(position).getHdaudioid());
			audio.setHdaudiotypeid(hdaudiotypeid);
			audio.setCreatedate(frontAudios.get(position).getCreatedate());
			audio.setImagepath(frontAudios.get(position).getImagepath());
			audio.setTitle(frontAudios.get(position).getTitle());
			audio.setVoicepath(frontAudios.get(position).getVoicepath());
			audio.setFilepath(loaclFilePath);
			mgr.addAudioDownLoad(audio);
			// 标识文件已下载
			map.put(audioId, loaclFilePath);
			apla.notifyDataSetChanged();

			mDownloadDir = new File(fileDir);
			mDownloadFile = new File(mDownloadDir, fileDir + fileName);
			if (mDownloadDir != null && !mDownloadDir.exists()) {
				mDownloadDir.mkdirs();
			}
			if (mDownloadFile != null && !mDownloadFile.exists()) {
				try {
					mDownloadFile.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			super.onPreExecute();
		}
	}

	public class ViewHolder {
		ImageView show;
		ImageView iv_playing;
		TextView title;
		TextView time;
		TextView tv_playcount;
		LinearLayout music_menu;
		ImageView iv_music_menu;
		LinearLayout music_share;
		LinearLayout music_download;
		LinearLayout music_delete;
		TextView music_local_title;
		// LoadingCircleView music_loadcv;
		LinearLayout music_ll_download;
	}
}

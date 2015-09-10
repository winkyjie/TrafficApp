package cn.fszt.trafficapp.ui.fragment;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import cn.fszt.trafficapp.R;
import cn.fszt.trafficapp.adapter.RadioProgrammeListAdapter;
import cn.fszt.trafficapp.db.DBManager;
import cn.fszt.trafficapp.domain.ReplaysData;
import cn.fszt.trafficapp.ui.activity.RadioCommentListActivity;
import cn.fszt.trafficapp.ui.service.IBtnCallListener;
import cn.fszt.trafficapp.ui.service.IItemClickListener;
import cn.fszt.trafficapp.util.HttpUtil;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

/**
 * 节目互动--选择节目
 * 
 * @author AeiouKong
 *
 */
public class RadioProgrammeListFragment extends Fragment implements IItemClickListener {

	IItemClickListener mitemclickListener;
	private GridView gv_choosereplayhudong;
	private RadioProgrammeListAdapter adapter;
	private String choosereplay_url, pgname;
	private List<ReplaysData> arrays;
	private DBManager mgr;
	private List<ReplaysData> sql_arrays;
	private Intent intent;
	private SharedPreferences sp_push;
	private Editor edit;
	private Set<String> programname;

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View messageLayout = inflater.inflate(R.layout.activity_choosereplayhudong, container, false);
		gv_choosereplayhudong = (GridView) messageLayout.findViewById(R.id.gv_choosereplayhudong);
		gv_choosereplayhudong.setOnItemClickListener(itemClickListener);

		sql_arrays = mgr.queryReplays();

		if (sql_arrays.size() == 0) { // 当数据库没有数据时，从网络更新并插入数据库
			initArrays(new Handler() {
				@Override
				public void handleMessage(Message msg) {

					arrays = (List<ReplaysData>) msg.obj;
					if (arrays != null) {
						for (int i = 0; i < arrays.size(); i++) {
							String typename = arrays.get(i).getTypename();
							if (programname != null && programname.size() > 0) {
								Iterator<String> it = programname.iterator();
								// TODO
								while (it.hasNext()) {
									if (typename.equals(it.next())) {
										arrays.get(i).setVisiable(true);
									} else {
										arrays.get(i).setVisiable(false);
									}
								}
							}
						}
						adapter = new RadioProgrammeListAdapter(getActivity(), arrays);
						gv_choosereplayhudong.setAdapter(adapter);
						adapter.notifyDataSetChanged();
						mgr.addReplays(arrays);
					}
				}
			});
		} else { // 查找数据库显示历史数据，然后再从网络更新并插入数据库
			arrays = sql_arrays;
			// if(arrays!=null){
			// for(int i=0;i<arrays.size();i++){
			// String typename = arrays.get(i).getTypename();
			// System.out.println("typename==="+typename);
			// System.out.println("programname==="+programname);
			// if(programname!=null){
			// Iterator<String> it = programname.iterator();
			// //TODO
			// while(it.hasNext()){
			// System.out.println("sqlsizt===111==="+it.next());
			// if(typename.equals(it.next())){
			// arrays.get(i).setVisiable(true);
			// }
			// }
			// }
			// }
			// }

			adapter = new RadioProgrammeListAdapter(getActivity(), arrays);
			gv_choosereplayhudong.setAdapter(adapter);
			adapter.notifyDataSetChanged();

			initArrays(new Handler() {
				@Override
				public void handleMessage(Message msg) {

					arrays = (List<ReplaysData>) msg.obj;
					if (arrays != null) {
						for (int i = 0; i < arrays.size(); i++) {
							String typename = arrays.get(i).getTypename();
							if (programname != null) {
								Iterator<String> it = programname.iterator();
								// TODO
								while (it.hasNext()) {
									if (typename.equals(it.next())) {
										arrays.get(i).setVisiable(true);
									}
								}
							}
						}
						mgr.deleteReplays();
						mgr.addReplays(arrays);
					} else {
						arrays = sql_arrays;
					}
					adapter = new RadioProgrammeListAdapter(getActivity(), arrays);
					gv_choosereplayhudong.setAdapter(adapter);
					adapter.notifyDataSetChanged();
				}
			});
		}
		return messageLayout;
	}

	private void initView() {
		mgr = new DBManager(getActivity());
		intent = getActivity().getIntent();
		pgname = intent.getStringExtra("pgname");
		choosereplay_url = getString(R.string.server_url) + getString(R.string.choosereplay_url);

	}

	@Override
	public void onAttach(Activity activity) {
		mitemclickListener = (IItemClickListener) activity;
		super.onAttach(activity);
	}

	OnItemClickListener itemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			Intent intent = new Intent(getActivity(), RadioCommentListActivity.class);
			String typename = arrays.get(position).getTypename();
			// System.out.println("click===typename===" + typename);
			if (programname != null) {
				if (programname.contains(typename)) {
					// programname.remove(typename);
					mitemclickListener.itemclick(programname);
					// edit.putStringSet("programname", programname);
					// edit.commit();
					// refreshData();
					// System.out.println("programnameclick===" + programname);
				}
			}
			// programname = sp_push.getStringSet("programname", null);
			// System.out.println("programnameclickprogramnamesize===--=="+programname);
			intent.putExtra("pgname", typename);
			intent.putExtra("image", arrays.get(position).getImagepath());
			intent.putExtra("djname", arrays.get(position).getDjname());
			intent.putExtra("imagebackground", arrays.get(position).getImagebackground());
			intent.putExtra("hdreplaytypeid", arrays.get(position).getHdreplaytypeid());
			startActivity(intent);
		}
	};

	private List<ReplaysData> getReplayList() {
		List<ReplaysData> lst = new ArrayList<ReplaysData>();

		String resultString = HttpUtil.GetStringFromUrl(choosereplay_url + "&startindex=1&endindex=30");
		if (resultString != null) {
			Type listType = new TypeToken<List<ReplaysData>>() {
			}.getType();
			Gson gson = new Gson();
			lst = gson.fromJson(resultString, listType);
			return lst;
		} else {
			return null;
		}
	}

	private void initArrays(final Handler handler) {
		new Thread(new Runnable() {
			List<ReplaysData> lst = new ArrayList<ReplaysData>();

			@Override
			public void run() {

				try {
					lst = getReplayList();
				} catch (Exception e) {
					e.printStackTrace();
				}
				handler.obtainMessage(0, lst).sendToTarget();
			}
		}).start();
	}

	@Override
	public void onHiddenChanged(boolean hidden) {
		super.onHiddenChanged(hidden);
		if (!hidden) {
			refreshData();
		}
	}

	private void refreshData() {
		sp_push = getActivity().getSharedPreferences("PUSH", Context.MODE_PRIVATE);
		programname = sp_push.getStringSet("programname", null);
		if (arrays != null) {
			if (programname != null) {
				for (int i = 0; i < arrays.size(); i++) {
					arrays.get(i).setVisiable(false);
					Iterator<String> it = programname.iterator();
					while (it.hasNext()) {
						if (arrays.get(i).getTypename().equals(it.next())) {
							arrays.get(i).setVisiable(true);
						}
					}
				}
			}
			adapter = new RadioProgrammeListAdapter(getActivity(), arrays);
			gv_choosereplayhudong.setAdapter(adapter);
			adapter.notifyDataSetChanged();
		}
	}

	@Override
	public void itemclick(Set<String> programname) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		initView();
		super.onCreate(savedInstanceState);
	}

	@Override
	public void onResume() {
		refreshData();
		super.onResume();
	}
}

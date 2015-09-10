package cn.fszt.trafficapp.ui.activity;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.umeng.analytics.MobclickAgent;

import android.app.Activity;
import android.app.ExpandableListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import cn.fszt.trafficapp.R;
import cn.fszt.trafficapp.db.DBManager;
import cn.fszt.trafficapp.domain.ReplaysData;
import cn.fszt.trafficapp.domain.ReplaysItemData;
import cn.fszt.trafficapp.util.HttpUtil;
import cn.fszt.trafficapp.widget.ImageLoadingDialog;
import cn.fszt.trafficapp.widget.msg.AppMsg;
import cn.fszt.trafficapp.widget.photoview.DisplayImageOptionsUtil;

/**
 * 点播室-选择节目
 * 
 * @author AeiouKong
 *
 */
public class ReplayListActivity extends ExpandableListActivity {

	ReplayItemAdapter adapter;

	private LayoutInflater inflater;
	private List<ReplaysData> grouparrays;
	private List<ReplaysItemData> childarrays;
	private String choosereplay_url, replayitem_url, hdreplaytypeid;
	private static final int REPLAYRADIO = 1;

	private SharedPreferences sp; // 存储是否正在播放的信息

	private int grouptag = -1; // 位置标记
	private int childtag = -1;

	private ImageLoadingDialog dialog;

	private DBManager mgr;
	private List<ReplaysData> sql_grouparrays;
	private List<ReplaysItemData> sql_childarrays;

	private ImageLoader imageLoader;
	private DisplayImageOptions options, options_child;

	private ProgressBar pb_re;

	AppMsg.Style style = AppMsg.STYLE_INFO;
	AppMsg appMsg;

	private RotateAnimation mRotateOTo180Animation;
	private RotateAnimation mRotate180To0Animation;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.choosere);

		initView();

		imageLoader = ImageLoader.getInstance();

		options = DisplayImageOptionsUtil.getOptions(R.drawable.ic_empty, R.drawable.ic_error,
				R.drawable.default_image);

		options_child = DisplayImageOptionsUtil.getOptions(R.drawable.ic_empty, R.drawable.ic_error,
				R.drawable.default_image, new RoundedBitmapDisplayer(90));

		sql_grouparrays = mgr.queryReplays();

		if (sql_grouparrays.size() == 0) { // 当数据库没有数据时，从网络更新并插入数据库
			initArrays(new Handler() {
				@Override
				public void handleMessage(Message msg) {
					grouparrays = (List<ReplaysData>) msg.obj;
					// if(grouptag!=-1){
					// getExpandableListView().expandGroup(grouptag);
					// }
					adapter.notifyDataSetChanged();
					// dialog.dismiss();
					pb_re.setVisibility(View.GONE);
					if (grouparrays != null) {
						mgr.addReplays(grouparrays);
					} else {
						// showMsg(getResources().getString(R.string.response_fail),
						// "info", "bottom");
					}
				}
			});
		} else { // 查找数据库显示历史数据，然后再从网络更新并插入数据库
			grouparrays = sql_grouparrays;
			adapter.notifyDataSetChanged();
			// dialog.dismiss();
			pb_re.setVisibility(View.GONE);
			initArrays(new Handler() {
				@Override
				public void handleMessage(Message msg) {

					grouparrays = (List<ReplaysData>) msg.obj;
					if (grouparrays == null) { // 网络更新失败时，还是显示数据库的历史数据
						grouparrays = sql_grouparrays;
						// showMsg(getResources().getString(R.string.response_fail),
						// "info", "bottom");
					} else { // 网络更新数据成功，更新数据库的数据
						mgr.deleteReplays();
						mgr.addReplays(grouparrays);
					}
					adapter.notifyDataSetChanged();
				}
			});
		}

		getActionBar().setTitle(getResources().getString(R.string.replay));
		getActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.titlebar)));
		getActionBar().setDisplayHomeAsUpEnabled(true);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		if (item.getItemId() == android.R.id.home) {
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
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

	private List<ReplaysData> getReplayList() {
		List<ReplaysData> lst = new ArrayList<ReplaysData>();

		String resultString = HttpUtil.GetStringFromUrl(choosereplay_url + "&startindex=1&endindex=50");
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

	// /////////child
	private void initChildArrays(final Handler handler) {
		dialog.show();
		// pb_re.setVisibility(View.VISIBLE);
		new Thread(new Runnable() {
			List<ReplaysItemData> lst = new ArrayList<ReplaysItemData>();

			@Override
			public void run() {

				try {
					lst = getReplayItemList();
				} catch (Exception e) {
					e.printStackTrace();
				}

				handler.obtainMessage(0, lst).sendToTarget();
			}
		}).start();
	}

	private List<ReplaysItemData> getReplayItemList() {
		int startindex = 1;
		int endindex = 7;
		List<ReplaysItemData> lst = new ArrayList<ReplaysItemData>();
		String req = replayitem_url + "&hdreplaytypeid=" + hdreplaytypeid + "&startindex=" + startindex + "&endindex="
				+ endindex;
		String resultString = HttpUtil.GetStringFromUrl(req);
		Type listType = new TypeToken<List<ReplaysItemData>>() {
		}.getType();
		Gson gson = new Gson();
		lst = gson.fromJson(resultString, listType);

		return lst;
	}

	private void initView() {
		mgr = new DBManager(this);
		choosereplay_url = getResources().getString(R.string.server_url)
				+ getResources().getString(R.string.choosereplay_url);
		replayitem_url = getResources().getString(R.string.server_url)
				+ getResources().getString(R.string.replayitem_url);
		inflater = getLayoutInflater();
		adapter = new ReplayItemAdapter();
		getExpandableListView().setAdapter(adapter);
		getExpandableListView().setCacheColorHint(0); // 设置拖动列表的时候防止出现黑色背景
		getExpandableListView().setGroupIndicator(null);
		getExpandableListView().setDivider(getResources().getDrawable(R.color.list_view_divider));
		getExpandableListView().setDividerHeight(1);

		sp = getSharedPreferences("REPLAY", Context.MODE_PRIVATE); // 标记正在播放的节目
		grouptag = sp.getInt("grouptag", -1);
		childtag = sp.getInt("childtag", -1);

		dialog = new ImageLoadingDialog(this);
		dialog.setCanceledOnTouchOutside(false);

		pb_re = (ProgressBar) findViewById(R.id.pb_re);

		// 注意，图片旋转之后，再执行旋转，坐标会重新开始计算
		mRotateOTo180Animation = new RotateAnimation(0, 180, Animation.RELATIVE_TO_SELF, 0.5f,
				Animation.RELATIVE_TO_SELF, 0.5f);
		mRotateOTo180Animation.setDuration(250);
		mRotateOTo180Animation.setFillAfter(true);

		mRotate180To0Animation = new RotateAnimation(180, 0, Animation.RELATIVE_TO_SELF, 0.5f,
				Animation.RELATIVE_TO_SELF, 0.5f);
		mRotate180To0Animation.setDuration(250);
		mRotate180To0Animation.setFillAfter(true);
	}

	class ReplayItemAdapter extends BaseExpandableListAdapter {

		// -----------------Child----------------//
		@Override
		public Object getChild(int groupPosition, int childPosition) {
			return childarrays.get(childPosition);
		}

		@Override
		public long getChildId(int groupPosition, int childPosition) {
			return childPosition;
		}

		@Override
		public int getChildrenCount(int groupPosition) {
			return childarrays == null ? 0 : childarrays.size();
		}

		@Override
		public int getChildType(int groupPosition, int childPosition) {
			if (groupPosition == grouptag && childPosition == childtag) {
				return 1;
			} else {
				return 2;
			}
		}

		@Override
		public int getChildTypeCount() {
			return 3;
		}

		@Override
		public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView,
				ViewGroup parent) {
			int type = getChildType(groupPosition, childPosition);
			ChildViewHolder holder = null;
			if (convertView == null) {
				switch (type) {
				case 1:
					holder = new ChildViewHolder();
					convertView = inflater.inflate(R.layout.item_replayitem2_list, null);
					holder.tv_title = (TextView) convertView.findViewById(R.id.tv_ri_title);
					holder.iv_imagepath = (ImageView) convertView.findViewById(R.id.iv_ri_imagepath);
					holder.iv_laba = (ImageView) convertView.findViewById(R.id.iv_ri_laba);
					holder.tv_ri_replycount = (TextView) convertView.findViewById(R.id.tv_ri_replycount);
					convertView.setTag(holder);
					break;
				case 2:
					holder = new ChildViewHolder();
					convertView = inflater.inflate(R.layout.item_replayitem2_list, null);
					holder.tv_title = (TextView) convertView.findViewById(R.id.tv_ri_title);
					holder.iv_imagepath = (ImageView) convertView.findViewById(R.id.iv_ri_imagepath);
					holder.iv_laba = (ImageView) convertView.findViewById(R.id.iv_ri_laba);
					holder.tv_ri_replycount = (TextView) convertView.findViewById(R.id.tv_ri_replycount);
					convertView.setTag(holder);
					break;
				}
			} else {
				switch (type) {
				case 1:
					holder = (ChildViewHolder) convertView.getTag();
					break;
				case 2:
					holder = (ChildViewHolder) convertView.getTag();
					break;
				}
			}

			switch (type) {
			case 1:
				String imageUrl = childarrays.get(childPosition).getImagepath();

				imageLoader.displayImage(imageUrl, holder.iv_imagepath, options_child, null);
				holder.tv_title.setText(childarrays.get(childPosition).getTitle());
				holder.iv_laba.setVisibility(View.VISIBLE);
				holder.tv_ri_replycount.setText(childarrays.get(childPosition).getReplaycount());
				break;
			case 2:
				String imageUrl2 = childarrays.get(childPosition).getImagepath();

				imageLoader.displayImage(imageUrl2, holder.iv_imagepath, options_child, null);
				holder.tv_title.setText(childarrays.get(childPosition).getTitle());
				holder.tv_ri_replycount.setText(childarrays.get(childPosition).getReplaycount());
				break;
			}

			return convertView;
		}

		// ----------------Group----------------//
		@Override
		public Object getGroup(int groupPosition) {
			return grouparrays.get(groupPosition);
		}

		@Override
		public long getGroupId(int groupPosition) {
			return groupPosition;
		}

		@Override
		public int getGroupCount() {
			return grouparrays == null ? 0 : grouparrays.size();
		}

		@Override
		public int getGroupType(int groupPosition) {
			if (groupPosition == grouptag) {
				return 1;
			} else {
				return 2;
			}
		}

		@Override
		public int getGroupTypeCount() {
			return 3;
		}

		@Override
		public View getGroupView(final int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
			int type = getGroupType(groupPosition);
			GroupViewHolder holder = null;
			if (convertView == null) {
				switch (type) {
				case 1:
					holder = new GroupViewHolder();
					convertView = inflater.inflate(R.layout.item_replay_list, null);
					holder.tv_typename = (TextView) convertView.findViewById(R.id.tv_cr_typename);
					holder.tv_livetime = (TextView) convertView.findViewById(R.id.tv_cr_livetime);
					holder.tv_djname = (TextView) convertView.findViewById(R.id.tv_cr_djname);
					holder.iv_imagepath = (ImageView) convertView.findViewById(R.id.iv_cr_imagepath);
					holder.btn_hud = (Button) convertView.findViewById(R.id.btn_cr_hud);
					holder.iv_laba = (ImageView) convertView.findViewById(R.id.iv_cr_laba);
					holder.iv_cr_arrow = (ImageView) convertView.findViewById(R.id.iv_cr_arrow);
					convertView.setTag(holder);
					break;
				case 2:
					holder = new GroupViewHolder();
					convertView = inflater.inflate(R.layout.item_replay_list, null);
					holder.tv_typename = (TextView) convertView.findViewById(R.id.tv_cr_typename);
					holder.tv_livetime = (TextView) convertView.findViewById(R.id.tv_cr_livetime);
					holder.tv_djname = (TextView) convertView.findViewById(R.id.tv_cr_djname);
					holder.iv_imagepath = (ImageView) convertView.findViewById(R.id.iv_cr_imagepath);
					holder.btn_hud = (Button) convertView.findViewById(R.id.btn_cr_hud);
					holder.iv_laba = (ImageView) convertView.findViewById(R.id.iv_cr_laba);
					holder.iv_cr_arrow = (ImageView) convertView.findViewById(R.id.iv_cr_arrow);
					convertView.setTag(holder);
					break;
				}
			} else {
				switch (type) {
				case 1:
					holder = (GroupViewHolder) convertView.getTag();
					break;
				case 2:
					holder = (GroupViewHolder) convertView.getTag();
					break;
				}
			}

			switch (type) {
			case 1:
				String imageUrl = grouparrays.get(groupPosition).getImagepath();
				imageLoader.displayImage(imageUrl, holder.iv_imagepath, options, null);
				holder.tv_typename.setText(grouparrays.get(groupPosition).getTypename());
				holder.tv_livetime.setText(grouparrays.get(groupPosition).getLivetime());
				holder.tv_djname.setText(grouparrays.get(groupPosition).getDjname());
				holder.iv_laba.setVisibility(View.VISIBLE);

				holder.btn_hud.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						Intent intent = new Intent(ReplayListActivity.this, RadioCommentListActivity.class);
						intent.putExtra("pgname", grouparrays.get(groupPosition).getTypename());
						intent.putExtra("image", grouparrays.get(groupPosition).getImagepath());
						intent.putExtra("djname", grouparrays.get(groupPosition).getDjname());
						startActivity(intent);
					}
				});
				break;
			case 2:
				String imageUrl2 = grouparrays.get(groupPosition).getImagepath();
				imageLoader.displayImage(imageUrl2, holder.iv_imagepath, options, null);
				holder.tv_typename.setText(grouparrays.get(groupPosition).getTypename());
				holder.tv_livetime.setText(grouparrays.get(groupPosition).getLivetime());
				holder.tv_djname.setText(grouparrays.get(groupPosition).getDjname());

				holder.btn_hud.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						Intent intent = new Intent(ReplayListActivity.this, RadioCommentListActivity.class);
						intent.putExtra("pgname", grouparrays.get(groupPosition).getTypename());
						intent.putExtra("image", grouparrays.get(groupPosition).getImagepath());
						intent.putExtra("djname", grouparrays.get(groupPosition).getDjname());
						startActivity(intent);
					}
				});
				break;
			}
			return convertView;
		}

		private class GroupViewHolder {
			TextView tv_typename;
			TextView tv_livetime;
			TextView tv_djname;
			ImageView iv_imagepath;
			Button btn_hud;
			ImageView iv_laba;
			ImageView iv_cr_arrow;
		}

		private class ChildViewHolder {
			TextView tv_title;
			TextView tv_ri_replycount;
			ImageView iv_imagepath;
			ImageView iv_laba;
		}

		@Override
		public boolean hasStableIds() {
			return false;
		}

		@Override
		public boolean isChildSelectable(int groupPosition, int childPosition) {
			return true;
		}

	}

	@Override
	public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
		Intent intent_item = new Intent(this, ReplayActivity.class);
		// 标记正在播放的节目，点播室不停止播放
		if (grouptag == groupPosition && childtag == childPosition) {
			intent_item.putExtra("isplaying", true);
		} else {
			intent_item.putExtra("isplaying", false);
		}
		intent_item.putExtra("id", hdreplaytypeid);
		intent_item.putExtra("pgname", grouparrays.get(groupPosition).getTypename());
		intent_item.putExtra("imagebackground", grouparrays.get(groupPosition).getImagebackground());
		intent_item.putExtra("djname", grouparrays.get(groupPosition).getDjname());
		intent_item.putExtra("image", grouparrays.get(groupPosition).getImagepath());
		intent_item.putExtra("voicepath", childarrays.get(childPosition).getVoicepath());
		intent_item.putExtra("title", childarrays.get(childPosition).getTitle());
		intent_item.putExtra("hdreplayid", childarrays.get(childPosition).getHdreplayid());
		intent_item.putExtra("imagepath", childarrays.get(childPosition).getImagepath());
		startActivityForResult(intent_item, REPLAYRADIO);
		Editor editor = sp.edit();
		editor.putInt("grouptag", groupPosition);
		editor.putInt("childtag", childPosition);
		editor.commit();
		return super.onChildClick(parent, v, groupPosition, childPosition, id);
	}

	// 列表收缩
	@Override
	public void onGroupCollapse(int groupPosition) {

		super.onGroupCollapse(groupPosition);
	}

	// 列表展开
	@Override
	public void onGroupExpand(int groupPosition) {
		if (grouparrays != null) {
			hdreplaytypeid = grouparrays.get(groupPosition).getHdreplaytypeid();
		}
		if (hdreplaytypeid != null) {
			sql_childarrays = mgr.queryReplayitemById(hdreplaytypeid);
			if (sql_childarrays.size() == 0) { // 当数据库没有数据时，从网络更新并插入数据库
				initChildArrays(new Handler() {
					@Override
					public void handleMessage(Message msg) {
						childarrays = (List<ReplaysItemData>) msg.obj;
						if (childarrays != null) {
							mgr.addReplayitem(childarrays);
						} else {
							// showMsg(getResources().getString(R.string.response_fail),
							// "info", "bottom");
						}
						adapter.notifyDataSetChanged();
						dialog.dismiss();
						// pb_re.setVisibility(View.GONE);
					}
				});
			} else { // 查找数据库显示历史数据，然后再从网络更新并插入数据库
				childarrays = sql_childarrays;
				adapter.notifyDataSetChanged();
				dialog.dismiss();
				// pb_re.setVisibility(View.GONE);
				initChildArrays(new Handler() {
					@Override
					public void handleMessage(Message msg) {

						childarrays = (List<ReplaysItemData>) msg.obj;
						if (childarrays == null) { // 网络更新失败时，还是显示数据库的历史数据
							childarrays = sql_childarrays;
						} else { // 网络更新数据成功，更新数据库的数据
							mgr.deleteReplayitemById(hdreplaytypeid);
							mgr.addReplayitem(childarrays);
						}
						adapter.notifyDataSetChanged();
						dialog.dismiss();
						// pb_re.setVisibility(View.GONE);
					}
				});
			}
		}
		for (int i = 0; i < adapter.getGroupCount(); i++) {
			if (groupPosition != i && getExpandableListView().isGroupExpanded(groupPosition)) {
				getExpandableListView().collapseGroup(i);
			}
		}
		super.onGroupExpand(groupPosition);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REPLAYRADIO && resultCode == Activity.RESULT_OK) {
			setResult(Activity.RESULT_OK);
			// 更新喇叭显示的位置
			grouptag = sp.getInt("grouptag", -1);
			childtag = sp.getInt("childtag", -1);
			adapter.notifyDataSetChanged();
		}
		if (requestCode == REPLAYRADIO && resultCode == Activity.RESULT_CANCELED) {
			initChildArrays(new Handler() {
				@Override
				public void handleMessage(Message msg) {
					childarrays = (List<ReplaysItemData>) msg.obj;
					if (childarrays != null) {
						mgr.addReplayitem(childarrays);
					} else {
					}
					adapter.notifyDataSetChanged();
					dialog.dismiss();
				}
			});
		}
		super.onActivityResult(requestCode, resultCode, data);
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

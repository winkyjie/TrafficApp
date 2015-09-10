package cn.fszt.trafficapp.adapter;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

import android.content.Context;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import cn.fszt.trafficapp.R;
import cn.fszt.trafficapp.domain.RadioCommentListData;
import cn.fszt.trafficapp.domain.RadioCommentListReplydetailData;
import cn.fszt.trafficapp.widget.expression.ExpressionUtil;
import cn.fszt.trafficapp.widget.photoview.DisplayImageOptionsUtil;

/**
 * 节目互动评论列表，显示最新3条回复
 * @author AeiouKong
 *
 */
public class RadioCommentListReplyAdapter extends BaseAdapter {
	
	private LayoutInflater inflater; // 视图容器
	private List<RadioCommentListReplydetailData> replydetailList;
	private ImageLoader imageLoader;
	private DisplayImageOptions options_head;
	private String zhengze;
	private int densityDpi;
	private Context context;
	
	public RadioCommentListReplyAdapter(){
		
	}
	
	public RadioCommentListReplyAdapter(Context context,String zhengze,int densityDpi) {
		inflater = LayoutInflater.from(context);
		imageLoader = ImageLoader.getInstance();
		options_head = DisplayImageOptionsUtil.getOptions(
				R.drawable.default_head_sq, R.drawable.default_head_sq,
				R.drawable.default_image, new RoundedBitmapDisplayer(90));
		this.zhengze = zhengze;
		this.densityDpi = densityDpi;
		this.context = context;
	}
	
	public void setReplydetailList(List<RadioCommentListData> arrays,int position){
		replydetailList = string2ReplydetailData(arrays,position);
	}

	public int getCount() {
		return replydetailList == null? 0 : replydetailList.size();
	}

	public Object getItem(int arg0) {

		return null;
	}

	public long getItemId(int arg0) {

		return 0;
	}

	/**
	 * ListView Item设置
	 */
	public View getView(int position, View convertView, ViewGroup parent) {
		
		ViewHolder holder = null;
		String content = null;
		String nickname = null;
		String headimg = null;
		
		if(replydetailList!=null){
			content = replydetailList.get(position).getReply_content();
			headimg = replydetailList.get(position).getReply_headimg();
			nickname = replydetailList.get(position).getReply_nickname();
		}
		
		if (convertView == null) {
			
			convertView = inflater.inflate(R.layout.item_sharelist_reply,
					parent, false);
			holder = new ViewHolder();
			holder.iv_head = (ImageView) convertView.findViewById(R.id.iv_head);
			holder.tv_nickname = (TextView) convertView.findViewById(R.id.tv_nickname);
			holder.tv_content = (TextView) convertView.findViewById(R.id.tv_content);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		if(headimg!=null){
			imageLoader.displayImage(headimg, holder.iv_head, options_head);
		}
		if(nickname!=null){
			holder.tv_nickname.setText(nickname);
		}
		if(content!=null){
			SpannableString spannableString = ExpressionUtil.getExpressionString(
					context, content, zhengze, densityDpi);
			holder.tv_content.setText(spannableString);
		}
		
		return convertView;
	}

	static class ViewHolder {
		private ImageView iv_head;
		private TextView tv_nickname;
		private TextView tv_content;
	}
	
	private List<RadioCommentListReplydetailData> string2ReplydetailData(List<RadioCommentListData> arrays,int position){
		List<RadioCommentListReplydetailData> replydetailList = new ArrayList<RadioCommentListReplydetailData>();
		Gson gson = new Gson();
		RadioCommentListData b = arrays.get(position);
    	List replydetail = b.getReplydetail();
    	//3条回复
    	String s = gson.toJson(replydetail);
    	String replycount = b.getReplycount();
    	if(!"0".equals(replycount)){
    		List<RadioCommentListReplydetailData> ReplydetailList = gson.fromJson(s,  
                    new TypeToken<List<RadioCommentListReplydetailData>>() {  
                    }.getType()); 
    		replydetailList = ReplydetailList;
    	}
		return replydetailList;
	}

}

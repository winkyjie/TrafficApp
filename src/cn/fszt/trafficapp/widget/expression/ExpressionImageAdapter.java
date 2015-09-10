package cn.fszt.trafficapp.widget.expression;

import java.util.List;

import cn.fszt.trafficapp.R;
import cn.fszt.trafficapp.domain.ExpressionData;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
/**
 * 表情列表
 * @author daobo.yuan
 *
 */
public class ExpressionImageAdapter extends BaseAdapter {
	List<ExpressionData> expressionList;
	Context context;
	LayoutInflater lfInflater;

	public ExpressionImageAdapter(Context context,List<ExpressionData> expressionList) {
		this.context = context;
		this.expressionList = expressionList;
		lfInflater = LayoutInflater.from(this.context);
	}

	public int getCount() {
		// TODO Auto-generated method stub
		return expressionList.size();
	}

	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return expressionList.get(position);
	}

	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}
	
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder holder = null;
		ExpressionData exp = expressionList.get(position);
		if (convertView == null) {
			convertView = lfInflater.inflate(R.layout.expression_list_item, null);
			holder = new ViewHolder();
			holder.iv_id = (ImageView)convertView.findViewById(R.id.iv_id);
			holder.tv_id = (TextView)convertView.findViewById(R.id.tv_id);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		//删除
		if(exp.getDrableId()==-1){
			holder.iv_id.setBackgroundResource(R.drawable.chat_del);
		}else{
			holder.iv_id.setBackgroundResource(exp.getDrableId());
		}
		holder.tv_id.setText(exp.getCode());
		return convertView;
	}
	
	class ViewHolder{
		ImageView iv_id;
		TextView tv_id;
	}
}
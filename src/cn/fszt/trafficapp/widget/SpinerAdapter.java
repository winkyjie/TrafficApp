package cn.fszt.trafficapp.widget;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import cn.fszt.trafficapp.R;

public class SpinerAdapter extends BaseAdapter {

	public static interface IOnItemSelectListener{
		public void onItemClick(int pos);
	};
	
	 private Context mContext;   
	 private List<String> list = new ArrayList<String>();
	 private int mSelectItem = 0;
	    
	 private LayoutInflater mInflater;
	
	 public  SpinerAdapter(Context context){
		 init(context);
	 }
	 
	 public void refreshData(List<String> list2, int selIndex){
		 this.list = list2;
		 if (selIndex < 0){
			 selIndex = 0;
		 }
		 if (selIndex >= list2.size()){
			 selIndex = list2.size() - 1;
		 }
		 
		 mSelectItem = selIndex;
	 }
	 
	 private void init(Context context) {
	        mContext = context;
	        mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	 }
	    
	    
	@Override
	public int getCount() {

		return list.size();
	}

	@Override
	public Object getItem(int pos) {
		return list.get(pos).toString();
	}

	@Override
	public long getItemId(int pos) {
		return pos;
	}

	@Override
	public View getView(int pos, View convertView, ViewGroup arg2) {
		 ViewHolder viewHolder;
    	 
	     if (convertView == null) {
	    	 convertView = mInflater.inflate(R.layout.spinner_item, null);
	         viewHolder = new ViewHolder();
	         viewHolder.mTextView = (TextView) convertView.findViewById(R.id.item_text);
	         convertView.setTag(viewHolder);
	     } else {
	         viewHolder = (ViewHolder) convertView.getTag();
	     }

	     
	     Object item =  getItem(pos);
		 viewHolder.mTextView.setText(item.toString());

	     return convertView;
	}

	public static class ViewHolder
	{
	    public TextView mTextView;
    }


}

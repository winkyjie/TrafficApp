package cn.fszt.trafficapp.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import cn.fszt.trafficapp.R;
import cn.fszt.trafficapp.domain.AudioListData;
import cn.fszt.trafficapp.widget.photoview.DisplayImageOptionsUtil;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

public class AudioListAdapter extends BaseAdapter {
	ImageLoader imageLoader;
	private DisplayImageOptions options;
	List<AudioListData> myAudios;
	LayoutInflater inflater;

	public AudioListAdapter(Context context, List<AudioListData> myAudios) {
		this.myAudios = myAudios;
		this.inflater = LayoutInflater.from(context);
		imageLoader = ImageLoader.getInstance();
		options = DisplayImageOptionsUtil.getOptions(R.drawable.ic_empty,
				R.drawable.ic_error, R.drawable.default_image);
	}

	@Override
	public int getCount() {
		return myAudios.size();
	}

	@Override
	public Object getItem(int position) {
		return myAudios.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View contentView, ViewGroup parent) {
		ViewHolder holder;
		if (contentView == null) {
			holder = new ViewHolder();
			contentView = inflater.inflate(R.layout.main_item, null);
			holder.typename = (TextView) contentView
					.findViewById(R.id.typename);
			holder.typedesc = (TextView) contentView
					.findViewById(R.id.typedesc);
			holder.image = (ImageView) contentView.findViewById(R.id.image);
			contentView.setTag(holder);
		} else {
			holder = (ViewHolder) contentView.getTag();
		}
		holder.typename.setText(myAudios.get(position).getTypename());
		holder.typedesc.setText(myAudios.get(position).getTypedesc());
		imageLoader.displayImage(myAudios.get(position).getImagepath(),
				holder.image, options);
		return contentView;
	}

	public class ViewHolder {
		ImageView image;
		TextView typename;
		TextView typedesc;
	}
}

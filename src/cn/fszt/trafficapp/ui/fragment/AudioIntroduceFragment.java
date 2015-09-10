package cn.fszt.trafficapp.ui.fragment;
import cn.fszt.trafficapp.R;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * 音频简介
 * @author AeiouKong
 *
 */
public class AudioIntroduceFragment extends Fragment {
	private TextView descTextView;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view=inflater.inflate(R.layout.tab_audiointroduce, container, false);
		Intent intent=getActivity().getIntent();
		descTextView=(TextView) view.findViewById(R.id.descTextView);
		descTextView.setText(intent.getStringExtra("desc"));
		return view;
	}
}

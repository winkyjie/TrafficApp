
package cn.fszt.trafficapp.widget.bargraph;

import java.lang.reflect.Type;
import java.util.ArrayList;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import cn.fszt.trafficapp.R;
import cn.fszt.trafficapp.util.HttpUtil;

public class BarFragment extends Fragment {
	ArrayList<BarBean> bbs;
	ArrayList<Bar> points;
	private String request_url,getsurvey_url;
	private String hdvoteid,hdsurveyvoteid;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Bundle args = getArguments();  
        if (args != null) {  
        	hdvoteid = args.getString("hdvoteid");  
        	hdsurveyvoteid = args.getString("hdsurveyvoteid");  
        }
	}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		final View v = inflater.inflate(R.layout.fragment_bargraph, container, false);
		points = new ArrayList<Bar>();
		bbs = new ArrayList<BarBean>();
		request_url = getResources().getString(R.string.server_url)+getResources().getString(R.string.getvote_url);
		getsurvey_url = getResources().getString(R.string.server_url)+getResources().getString(R.string.getsurvey_url);
		initArrays(new Handler(){
           	@Override
           	public void handleMessage(Message msg) {
           		
           		bbs = (ArrayList<BarBean>) msg.obj;
           		if(bbs==null){  
           		}else{             
           			for(int i=0;i<bbs.size();i++){
           				
           				Bar d = new Bar();
           				d.setColor(0x99CC00);
           				d.setName(bbs.get(i).getOptioncontent());
           				d.setValue(Integer.parseInt(bbs.get(i).getOptioncount()));
           			
           				points.add(d);
           			}
           			
           			BarGraph g = (BarGraph)v.findViewById(R.id.bargraph);
           			g.setShowBarText(false);
           			g.setBars(points);
           		}
           		
           	}
           });
		return v;
	}
	
	private ArrayList<BarBean> getDataFromNetwork() throws Exception{
		ArrayList<BarBean> lst = new ArrayList<BarBean>();
		String resultString = null;
		if(hdsurveyvoteid==null&&hdvoteid!=null){
			resultString = HttpUtil.GetStringFromUrl(request_url+"&hdvoteid="+hdvoteid);
		}else if(hdsurveyvoteid!=null&&hdvoteid==null){
			resultString = HttpUtil.GetStringFromUrl(getsurvey_url+"&hdsurveyvoteid="+hdsurveyvoteid);
		}
		
		if(resultString != null){
			Type listType = new TypeToken<ArrayList<BarBean>>(){}.getType();
	        Gson gson = new Gson();
	        lst = gson.fromJson(resultString, listType);
	        return lst;
		}else{
			return null;
		}
	}
	
	/**
	 * 初始化数据
	 * @param handler
	 */
	private void initArrays(final Handler handler) {
		new Thread(new Runnable() {
			ArrayList<BarBean> lst = new ArrayList<BarBean>();
			@Override
			public void run() {
				
					try {  
						lst = getDataFromNetwork();
				} catch (Exception e) {
					e.printStackTrace();
				}
					handler.obtainMessage(0, lst).sendToTarget();
			}
		}).start();
	}
}

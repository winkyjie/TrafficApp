package cn.fszt.trafficapp.widget.viewflow;

import cn.fszt.trafficapp.widget.viewflow.ViewFlow.ViewSwitchListener;


public interface FlowIndicator extends ViewSwitchListener {

	public void setViewFlow(ViewFlow view);
	
	public void onScrolled(int h, int v, int oldh, int oldv);
}

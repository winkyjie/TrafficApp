package cn.fszt.trafficapp.widget;


import cn.fszt.trafficapp.R;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;


public class ImageLoadingDialog extends Dialog {

	public ImageLoadingDialog(Context context) {
		super(context, R.style.ImageloadingDialogStyle);
		//setOwnerActivity((Activity) context);// 设置dialog全屏显示
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_imageloading);
	}

}

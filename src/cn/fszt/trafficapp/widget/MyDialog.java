package cn.fszt.trafficapp.widget;

import java.util.HashMap;
import java.util.Map;

import cn.fszt.trafficapp.R;



import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * 自定义对话框
 * @author Aeiou
 *
 */
public class MyDialog {
	private Dialog dialog; 
	private Map<Object, Button> btns = new HashMap<Object, Button>();
	private Map<Object, Object> text = new HashMap<Object, Object>();
	
	/**
	 * 显示一个对话框
	 * @param titleText 标题
	 * @param contentText 内容
	 * @param left 左边的按钮
	 * @param right 右边的按钮
	 * @param force 是否要给右边的按钮设置监听器
	 */
	public static MyDialog showDialog(Context context, Object titleText, Object contentText, 
			Object leftText, Object rightText, boolean force) {
		final MyDialog myDialog = new MyDialog();
		
		LinearLayout dialogView = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.dialog, null);
		
		TextView title = (TextView) dialogView.findViewById(R.id.title);
		title.setText(parseParam(context, titleText));
		
		myDialog.text.put("title", titleText);
		
		TextView cont = (TextView) dialogView.findViewById(R.id.content);
		if(contentText instanceof View) { 
			LinearLayout contLayout = (LinearLayout) cont.getParent();
			contLayout.removeView(cont); 
			contLayout.addView((View) contentText); 
		} else { 
			cont.setText(parseParam(context, contentText));
		}
		
		myDialog.text.put("content", contentText);
		
		Button left = (Button) dialogView.findViewById(R.id.left);
		if(leftText == null) { 
			View btnsLayout = (View) left.getParent().getParent();
			dialogView.removeView(btnsLayout);
		} else {
			left.setText(parseParam(context, leftText));
			if(context instanceof View.OnClickListener) {
				left.setOnClickListener((OnClickListener) context);
			}
			myDialog.btns.put(leftText, left); 
			
			Button right = (Button) dialogView.findViewById(R.id.right);
			if(rightText == null) { 
				LinearLayout rightParent = (LinearLayout) right.getParent(); 
				LinearLayout bottomLayout = (LinearLayout)rightParent.getParent(); 
				bottomLayout.removeView(rightParent); 
			} else {
				right.setText(parseParam(context, rightText));
				
				if(force) { 
					if(context instanceof View.OnClickListener) {
						right.setOnClickListener((OnClickListener) context);
					}
				} else {
					right.setOnClickListener(new OnClickListener() {
						public void onClick(View v) {
							dismiss(myDialog); 
						}
					});
				}
				myDialog.btns.put(rightText, right); 
			}
		}
		
		Dialog dialog = new Dialog(context, R.style.Dialog);
		dialog.show(); 
		dialog.getWindow().setContentView(dialogView); 
		myDialog.dialog = dialog;
		
		return myDialog;
	}
	
	/**
	 * 显示一个对话框
	 * @param titleText 标题
	 * @param contentText 内容
	 * @param left 左边的按钮
	 * @param right 右边的按钮
	 * @param force 是否要给右边的按钮设置监听器
	 */
	public static MyDialog showDialog(Context context, Object titleText, Object contentText, 
			Object leftText, Object rightText) {
		return showDialog(context, titleText, contentText, leftText, rightText, false);
	}
	
	/**
	 * 只显示一个按钮
	 */
	public static MyDialog showDialog(Context context, Object titleText, Object contentText, Object leftText) {
		return showDialog(context, titleText, contentText, leftText, null);
	}
	
	/**
	 * 不要按钮
	 */
	public static MyDialog showDialog(Context context, Object titleText, Object contentText) {
		return showDialog(context, titleText, contentText, null, null);
	}
	
	/**
	 * 解析参数，返回R.string对应字符串内容
	 * @param msg
	 * @return
	 */
	private static String parseParam(Context context, Object msg) {
		if(msg instanceof Integer) { 
			int resId = Integer.parseInt(msg.toString());
			msg = context.getString(resId);
		}
		return msg.toString();
	}
	
	/**
	 * 得到相应的按钮
	 * @param myDialog 所属的对话框
	 * @param key 按钮的key
	 * @return
	 */
	public static Button getBtn(MyDialog myDialog, Object key) {
		if(myDialog == null) {
			return null;
		} else {
			return myDialog.btns.get(key);
		}
	}
	
	public static Object getText(MyDialog myDialog,Object key){
		if(myDialog == null) {
			return null;
		} else {
			return myDialog.text.get(key);
		}
	}
	
	/**
	 * 关闭对话框
	 * @param myDialog
	 */
	public static void dismiss(MyDialog myDialog) {
		if(myDialog != null && myDialog.dialog!=null && myDialog.dialog.isShowing()) {
			myDialog.dialog.dismiss();
		}
	}

	public Dialog getDialog() {
		return dialog;
	}

	public void setDialog(Dialog dialog) {
		this.dialog = dialog;
	}

	public Map<Object, Button> getBtns() {
		return btns;
	}

	public void setBtns(Map<Object, Button> btns) {
		this.btns = btns;
	}
	
	public Map<Object, Object> getText() {
		return text;
	}
	
	
}

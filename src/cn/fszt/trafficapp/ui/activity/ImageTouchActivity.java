package cn.fszt.trafficapp.ui.activity;

import cn.fszt.trafficapp.R;
import cn.fszt.trafficapp.util.PhotoUtil;
import cn.fszt.trafficapp.widget.ImageLoadingDialog;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.Toast;

/**
 * 点击图片放大
 * 
 * @author AeiouKong
 *
 */
public class ImageTouchActivity extends Activity implements OnTouchListener {
	private ImageLoadingDialog dialog;
	Matrix matrix = new Matrix();
	Matrix saveMatrix = new Matrix();
	DisplayMetrics dm;
	ImageView imageView;
	Bitmap bitmap;
	float minScaleR;// 最小缩放比例
	static final float MAX_SCALE = 6f;// 最大缩放比例
	static final int NONE = 0;
	static final int DRAG = 1;
	static final int ZOOM = 2;
	int mode = NONE;
	PointF prev = new PointF();
	PointF mid = new PointF();
	float dist = 1f;
	private boolean keyUpDown = false;
	private int timer = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.imagetouch);
		imageView = (ImageView) findViewById(R.id.imag);
		dialog = new ImageLoadingDialog(this);
		dialog.setCanceledOnTouchOutside(false);
		dialog.show();

		new Thread() {
			public void run() {
				Intent intent = getIntent();
				String imgurl = intent.getStringExtra("imgurl");
				if (imgurl != null) { // 一般为评论里面的图片，也可以是非默认的头像
					bitmap = PhotoUtil.getbitmapAndwrite(imgurl);
				} else { // 一般为默认的头像
					int imgid = intent.getIntExtra("imgid",
							R.drawable.default_head);
					bitmap = BitmapFactory
							.decodeResource(getResources(), imgid);
				}
				handler.sendEmptyMessage(1);
			}
		}.start();
		imageView.setOnTouchListener(this);
		dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);

	}

	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			if (msg.what == 1) {
				dialog.dismiss();
				if (bitmap != null) {
					imageView.setImageBitmap(bitmap);
					minZoom();
					CheckView();
				} else {
					Toast.makeText(ImageTouchActivity.this,
							getResources().getString(R.string.imagetouch_fail),
							Toast.LENGTH_SHORT).show();
				}
				imageView.setImageMatrix(matrix);
			}
		}
	};

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		switch (event.getAction() & MotionEvent.ACTION_MASK) {
		case MotionEvent.ACTION_DOWN: // 主点按下
			saveMatrix.set(matrix);
			prev.set(event.getX(), event.getY());
			clickHandler.sendEmptyMessage(0);
			mode = DRAG;
			break;

		case MotionEvent.ACTION_POINTER_DOWN: // 副点按下
			dist = spacing(event);
			if (dist > 10f) {
				saveMatrix.set(matrix);
				midPoint(mid, event);
				mode = ZOOM;
			}
			break;

		case MotionEvent.ACTION_UP:
			clickHandler.sendEmptyMessage(1);
		case MotionEvent.ACTION_POINTER_UP:
			mode = NONE;
			break;

		case MotionEvent.ACTION_MOVE:
			if (mode == DRAG) {
				matrix.set(saveMatrix);
				matrix.postTranslate(event.getX() - prev.x, event.getY()
						- prev.y);
			} else if (mode == ZOOM) {
				float newDist = spacing(event);
				if (newDist > 10f) {
					matrix.set(saveMatrix);
					float tScale = newDist / dist;
					matrix.postScale(tScale, tScale, mid.x, mid.y);
				}
			}
			break;
		}
		imageView.setImageMatrix(matrix);
		if (bitmap != null) {
			CheckView();
		}
		return true;
	}

	/**
	 * 限制最大最小缩放比例，自动居中
	 */
	private void CheckView() {
		float p[] = new float[9];
		matrix.getValues(p);
		if (p[0] < minScaleR) {
			matrix.setScale(minScaleR, minScaleR);
		}
		if (p[0] > MAX_SCALE) {
			matrix.set(saveMatrix);
		}
		center();
	}

	/**
	 * 最小缩放比例，最大为100%
	 */
	private void minZoom() {
		minScaleR = Math.min(
				(float) dm.widthPixels / (float) bitmap.getWidth(),
				(float) dm.heightPixels / (float) bitmap.getHeight());
		if (minScaleR < 1.0) {
			matrix.postScale(minScaleR, minScaleR);
		}
	}

	private void center() {
		center(true, true);
	}

	/**
	 * 横向、纵向居中
	 * 
	 * @param horizontal
	 * @param vertical
	 */
	protected void center(boolean horizontal, boolean vertical) {
		Matrix m = new Matrix();
		m.set(matrix);
		RectF rect = new RectF(0, 0, bitmap.getWidth(), bitmap.getHeight());
		m.mapRect(rect);
		float height = rect.height();
		float width = rect.width();
		float deltaX = 0, deltaY = 0;
		if (vertical) {
			// 图片小于屏幕大小，则居中显示，大于屏幕，上方留空则往上移，下方留空则往下移
			int screenHeight = dm.heightPixels;
			if (height < screenHeight) {
				deltaY = (screenHeight - height) / 2 - rect.top;
			} else if (rect.top > 0) {
				deltaY = -rect.top;
			} else if (rect.bottom < screenHeight) {
				deltaY = imageView.getHeight() - rect.bottom;
			}
		}
		if (horizontal) {
			int screenWidth = dm.widthPixels;
			if (width < screenWidth) {
				deltaX = (screenWidth - width) / 2 - rect.left;
			} else if (rect.left > 0) {
				deltaX = -rect.left;
			} else if (rect.right < screenWidth) {
				deltaX = screenWidth - rect.right;
			}
		}
		matrix.postTranslate(deltaX, deltaY);
	}

	/**
	 * 两点的距离
	 */
	private float spacing(MotionEvent event) {
		float x = event.getX(0) - event.getX(1);
		float y = event.getY(0) - event.getY(1);
		return (float) Math.sqrt(x * x + y * y);
	}

	/**
	 * 两点的中点
	 */
	private void midPoint(PointF point, MotionEvent event) {
		float x = event.getX(0) + event.getX(1);
		float y = event.getY(0) + event.getY(1);
		point.set(x / 2, y / 2);
	}

	private Handler clickHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == 0) {
				keyUpDown = true;
				keyUpDownListener();
			} else if (msg.what == 1) {
				keyUpDown = false;
				if (timer <= 1) {
					finish();
					overridePendingTransition(0, R.anim.zoom_exit);
				} else
					timer = 0;
			}
		}
	};

	private int keyUpDownListener() {
		new Thread() {
			public void run() {
				while (keyUpDown) {
					try {
						sleep(100);
						timer++;
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}.start();
		return timer;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// 如果按下的是返回键，并且没有重复
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			finish();
			overridePendingTransition(0, R.anim.zoom_exit);
			return false;
		}
		return false;
	}

}

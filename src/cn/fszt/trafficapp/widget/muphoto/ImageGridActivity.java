package cn.fszt.trafficapp.widget.muphoto;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import cn.fszt.trafficapp.R;
import cn.fszt.trafficapp.util.PhotoUtil;
import cn.fszt.trafficapp.widget.muphoto.ImageGridAdapter.TextCallback;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

/**
 * 相册中选择图片页面
 * 
 * @author AeiouKong
 *
 */
public class ImageGridActivity extends Activity {
	public static final String EXTRA_IMAGE_LIST = "imagelist";

	private List<ImageItem> dataList;
	private GridView gridView;
	private ImageGridAdapter adapter;//
	private AlbumHelper helper;

	private MenuItem item_photo_finish;
	private String _imageDir;

	Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				Toast.makeText(ImageGridActivity.this, "只能选择" + Bimp.limit + "张图片", Toast.LENGTH_SHORT).show();
				break;

			default:
				break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_image_grid);

		_imageDir = Environment.getExternalStorageDirectory().getAbsolutePath()
				+ getResources().getString(R.string.phototemp_dir);

		File imageDir = new File(_imageDir);
		if (!imageDir.exists()) {
			imageDir.mkdirs();
		}

		helper = AlbumHelper.getHelper();
		helper.init(getApplicationContext());

		dataList = (List<ImageItem>) getIntent().getSerializableExtra(EXTRA_IMAGE_LIST);
		if (dataList != null && dataList.size() > 0) {
			Collections.reverse(dataList);
		}
		getActionBar().setTitle(getResources().getString(R.string.photo));
		getActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.titlebar)));
		getActionBar().setDisplayHomeAsUpEnabled(true);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		MenuInflater inflater = getMenuInflater();

		inflater.inflate(R.menu.imagegridactivity, menu);

		item_photo_finish = menu.findItem(R.id.item_photo_finish);

		item_photo_finish.setTitle(
				getResources().getString(R.string.photo_submit) + "(0/" + (Bimp.limit - Bimp.drr.size()) + ")");

		gridView = (GridView) findViewById(R.id.gridview);
		gridView.setSelector(new ColorDrawable(Color.TRANSPARENT));
		adapter = new ImageGridAdapter(ImageGridActivity.this, dataList, mHandler);
		gridView.setAdapter(adapter);
		adapter.setTextCallback(new TextCallback() {
			public void onListen(int count) {
				item_photo_finish.setTitle(getResources().getString(R.string.photo_submit) + "(" + count + "/"
						+ (Bimp.limit - Bimp.drr.size()) + ")");
			}
		});

		gridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				adapter.notifyDataSetChanged();
			}

		});

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			break;

		case R.id.item_photo_finish:
			List<String> list = new ArrayList<String>();
			// Collection<String> c = adapter.map.values();
			list = adapter.list;
//			Iterator<String> it = c.iterator();
//			for (; it.hasNext();) {
//				list.add(it.next());
//			}

			if (Bimp.act_bool) {
				Bimp.act_bool = false;
			}
			for (int i = 0; i < list.size(); i++) {
				if (Bimp.drr.size() < Bimp.limit) {
					int degree = PhotoUtil.readPictureDegree(list.get(i));
					Bitmap bm = PhotoUtil.createThumbFromFile(list.get(i), 600, 600);
					if (bm != null) {
						bm = PhotoUtil.rotaingImageView(degree, bm);

						String rotaingpaths = _imageDir + String.valueOf(System.currentTimeMillis()) + ".jpg";
						boolean df = PhotoUtil.saveBitmap2file(bm, rotaingpaths);
						if (df) {
							Bimp.drr.add(rotaingpaths);
						}
					}
				}
			}
			finish();
			break;
		}
		return true;
	}
}

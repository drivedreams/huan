package com.mycreat.huan;

import java.util.ArrayList;

import com.mycreat.huan.adapters.PathsAdapter;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

/**
 * 
 * @author zhanghaihai(drivedreams@163.com)
 * 
 */
public class PhotoSelector extends Activity {
	static final int REQUEST_IMAGE_CAPTURE = 1;
	static final int REQUEST_IMAGE_GALLERY = 2;

	public static final String KEY_PHOTO_PATH = "photo_path";

	// ��ȡ���
	ListView imageContainer;
	// ��ȡͼƬ·��
	private String picPath;

	private Intent lastIntent;
	private Uri photoUri;

	PathsAdapter pathsAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.layout_photo_selector);

		lastIntent = getIntent();
		initViews(this);
		super.onCreate(savedInstanceState);
	}

	/**
	 * 
	 * @param context
	 */
	private void initViews(Context context) {
		Button photoSelectBtn = (Button) findViewById(R.id.selector_btn);
		photoSelectBtn.setOnClickListener(new ButonClickListener());

		Button photoSelectExtraBtn = (Button) findViewById(R.id.selector_extra_btn);
		photoSelectExtraBtn.setOnClickListener(new ButonClickListener());

		pathsAdapter = new PathsAdapter(this, R.layout.message_item_tpl,
				new ArrayList<String>());
		System.out.println("Inialize adapter.");
		imageContainer = (ListView) findViewById(R.id.change_board_body);
		imageContainer.setAdapter(pathsAdapter);
		System.out.println("Adapter has been setted.");
	}

	/**
	 * Used to implement click action.
	 */
	class ButonClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.selector_btn:
				pickPhoto();
				break;
			case R.id.selector_extra_btn:
				takePhoto();
				break;
			}

		}
	}

	/**
	 * ���ջ�ȡͼƬ
	 */
	private void takePhoto() {
		// ִ������ǰ��Ӧ�����ж�SD���Ƿ����
		String SDState = Environment.getExternalStorageState();
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);// "android.media.action.IMAGE_CAPTURE"
		/***
		 * ��Ҫ˵��һ�£����²���ʹ����������գ����պ��ͼƬ����������е� ����ʹ�õ����ַ�ʽ��һ���ô����ǻ�ȡ��ͼƬ�����պ��ԭͼ
		 * �����ʵ��ContentValues�����Ƭ·���Ļ������պ��ȡ��ͼƬΪ����ͼ������
		 */
		ContentValues values = new ContentValues();
		if (SDState.equals(Environment.MEDIA_MOUNTED)) {
			photoUri = this.getContentResolver().insert(
					MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
			intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, photoUri);
			/** ----------------- */
			startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
		} else {
			photoUri = this.getContentResolver().insert(
					MediaStore.Images.Media.INTERNAL_CONTENT_URI, values);
			intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, photoUri);
			/** ----------------- */
			startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
		}
	}

	/***
	 * �������ȡͼƬ
	 */
	private void pickPhoto() {
		Intent intent = new Intent();
		intent.setType("image/*");
		intent.setAction(Intent.ACTION_GET_CONTENT);
		startActivityForResult(intent, REQUEST_IMAGE_GALLERY);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == Activity.RESULT_OK) {
			doPhoto(requestCode, data);
		}

	}

	/**
	 * ѡ��ͼƬ�󣬻�ȡͼƬ��·��
	 * 
	 * @param requestCode
	 * @param data
	 */
	private void doPhoto(int requestCode, Intent data) {
		if (requestCode == REQUEST_IMAGE_GALLERY) // �����ȡͼƬ����Щ�ֻ����쳣�������ע��
		{
			if (data == null) {
				Toast.makeText(this, "ѡ��ͼƬ�ļ�����", Toast.LENGTH_LONG).show();
				return;
			}
			photoUri = data.getData();
			Log.i("Pic", photoUri.getPath());
			if (photoUri == null) {
				Toast.makeText(this, "ѡ��ͼƬ�ļ�����", Toast.LENGTH_LONG).show();
				return;
			}
		}
		String[] pojo = { MediaStore.Images.Media.DATA };
		Cursor cursor = this.getContentResolver().query(photoUri, pojo, null,
				null, null);
		if (cursor != null) {
			int columnIndex = cursor.getColumnIndexOrThrow(pojo[0]);
			cursor.moveToFirst();
			picPath = cursor.getString(columnIndex);
			cursor.close();
		}
		if (picPath != null
				&& (picPath.endsWith(".png") || picPath.endsWith(".PNG")
						|| picPath.endsWith(".jpg") || picPath.endsWith(".JPG"))) {
			lastIntent.putExtra(KEY_PHOTO_PATH, picPath);
			setResult(Activity.RESULT_OK, lastIntent);
			Log.w("Pic", "Begin to add image with specified path");
			addFromMessage(picPath);
		} else {
			Toast.makeText(this, "ѡ��ͼƬ�ļ�����ȷ", Toast.LENGTH_LONG).show();
		}
	}

	private void addFromMessage(String picPath) {
		Log.w("Pic",
				"Begin to insert image to index " + pathsAdapter.getCount());
		pathsAdapter.add(picPath);
		// pathsAdapter.insert(picPath, pathsAdapter.getCount());

		// pathsAdapter.notify();
		Log.w("Pic", "An image has been inserted.");
		// pathsAdapter.notify();
	}
}

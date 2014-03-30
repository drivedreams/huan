package com.mycreat.huan;

import java.util.ArrayList;

import com.mycreat.huan.adapters.PathsAdapter;
import com.mycreat.huan.util.FileUploader;

import android.app.Activity;
import android.content.ContentResolver;
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
import android.provider.MediaStore.Images.Thumbnails;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
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

	public String serverUrl = "http://mcapi.sinaapp.com/v1/file/upload";
	// 获取组件
	ListView imageContainer;
	// 获取图片路径

	private Intent lastIntent;

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
		imageContainer = (ListView) findViewById(R.id.change_board_body);
		imageContainer.setAdapter(pathsAdapter);
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
	 * Get photo through taking photo.
	 */
	private void takePhoto() {
		//Check is there SD card.
		String SDState = Environment.getExternalStorageState();
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);// "android.media.action.IMAGE_CAPTURE"
		/***
		 * You had better to save photo into gallery for getting higher quality photo rather than thumbnail.
		 */
		ContentValues values = new ContentValues();

		Uri photoUri;

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
	 * 从相册中取图片
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
			sendMessage(getPhotoPath(requestCode, data));
		}

	}


	/**
	 * 
	 * Get the path of the photo in file storage system.
	 * 
	 * @param photoUri
	 * @return
	 */
	private String getPhotoPath(int requestCode, Intent data) {
		if (requestCode == REQUEST_IMAGE_GALLERY) {
			if (data == null) {
				alert("Data is null");
				return null;
			}
		}
		String picPath = null;
		Uri photoUri = data.getData();
		
		if (photoUri == null) {
			alert("photoUri is null");
			return null;
		}

		String[] pojo = { MediaStore.Images.Media.DATA };
		Cursor cursor = this.getContentResolver().query(photoUri, pojo, null,
				null, null);
		if (cursor != null) {

			int columnIndex = cursor.getColumnIndexOrThrow(pojo[0]);
			cursor.moveToFirst();
			picPath = cursor.getString(columnIndex);
			cursor.close();
		}else{
			alert("cursor is null");
		}
		if (isPhotoPath(picPath)) {
			lastIntent.putExtra(KEY_PHOTO_PATH, picPath);
			setResult(Activity.RESULT_OK, lastIntent);

		} else {
			alert("Not a picPath");
		}
		return picPath;
	}
	
	private String getThumbnail(int requestCode, Intent data){
		if (requestCode == REQUEST_IMAGE_GALLERY) {
			Log.w("Pic", "requestCode :" + requestCode);
			if (data == null) {
				alert(getString(R.string.error_message_selecting_photo));
				return null;
			}
		}
	
		String picPath = null;
		Uri photoUri = data.getData();
		Log.w("Pic", "photoUri path :" + photoUri.getPath());

		String[] pojo = { Thumbnails._ID, Thumbnails.IMAGE_ID,  
                Thumbnails.DATA};
		
		ContentResolver cr = getContentResolver(); ;  
		//获取指定Uri的一条数据
		Cursor cursor = cr.query(Thumbnails.EXTERNAL_CONTENT_URI, pojo, null,
				null, null);
		String colums [] = cursor.getColumnNames();
		for (String string : colums) {
			Log.w("Pic", "cursor: " + string );
		}
	
		if (cursor != null) {
			//获取IMAGE_ID在第几列
			int column = cursor.getColumnIndex(Thumbnails._ID);
			Log.w("Pic", "column = " + column);
			//获取ImageId
			int iamgeID = cursor.getInt(column);
			//关闭cursor
			cursor.close();
			
			Cursor thumbnailsCursor	= Thumbnails.queryMiniThumbnail(getContentResolver(), iamgeID, Thumbnails.MINI_KIND, null);
			picPath = thumbnailsCursor.getString(thumbnailsCursor.getColumnIndex(Thumbnails.THUMB_DATA));
			
		}
		if (isPhotoPath(picPath)) {
			lastIntent.putExtra(KEY_PHOTO_PATH, picPath);
			setResult(Activity.RESULT_OK, lastIntent);

		} else {
			alert(getString(R.string.error_message_selecting_photo));
		}
		return picPath;
	}
	//
	private boolean isPhotoPath(String picPath) {

		return picPath != null
				&& (picPath.endsWith(".png") || picPath.endsWith(".PNG")
						|| picPath.endsWith(".jpg") || picPath.endsWith(".JPG")
						|| picPath.endsWith(".bmp") || picPath.endsWith(".BMP")
						|| picPath.endsWith(".jpeg") || picPath.endsWith(".JPEG")
						|| picPath.endsWith(".gif") || picPath.endsWith(".GIF"));

	}

	private void sendMessage(String picPath) {
		Log.w("Pic", "picPath: " + picPath);
		Log.w("Pic", "Begin to upload pic");
		FileUploader.upload(serverUrl, picPath);
		Log.w("Pic", "Pic is uploaded!");
		pathsAdapter.add(picPath);
		//pathsAdapter.notify();
	}

	private void alert(String message) {
		Log.w("Pic", message);
		Toast.makeText(this, message, Toast.LENGTH_LONG).show();
	}
}

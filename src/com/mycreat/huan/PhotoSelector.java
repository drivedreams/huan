package com.mycreat.huan;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

/**
 * 
 * @author zhanghaihai(drivedreams@163.com)
 *
 */
public class PhotoSelector extends Activity {
	static final int REQUEST_IMAGE_CAPTURE = 1;
	static final int TO_SELECT_PHOTO = 2;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.layout_photo_selector);
		initializeButtons(this);
		super.onCreate(savedInstanceState);
	}

	/**
	 * 
	 * @param context
	 */
	private void initializeButtons(Context context) {
		Button photoSelectBtn = (Button)findViewById(R.id.selector_btn);
		photoSelectBtn.setOnClickListener(new ButonClickListener());
	}
	
	/**
	 * Used to implement click action.
	 */
	class ButonClickListener implements OnClickListener {
		
		@Override
		public void onClick(View v) {
			switch(v.getId()){
				case R.id.selector_btn: startPhotoSelector();
			}
			
		}

		private void startPhotoSelector() {
			Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		    if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
		        startActivityForResult(takePictureIntent, TO_SELECT_PHOTO);
		    }
			
		}
		
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
	        Bundle extras = data.getExtras();
	        Bitmap imageBitmap = (Bitmap) extras.get("data");
	        ImageView mImageView = new ImageView(this);
	        mImageView.setImageBitmap(imageBitmap);
	        LinearLayout linearLayout = (LinearLayout)findViewById(R.id.change_board_body);
	        linearLayout.addView(mImageView);
	      /*  mImageView.setImageBitmap(imageBitmap);*/
	    }
	}
}

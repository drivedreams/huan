package com.mycreat.huan.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class BitmapManager {

	public static Bitmap getCompressedPic(String itemPath, float imageWidth) {
		
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(itemPath, options);
		
		options.inJustDecodeBounds = false;
		int rate = (int)imageWidth / options.outWidth;
		options.inSampleSize = rate;
		options.inJustDecodeBounds = false;
		
		return  BitmapFactory.decodeFile(itemPath, options);
	}
}

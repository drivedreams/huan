package com.mycreat.huan.util;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;

public class DeviceInformationManager {

	//Gets the width( Unit: dp ) of the device 
	public static float getWidth(Context context){
		float width = ((Activity) context).getWindowManager()
				.getDefaultDisplay().getWidth();
		Resources resources = context.getResources();
		DisplayMetrics metrics = resources.getDisplayMetrics();
		float px = width * (metrics.densityDpi / 160f);
		return px;
	}

}

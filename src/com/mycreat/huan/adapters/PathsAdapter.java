package com.mycreat.huan.adapters;

import java.util.List;

import com.mycreat.huan.R;
import com.mycreat.huan.util.BitmapManager;
import com.mycreat.huan.util.DeviceInformationManager;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;

public class PathsAdapter extends ArrayAdapter<String> {

	private Context context;
	int resourceId;
	private LayoutInflater inflater;
	private float deviceWidth;
	public PathsAdapter(Context context, int resourceId, List<String> paths) {
		super(context, resourceId, paths);
		this.resourceId = resourceId;
		this.context = context;
		deviceWidth = DeviceInformationManager.getWidth(context);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		FrameLayout row = (FrameLayout) convertView;
		ItemHolder itemHolder;

		String itemPath = getItem(position);
		if (row == null) {
			itemHolder = new ItemHolder();
			itemHolder.position = position;
			inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			row = (FrameLayout) inflater.inflate(resourceId, parent, false);
			// row = (FrameLayout) View.inflate(context, resourceId, parent);

			Log.w("Pic", "Got row ...");

			ImageView item = (ImageView) row.findViewById(R.id.item_image);

			Log.w("Pic", "Begin to get item ...");
			itemHolder.item = item;
			row.setTag(itemHolder);
			itemHolder.item.setMinimumHeight(200);
		} else {
			itemHolder = (ItemHolder) row.getTag();
			if (itemHolder.position != position) {
				itemHolder.item.setImageDrawable(null);
			}
		}
		new ImageLoadTask(itemHolder.item).execute(itemPath);

		return row;
	}

	private static class ItemHolder {
		ImageView item;
		/**
		 * Used to remove the remained things.
		 */
		int position;
	}

	private class ImageLoadTask extends AsyncTask<String, Integer, Bitmap> {

		private ImageView itemImg;

		public ImageLoadTask(ImageView itemImg) {
			super();
			this.itemImg = itemImg;
		}

		@Override
		protected Bitmap doInBackground(String... params) {
			return BitmapManager.getCompressedPic(params[0], deviceWidth); 
		}

		@Override
		protected void onPostExecute(Bitmap result) {
			itemImg.setImageBitmap(result);
			super.onPostExecute(result);
		}

	}

	

}

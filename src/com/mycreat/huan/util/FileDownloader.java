package com.mycreat.huan.util;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import com.mycreat.huan.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class FileDownloader {

	/**
	 * 
	 * @author zhanghaihai
	 *
	 */
	public static enum REQUEST_MODE {REQUEST_MODE_POST, REQUEST_MODE_GET};
	
	
	public static FileDownloader getInstance(){
		return new FileDownloader();
	}

	/**
	 * 
	 * @param c
	 * @param url
	 * @param requestMode {@link REQUEST_MODE}
	 * @return
	 */
	public Bitmap getBitMap(Context c, String url, REQUEST_MODE requestMode) {
		URL myFileUrl = null;
		Bitmap bitmap = null;
		HttpURLConnection conn = null;
		try {
			myFileUrl = new URL(url);
		} catch (MalformedURLException e) {

			bitmap = BitmapFactory.decodeResource(c.getResources(),
					R.drawable.ic_launcher);
			return bitmap;
		}


		try {
			conn = (HttpURLConnection) myFileUrl.openConnection();
			setRequestMode(conn, requestMode);
			conn.setDoInput(true);
			conn.connect();


			InputStream is = conn.getInputStream();


			int length = (int) conn.getContentLength();
			if (length != -1) {
				byte[] imgData = new byte[length];
				byte[] temp = new byte[512];
				int readLen = 0;
				int destPos = 0;
				while ((readLen = is.read(temp)) > 0) {
					System.arraycopy(temp, 0, imgData, destPos, readLen);
					destPos += readLen;
				}

				bitmap = BitmapFactory.decodeByteArray(imgData, 0,
						imgData.length);
				imgData = null;
			}
			
		} catch (IOException e) {
			bitmap = BitmapFactory.decodeResource(c.getResources(), R.drawable.ic_launcher);
			e.printStackTrace();
		} finally {
			if (conn != null) {
				conn.disconnect();
				
			}
		}

		return bitmap;
	}

	private void setRequestMode(HttpURLConnection conn, REQUEST_MODE requestMode) throws ProtocolException {
		if(requestMode == REQUEST_MODE.REQUEST_MODE_POST){
			conn.setRequestMethod("post");
		}else{
			conn.setRequestMethod("get");
		}
	}

	/**
	 * 
	 * @param c
	 * @param url
	 * @return
	 * @throws IOException
	 */
	public synchronized Bitmap getBitMap(Context c, String url, float imageWidth, REQUEST_MODE requestMode) {
		URL myFileUrl = null;
		Bitmap bitmap = null;
		HttpURLConnection conn = null;
		try {
			myFileUrl = new URL(url);
		} catch (MalformedURLException e) {

			bitmap = BitmapFactory.decodeResource(c.getResources(),
					R.drawable.ic_launcher);
			return bitmap;
		}

		try {
			conn = (HttpURLConnection) myFileUrl.openConnection();
			setRequestMode(conn, requestMode);
			conn.setDoInput(true);
			conn.connect();
			InputStream is = conn.getInputStream();
			int length = (int) conn.getContentLength();
			if (length != -1) {
				byte[] imgData = new byte[length];
				byte[] temp = new byte[512];
				int readLen = 0;
				int destPos = 0;
				while ((readLen = is.read(temp)) > 0) {
					System.arraycopy(temp, 0, imgData, destPos, readLen);
					destPos += readLen;
				}

				bitmap =  getCompressedPic(imgData, imageWidth);
				
			}

		} catch (IOException e) {
			bitmap = BitmapFactory.decodeResource(c.getResources(), R.drawable.ic_launcher);
			e.printStackTrace();
		} finally {
			if (conn != null) {
				conn.disconnect();
			}
		}

		return bitmap;
	}
	
	private Bitmap getCompressedPic(byte[] imgData, float imageWidth) {
		
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeByteArray(imgData, 0,
				imgData.length, options);
		
		options.inJustDecodeBounds = false;
		int rate = (int)imageWidth / options.outWidth;
		options.inSampleSize = rate;
		options.inJustDecodeBounds = false;
		
		return  BitmapFactory.decodeByteArray(imgData, 0, imgData.length, options);
	}
	
	/**
	 * 
	 * @param url
	 * @return
	 */
	public synchronized String getText(String url, REQUEST_MODE requestMode) {
		URL myFileUrl = null;
		HttpURLConnection conn = null;
		String txtString = "";
		try {
			myFileUrl = new URL(url);
		} catch (MalformedURLException e) {
			return txtString;
		}
		try {
			conn = (HttpURLConnection) myFileUrl.openConnection();
			setRequestMode(conn, requestMode);
			conn.setDoInput(true);
			conn.connect();
			InputStream is = conn.getInputStream();
			int length = (int) conn.getContentLength();
			if (length != -1) {
				byte[] stringData = new byte[length];
				byte[] temp = new byte[512];
				int readLen = 0;
				int destPos = 0;
				while ((readLen = is.read(temp)) > 0) {
					System.arraycopy(temp, 0, stringData, destPos, readLen);
					destPos += readLen;
				}
				is = new ByteArrayInputStream(stringData);
				InputStreamReader read = new InputStreamReader(is, "UTF-8");
				BufferedReader bufferedReader = new BufferedReader(read);
				String tempString = null;
				while ((tempString = bufferedReader.readLine()) != null) {
					txtString += tempString;
				}
			}
		} catch (IOException e) {
			conn.disconnect();
			return txtString;
		}
		return txtString;
	}
}

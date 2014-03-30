package com.mycreat.huan.util;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;

import android.util.Log;

public class HttpWrapper {

	/**
	 * To get data through requesting the Internet
	 * 
	 * @author zhanghaihai@gsegment.com
	 * 
	 */
	public class RequestWrapper {

		public String getRequestResult(String url) {

			String line = "";
			String responseData = null;
			Log.w("test", url);
			try {
				StringBuilder sb = new StringBuilder();
				String x = "";
				Log.w("test", "begin set url");
				URL httpurl = new URL(url);
				Log.w("test", "begin open url");
				URLConnection tc = httpurl.openConnection();
				Log.w("test", "opened url + " + tc.getInputStream());
				BufferedReader in = new BufferedReader(new InputStreamReader(
						tc.getInputStream()));

				Log.w("test", "begin read url");
				while ((line = in.readLine()) != null) {
					sb.append(line + "\n");
					x = sb.toString();
				}
				Log.w("test", "begin set url to string");
				responseData = new String(x);
				Log.w("test", responseData);
			} catch (UnknownHostException uh) {
				Log.v("NewWebHelper", "Unknown host :");
				uh.printStackTrace();
			} catch (FileNotFoundException e) {
				Log.v("NewWebHelper", "FileNotFoundException :");
				e.printStackTrace();
			} catch (IOException e) {
				Log.v("NewWebHelper", "IOException :");
				e.printStackTrace();
			} catch (Exception e) {
				Log.v("NewWebHelper", "Exception :");
				e.printStackTrace();
			}
			return responseData;
		}
	}
}

package com.mycreat.huan.util;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.http.HttpResponse;

import android.os.AsyncTask;
import android.util.Log;

public class FileUploader {

	/**
	 * Upload you file to the server.
	 * 
	 * @param url
	 *            The address of your server
	 * @param filePath
	 *            The path to the file you want to be uploaded
	 */
	public static void upload(String url, String filePath) {
		new FileUpLoaderTask().execute(url, filePath);
	}

	/**
	 * Do uploading in background.
	 * 
	 * @author zhanghaihai
	 * 
	 */
	private static class FileUpLoaderTask extends
			AsyncTask<String, Integer, Void> {

		@Override
		protected Void doInBackground(String... params) {
			String uploadUrl = params[0];
			String filePath = params[1];
			String boundary = "*****";
			String lineEnd = "\r\n";
			String twoHyphens = "--";
			HttpURLConnection con = null;
			URL url;
			DataOutputStream outputStream = null;
			long length = 0;
			int progress;
			int bytesRead, bytesAvailable, bufferSize;
			byte[] buffer;
			int maxBufferSize = 256 * 1024;// 256KB
			Log.w("huan", "Find file in given path: " + filePath);
			File uploadFile = new File(filePath);
			Log.w("huan", "The file is found");
			long totalSize = uploadFile.length(); // Get size of file, bytes
			Log.w("huan", "The size of file is: " + totalSize);

			try {
				FileInputStream fileInputStream = new FileInputStream(new File(
						filePath));
				url = new URL(uploadUrl);
				Log.w("huan", "Try to connect to the server: " + url.getPath());
				con = (HttpURLConnection) url.openConnection();
				Log.w("huan", "Sucessfully connect to the server.");
				// Set size of every block for post
				con.setChunkedStreamingMode(256 * 1024);

				// Allow output and input
				con.setDoInput(true);
				con.setDoOutput(true);
				con.setUseCaches(false);
				// Enable Post method.
				con.setRequestMethod("POST");
				con.setRequestProperty("Connection", "Keep-Alive");
				con.setRequestProperty("Content-Type",
						"image/jpeg;boundary=" + boundary);
				// con.setRequestProperty("file", "filename");
				Log.w("huan", "Begin to write file to the server ");
				OutputStream serverOutputStream = con.getOutputStream();
				Log.w("huan", "Begin to write file to the server ...");
				outputStream = new DataOutputStream(serverOutputStream);
				Log.w("huan", "OutputStream has got");
				outputStream.writeBytes(twoHyphens + boundary + lineEnd);
				outputStream
						.writeBytes("Content-Disposition: form-data; name=\"file\"; filename=\""
								+ filePath.substring(filePath.lastIndexOf("/") + 1)
								+ "\"" + lineEnd);
				outputStream.writeBytes(lineEnd);
				Log.w("huan", "The header has been written to the server ");
				bytesAvailable = fileInputStream.available();
				bufferSize = Math.min(bytesAvailable, maxBufferSize);
				buffer = new byte[bufferSize];

				// Read file
				// bytesRead = fileInputStream.read(buffer, 0, bufferSize);
				Log.w("huan", "File has been read to a stream");
				int counter = 0;
				int count = 0;
				while ((count = fileInputStream.read(buffer)) != -1) {
					outputStream.write(buffer, 0, count);
					counter += count;
					publishProgress(counter);
					/*if (uploadListener != null) {
						uploadListener.onUploadProcess(counter);
					}*/
				}
				/*while (bytesRead > 0) {
					outputStream.write(buffer, 0, bufferSize);
					length += bufferSize;
					progress = (int) ((length * 100) / totalSize);
					publishProgress(progress);

					bytesAvailable = fileInputStream.available();
					bufferSize = Math.min(bytesAvailable, maxBufferSize);
					bytesRead = fileInputStream.read(buffer, 0, bufferSize);
				}*/
				Log.w("huan", "Main content of file has uploaded");
				outputStream.writeBytes(lineEnd);
				outputStream.writeBytes(twoHyphens + boundary + twoHyphens
						+ lineEnd);
				publishProgress(100);

				// Responses from the server (code and message)
				int serverResponseCode = con.getResponseCode();
				String serverResponseMessage = con.getResponseMessage();
				InputStream in = null;

				Log.w("huan", "serverResponseCode: " + serverResponseCode
						+ ": " + serverResponseMessage);

				outputStream.flush();
				if (serverResponseCode == 200) {
					in = con.getInputStream();
					int ch;
					StringBuilder sb2 = new StringBuilder();
					while ((ch = in.read()) != -1) {
						sb2.append((char) ch);
					}
					Log.w("huan", "Response text: " + sb2.toString());
				}
				outputStream.close();
				fileInputStream.close();
				Log.w("huan", "Response message: " + con.getResponseMessage());
				Log.w("huan", "Response message: " + con.getResponseCode());

			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				if (con != null) {
					con.disconnect();
				}

			}

			return null;
		}

		@Override
		protected void onPreExecute() {

			super.onPreExecute();
		}

		@Override
		protected void onPostExecute(Void result) {
			//Log.w("huan", "Begin to upload!");
			super.onPostExecute(result);
		}

		@Override
		protected void onProgressUpdate(Integer... progress) {
			Log.w("huan", progress + "% has been uploaded!");
			super.onProgressUpdate(progress);
		}

	}

}

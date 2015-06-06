package com.danielbahmani.webpiccapture;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

public class DownloadImageTask extends AsyncTask<String, Void, Boolean> {
	private String LOG_TAG = "DownloadImageTask";

	Activity activity;
	String imageUrl;

	public DownloadImageTask(Activity activity) {
		this.activity = activity;
	}

	protected Boolean doInBackground(String... urls) {
		
		try {

			imageUrl = urls[0];
			Log.v(LOG_TAG, "url: " + imageUrl);

			URL url = new URL(imageUrl);
			HttpURLConnection httpCnn = (HttpURLConnection) url	.openConnection();
			httpCnn.setRequestMethod("GET");
			httpCnn.setDoOutput(true);
			httpCnn.connect();
			int lenghtOfFile = httpCnn.getContentLength();
			Log.v(LOG_TAG, "lenghtOfFile: " + lenghtOfFile);
			String fileName = AppHelper.getFileName(imageUrl);
			Log.v(LOG_TAG, "PATH: " + fileName);
			
			InputStream input = new BufferedInputStream(url.openStream(), 8192);
			FileOutputStream output = activity.openFileOutput(fileName, Context.MODE_PRIVATE);

			byte data[] = new byte[1024];
			int count = 0;
			while ((count = input.read(data)) != -1) {
				output.write(data, 0, count);
			}
			output.close();
			input.close();
			httpCnn.disconnect();

		} catch (IOException e) {
			Log.d(LOG_TAG, "Error: " + e);
			//Toast.makeText(activity.getApplicationContext(), "error " + e.toString(), Toast.LENGTH_LONG).show();
			
			return false;
		}
		
		return true;
	}	
	
	@Override
	protected void onPostExecute(Boolean result) {

		if(result){
			Toast.makeText(activity.getApplicationContext(), "Successfully downloaded.", Toast.LENGTH_LONG).show();
			
			PictureDB pictureDB = new PictureDB(activity.getApplicationContext());
			try{
				Picture pic = new Picture();
				pic.setUrl(imageUrl);
				pic.setFilename(AppHelper.getFileName(imageUrl));
				pic.setTimestamp("" + AppHelper.getTimestamp());
				
				pictureDB.insert(pic);
				
			}catch(Exception e){
				Log.d(LOG_TAG, "onPostExecute.insert: " + e);
				Toast.makeText(activity.getApplicationContext(), "Error in saving Image to db", Toast.LENGTH_LONG).show();
				
			}finally{
				pictureDB.close();				
			}		
			
		}
		else{
			Toast.makeText(activity.getApplicationContext(), "Error in downloading the Image", Toast.LENGTH_LONG).show();
		}
		
	}
}
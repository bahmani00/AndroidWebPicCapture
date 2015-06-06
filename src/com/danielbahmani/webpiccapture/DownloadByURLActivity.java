package com.danielbahmani.webpiccapture;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import cs325.bahmani.webpiccapture.R;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.app.Activity;
import android.content.Context;

public class DownloadByURLActivity extends Activity {

	ProgressBar progressBar;
	EditText txtUrl;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_download_by_url);

		txtUrl = (EditText) findViewById(R.id.txtImageUrl);
		txtUrl.setText("http://www.mcgill.ca/sites/all/themes/blofeld/images/logo.png");

		progressBar = (ProgressBar) findViewById(R.id.progressBar);
		progressBar.setMax(100);

	}

	public void btnDownload(View v) {
		String urlStr = txtUrl.getText().toString();

		if (urlStr == null || urlStr.isEmpty()) {
			Toast.makeText(getApplicationContext(), "Url is empty",	Toast.LENGTH_LONG).show();
		} else {
			new DownloadImageFromUrlTask(this, progressBar).execute(urlStr);
		}
	}

	
	public class DownloadImageFromUrlTask extends AsyncTask<String, Integer, Boolean> {

		private String LOG_TAG = "DownloadImageFromUrlTask";

		Activity activity;
		ProgressBar progressBar;
		String imageUrl;

		public DownloadImageFromUrlTask(Activity activity, ProgressBar progressBar) {
			this.activity = activity;
			this.progressBar = progressBar;
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
				FileOutputStream output = activity.openFileOutput(fileName, Context.MODE_WORLD_READABLE);

				byte data[] = new byte[1024];
				int count = 0, totalDownloaded = 0;
				while ((count = input.read(data)) != -1) {
					totalDownloaded += count;

					// publish progress bar
					if(totalDownloaded >= 10 * 1024 || lenghtOfFile  <= 10 * 1024)
						publishProgress((int) ((totalDownloaded * 100) / lenghtOfFile));
					
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
					}

					output.write(data, 0, count);
				}
				output.close();
				input.close();
				httpCnn.disconnect();

			} catch (IOException e) {
				Log.d(LOG_TAG, "Error: " + e);				
				return false;
			}
			
			return true;
		}	

		@Override
		protected void onProgressUpdate(Integer... progress) {
			super.onProgressUpdate(progress);

			Log.d(LOG_TAG, "onProgressUpdate: " + progress[0]);
			progressBar.setProgress(progress[0]);
		}

		@Override
		protected void onPostExecute(Boolean result) {
			if(result){
				Toast.makeText(activity.getApplicationContext(), "Successfully downloaded.", Toast.LENGTH_LONG).show();
				progressBar.setProgress(0);

				PictureDB pictureDB = new PictureDB(activity.getApplicationContext());
				try{
					Picture pic = new Picture(
						-1,  imageUrl,
						"" + AppHelper.getTimestamp(),
						AppHelper.getFileName(imageUrl));
					
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
}

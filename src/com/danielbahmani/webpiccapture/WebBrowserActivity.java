package com.danielbahmani.webpiccapture;


import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebView.HitTestResult;
import android.webkit.WebViewClient;
import android.widget.Toast;
import android.app.Activity;

public class WebBrowserActivity extends Activity {

	private static final String TAG = "WebBrowserActivity";
	private String imageUrlForDownload = null;

	WebView webView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Let's display the progress in the activity title bar, like the
		// browser app does.
		getWindow().requestFeature(Window.FEATURE_PROGRESS);

		setContentView(R.layout.activity_web_browser);

		webView = (WebView) findViewById(R.id.webView);
		
		initializeWebView();
	}

	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View view, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, view, menuInfo);

		Log.d(TAG, "onCreateContextMenu" + getFileName(imageUrlForDownload));

		MenuItem mnu1 = menu.add(0, 0, 0, "Save Image(" + getFileName(imageUrlForDownload) + ")");
		mnu1.setAlphabeticShortcut('d');
		// mnu1.setIcon(R.drawable.image1);
		MenuItem mnu2 = menu.add(0, 1, 1, "Cancel");
		mnu2.setAlphabeticShortcut('c');
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case 0:
			if(imageUrlForDownload != null && !imageUrlForDownload.isEmpty()) {
				//TODO: download the image
				new DownloadImageTask(this).execute(imageUrlForDownload);				
				//imageUrlForDownload = null;
			}
			return true;
		default:
			imageUrlForDownload = null;
			return false;
		}
	}

	private void initializeWebView() {

		webView.getSettings().setJavaScriptEnabled(true);

		final Activity activity = this;
		webView.setWebChromeClient(new WebChromeClient() {
			public void onProgressChanged(WebView view, int progress) {
				// Activities and WebViews measure progress with different
				// scales.
				// The progress meter will automatically disappear when we reach
				// 100%
				activity.setProgress(progress * 1000);
			}
		});

		webView.setWebViewClient(new WebViewClient() {
			public void onReceivedError(WebView view, int errorCode,
					String description, String failingUrl) {
				Toast.makeText(activity, "Oh no! " + description,
						Toast.LENGTH_SHORT).show();
			}
		});

		webView.loadUrl("http://www.google.com");

		webView.setLongClickable(true);
		webView.setOnLongClickListener(new View.OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {
				//Log.d(TAG, "onLongClick: " + v.getId());

				WebView wView = (WebView)v;
				HitTestResult result = wView.getHitTestResult();
				if (result.getType() == HitTestResult.IMAGE_TYPE || result.getType() == HitTestResult.SRC_IMAGE_ANCHOR_TYPE) {
					Log.d(TAG, "onLongClick: " + result.getExtra());
					imageUrlForDownload = result.getExtra();
					((Activity) activity).openContextMenu(v);
				}
				return true;
			}

		});

		registerForContextMenu(webView);
	}

	private String getFileName(String url){
		return url.substring( url.lastIndexOf('/')+1, url.length() );
	}
}

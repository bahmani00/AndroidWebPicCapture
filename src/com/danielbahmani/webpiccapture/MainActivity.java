package com.danielbahmani.webpiccapture;

import android.os.Bundle;
import android.view.View;
import android.app.Activity;
import android.content.Intent;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
	}

	public void btnActionClicked(View v) {
		Intent intent = null;
		
		switch(v.getId())
		{
			case R.id.btnBrowseWebForPics:
				intent = new Intent(this, WebBrowserActivity.class);
				break;
			case R.id.btnDownloadByURL:
				intent = new Intent(this, DownloadByURLActivity.class);
				break;
			case R.id.btnBrowseLocalGalery:
				intent = new Intent(this, PictureGalleryActivity.class);
				break;
		}

		startActivity(intent);
	}	
}

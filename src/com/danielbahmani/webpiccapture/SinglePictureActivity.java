package com.danielbahmani.webpiccapture;


import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class SinglePictureActivity extends Activity {

	static final String Key_FileName = "FileName"; 
	
	ImageView imgView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_single_picture);
		
		imgView = (ImageView) findViewById(R.id.imageView);
		String fileName = getIntent().getStringExtra(Key_FileName);
		if(fileName != null && !fileName.isEmpty())
		{
			try{
				String imagePath = getFilesDir() + "/" + fileName;
				//Log.v(LOG_TAG, "getView.imagePath: " + imagePath);
				//imgView.setImageBitmap(BitmapFactory.decodeFile(imagePath));

				/*
					use BitmapFactory to rescale the bitmap to size close to the
					physical resolution of the screen to avoid OutOfMemory errors on files with huge resolution.				 
				 */
				Bitmap bm1 = AppHelper.decodeSampledBitmapFromResource(imagePath, imgView.getWidth(), imgView.getHeight());
				imgView.setImageBitmap(bm1);
				
			}catch(Exception e){}
			
		}
	}
	
	public void imageViewClick(View v){
		this.finish();
	}
	
}

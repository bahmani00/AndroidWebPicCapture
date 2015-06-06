package com.danielbahmani.webpiccapture;

import java.util.List;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;

public class PictureGalleryActivity extends Activity {

	private String LOG_TAG = "PictureGalleryActivity";

	List<Picture> pictures;
	AlertDialog alert;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_picture_gallery);

		initializeListView();
	}

	@Override
	public void onResume() {
		super.onResume();

		initializeListView();
	}

	private void initializeListView() {
		final ListView listview = (ListView) findViewById(R.id.lstPictures);

		PictureDB pictureDB = new PictureDB(getApplicationContext());
		pictures = pictureDB.getPictures();
		pictureDB.close();

		final MySimpleArrayAdapter adapter = new MySimpleArrayAdapter(this, pictures);
		listview.setAdapter(adapter);

		listview.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				String filename = pictures.get(position).getFilename();
				Intent intent = new Intent(parent.getContext(), SinglePictureActivity.class);
				intent.putExtra(SinglePictureActivity.Key_FileName , filename);
				startActivity(intent);
			}
		});
		
		listview.setLongClickable(true);
		listview.setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override 
            public boolean onItemLongClick(AdapterView<?> parent, View v, int position, long id) 
           { 
				Picture picture = pictures.get(position);
				showAlert("Warning", "Are you sure you want to delete?", picture);				
				return true;
           } 			
		});
		
	}

	public class MySimpleArrayAdapter extends ArrayAdapter<Picture> {
		private final Context context;
		private final List<Picture> values;

		public MySimpleArrayAdapter(Context context, List<Picture> list) {
			super(context, R.layout.picture_gallery_item, list);
			this.context = context;
			this.values = list;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View rowView = inflater.inflate(R.layout.picture_gallery_item, parent, false);
			
			TextView lblInfo = (TextView) rowView.findViewById(R.id.lblInfo);
			ImageView imgView = (ImageView) rowView.findViewById(R.id.imgPicture);

			Picture picture = values.get(position);
			String info = String.format("Download on %s \nOrigin URL was: \n%s", picture.getDownLodedDate(), picture.getUrl());
					
			Log.v(LOG_TAG, "getView.info1: " + info);
			Log.v(LOG_TAG, "getView.info: " + picture.getTimestamp() + " .. " + picture.getDownLodedDate());
			lblInfo.setText(info);

			
			if(picture.getFilename() != null && !picture.getFilename().isEmpty()){
				try{
					String imagePath = getFilesDir() + "/" + picture.getFilename();
					Log.v(LOG_TAG, "getView.imagePath: " + imagePath);
					
					//imgView.setImageBitmap(BitmapFactory.decodeFile(imagePath));
					
					/*
					utilize BitmapFactory to rescale the
					bitmap to the size you need for the thumbnail display. This will help you avoid OutOfMemory
					exceptions being throw.					 
					 */
					//Bitmap bm1 = AppHelper.decodeSampledBitmapFromResource(imagePath, imgView.getWidth(), imgView.getHeight());
					Bitmap bm1 = AppHelper.ShrinkBitmap(imagePath, imgView.getWidth(), imgView.getHeight());
					imgView.setImageBitmap(bm1);

					
				}catch(Exception e){}
			}


			return rowView;
		}
	}

	private void showAlert(String title, String message, final Picture picture){
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title)
        .setMessage(message)
        .setCancelable(true)
        .setPositiveButton("Ok",new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            	//Delete picId
				PictureDB pictureDB = new PictureDB(getApplicationContext());
				try{
					//delete the entry from the database
					pictureDB.delete(picture.getId());
					String imagePath = getFilesDir() + "/" + picture.getFilename();
					//file from the storage
					deleteFile(imagePath);

					initializeListView();

					Toast.makeText(getApplicationContext(), "Successfully deleted from db.", Toast.LENGTH_LONG).show();					
				}catch(Exception e){
					Log.d(LOG_TAG, "showAlert.Ok.delete: " + e);
					//Toast.makeText(activity.getApplicationContext(), "Error in saving Image to db", Toast.LENGTH_LONG).show();
					
				}finally{
					pictureDB.close();				
				}		
            	
                dialog.cancel();
            }
        })
        .setNegativeButton("Cancel",new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
                //finish();
            }
        });

        alert = builder.create();
		
        alert.setMessage(message);
        
        alert.show();
    }
}

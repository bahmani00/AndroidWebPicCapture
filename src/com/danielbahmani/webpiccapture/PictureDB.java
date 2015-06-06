package com.danielbahmani.webpiccapture;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

public class PictureDB {

	private static final String TAG = "PictureDB";

	class DbHelper extends SQLiteOpenHelper {
		static final String TAG = "DbHelper";
		static final int DB_VERSION = 3;
		static final String DB_NAME = "webpiccapture.db";
		static final String TABLE_NAME = "picture";
		static final String C_ID = BaseColumns._ID;
		static final String C_URL = "url";
		static final String C_TIMESTAMP = "timestamp";
		static final String C_FILENAME = "filename";
		
		Context context;

		public DbHelper(Context context) {
			super(context, DB_NAME, null, DB_VERSION);
			this.context = context;
		}

		// Called only once, first time the DB is created
		@Override
		public void onCreate(SQLiteDatabase db) {
			String sql = String.format("create table %s (%s INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, %s text, %s text, %s text)", TABLE_NAME, C_ID, C_URL, C_TIMESTAMP, C_FILENAME);
			db.execSQL(sql); //
			Log.d(TAG, "onCreated sql: " + sql);
		}

		// Called whenever newVersion != oldVersion
		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			db.execSQL(String.format("drop table if exists %s", TABLE_NAME)); // drops the old database
			Log.d(TAG, "onUpdated");
			onCreate(db); // run onCreate to get new database
		}
	}

	private final DbHelper dbHelper;

	public PictureDB(Context context) {
		this.dbHelper = new DbHelper(context);
		Log.i(TAG, "Initialized data");
	}

	public void close() {
		this.dbHelper.close();
	}

	public void insert(Picture picture) {
		Log.d(TAG, "insert on " + picture.getUrl());
		ContentValues values = new ContentValues();
		values.put("url", picture.getUrl());
		values.put("timestamp", picture.getTimestamp());
		values.put("filename", picture.getFilename());

		SQLiteDatabase db = this.dbHelper.getWritableDatabase();
		try {
			db.insertOrThrow(DbHelper.TABLE_NAME, null, values);
			Log.d(TAG, "insert done " + picture.getUrl());
		} finally {
			db.close();
		}
	}
	public int update(Picture picture) {
		ContentValues values = new ContentValues();
		values.put(DbHelper.C_URL, picture.getUrl());
		values.put(DbHelper.C_TIMESTAMP, picture.getTimestamp());
		values.put(DbHelper.C_FILENAME, picture.getFilename());

		SQLiteDatabase db = this.dbHelper.getWritableDatabase();
		try {
			String where = String.format("%s=?", DbHelper.C_ID);
			return db.update(DbHelper.TABLE_NAME, values, where, new String []{String.valueOf(picture.getId())});
		} finally {
			db.close();
		}
	}
	public void delete(int id)
	  {
	   SQLiteDatabase db = this.dbHelper.getWritableDatabase();
	   try {
			String where = String.format("%s=?", DbHelper.C_ID);
		   db.delete(DbHelper.TABLE_NAME, where, new String [] {String.valueOf(id)});
	   } finally {
			db.close();
		}
	  }
	
	public List<Picture> getPictures() {
		SQLiteDatabase db = this.dbHelper.getReadableDatabase();
		List<Picture> persons = new ArrayList<Picture>();
		try {
			Cursor cursor = db.query(DbHelper.TABLE_NAME, null, null, null, null, null, null);
			if (cursor != null) {
				while (cursor.moveToNext()) {
					persons.add(createPictureFromCursor(cursor));
				}
			}
		} finally {
			db.close();
		}

		return persons;
	}

	public Picture getPictureById(long id) {
		SQLiteDatabase db = this.dbHelper.getReadableDatabase();
		try {
			String where = String.format("%s=%s", DbHelper.C_ID, id);
			Cursor cursor = db.query(DbHelper.TABLE_NAME, null, where, null, null, null, null);
			if (cursor != null && cursor.moveToNext())					
				return createPictureFromCursor(cursor);
		} finally {
			db.close();
		}
		
		return null;
	}
	
	private Picture createPictureFromCursor(Cursor cursor){
		return new Picture(cursor.getInt(0), cursor.getString(1), cursor.getString(2), cursor.getString(3));		
	}
}


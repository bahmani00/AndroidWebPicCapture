package com.danielbahmani.webpiccapture;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Picture {
	private int id;
	private String url;
	private String timestamp;
	private String filename;
	
	public Picture(){
		
	}
	public Picture(int id, String url,String timestamp, String filename) {
		this.id = id;
		this.url = url;
		this.timestamp = timestamp;
		this.filename = filename;
	}

	public int getId() {
		return id;
	}
	
	public String getTimestamp() {
		return timestamp;
	}
	public String getDownLodedDate() {
		Date date = new Date(Long.parseLong(timestamp));
		return new SimpleDateFormat("dd/MM/yy").format(date);
	}
	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
	
	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

}
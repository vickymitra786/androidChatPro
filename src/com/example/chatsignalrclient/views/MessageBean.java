package com.example.chatsignalrclient.views;

import android.graphics.Bitmap;
import android.net.Uri;

public class MessageBean {
	
	private String message = "";
	private Uri image   = null;
	private Uri video   = null; 
	
	
	public MessageBean(String message, Uri image, Uri video) {
		this.message = message;
		this.image   = image;
		this.video   = video;
	}

	public String getMessage() {
		return message;
	}

	public Uri getImage() {
		return image;
	}
	
	public Uri getVideo() {
		return video;
	}
	

}

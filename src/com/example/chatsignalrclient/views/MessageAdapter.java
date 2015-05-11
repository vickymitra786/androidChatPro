package com.example.chatsignalrclient.views;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URLConnection;
import java.util.ArrayList;

import com.example.chatsignalrclient.R;
import com.example.chatsignalrclient.R.id;
import com.example.chatsignalrclient.R.layout;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.MediaStore.Video.Thumbnails;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebView.FindListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

public class MessageAdapter extends ArrayAdapter<MessageBean>{
	
	Context context;
    int layoutResourceId;   
    ArrayList<MessageBean> dataArrList;
   
    public MessageAdapter(Context context, int layoutResourceId,ArrayList<MessageBean> data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.dataArrList = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        MessageHolder holder = null;
       
        if(row == null)
        {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);
           
            holder = new MessageHolder();
            holder.imgIcon        = (ImageView)row.findViewById(R.id.msgImage);
            holder.message        = (TextView)row.findViewById(R.id.msgText);
            holder.image_Layout   = (LinearLayout)row.findViewById(R.id.imageLayout);
            holder.message_Layout = (LinearLayout)row.findViewById(R.id.textLayout);
            holder.play_Button    = (ImageView)row.findViewById(R.id.playBtn);
            row.setTag(holder);
        }
        else
        {
            holder = (MessageHolder)row.getTag();
        }
       
        final MessageBean msgItem = dataArrList.get(position);
        
        if(!msgItem.getMessage().equalsIgnoreCase("")){
        	
        	holder.message_Layout.setVisibility(View.VISIBLE);
        	holder.image_Layout.setVisibility(View.GONE);
        	holder.message.setText(msgItem.getMessage());
        	holder.play_Button.setVisibility(View.GONE);
        }
        else if(msgItem.getImage() != null){
        	
        	holder.message_Layout.setVisibility(View.GONE);
        	holder.image_Layout.setVisibility(View.VISIBLE);
        	try { holder.imgIcon.setImageBitmap(decodeUri(msgItem.getImage())); }
        	 catch (FileNotFoundException e) { e.printStackTrace(); }
        	holder.play_Button.setVisibility(View.GONE);
        	holder.imgIcon.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					
					AlertDialog dialog = new AlertDialog.Builder(context).create();
					LayoutInflater inflater = LayoutInflater.from(context);
					View view = inflater.inflate(R.layout.dialog_image, null); // xml Layout file for imageView
					ImageView img = (ImageView) view.findViewById(R.id.imageView_Dialog);
					img.setImageURI(msgItem.getImage());
					dialog.setView(view);
					dialog.show();
				}
			});
        }
        else if(msgItem.getVideo() != null){
        	
        	holder.message_Layout.setVisibility(View.GONE);
        	holder.image_Layout.setVisibility(View.VISIBLE);
        	
        	holder.imgIcon.setImageBitmap(ThumbnailUtils.createVideoThumbnail(getRealPathFromURI(msgItem.getVideo()), Thumbnails.MICRO_KIND));
        	holder.play_Button.setVisibility(View.VISIBLE);
            holder.play_Button.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					
					
					//playVideoOffline(context, new File(getRealPathFromURI(msgItem.getVideo())).getAbsolutePath().toString().trim(), vid);
					  
					  Intent intent = new Intent();
					  intent.setAction(Intent.ACTION_VIEW);
					  intent.setDataAndType(msgItem.getVideo(),URLConnection.guessContentTypeFromName(msgItem.getVideo().toString()));
					  context.startActivity(intent);
					  
				}
			});
        }
        
        
     
       
        return row;
    }
   
    static class MessageHolder
    {
        ImageView imgIcon;
        TextView  message;
        LinearLayout message_Layout;
        LinearLayout image_Layout;
        ImageView play_Button;
    }
  
    
    
    
    // Method to reduce the Image file size......... By Kumar Vivek Mitra 29-5-2014
 	private Bitmap decodeUri(Uri selectedImage) throws FileNotFoundException {

 		// Decode image size
 		BitmapFactory.Options o = new BitmapFactory.Options();
 		o.inJustDecodeBounds = true;
 		BitmapFactory.decodeStream(context.getContentResolver().openInputStream(selectedImage), null, o);

 		// The new size we want to scale to
 		final int REQUIRED_SIZE = 140;

 		// Find the correct scale value. It should be the power of 2.
 		int width_tmp = o.outWidth, height_tmp = o.outHeight;
 		int scale = 1;
 		while (true) {
 			if (width_tmp / 2 < REQUIRED_SIZE || height_tmp / 2 < REQUIRED_SIZE) {
 				break;
 			}
 			width_tmp /= 2;
 			height_tmp /= 2;
 			scale *= 2;
 		}

 		// Decode with inSampleSize
 		BitmapFactory.Options o2 = new BitmapFactory.Options();
 		o2.inSampleSize = scale;
 		return BitmapFactory.decodeStream(context.getContentResolver().openInputStream(selectedImage), null, o2);

 	}
 	
  
 	// Method to convert Uri to File Path
 	private String getRealPathFromURI(Uri contentURI) {
 	    String result;
 	    Cursor cursor = context.getContentResolver().query(contentURI, null, null, null, null);
 	    if (cursor == null) { // Source is Dropbox or other similar local file path
 	        result = contentURI.getPath();
 	    } else { 
 	        cursor.moveToFirst(); 
 	        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA); 
 	        result = cursor.getString(idx);
 	        cursor.close();
 	    }
 	    return result;
 	}
 	
    
 	// Method to play video from sdcard
 	public Void playVideoOffline(Context context,String filePath, VideoView videoView) {

 	    videoView.setVideoPath(filePath);

 	    MediaController mediaController = new MediaController(context);
 	    mediaController.setMediaPlayer(videoView);

 	    videoView.setMediaController(mediaController);
 	    videoView.requestFocus();

 	    videoView.start();

 	    return null;
 	}
    
    

}

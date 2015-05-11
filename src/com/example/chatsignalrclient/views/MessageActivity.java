package com.example.chatsignalrclient.views;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;

import com.example.chatsignalrclient.R;
import com.example.chatsignalrclient.R.id;
import com.example.chatsignalrclient.R.layout;
import com.example.chatsignalrclient.model.APIHandling;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.provider.MediaStore.Video.Thumbnails;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

public class MessageActivity extends Activity{
	
	
	
	// -------------------------------- Java Instance Variable
	 
	   APIHandling uniqueMA = APIHandling.getInstance();
       public static String userTo   = "";
       private String userFrom = "";
       public static ArrayAdapter<MessageBean> mAdapter;
       private static ArrayList<MessageBean> mStrings = new ArrayList<MessageBean>();
       private static final int SELECT_PHOTO = 100;
       
	// -------------------------------- Java Instance Variable
	
	
	// -------------------------------- Android Instance Variable
	
       private  EditText editText_Message;
       private  Button button_Submit;
       private  Button button_Share;
	   public  static ListView listView_Message;
	   public static Handler handler;
	   
	// -------------------------------- Android Instance Variable
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_msg);
		
		handler = new Handler();
		setContents();
		setHandlers();
		
	}

	
	private void setContents(){
		
		
	   listView_Message = (ListView)findViewById(R.id.list_Msg_User);
	   editText_Message = (EditText)findViewById(R.id.editText_Messages);
	   button_Submit    = (Button)findViewById(R.id.button_Submit);
	   button_Share    = (Button)findViewById(R.id.button_Share);
	   mAdapter = new MessageAdapter(MessageActivity.this, R.layout.msg_row, mStrings);
	   listView_Message.setAdapter(mAdapter);
	   mAdapter.notifyDataSetChanged();
	   
	   
	   
	}
	
	
	
	private void setHandlers(){
		
		if(getIntent().getExtras().getString("SOrG").equalsIgnoreCase("SINGLE")){ userTo = getIntent().getExtras().getString("USER_TO"); }
		else if(getIntent().getExtras().getString("SOrG").equalsIgnoreCase("GROUP")){  userTo = getIntent().getExtras().getString("USER_TO"); }
		
	    userFrom   = uniqueMA.userName;
		
	    
        // Code that executes on the pressing of the send button......... By Kumar Vivek Mitra 26-5-2014	    		
		button_Submit.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				if(!editText_Message.getText().toString().trim().equalsIgnoreCase("")){
					
					String msg = editText_Message.getText().toString().trim();
					
					if (getIntent().getExtras().getString("SOrG").equalsIgnoreCase("SINGLE")) {
						uniqueMA.hub.invoke("EntryPoint", "SendToClient",userFrom, msg, userTo, "");
						mAdapter.add(new MessageBean(msg, null, null));
						editText_Message.setText("");
					}
					else if(getIntent().getExtras().getString("SOrG").equalsIgnoreCase("GROUP")){
						System.out.println("I AM IN GROUP");
						Toast.makeText(MessageActivity.this,"I AM IN GROUP",Toast.LENGTH_SHORT).show();	
						uniqueMA.hub.invoke("EntryPoint", "GroupMessage",userFrom, msg, "",userTo);
						mAdapter.add(new MessageBean(msg, null, null));
						editText_Message.setText("");
					}
				}
			        
				
			}
		});
		
		
		// Code to handle the upload of the media files......... By Kumar Vivek Mitra 29-5-2014
		button_Share.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
				photoPickerIntent.setType("image/* video/*");
				startActivityForResult(photoPickerIntent, SELECT_PHOTO); 
				
			}
		});
	}
	
	
	// static method to handle the message from the user......... By Kumar Vivek Mitra 27-5-2014         
	public static void receiveText(final String msgFromUser) {
	
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				
				handler.post(new Runnable() {
					
					@Override
					public void run() {
						
						mAdapter.add(new MessageBean(msgFromUser, null, null));
				        mAdapter.notifyDataSetChanged();
				        listView_Message.invalidateViews();
					}
				});
				
			}
		}).start();
		 
    }
	
	
	
	// static method to handle the media file from the user.......... By Kumar Vivek Mitra 30-5-2014
	public static void receiveFile(final String fileFromUser) {
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				
				handler.post(new Runnable() {
					
					@Override
					public void run() {

                         Uri externalFile = Uri.fromFile(new File(fileFromUser));
                         
						if(fileFromUser.endsWith(".jpg") || fileFromUser.endsWith(".png") || fileFromUser.endsWith(".JPG") || fileFromUser.endsWith(".PNG")){
						
							mAdapter.add(new MessageBean("", externalFile, null));
					        mAdapter.notifyDataSetChanged();
					        listView_Message.invalidateViews();
						}
						else if(fileFromUser.endsWith(".3gp") || fileFromUser.endsWith(".3GP") || fileFromUser.endsWith(".mp4") || fileFromUser.endsWith(".MP4") || fileFromUser.endsWith(".avi") || fileFromUser.endsWith(".AVI")){
							
							mAdapter.add(new MessageBean("", null, externalFile));
					        mAdapter.notifyDataSetChanged();
					        listView_Message.invalidateViews();
						}
						
						
					}
				});
				
			}
		}).start();
		 
    }
	
	
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) { 
	   super.onActivityResult(requestCode, resultCode, imageReturnedIntent); 

	   System.out.println("I AM HERE JUST OUTSIDE SELECT_1");
	    switch(requestCode) { 
	    case SELECT_PHOTO:
	    	System.out.println("I AM HERE JUST INSIDE SELECT_2");
	        if(resultCode == RESULT_OK){  
	        	System.out.println("I AM HERE JUST INSIDE SELECT_3");
	            Uri selectedImage = imageReturnedIntent.getData();
				File fx = new File(getRealPathFromURI(selectedImage));
				String filePath = fx.getAbsolutePath().toString().trim();
				
				if(filePath.endsWith(".jpg") || filePath.endsWith(".png") || filePath.endsWith(".JPG") || filePath.endsWith(".PNG")){
					
					mAdapter.add(new MessageBean("",selectedImage, null));
				}
				else if(filePath.endsWith(".3gp") || filePath.endsWith(".3GP") || filePath.endsWith(".mp4") || filePath.endsWith(".MP4") || filePath.endsWith(".avi") || filePath.endsWith(".AVI")){
				
					mAdapter.add(new MessageBean("", null, selectedImage));
				}
				
				// Uploading Invoked
				uniqueMA.uploadFile(uniqueMA.userName, filePath);
	        }
	        else{
	        	
	        	
	        }
	    }
	}

	
	
 
	// Method to convert Uri to File Path
	private String getRealPathFromURI(Uri contentURI) {
	    String result;
	    Cursor cursor = getContentResolver().query(contentURI, null, null, null, null);
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
	

}

package com.example.chatsignalrclient.views;


import java.io.File;
import java.util.concurrent.TimeUnit;

import com.example.chatsignalrclient.R;
import com.example.chatsignalrclient.R.id;
import com.example.chatsignalrclient.R.layout;
import com.example.chatsignalrclient.model.APIHandling;
import com.example.chatsignalrclient.model.ExternalStorageFinderClass;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


public class MainActivity extends Activity {

	
	// ---------------------------- Java Instance Variables
	
	   APIHandling uniqueInstance = APIHandling.getInstance();
	   public static String pathToSDCard = "";
	   
	// ---------------------------- Java Instance Variables
	
	
	// ---------------------------- Android Instance Variables
	   
	   EditText editText_UserName;
	   Button   button_Login;
	   Handler handler;
	   
	// ---------------------------- Android Instance Variables
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		handler = new Handler();
		pathToSDCard = ExternalStorageFinderClass.getInstance().getExtPath()+"/"+"SignalRChat";
		 File fCheck = new File(MainActivity.pathToSDCard);
		   if(fCheck.exists() == false){ fCheck.mkdirs(); }
		
		
		setContents();
		setHandlers();
		
		
	}
	
	
	private void setContents(){
		
		editText_UserName = (EditText)findViewById(R.id.editText_UserName);
		button_Login      = (Button)findViewById(R.id.button_Login);
		
	}
	
	
	private void setHandlers(){
		
		button_Login.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				 // ----------------------------------------------- Block to handle empty username ..........  By Kumar Vivek Mitra 16-5-2014
				 if(editText_UserName.getText().toString().trim().equalsIgnoreCase("")){
					 
					 Toast.makeText(MainActivity.this,"Please input the username", Toast.LENGTH_SHORT).show();
				 }
				 // ----------------------------------------------- Block to handle empty username ..........  By Kumar Vivek Mitra 16-5-2014
				 
				 
				 
				 
				 				 
				 // To register the client
				 
				  Thread tx = new Thread(new Runnable() {
					
					@Override
					public void run() {
						uniqueInstance.hub.invoke("EntryPoint","RegisterClient",editText_UserName.getText().toString().trim(),"","",""); 	
					}
				});
				  
				  tx.start();
				  try { tx.join(); } 
				     catch (InterruptedException e){ e.printStackTrace(); }
				 					
					
				  try { TimeUnit.SECONDS.sleep(3); }    // Hack to delay the execution of the below lines, as the above thread invokes a method onto the server side, then server will call a remote method within the client whose time of execution can be delayed due to network latency, so getting it delayed by 3sec works well, can be tuned as per needed............. By Kumar Vivek Mitra 20_5_2014
				         catch (InterruptedException e) { e.printStackTrace(); }
				  
					
			     // ----------------------------------------------- Block to deal when user exist or not ..........  By Kumar Vivek Mitra 16-5-2014	
					if(uniqueInstance.isUserExist() == true){
						
						Toast.makeText(MainActivity.this, "User already exists, please choose another", Toast.LENGTH_SHORT).show();
					}
					else if(uniqueInstance.isUserExist() == false){
						uniqueInstance.userName = editText_UserName.getText().toString().trim();
						Intent i = new Intent(MainActivity.this,SingleORGroupActivity.class);
						startActivity(i);
						finish();
					}
					
					uniqueInstance.setUserExist(false);
	             // ----------------------------------------------- Block to deal when user exist or not ..........  By Kumar Vivek Mitra 16-5-2014
					
					
					
			    
			}
		});
		
		
	}

}

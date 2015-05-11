 package com.example.chatsignalrclient.views;

import java.util.concurrent.TimeUnit;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.example.chatsignalrclient.R;
import com.example.chatsignalrclient.model.APIHandling;

public class SingleORGroupActivity extends Activity{
	
	// -------------------------------------------- Java Instance Variables
	
	APIHandling uniqueSOG = APIHandling.getInstance();
	
	// -------------------------------------------- Java Instance Variables
	
	
	// -------------------------------------------- Android Instance Variables
	
	   RadioGroup  radioGroupSG;
	   RadioButton radioButtonSingle;
	   RadioButton radioButtonMulitple;
	   EditText    editTextGroupName;
	   Button      buttonSubmit;
	   
	// -------------------------------------------- Android Instance Variables
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.single_or_group);
		
		
		setContent();
		setHandler();
		
		
	}

	
	private void setContent(){
		
		radioGroupSG          = (RadioGroup)findViewById(R.id.radioGroup_SG);
		radioButtonSingle     = (RadioButton)findViewById(R.id.radio_Single);
		radioButtonMulitple   = (RadioButton)findViewById(R.id.radio_Group);
		editTextGroupName     = (EditText)findViewById(R.id.editText_GroupName);
		buttonSubmit          = (Button)findViewById(R.id.button_FireToChat);
	}
	
	
	
	private void setHandler(){
		 
		radioGroupSG.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				
				switch(checkedId){
				
				case R.id.radio_Single:
					
					editTextGroupName.setVisibility(View.INVISIBLE);
					break;
					
				case R.id.radio_Group:
					
					editTextGroupName.setVisibility(View.VISIBLE);
					break;
					
				}
			}
		});
		
		
		buttonSubmit.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				if (radioButtonSingle.isChecked() == true) {
					Intent i = new Intent(SingleORGroupActivity.this,ChatListActivity.class);
					startActivity(i);
					//finish();
				}
				else if(radioButtonMulitple.isChecked() == true){
					
					if(editTextGroupName.getText().toString().trim().equalsIgnoreCase("")){
						
						Toast.makeText(SingleORGroupActivity.this,"Please enter the name of the Group to join",Toast.LENGTH_SHORT).show();
						
					}
					else{
						 Thread tx = new Thread(new Runnable() {
								
								@Override
								public void run() {
									// Method to join the group chat
									uniqueSOG.hub.invoke("EntryPoint","JoinGroup",uniqueSOG.userName,"","",editTextGroupName.getText().toString().trim()); 	
									uniqueSOG.groupName = editTextGroupName.getText().toString().trim();
								}
							});
							  
							  tx.start();
							  try { tx.join(); } 
							     catch (InterruptedException e){ e.printStackTrace(); }
							 					
								
							  try { TimeUnit.SECONDS.sleep(3); }    // Hack to delay the execution of the below lines, as the above thread invokes a method onto the server side, then server will call a remote method within the client whose time of execution can be delayed due to network latency, so getting it delayed by 3sec works well, can be tuned as per needed............. By Kumar Vivek Mitra 20_5_2014
							         catch (InterruptedException e) { e.printStackTrace(); }
						Intent i = new Intent(SingleORGroupActivity.this,MessageActivity.class);
						i.putExtra("SOrG", "GROUP");
						i.putExtra("USER_TO", editTextGroupName.getText().toString().trim());
						startActivity(i);
						//finish();	
					}
									
				}
			}
		});
		
		
	}
	
	
	@Override
	public void onBackPressed() {
		
        Thread tx = new Thread(new Runnable() {
			
			@Override
			public void run() {
				
				if(uniqueSOG.groupName.equalsIgnoreCase("")){
				  
				   // Method to disconnect the client from the server
				   uniqueSOG.hub.invoke("EntryPoint","DisconnectClient",uniqueSOG.userName,"","","");         // for Console
			    }
				else{
					
					 // Method to disconnect the client from group
					   uniqueSOG.hub.invoke("EntryPoint","DisconnectGroup",uniqueSOG.userName,"","",uniqueSOG.groupName);         // for Console
					   
					
				    // Method to disconnect client from the server. when user is NOT within any group
					   uniqueSOG.hub.invoke("EntryPoint","DisconnectClient",uniqueSOG.userName,"","","");         // for Console
				}
			}
		});
	     
		  tx.start();
		  try { tx.join(); } 
		     catch (InterruptedException e){ e.printStackTrace(); }
		  
		  try { TimeUnit.SECONDS.sleep(3); }    // Hack to delay the execution of the below lines, as the above thread invokes a method onto the server side, then server will call a remote method within the client whose time of execution can be delayed due to network latency, so getting it delayed by 3sec works well, can be tuned as per needed............. By Kumar Vivek Mitra 20_5_2014
	         catch (InterruptedException e) { e.printStackTrace(); }
		  
		  System.out.println("Client signout");
		  uniqueSOG.connection.stop();
		  ChatListActivity.userList.clear();
		  finish();
		  System.exit(0);
	}
}

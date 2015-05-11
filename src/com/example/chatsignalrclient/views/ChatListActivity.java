package com.example.chatsignalrclient.views;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import com.example.chatsignalrclient.R;
import com.example.chatsignalrclient.R.id;
import com.example.chatsignalrclient.R.layout;
import com.example.chatsignalrclient.model.APIHandling;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

public class ChatListActivity extends Activity{
	
	// ------------------------ Java Instance Variables
	    
	   APIHandling uniqueIns = APIHandling.getInstance();
	   public static ArrayList<String> userList = new ArrayList<String>();
	   public static ArrayAdapter<String>  arrAdpt;
	   
	  
	// ------------------------ Java Instance Variables
	
	
	
	// ------------------------ Android Instance Variables
	
	   private ListView userListView;
	   private Button buttonRefreshUserList;
	   
	    
	// ------------------------ Android Instance Variables
	   
	   
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_clientlist);
		System.out.println("USER:"+uniqueIns.isUserExist());
		setContents();
		setHandlers();
		
		
	}
	
	
	public void setContents(){
		
		userListView          = (ListView)findViewById(R.id.listView_UserList);
		buttonRefreshUserList = (Button)findViewById(R.id.button_Refresh);
		
	}
	
	
	public void setHandlers(){
		
		userListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,long arg3) {
				
				Intent i = new Intent(ChatListActivity.this,MessageActivity.class);
				i.putExtra("SOrG", "SINGLE");
				i.putExtra("USER_TO", userList.get(arg2));
				startActivity(i);
				
			}
			
			
		});
		
		
		buttonRefreshUserList.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
			
				doRefresh();
			}
		});
		
	}
	
	
	private void fillList(){
		
		// Method to fetch the online users
		uniqueIns.hub.invoke("EntryPoint","FetchUserList",uniqueIns.userName,"","",""); 
	}
	
	
	
	@Override
	protected void onResume() {
		super.onResume();
		
		new AsyncTask<Void, Void, Void>() {

			@Override
			protected Void doInBackground(Void... params) {
				
				fillList();
				return null;
			}
			
			@Override
			protected void onPostExecute(Void result) {
				super.onPostExecute(result);
				doRefresh();
				
			}
			
			
		}.execute();
		
		
		
		
	}
	
   
	/*@Override
	public void onBackPressed() {
		
		  Thread tx = new Thread(new Runnable() {
			
			@Override
			public void run() {
				
				 // Method to disconnect the client
				 uniqueIns.hub.invoke("EntryPoint","DisconnectClient",uniqueIns.userName,"","","");         // for Console
			}
		});
	     
		  tx.start();
		  try { tx.join(); } 
		     catch (InterruptedException e){ e.printStackTrace(); }
		  
		  try { TimeUnit.SECONDS.sleep(3); }    // Hack to delay the execution of the below lines, as the above thread invokes a method onto the server side, then server will call a remote method within the client whose time of execution can be delayed due to network latency, so getting it delayed by 3sec works well, can be tuned as per needed............. By Kumar Vivek Mitra 20_5_2014
	         catch (InterruptedException e) { e.printStackTrace(); }
		
		  System.out.println("Client signout");
		  uniqueIns.connection.stop();
		  ChatListActivity.userList.clear();
		  finish();
		  System.exit(0);
	
		
	}*/
	
	
	  public void doRefresh(){
		
			arrAdpt = new ArrayAdapter<String>(ChatListActivity.this, android.R.layout.simple_list_item_1,userList);
			userListView.setAdapter(arrAdpt);
			arrAdpt.notifyDataSetChanged();
		
	}

}

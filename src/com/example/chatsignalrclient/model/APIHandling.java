package com.example.chatsignalrclient.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import microsoft.aspnet.signalr.client.Action;
import microsoft.aspnet.signalr.client.SignalRFuture;
import microsoft.aspnet.signalr.client.hubs.HubConnection;
import microsoft.aspnet.signalr.client.hubs.HubProxy;
import microsoft.aspnet.signalr.client.hubs.Subscription;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.json.JSONObject;
import android.app.Activity;
import android.content.Intent;
import android.sax.StartElementListener;
import android.transition.ChangeBounds;
import android.util.Log;
import android.widget.ArrayAdapter;

import com.example.chatsignalrclient.views.ChatListActivity;
import com.example.chatsignalrclient.views.MainActivity;
import com.example.chatsignalrclient.views.MessageActivity;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class APIHandling{
	
	
	// ---------------------------- Java Instance Variable
	   private static APIHandling uniqueInstance = new APIHandling();
	   public static final String HOST= "http://10.1.81.11:8080";                        // for Console
	   public HubConnection connection;
	   public HubProxy hub;
	   public SignalRFuture<Void> awaitConnection;
	   public String userName  = "";
	   public String groupName = "";
	   public static boolean hasArrived = false;                                          // for controlling the update user list
	   public static boolean hasMessagedArrived = false;                                  // for updating received msg on the screen
	   public boolean isUserExist = false;                                                // If user exist in the chatroom or not
	   private MessageActivity messageInstance;
	   public static final String FTP_HOST = "10.1.81.11";
	   public static final String FTP_USER = "anonymous";
	   public static final String FTP_PASS = "anonymous";
	   
			   
		  
	// ---------------------------- Java Instance Variable
	
	
	
	
	private APIHandling(){
		
		connection = new HubConnection(HOST);
		hub = connection.createHubProxy("MyHub");
		awaitConnection = connection.start();
		try { awaitConnection.get(); } 
		   catch (InterruptedException e) {} 
		      catch (ExecutionException e) {}
	
		   
		  // ----------------------------------------------- Block to handle the exception thrown if a username already exists..........  By Kumar Vivek Mitra 16-5-2014
		 		Subscription sub_userExists = hub.subscribe("userExists");
				sub_userExists.addReceivedHandler(new Action<JsonElement[]>() {
					
					@Override
					public void run(JsonElement[] obj) throws Exception {
						
						setUserExist(true);
						System.out.println("I AM CALLED INTO USEREXISTS");
					}
				});
	     // ----------------------------------------------- Block to handle the exception thrown if a username already exists..........  By Kumar Vivek Mitra 16-5-2014
				
				
		 // ----------------------------------------------- Block to handle the exception thrown if a username already exists..........  By Kumar Vivek Mitra 16-5-2014
				Subscription sub_fetch = hub.subscribe("fetchUserList");
				sub_fetch.addReceivedHandler(new Action<JsonElement[]>() {
					
					@Override
					public void run(JsonElement[] obj) throws Exception {
						
						
						System.out.println("xxx "+obj[0]);
						String strTempOne = obj[0].toString().replaceAll("[\\[\"\\]]", "");  // This is a hack, a proper deserialzation is recommended in full version... By Kumar Vivek Mitra 19_5_2014
						String[] arrTempOne = strTempOne.split(",");
						ChatListActivity.userList.clear();
						
						for(int i=0 ; i<arrTempOne.length ; i++){ 
							
							if(!userName.equalsIgnoreCase(arrTempOne[i])){   // Filter our own name from the received online users list
								
								ChatListActivity.userList.add(arrTempOne[i]); 
								
							}	
						}
						
						
						
						
					}
				});
	    // ----------------------------------------------- Block to handle the exception thrown if a username already exists..........  By Kumar Vivek Mitra 16-5-2014
				
		
	    // ----------------------------------------------- Block to handle the message event handlers By Kumar Vivek Mitra 15-5-2014
				Subscription subs = hub.subscribe("receiveMessage");
				subs.addReceivedHandler(new Action<JsonElement[]>() {
					
					@Override
					public void run(JsonElement[] obj) throws Exception {
						
						MessageActivity.receiveText(obj[0].toString().replaceAll("\"","")+" : "+obj[1].toString().replaceAll("\"",""));
						System.out.println(obj[0]+" : "+obj[1]);
						
					}
				});
	   // ------------------------------------------------- Block to handle the message event handlers By Kumar Vivek Mitra 15-5-2014
				
				
				
	  // ----------------------------------------------- Block to handle the download event handlers By Kumar Vivek Mitra 15-5-2014
				Subscription subs_Download = hub.subscribe("downloadFile");
				subs_Download.addReceivedHandler(new Action<JsonElement[]>() {
					
					@Override
					public void run(JsonElement[] obj) throws Exception {
						
						downloadFile(obj[1].toString().replaceAll("\"",""),MainActivity.pathToSDCard);	
						System.out.println(obj[1].toString().replaceAll("\"","")+" : "+MainActivity.pathToSDCard);
						
					}
				});
	   // ------------------------------------------------- Block to handle the download event handlers By Kumar Vivek Mitra 15-5-2014
				
	}
	
	public static APIHandling getInstance(){
		
		return uniqueInstance;
	}

	public boolean isUserExist() {
		return isUserExist;
	}

	public void setUserExist(boolean isUserExist) {
		this.isUserExist = isUserExist;
	}
	
	
	
	
	/**
	 * Method to handle the upload of the media file
	 * @param user
	 * @param filePath
	 */
      public void uploadFile(final String user,final String filePath){
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				
				FTPClient con     = null;
		        String[] tempArr  = filePath.split("/");         // To get the filename of the path. eg: /mnt/sdcard/vivek.mp3
		        String   fileName = tempArr[tempArr.length-1];   // vivek.mp3 will be the intended result
		        
		        try
		        {
		            con = new FTPClient();
		            con.connect(FTP_HOST);

		            if (con.login(FTP_USER,FTP_PASS))
		            {
		                con.enterLocalPassiveMode(); // important!
		                con.setFileType(FTP.BINARY_FILE_TYPE);
		                String data = filePath;

		                FileInputStream in = new FileInputStream(new File(data));
		                boolean result = con.storeFile(user+"_"+MessageActivity.userTo+"_"+fileName, in);
		                in.close();
		                if (result){
		                	if(groupName.equalsIgnoreCase("")){
		                	hub.invoke("EntryPoint","DownloadFile",userName,user+"_"+MessageActivity.userTo+"_"+fileName,MessageActivity.userTo,"");
		                	}
		                	else{
		                	hub.invoke("EntryPoint","DownloadFile",userName,user+"_"+MessageActivity.userTo+"_"+fileName,MessageActivity.userTo,groupName);
		                	}
		                	
		                	
		                	Log.v("upload result", "succeeded");
		                }
		                	
		                con.logout();
		                con.disconnect();
		            }
		        }
		        catch (Exception e) { e.printStackTrace(); }
				
			}
		}).start();
		
	}
	
	
	
	
	
      
      
    /**
     * Method to handle the download of the media file
     * @param fileName
     * @param filePath
     */
      public void downloadFile(final String fileName, final String filePath){
  		
    	 
  		new Thread(new Runnable() {
  			
  			@Override
  			public void run() {
  				
  				
  				 FTPClient con = null;
  				
  				 try
  				    {
  				        con = new FTPClient();
  				        con.connect(FTP_HOST);

  				        if (con.login(FTP_USER,FTP_PASS))
  				        {
  				            con.enterLocalPassiveMode(); // important!
  				            con.setFileType(FTP.BINARY_FILE_TYPE);
  				            String data = filePath+"/"+fileName;

  				            System.out.println(data);
  				            OutputStream out = new FileOutputStream(new File(data));
  				            boolean result = con.retrieveFile(fileName, out);
  				            out.close();
  				            if (result) {
  				            	// -------------------- Put the code here to update the UI of the listView
  				            	MessageActivity.receiveFile(data);
  				            	Log.v("download result", "succeeded");
  				            }
  				            	
  				            con.logout();
  				            con.disconnect();
  				        }
  				    }
  				    catch (Exception e)
  				    {
  				        Log.v("download result","failed");
  				        e.printStackTrace();
  				    }
  			}
  		}).start();
  		
  	}  
      
    // ---------------------------------------------------- Method to handle the download of the media file
      
      
      
	/*public void setMessageInstance(MessageActivity messageIns){
		
		 this.messageInstance = messageIns;
	}*/

}

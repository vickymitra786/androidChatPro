/**
 * This class is implemented by Kumar Vivek Mitra on 10_1_2014
 * to find the exact external storage onto the android device,
 * as external storage keep changing according to the manufactures.
 * 
 */



package com.example.chatsignalrclient.model;

import java.io.InputStream;
import java.util.HashSet;
import java.util.Locale;

public class ExternalStorageFinderClass {
	
	
	///// ----------------------------------- Java Instance Variables
	
	private static ExternalStorageFinderClass externalStorageUniquesInstance = new ExternalStorageFinderClass();
	
	///// ----------------------------------- Java Instance Variables
	
	
	///// ----------------------------------- Android Instance Variables
	
	
	
	///// ----------------------------------- Android Instance Variables
	
	
	
	/**
	 * Private Constructor to prevent creation of an instance of this class
	 * from outside.
	 */
	private ExternalStorageFinderClass(){}  
	
	
	
	
	/**
	 * Method to get the Unique instance of this class
	 */
	public static ExternalStorageFinderClass getInstance(){
		
		return externalStorageUniquesInstance;
	}
	
	

	/**
	 * Method to get the external storages
	 */
	public static HashSet<String> getExternalStorages() {
	    final HashSet<String> out = new HashSet<String>();
	    String reg = "(?i).*vold.*(vfat|ntfs|exfat|fat32|ext3|ext4).*rw.*";
	    String s = "";
	    try {
	        final Process process = new ProcessBuilder().command("mount").redirectErrorStream(true).start();
	        process.waitFor();
	        final InputStream is = process.getInputStream();
	        final byte[] buffer  = new byte[1024];
	        
	        while (is.read(buffer) != -1) {
	            s = s + new String(buffer);
	        }
	        
	        is.close();
	    } catch (final Exception e) {e.printStackTrace();}

	    // filter/process output
	    final String[] lines = s.split("\n");
	    for (String line : lines) {
	        if (!line.toLowerCase(Locale.US).contains("asec")) {
	            if (line.matches(reg)) {
	                String[] parts = line.split(" ");
	                for (String part : parts) {
	                    if (part.startsWith("/"))
	                        if (!part.toLowerCase(Locale.US).contains("vold"))
	                            out.add(part);
	                }
	            }
	        }
	    }
	    return out;
	}
	
	/** 
	 * To get the first entry of the
	 * HashSet returned from ExternalStorageFinderClass
	 * by invoking its getExternalStorage() method, by Kumar Vivek Mitra 1_10_2014
	 */
	public String getExtPath(){
		
		String extPath = getExternalStorages().toArray()[0].toString();
		return extPath;
	}
}

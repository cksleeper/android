package com.taipower.west_branch.utility;

import com.taipower.west_branch.R;
import com.taipower.west_branch.R.style;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.util.Log;



@SuppressLint("NewApi")
public class NoTitleBar {
	
	public NoTitleBar()
	{
		
	}
	
	
	public NoTitleBar(int sdk_version,Context context_name,Activity activity_name)
	{
		if(VERSION.SDK_INT >= VERSION_CODES.GINGERBREAD) 
		{    
			//Log.i("Serial :",Build.SERIAL);
			//long hash_code = Integer.valueOf(Build.SERIAL.replaceAll("[^0-9]+", "")).longValue();
			//Log.i("Serial without letter :", "" + hash_code);
		}
		
		
		if(  sdk_version >= VERSION_CODES.HONEYCOMB )
        {		
			ActionBar actionBar = activity_name.getActionBar();			//hide the activity action bar
        	actionBar.hide();
        	context_name.setTheme(R.style.AppBaseTheme);
        }   
        else
        {
        	//??? not work
        	//ContextThemeWrapper theme_data = new ContextThemeWrapper(context_name , 0);
        	//theme_data.setTheme(R.style.AppTheme_no_action_bar);
        	
        	//I'm shame when I see a sentence above this; 
        	
        	context_name.setTheme(R.style.AppTheme_no_action_bar);
        	
        	
        }
	}
	
	
	
	
	public void actionOrTitleBarHide(int sdk_version,Context context_name,Activity activity_name)
	{
		
		if(  sdk_version >= VERSION_CODES.HONEYCOMB )
        {		
        	ActionBar actionBar = activity_name.getActionBar();			//hide the activity action bar
        	actionBar.hide();
        }   
        else
        {
        	//??? not work
        	//ContextThemeWrapper theme_data = new ContextThemeWrapper(context_name , 0);
        	//theme_data.setTheme(R.style.AppTheme_no_action_bar);
        	
        	context_name.setTheme(R.style.AppTheme_no_action_bar);
        	
        }
	}
	

}

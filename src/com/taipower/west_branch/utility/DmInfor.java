package com.taipower.west_branch.utility;

import java.util.HashMap;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
import android.util.Log;


public class DmInfor
{
	public int v_width;
	public int v_height; 
	
	public float scale;
	public int scale_dpi;
	public int menu_linear_height;
	public int linear_remain;
	public int text_size;
	
	public DisplayMetrics dm;
	
	public DmInfor( Activity activity , Context context) 
	{
		scale = context.getResources().getDisplayMetrics().density;
		scale_dpi = context.getResources().getDisplayMetrics().densityDpi;
		Log.i("scale", "" + scale);
		Log.i("scale_dpi", "" + scale_dpi);
		
		dm = new DisplayMetrics();
		activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
    
		v_width = dm.widthPixels;
		v_height = dm.heightPixels;
            
		menu_linear_height = (int) (v_height / 10 );
		linear_remain = v_height - menu_linear_height;
		
		HashMap<Double,String> recommand_dpi = new HashMap<Double,String>();
		recommand_dpi.put(Double.valueOf(120.0f), "ldpi");
		recommand_dpi.put(Double.valueOf(160.0f), "mdpi");
		recommand_dpi.put(Double.valueOf(240.0f), "hdpi");
		recommand_dpi.put(Double.valueOf(320.0f), "xhdpi");
		recommand_dpi.put(Double.valueOf(480.0f), "xxhdpi");
		
		
		Log.i("This device resolution :" , v_width + " X " + v_height );
		Log.i("DPI Level : ", String.valueOf(scale_dpi)  + " " +  recommand_dpi.get(Double.valueOf(scale_dpi)) );
		
		
		text_size = (  ( (int) ( ( (float) ( v_height / 100) ) / scale ) ) < 16)? 16 : (int) ( ( (float) ( v_height / 100) ) / scale ) ; 
	
	}

	/*
	    //The gesture threshold expressed in dp
        float GESTURE_THRESHOLD_DP = 16.0f;
        // Get the screen's density scale
        final float scale = getResources().getDisplayMetrics().density;
        // Convert the dps to pixels, based on density scale
        int mGestureThreshold = (int) (GESTURE_THRESHOLD_DP * scale + 0.5f);
        // Use mGestureThreshold as a distance in pixels...
        
        DisplayMetrics dm = new DisplayMetrics();					// 取得螢幕解析度
        this.getWindowManager().getDefaultDisplay().getMetrics(dm);
        
        v_width = dm.widthPixels;
        v_height = dm.heightPixels;
        
        int  set_width = (int) (((mGestureThreshold*1f)/(24*1f)) * 150f);
	  
	  */
	  
	  
	

}
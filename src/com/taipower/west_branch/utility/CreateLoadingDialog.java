package com.taipower.west_branch.utility;

import com.taipower.west_branch.R;
import com.taipower.west_branch.R.anim;
import com.taipower.west_branch.R.drawable;
import com.taipower.west_branch.R.id;
import com.taipower.west_branch.R.layout;
import com.taipower.west_branch.R.style;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;



public class CreateLoadingDialog
{
	public static final int NON_DOWNLOAD_TAG = -1;
	public static final boolean CANCELABLE = true;
	public static final boolean NONCANCELABLE = false;
	/*
	public static Dialog create_loading_dialog(Context context, String msg, int progress_percent) 
	{  
  	  
        LayoutInflater inflater = LayoutInflater.from(context);  
        
        //load dialog layout
        View view = inflater.inflate(R.layout.loading_dialog, null);   
        //load layout linear
        LinearLayout layout = (LinearLayout) view.findViewById(R.id.download_dialog);  
        
        //load layout ImageView  
        ImageView spaceshipImage = (ImageView) view.findViewById(R.id.loading_image);  
        
        TextView loading_text = (TextView) view.findViewById(R.id.loading_text);  
        //mount animation  
        Animation hyperspaceJumpAnimation = AnimationUtils.loadAnimation(context, R.anim.loading_animation);  
        
        //ImageView to animation  
        spaceshipImage.startAnimation(hyperspaceJumpAnimation);  
        loading_text.setText(msg);
        
        if(progress_percent  != NON_DOWNLOAD_PERCENT_TAG)
        {
        	loading_percent = (TextView) view.findViewById(R.id.loading_percent);
        	loading_percent.setText(String.valueOf(progress_percent) + "%");
        }
        
        //new dialog
        Dialog loadingDialog = new Dialog(context, R.style.loading_style);
  
        loadingDialog.setCancelable(true);  
        loadingDialog.setContentView(layout, new LinearLayout.LayoutParams(  
                LinearLayout.LayoutParams.MATCH_PARENT,  
                LinearLayout.LayoutParams.MATCH_PARENT));  
                
        return loadingDialog;      
	}  
	*/
	
		
	public static Dialog createLoadingDialog(Context context, String msg, int be_downloaded_byte , int total_byte, boolean cancelable) 
	{  
  	  	
        LayoutInflater inflater = LayoutInflater.from(context);  
        
        //load dialog layout
        View view = inflater.inflate(R.layout.loading_dialog, null);   
        //load layout linear
        LinearLayout layout = (LinearLayout) view.findViewById(R.id.download_dialog);  
        
        if(be_downloaded_byte  != NON_DOWNLOAD_TAG)
        {
        	ImageView running = (ImageView) view.findViewById(R.id.loading_image); 
        	running.setImageResource(R.drawable.taipower_boy1_running1);
        	
        	TextView loading_percent = (TextView) view.findViewById(R.id.loading_data_progress);
        	loading_percent.setText(String.valueOf(be_downloaded_byte) + "/" + String.valueOf(total_byte));
        }
        else
        {
        	//load layout ImageView  
        	ImageView cycle_image_view = (ImageView) view.findViewById(R.id.loading_image);  
        
        	//mount animation  
        	Animation hyperspaceJumpAnimation = AnimationUtils.loadAnimation(context, R.anim.loading_animation);  
        
        	//ImageView to animation  
        	cycle_image_view.startAnimation(hyperspaceJumpAnimation);  	
        }
        
        TextView loading_text = (TextView) view.findViewById(R.id.loading_text);  
        loading_text.setText(msg);
        
        //new dialog
        Dialog loadingDialog = new Dialog(context, R.style.loading_style);
        //Dialog loadingDialog = new Dialog(context);
        loadingDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        
        loadingDialog.setCancelable(false);  
        loadingDialog.setContentView(view, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));  
        
        if( cancelable)
        {
        	loadingDialog.setOnKeyListener( new DialogInterface.OnKeyListener()
        	{
        		@Override
        		public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) 
        		{
        			// TODO Auto-generated method stub
        			if( keyCode == KeyEvent.KEYCODE_BACK )
        				dialog.dismiss();
				
        			return false;
        		}
        	});
        }
        
        loadingDialog.show();
        
        return loadingDialog;
	}
	
	
	
	public static void update_percent(Dialog dialog , int  be_downloaded_byte , int total_byte)
	{
		//find view ine dialog
		ImageView running = (ImageView) dialog.findViewById(R.id.loading_image);
		
		if( (be_downloaded_byte % 10240) < 5120 )
			running.setImageResource(R.drawable.taipower_boy1_running0);
		else
			running.setImageResource(R.drawable.taipower_boy1_running1);
			
		TextView loading_percent = (TextView) dialog.findViewById(R.id.loading_data_progress);
        loading_percent.setText(String.valueOf(be_downloaded_byte) + "/" + String.valueOf(total_byte));
	}    
        
	
	public static void dialog_dismiss(Dialog dialog)
	{
		dialog.dismiss();
	}

}